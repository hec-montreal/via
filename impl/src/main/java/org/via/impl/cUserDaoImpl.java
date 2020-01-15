package org.via.impl;

import lombok.*;
import org.apache.log4j.*;
import org.sakaiproject.db.api.*;
import org.sakaiproject.site.api.*;
import org.sakaiproject.user.api.*;
import org.via.*;
import org.via.dao.*;
import org.via.enums.*;

import java.sql.*;
import java.text.*;
import java.util.*;

public class cUserDaoImpl implements cUserDao {
    
    private static final Logger log = Logger.getLogger(cUserDaoImpl.class);
    
    @Getter @Setter
    private static SqlService sqlService;
    
    @Setter @Getter
    protected IapiTool apiTool;
    
    private static String query;
    private static Object[] fields;
    private cUserPermissions permissions;
    
    private String SakaiSiteID = "";
    private String SakaiID = "";
    private ApiRequestImpl Api;
    private UserDirectoryService Uds;


    public cUserDaoImpl() {
        super();
    }
    
    @Override
    public void setPermission (cUserPermissions Newpermissions){
        this.permissions=Newpermissions;
        }
    @Override
    public void setSakaiSiteID (String sakaiSiteID){
        this.SakaiSiteID=sakaiSiteID;
        };
    @Override
    public void setSakaiID (String sakaiID){
        this.SakaiID=sakaiID;
        };
    

    @Override
    public List<IcActivity> getActivityList(UserDirectoryService uds,IcUsers user) {

        Uds =uds;
        return getActivityList("", apiTool, user);
    }

    private Map<IcActivity, Boolean> getSiteActivities(IapiTool ApiTool, IcUsers user){
        final Map<IcActivity, Boolean> activitiesMap = new HashMap<IcActivity, Boolean>();

        query = "SELECT au.ActivityID FROM via_activity au left JOIN via_activitygroups aw ON au.activityID = aw.activityId" +
                " where aw.activityId is null and au.sakaisiteid = ? and au.sessionstate=1 order by au.sessiondate";
        fields = new Object[] { user.getSakaiSiteID()};

         try {



             final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try
            {

                sqlService.dbRead(connection, query, fields, new SqlReader<Object>()
                {
                    public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                        try
                        {
                            IcActivity activity = apiTool.getActivity(rs.getString("ActivityID"), user.getSakaiID());
                            activitiesMap.put(activity, apiTool.canEditActivity(activity, user.getSakaiID()));
                        } catch (SQLException e) {
                            log.error("cUserDaoImpl, activities retrieval: " + e.getMessage());
                        }
                        return null;
                    }
                });
                //  return activitiesMap;
            }
            catch (Exception e)
            {
                log.error("cUserDaoImpl : " + e.getMessage());
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
            log.error("Cannot get database connection: " + e, e);
            log.error("cUserDaoImpl : " + e.getMessage());
            //e.printStackTrace();
        }

        return activitiesMap;
    }

