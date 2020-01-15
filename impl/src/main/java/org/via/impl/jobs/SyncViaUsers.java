package org.via.impl.jobs;

import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlReaderFinishedException;
import org.sakaiproject.db.api.SqlService;

import org.sakaiproject.user.api.User;

import org.via.IapiTool;
import org.via.cUserPermissions;
import org.via.enums.eParticipantType;
import org.via.enums.eActivityState;

public class SyncViaUsers implements Job {

    private static final Logger log = Logger.getLogger(SyncViaUsers.class);
    private String configMessage;
    
    private static boolean isRunning = false;

    @Setter
    @Getter
    protected IapiTool apiTool = null;

    @Getter
    @Setter
    private static SqlService sqlService;
    private static String query;
    private static Object[] fields;
	
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
    {
            if (isRunning) {
                    log.warn("Stopping job since it's already running");
                    return;
            }
            isRunning = true;
        log.debug("SyncViaUsers is running");
            
        try
        {
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {   
                    if(apiTool == null)                
                            log.error("apiTool NULL! ");
                    else
                    {
                        query = "SELECT DISTINCT SakaiGroupID, SakaiSiteID FROM via_groupsusers";
                        fields = new Object[] {};
                                        
                        sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                        {
                            public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                                try 
                                {    
                                        final List<String> lstUsers = new ArrayList<String>();
                                        List<String> lstUsersToAdd = new ArrayList<String>();

                                        query = "SELECT SakaiUserID FROM via_groupsusers WHERE SakaiGroupID = ?";
                                        fields = new Object[] { rs.getString("SakaiGroupID") };
                                                        
                                        sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                        {
                                            public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                try 
                                                {  
                                                        lstUsers.add(rsUsers.getString("SakaiUserID"));
                                                 } catch (SQLException e) {
                                                    log.error("SyncViaUsers error : " + e.getMessage());
                                                }
                                                return null;
                                            }
                                        });
                                    
                                    try {
                                        //Compare with the users in the sakai groups.
                                       
                                        for(String userid : apiTool.getSiteService().getSite(rs.getString("SakaiSiteID")).getGroup(rs.getString("SakaiGroupID")).getUsers())
                                        {
                                            //New user in the group, we need to add it to every single groups...
                                          if(lstUsers.indexOf(userid) == -1)
                                            {
                                                    lstUsersToAdd.add(userid);
                                            }
                                            else
                                            {
                                                    lstUsers.remove(userid);    
                                            }
                                        }
                                    }
                                    catch(Exception e) {
                                        log.error("SyncViaUsers : apiTool.getSiteService().getSite " + e.getMessage());
                                    }
                                    
                                        //If there are users remaining in the list, it means they were removed from the group.
                                        if(lstUsers.size() > 0)
                                        {
                                            for(final String userToRemove : lstUsers)
                                            {
                                                    //Remove users from all activities this group is assigned to.[NEED TO CHECK IF IT IS ASSIGNED TO ANOTHER GROUP BEFORE REMOVING]
                                                    query = "SELECT vag.ActivityID, COUNT(SakaiUserID) count, " +
                                                                    " (SELECT ParticipantType FROM via_activityusers WHERE ActivityID = vag.ActivityID AND SakaiUserID = vgu.SakaiUserID) ParticipantType" +
                                                                    " FROM via_activitygroups vag " + 
                                                                    " JOIN via_groupsusers vgu ON vgu.SakaiGroupID = vag.SakaiGroupID " + 
                                                                    " WHERE SakaiUserID = ?" + 
                                                                    " group by SakaiUserID, ActivityID";
                                                fields = new Object[] { userToRemove };
                                                                
                                                sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                                {
                                                        public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                                try 
                                                                {  
                                                                        if(rsUsers.getInt("count") == 1)
                                                                        {
                                                                                if(rsUsers.getInt("ParticipantType") != eParticipantType.Presentateur.getValue()) {
                                                                                    apiTool.removeParticipantFromActivity(rsUsers.getString("ActivityID"), userToRemove, rs.getString("SakaiSiteID"));
                                                                                    log.debug("removeParticipantFromActivity - activityId: " + rsUsers.getString("ActivityID") + " site: " + rs.getString("SakaiSiteID") + " user: " + userToRemove);
                                                                                }
                                                                        }
                                                                } catch (SQLException e) {
                                                                        log.error("SyncViaUsers : " + e.getMessage());
                                                                }
                                                                return null;
                                                        }
                                                });
                                                
                                                //Remove from the group sync table.
                                                query = "DELETE FROM via_groupsusers WHERE SakaiGroupID = ? and SakaiSiteID = ? and SakaiUserID = ?";
                                                fields = new Object[] { rs.getString("SakaiGroupID"), rs.getString("SakaiSiteID"), userToRemove };
                                                sqlService.dbWrite(connection, query, fields);
                                                connection.commit();
                                                log.debug("Delete from VIA_GROUPSUSERS WHERE SakaiGroupID = " + rs.getString("SakaiGroupID") + " and SakaiSiteID = " + rs.getString("SakaiSiteID") +
                                                                " and SakaiUserID = " + userToRemove);
                                            }
                                        }

                                        //If there are users in this list, it means we want to add them to every activities this group is assigned to.
                                        if(lstUsersToAdd.size() > 0)
                                        {
                                            for(final String userToAdd : lstUsersToAdd)
                                            {
                                                    //Add users to all activities this group is assigned to.[NEED TO CHECK IF IT IS ALREADY ASSIGNED BY ANOTHER GROUP OR BY HAND]
                                                    query = "SELECT vag.ActivityID, vau.SakaiUserID FROM via_activitygroups vag left JOIN via_activityusers vau ON vag.ActivityID = vau.activityID AND vau.SakaiUserID = ? " +
                                                                    " WHERE SakaiGroupID = ? ";
                                                    fields = new Object[] { userToAdd, rs.getString("SakaiGroupID") };
                                                                
                                                    sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                                    {
                                                        public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                            try 
                                                            {
                                                                     if(rsUsers.getObject("SakaiUserID") == null) {
                                                                         apiTool.addParticipantToActivity(rsUsers.getString("ActivityID"), userToAdd, rs.getString("SakaiSiteID"));
                                                                         log.debug("addParticipantToActivity - activityId: " + rsUsers.getString("ActivityID") + " userId: " + userToAdd + " siteId: " + rs.getString("SakaiSiteID"));
                                                                     }
                                                            } catch (SQLException e) {
                                                                log.error("SyncViaUsers : " + e.getMessage());
                                                            }
                                                            return null;
                                                        }
                                                    });

                                                    //Add to the group sync table.
                                                    query = "INSERT INTO via_groupsusers (SakaiGroupID, SakaiSiteID, SakaiUserID) VALUES (?, ?, ?)";
                                                    fields = new Object[] { rs.getString("SakaiGroupID"), rs.getString("SakaiSiteID"), userToAdd };
                                                    sqlService.dbWrite(connection, query, fields);
                                                    connection.commit();
                                                    log.debug("INSERT INTO via_groupsusers (SakaiGroupID, SakaiSiteID, SakaiUserID) VALUES ("+rs.getString("SakaiGroupID")+", "+rs.getString("SakaiSiteID")+", "+userToAdd+")");
                                                }
                                            }
                                    } catch (SQLException e) {
                                        log.error("SyncViaUsers : " + e.getMessage());
                                    }
                                return null;
                            }
                        });

                        //SYNC SITE
                        try
                        {
                            query = "SELECT distinct SakaiSiteID FROM via_activity";
                            fields = new Object[] {};
                                            
                            sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                            {
                                public Object readSqlResultRecord(ResultSet r1) throws SqlReaderFinishedException {
                                    final ResultSet rs = r1;
                                    try 
                                    {
                                            final List<String> lstUsers = new ArrayList<String>();
                                            List<String> lstUsersToAdd = new ArrayList<String>();
                                            List<String> lstUsersToChangeToCollab = new ArrayList<String>();

                                            query = "SELECT SakaiUserID FROM via_siteusers WHERE SakaiSiteID = ?";
                                            fields = new Object[] { rs.getString("SakaiSiteID") };
                                            
                                            sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                            {
                                                public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                    try 
                                                    {  
                                                        //Fill list of users.
                                                                lstUsers.add(rsUsers.getString("SakaiUserID"));
                                                    } catch (SQLException e) {
                                                        log.error("SyncViaUsers : " + e.getMessage());
                                                    }
                                                    return null;
                                                }
                                            });

                                            try 
                                            {
                                                Site site = apiTool.getSiteService().getSite(rs.getString("SakaiSiteID"));
                                                //Compare with the users in the sakai site.
                                                for(String userid : site.getUsers())
                                                {
                                                    //New user in the group, we need to add it to every single groups...
                                                    if(lstUsers.indexOf(userid) == -1)
                                                    {
                                                        lstUsersToAdd.add(userid);
                                                    }
                                                    else
                                                    {
                                                        lstUsers.remove(userid);    
                                                    }

                                                    //Verify user permissions to update to Collabo in VIA.
                                                    User user = apiTool.getSakaiUserInfo(userid);
                                                    if(user != null)
                                                        if(apiTool.getSecurityService().unlock(user, cUserPermissions.canEditActivityFN, site.getReference()))
                                                                lstUsersToChangeToCollab.add(userid);
                                                }
                                            }
                                            catch(Exception e) 
                                            {
                                                log.error("SyncViaUsers : apiTool.getSiteService().getSite " + e.getMessage());
                                            }
                                            

                                            if(lstUsersToChangeToCollab.size() > 0)
                                            {
                                                String sqlIn = "";
                                                for(String userToChange : lstUsersToChangeToCollab)
                                                {
                                                        if(sqlIn == "")
                                                                sqlIn = "'" + userToChange + "'";
                                                        else
                                                                sqlIn += ", '" + userToChange + "'";
                                                }

                                                //Find the VIA user IDs that needs to be updated to Collaborator in this site.
                                                query = "SELECT UserID, SakaiUserID FROM via_users WHERE ViaType != 4 AND SakaiUserID IN (" + sqlIn + ")";
                                                fields = new Object[] {};
                                                                
                                                sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                                {
                                                    public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                        try 
                                                        {                                                          
                                                                    //API call to update from "Participant(2)" to "Collaborator(4)"
                                                                    apiTool.UpdateVIAUserType(rsUsers.getString("UserID"), rsUsers.getString("SakaiUserID"), rs.getString("SakaiSiteID"));
                                                                    log.debug("UpdateVIAUserType (participant(2) to Collaborator(4)) - UserID: " + rsUsers.getString("UserID") + " SakaiUserID: " + rsUsers.getString("SakaiUserID") +
                                                                            " SakaiSiteID: " + rs.getString("SakaiSiteID"));
                                                        } catch (SQLException e) {
                                                            log.error("SyncViaUsers : " + e.getMessage());
                                                        }
                                                        return null;
                                                    }
                                                });
                                               
                                                //Add to the user sync table.
                                                query = "UPDATE via_users SET ViaType = 4 WHERE SakaiUserID IN (" + sqlIn + ")";
                                                fields = new Object[] {};
                                                sqlService.dbWrite(connection, query, fields);
                                                connection.commit();
                                                log.debug("Update VIA_USERS set ViaType = 4 where SakaiUserID in (" + sqlIn + ")");
                                            }

                                            //REMOVE
                                            if(lstUsers.size() > 0)
                                            {
                                                for(final String userToRemove : lstUsers)
                                                {
                                                    //Add to activity
                                                    query = "SELECT va.ActivityID, vau.SakaiUserID, " +
                                                                    " (SELECT ParticipantType FROM via_activityusers WHERE ActivityID = va.ActivityID AND SakaiUserID = vau.SakaiUserID) ParticipantType " +
                                                                    " FROM via_activity va" + 
                                                                    " LEFT JOIN via_activitygroups vag ON va.ActivityID = vag.ActivityID " + 
                                                                    " JOIN via_activityusers vau on  va.ActivityID = vau.ActivityID AND SakaiUserID = ?" + 
                                                                    " WHERE SakaiGroupID IS NULL" + 
                                                                    " AND not SessionState = ?" + 
                                                                    " AND SakaiSiteID = ?" + 
                                                                    " AND not ParticipantType = ?" + 
                                                                    " group by va.ActivityID, SakaiUserID";
                                                    fields = new Object[] { userToRemove, eActivityState.Supprimer.getValue(), rs.getString("SakaiSiteID"), eParticipantType.Presentateur.getValue() };
                                                                    
                                                    sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                                    {
                                                        public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                            try 
                                                            {  
                                                                    apiTool.removeParticipantFromActivity(rsUsers.getString("ActivityID"), userToRemove, rs.getString("SakaiSiteID"));
                                                                    log.debug("removeParticipantFromActivity - activityId: " + rsUsers.getString("ActivityID") + " userId: " + userToRemove +
                                                                            " siteId: " +  rs.getString("SakaiSiteID"));
                                                            } catch (SQLException e) {
                                                                log.error("SyncViaUsers : " + e.getMessage());
                                                            }
                                                            return null;
                                                        }
                                                    });

                                                    //Add to the group sync table.
                                                    query = "DELETE FROM via_siteusers WHERE SakaiUserID = ? AND SakaiSiteID = ?";
                                                    fields = new Object[] {userToRemove, rs.getString("SakaiSiteID")};
                                                    sqlService.dbWrite(connection, query, fields);
                                                    connection.commit();
                                                    log.debug("Delete from VIA_SITEUSERS where SakaiUserID = " + userToRemove + " AND SakaiSiteID = " + rs.getString("SakaiSiteID"));
                                                }
                                            }

                                            //ADD
                                            if(lstUsersToAdd.size() > 0)
                                            {
                                                for(final String userToAdd : lstUsersToAdd)
                                                {
                                                    //Add to activity
                                                    query = "SELECT va.ActivityID, vau.SakaiUserID FROM via_activity va" + 
                                                                    " LEFT JOIN via_activitygroups vag ON va.ActivityID = vag.ActivityID" + 
                                                                    " LEFT JOIN via_activityusers vau on  va.ActivityID = vau.ActivityID AND SakaiUserID = ?" + 
                                                                    " WHERE SakaiGroupID IS NULL" + 
                                                                    " AND not SessionState = ?" +
                                                                    " AND va.SakaiSiteID = ?" + 
                                                                    " AND va.EnrollmentType = 0" + 
                                                                    " group by va.ActivityID, SakaiUserID";
                                                    fields = new Object[] { userToAdd, eActivityState.Supprimer.getValue(), rs.getString("SakaiSiteID") };
                                                                    
                                                    sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                                                    {
                                                        public Object readSqlResultRecord(ResultSet rsUsers) throws SqlReaderFinishedException {
                                                            try 
                                                            {  
                                                                
                                                                    if(rsUsers.getObject("SakaiUserID") == null) {
                                                                        apiTool.addParticipantToActivity(rsUsers.getString("ActivityID"), userToAdd, rs.getString("SakaiSiteID"));
                                                                        log.debug("addParticipantToActivity - activityId: " + rsUsers.getString("ActivityID") + " userId: " + userToAdd + " SakaiSiteID: " + rs.getString("SakaiSiteID"));
                                                                    }
                                                            } catch (SQLException e) {
                                                                log.error("SyncViaUsers : " + e.getMessage());
                                                            }
                                                            return null;
                                                        }
                                                    });
                                                     //Add to the group sync table.
                                                    query = "INSERT INTO via_siteusers (SakaiUserID, SakaiSiteID) VALUES (?, ?)";
                                                    fields = new Object[] {userToAdd, rs.getString("SakaiSiteID")};
                                                    sqlService.dbWrite(connection, query, fields);
                                                    connection.commit();
                                                    log.debug("Insert into VIA_SITEUSERS (SakaiUserID, SakaiSiteID) VALUES (" + userToAdd + ", " + rs.getString("SakaiSiteID")+")");
                                                }
                                            }
                                    } catch (SQLException e) {
                                        log.error("SyncViaUsers : " + e.getMessage());
                                    }
                                    return null;
                                }
                            });
                        } 
                        catch (Exception e) 
                        {
                                log.error("SyncViaUsers : " + e.getMessage());
                                e.printStackTrace();
                        }
                    }                                
                }
                catch (Exception e) 
                {
                    log.error("SyncViaUsers : " + e.getMessage());
                    e.printStackTrace();
                }
                finally
                {
                    connection.setAutoCommit(wasCommit);
                    sqlService.returnConnection(connection); 
                }
        }
        catch(Exception e)
        {
            log.error("SyncViaUsers : " + e.getMessage());
            e.printStackTrace();
        }

        isRunning = false;
        log.debug("SyncViaUsers finished");
    }
    
    public String getConfigMessage() {
            return configMessage;
    }

    public void setConfigMessage(String configMessage) {
            this.configMessage = configMessage;
    }
}