    private Map<IcActivity, Boolean> getGroupActivities(IapiTool ApiTool, IcUsers user, String groupFilter){
        final Map<IcActivity, Boolean> activitiesMap = new HashMap<IcActivity, Boolean>();

        query = "SELECT au.ActivityID FROM via_activity au left JOIN via_activitygroups aw ON au.activityID = aw.activityId" +
                " where au.sakaisiteid = ? and aw.sakaigroupId = ? and au.sessionstate=1 order by au.sessiondate";
        fields = new Object[] { user.getSakaiSiteID(), groupFilter};

         try
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try
            {

                sqlService.dbRead(connection, query, fields, new SqlReader<Object>()
                {
                    public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                        try
                        {
                            IcActivity activity = apiTool.getActivity(rs.getString("ActivityID"), user.getSakaiID());
                            activitiesMap.put(activity, apiTool.canEditActivity(activity, user.getSakaiID()));
                        } catch (SQLException e) {
                            log.error("cUserDaoImpl, activities retrieval: " + e.getMessage());
                        }
                        return null;
                    }
                });
                //  return activitiesMap;
            }
            catch (Exception e)
            {
                log.error("cUserDaoImpl : " + e.getMessage());
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
            log.error("Cannot get database connection: " + e, e);
            log.error("cUserDaoImpl : " + e.getMessage());
            //e.printStackTrace();
        }
        return activitiesMap;

    }

    private Map<IcActivity, Boolean> getAllActivities(IapiTool ApiTool, IcUsers user){
        final Map<IcActivity, Boolean> activitiesMap = new HashMap<IcActivity, Boolean>();
        final Set<String> checkDuplicateActivitiesMap = new HashSet<String>();
       if (user.getPermissions().getCanCreateActivity() || user.getPermissions().getCanEditActivity()) {

            query = "SELECT au.ActivityID FROM via_activity au left outer JOIN via_activitygroups aw ON au.activityID = aw.activityId" +
                    " where au.sakaisiteid = ? and au.sessionstate=1 order by sessiondate";
            fields = new Object[]{user.getSakaiSiteID()};
        }
        else{
            List<Group> userGroups  = apiTool.getUserGroupList( user.getSakaiSiteID());
            if (userGroups.size() == 0)
                return activitiesMap;
            query = "SELECT au.ActivityID FROM via_activity au JOIN via_activitygroups aw ON au.activityID = aw.activityId" +
                    " where au.sakaisiteid = ? and (";
            fields = new Object[userGroups.size() + 1] ;
            fields[0] = user.getSakaiSiteID();
            int position = 1;
            boolean addedGroup = false;
            List<String> groups = new ArrayList<String>();
            for (Group group: userGroups){
                if (group.isAllowed(user.getSakaiID(),  cUserPermissions.canEditActivityFN )
                        || group.isAllowed(user.getSakaiID(),  cUserPermissions.canCreateActivityFN)) {
                    query += " aw.sakaigroupId = ? or ";
                    fields[position++] = group.getId();
                    addedGroup = true;
                }
            }
            if (addedGroup) {
                query = query.substring(0, query.lastIndexOf("or")) + ") and au.sessionstate=1 order by au.sessiondate";
            } else {
                // no groups added, return empty map instead of executing
                // a malformed query
                return activitiesMap;
            }
        }

        try
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try
            {

                sqlService.dbRead(connection, query, fields, new SqlReader<Object>()
                {
                    public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                        try
                        {
                            IcActivity activity = apiTool.getActivity(rs.getString("ActivityID"), user.getSakaiID());
                            if (!checkDuplicateActivitiesMap.contains(activity.getActivityID())) {
                                checkDuplicateActivitiesMap.add(activity.getActivityID());
                                activitiesMap.put(activity, apiTool.canEditActivity(activity, user.getSakaiID()));
                            }
                        } catch (SQLException e) {
                            log.error("cUserDaoImpl, activities retrieval: " + e.getMessage());
                        }
                        return null;
                    }
                });
                //  return activitiesMap;
            }
            catch (Exception e)
            {
                log.error("cUserDaoImpl : " + e.getMessage());
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
            log.error("Cannot get database connection: " + e, e);
            log.error("cUserDaoImpl : " + e.getMessage());
            //e.printStackTrace();
        }


        return activitiesMap;

    }

    private Map <IcActivity, Boolean> getJoinedActivities ( IapiTool ApiTool, IcUsers user){

        final Map<IcActivity, Boolean> activitiesMap = new HashMap<IcActivity, Boolean>();
        String query = "SELECT au.ActivityID FROM via_activity au " +
                "join via_activityusers av on av.activityId=au.ACTIVITYID " +
                "where au.sakaisiteid = ? and av.SAKAIUSERID like ? and au.sessionstate=1 order by au.sessiondate";

        fields = new Object[] { user.getSakaiSiteID(), user.getSakaiID() };
        Boolean canEdit = false;
        try
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try
            {

                sqlService.dbRead(connection, query, fields, new SqlReader<Object>()
                {
                    public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                        try
                        {
                            IcActivity activity = apiTool.getActivity(rs.getString("ActivityID"), user.getSakaiID());
                            activitiesMap.put(activity, apiTool.canEditActivity(activity, user.getSakaiID()));

                        } catch (SQLException e) {
                            log.error("cUserDaoImpl, activities retrieval: " + e.getMessage());
                        }
                        return null;
                    }
                });
                return activitiesMap;
            }
            catch (Exception e)
            {
                log.error("cUserDaoImpl : " + e.getMessage());
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
            log.error("Cannot get database connection: " + e, e);
            log.error("cUserDaoImpl : " + e.getMessage());
            //e.printStackTrace();
        }

        return activitiesMap;
    }

    public Map<IcActivity, Boolean> getActivities(String groupFilter, IapiTool ApiTool, IcUsers user){

        apiTool=ApiTool;

        // ApiTol is null for duplicate activity
        if(apiTool==null)
            apiTool=new apiToolImpl();


        final Map<IcActivity, Boolean> activitiesMap = new HashMap<IcActivity, Boolean>();

        //activities associated to site
        if (groupFilter.equalsIgnoreCase("site") ){
           activitiesMap.putAll(getSiteActivities(apiTool, user));
        }
        else if (groupFilter.equalsIgnoreCase("")){
            activitiesMap.putAll(getAllActivities(apiTool, user));
            //Add joined activities
            boolean alreadyIn;
            Map<IcActivity, Boolean> joinedActivities = getJoinedActivities(apiTool, user);
            Map<IcActivity, Boolean> temMapActivities = new HashMap<IcActivity, Boolean>();
            for (IcActivity activity: joinedActivities.keySet()){
                alreadyIn = false;
                for (IcActivity recordedActivity: activitiesMap.keySet()){
                    if (recordedActivity.getActivityID().equals(activity.getActivityID())){
                        alreadyIn = true;
                        continue;
                    }
                }
                if (!alreadyIn){
                    temMapActivities.put(activity, joinedActivities.get(activity));
                }
            }
            activitiesMap.putAll(temMapActivities);

        }else {
            activitiesMap.putAll(getGroupActivities(apiTool, user, groupFilter));
        }

        return activitiesMap;
    }



    @Override
    @Deprecated
    public List<IcActivity> getActivityList(String groupFilter,IapiTool ApiTool, IcUsers user) {

        apiTool=ApiTool;
        
        // ApiTol is null for duplicate activity
        if(apiTool==null)
            apiTool=new apiToolImpl();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      
        final List<IcActivity> lstActivity = new ArrayList<IcActivity>();



        try
        {  
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {   
                    query = "SELECT au.ActivityID" + 
                    " FROM via_activityusers au" + 
                    " JOIN via_activity aw ON au.ActivityID = aw.ActivityID";

                    if (groupFilter.equalsIgnoreCase("")){
                        query += " WHERE SessionState = ?" +
                                " AND SakaiSiteID = ?";
                    }
                    if (groupFilter.equalsIgnoreCase("site")){
                        query += " left JOIN via_activitygroups vag ON au.ActivityID = vag.ActivityID";
                        query += " WHERE SessionState = ? AND SakaiSiteID = ?";
                    }
                    if(groupFilter != "" && groupFilter != "site")
                    {
                        query += " JOIN via_activitygroups vag ON au.ActivityID = vag.ActivityID";
                        query += " WHERE SessionState = ? AND SakaiSiteID = ?";
                    }
                    

                    
                    if(!user.getPermissions().getCanSeeAllActivities())
                        query += " AND SakaiUserID = ?";     
                    
                    if(groupFilter != ""  && groupFilter != "site")
                        query += " AND SakaiGroupID = ?";
                    
                    query += " group by au.activityid, aw.sessiontype, aw.sessiondate, aw.title";            
                    query += " order by aw.sessiontype, aw.sessiondate, aw.title";
                        
                    if(!user.getPermissions().getCanSeeAllActivities()) 
                    {
                        if(groupFilter != ""  && groupFilter != "site")
                        {
                            fields = new Object[] { eActivityState.Activer.getValue(), user.getSakaiSiteID(), user.getSakaiID(), groupFilter };
                        }
                        else {
                            fields = new Object[] { eActivityState.Activer.getValue(), user.getSakaiSiteID(), user.getSakaiID() };
                        }
                    }
                    else {
                        if(groupFilter != ""  && groupFilter != "site")
                        {
                            fields = new Object[] { eActivityState.Activer.getValue(), user.getSakaiSiteID(), groupFilter };
                        }
                        else {
                            fields = new Object[] { eActivityState.Activer.getValue(), user.getSakaiSiteID() };
                        }
                    }
                    //log.error("Test cUserDaoImpl sql "+query+"fields "+fields[0]+"fields2 "+fields[1]);
                    sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                    {
                        public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                                try 
                                {
                                        lstActivity.add(apiTool.getActivity(rs.getString("ActivityID"), user.getSakaiID()));
                                } catch (SQLException e) {
                                    log.error("cUserDaoImpl, activities retrieval: " + e.getMessage());
                                }
                                return null;
                        }
                    });
                    return lstActivity;
                }
                catch (Exception e) 
                {
                        log.error("cUserDaoImpl : " + e.getMessage());
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
            log.error("Cannot get database connection: " + e, e);
                log.error("cUserDaoImpl : " + e.getMessage());
                //e.printStackTrace();
        }
        
        return null;
    }
}
