package org.via.impl;

import lombok.*;
import org.apache.log4j.*;
import org.sakaiproject.authz.api.*;
import org.sakaiproject.db.api.*;
import org.sakaiproject.site.api.*;
import org.sakaiproject.user.api.*;
import org.via.*;
import org.via.dao.*;
import org.via.enums.*;
import org.via.impl.api.*;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

public class cActivityDaoImpl implements cActivityDao {
    private static final Logger log = Logger.getLogger(cActivityDaoImpl.class);
    
    private String SakaiSiteID = "";
    private static String activityID = "";
    private String Title = "";
    
    private String Description = "";
    private String LastModificationUser = "";
    private Date LastModificationDate;
    
    private eActivityState ActivityState = eActivityState.Activer;
    private eAudioType AudioType = eAudioType.VOIP;
    private  eActivityType ActivityType = eActivityType.Normale;
    private Date DateBegin;
    private Integer Duration = 0;
    private Integer NeedConfirmation = 0;
    private eRecordingMode RecordingMode = eRecordingMode.Desactive;

    private eRecordModeBehavior RecordModeBehavior = eRecordModeBehavior.Automatique;
    private Boolean IsReplayAllowed = false;
    private eWaitingRoomAccessMode WaitingRoomAccessMode = eWaitingRoomAccessMode.Desactive;
    
    private String ProfilID = "";
    private eRoomType RoomType =  eRoomType.Standard;
    
    private List<ActivityUser> lstUser = new ArrayList<ActivityUser>();    
    private List<String> lstGroup = new ArrayList<String>();
    private static cApiListPlayback lstPlayback;
    
    private  static ApiRequestImpl Api;
    @Setter
    private static UserDirectoryService Uds;
    
    private eParticipantType ParticipantType;
    
    private Integer Reminder = 0;
    private Boolean IsNewVia = false;
    private Integer EnrollmentType = 0; 
    
    private Boolean isNew = false;

    @Getter @Setter
    private static SqlService sqlService;
    
    @Setter @Getter
    protected static IapiTool apiTool;
    
    @Setter @Getter
    protected static apiToolImpl apiToolImpl;
    
    private  static String query;
    private static Object[] fields;

    private static String uID;
    
    private static boolean doReturn;
   // private String uID;
    public cActivityDaoImpl() {
        super();
    }

    @Override
    public String Save(String SakaiUserID,IcActivity activite){
        if(activite.getDescription() != null && activite.getDescription().length() > 250)
            activite.setDescription(activite.getDescription().substring(0, 250));
        
        if(activite.getTitle() != null && activite.getTitle().length() > 100)
            activite.setTitle(activite.getTitle().substring(0,100));
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Api = new ApiRequestImpl(apiTool.getViaURL(), apiTool.getApiID() , apiTool.getCieID(), apiTool.getAdminID());

        cApiActivity apiActivity = new cApiActivity(Api.getAdminID(), activite.getTitle(), String.valueOf(activite.getAudioType().getValue()),
                                                    String.valueOf(activite.getActivityType().getValue()), 
                                                    (activite.getDateBegin()==null?"":sdf.format(activite.getDateBegin())),
                                                    activite.getDuration().toString(),  activite.getIsReplayAllowed() ? "1" : "0", 
                                                    activite.getProfilID(), String.valueOf( activite.getRoomType().getValue()),
                                                    String.valueOf( activite.getRecordingMode().getValue()),
                                                    String.valueOf( activite.getRecordModeBehavior().getValue()),
                                                    String.valueOf( activite.getWaitingRoomAccessMode().getValue()),
                                                     activite.getIsNewVia() ? "1" : "0", Api.getCieID(), Api.getApiID());
        
        apiActivity.setActivityState(String.valueOf( activite.getActivityState().getValue()));
        
        try 
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {  
                if(activite.getIsNew())
                {
                    //VIA ADD
                    apiActivity = Api.SendCreateActivity(apiActivity);
                    activite.setActivityID(apiActivity.getActivityID() );
                    if( activite.getProfilID() == "")
                         activite.setProfilID( apiActivity.getProfilID() );
                    

                    //ADD Sakai DB.
                    try 
                    {
                        query = "INSERT INTO via_activity (ActivityID, Title, SakaiSiteID, Description, SessionDate, SessionDuration, SessionState, IsReplayAllowed," +
                            " RoomType, AudioType, SessionType, ProfilID, RecordingMode, RecordingModeBehavior, WaitingRoomAccessMode, Reminder, IsNewVia, EnrollmentType) " +
                                       " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        fields = new Object[] { activite.getActivityID(), activite.getTitle(),activite.getSakaiSiteID(), activite.getDescription(), 
                                                (activite.getDateBegin() == null? null:new Timestamp(activite.getDateBegin().getTime())),
                                                activite.getDuration(), eActivityState.Activer.getValue()
                                                , activite.getIsReplayAllowed(), activite.getRoomType().getValue(),
                                                activite.getAudioType().getValue(), activite.getActivityType().getValue(),
                                                activite.getProfilID(), activite.getRecordingMode().getValue(),
                                                activite.getRecordModeBehavior().getValue()
                                                ,activite.getWaitingRoomAccessMode().getValue(), activite.getReminder(), 
                                                activite.getIsNewVia(), activite.getEnrollmentType() };
                        sqlService.dbWrite(connection, query, fields);
                        connection.commit();
                        
                        addUserToActivity(SakaiUserID, eParticipantType.Presentateur,activite);
                        Api.SendRemoveUserActivity(Api.getAdminID(), activite.getActivityID(), Api.getCieID(), Api.getApiID());
                    } 
                    catch (Exception e) 
                    {
                        log.error("cActivityaoImple"+e.getMessage());
                        e.printStackTrace();
                    }
                }
                else
                {
                    //VIA EDIT
                    apiActivity.setActivityID(activite.getActivityID());
                    apiActivity = Api.SendEditActivity(apiActivity);   
                    
                    if(apiActivity.GetDetail().equals("_CANNOT_CHANGE_ISNEWVIA"))
                    {            
                        return apiActivity.GetDetail();
                    }
                    
                    //Edit Sakai DB.
                    try 
                    {
                        query = "UPDATE via_activity " +
                            " SET Title = ?" +
                            " , Description = ?" +
                            " , IsReplayAllowed = ?" +
                            " , RoomType = ?" + 
                            " , AudioType = ?" + 
                            " , SessionType = ?" +
                            " , SessionState = ?" +
                            " , ProfilID = ?" +
                            " , RecordingMode = ?" + 
                            " , RecordingModeBehavior = ?" + 
                            " , WaitingRoomAccessMode = ?" +
                            " , Reminder = ?" +
                            " , IsNewVia = ?" +
                            " , EnrollmentType = ?";
                        
                        if(activite.getActivityType() == eActivityType.Normale)
                        {
                            query += " , SessionDate = ?" +
                                     " , SessionDuration = ?";
                        }
                        
                        query +=  " WHERE ActivityID = ?";
                    
                        if(activite.getActivityType() == eActivityType.Normale)
                        {                                            
                            fields = new Object[] {activite.getTitle(), activite.getDescription(),activite.getIsReplayAllowed(), 
                                        activite.getRoomType().getValue(), activite.getAudioType().getValue(), 
                                        activite.getActivityType().getValue(),activite.getActivityState().getValue(), activite.getProfilID(),
                                        activite.getRecordingMode().getValue(), activite.getRecordModeBehavior().getValue(),
                                        activite.getWaitingRoomAccessMode().getValue(), activite.getReminder(), activite.getIsNewVia(),
                                        activite.getEnrollmentType(), new Timestamp(activite.getDateBegin().getTime())
                                                   ,activite.getDuration(), activite.getActivityID()};
                        }
                        else
                        {
                            fields = new Object[] {activite.getTitle(), activite.getDescription(),activite.getIsReplayAllowed(), 
                                        activite.getRoomType().getValue(), activite.getAudioType().getValue(), 
                                        activite.getActivityType().getValue(),activite.getActivityState().getValue(), activite.getProfilID(),
                                        activite.getRecordingMode().getValue(), activite.getRecordModeBehavior().getValue(),
                                        activite.getWaitingRoomAccessMode().getValue(), activite.getReminder(), activite.getIsNewVia(),
                                        activite.getEnrollmentType(), activite.getActivityID()};
                        }
                        sqlService.dbWrite(connection, query, fields);
                        connection.commit();
                    } 
                    catch (Exception e) 
                    {
                        log.error("cActivityDaoImpl Save error: " + e.getMessage());
                        e.printStackTrace();
                    } 
                }
            }
            catch (Exception e) 
            {
                log.error("cActivityaoImpl.Save : " + e.getMessage());
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
                log.error("cActivityaoImpl.Save : " + e.getMessage());
                e.printStackTrace();
        }
        
        
        
        updateLastModified(SakaiUserID, activite);
        
        return "";
    }


    public void Delete(String SakaiUserID, IapiTool ApiTool,IcActivity activite)     {
        if(activite.getIsNew())
            return;

        IcActivity activity = apiTool.getActivity(activite.getActivityID(), SakaiUserID);
        activity.setActivityState( eActivityState.Supprimer);
        
        this.Save(SakaiUserID,activity);
        try 
        {
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {  
                    query = "DELETE FROM via_activitygroups WHERE ActivityID = ?";
                    fields = new Object[] { activite.getActivityID() };
                    sqlService.dbWrite(connection, query, fields);
                    connection.commit();
                    
                    query = "DELETE FROM via_activityusers WHERE ActivityID = ?";
                    sqlService.dbWrite(connection, query, fields);
                    connection.commit();
                }
                catch (Exception e) 
                {
                    log.error("cActivityaoImpl.Delete : " + e.getMessage());
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
            log.error("cActivityaoImpl.Delete : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void DuplicateActivity(String siteFrom, String siteTo, String userSakaiID, IcActivity activite)     {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //One month later than right now
        Date dateStart = new Date();
        if (activite.getDateBegin()!= null)
            dateStart.setTime(dateStart.getTime() + (30*24*60*60*1000l));
        
        cApiActivityDuplicate apiActivityDuplicate = new cApiActivityDuplicate(Api.getCieID(), Api.getApiID(), activite.getActivityID(),
                                                    Api.getAdminID(), activite.getTitle() + " - Copie", (activite.getDateBegin()==null?"":sdf.format(dateStart)),
                                                    activite.getDuration().toString(), String.valueOf(activite.getActivityType().getValue()));
        apiActivityDuplicate = Api.SendActivityDuplicate(apiActivityDuplicate);
        
        //ADD Sakai DB.
        try 
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {  
                query = "INSERT INTO via_activity (ActivityID, Title, SakaiSiteID, Description, SessionDate, SessionDuration, SessionState, IsReplayAllowed," +
                                " RoomType, AudioType, SessionType, ProfilID, RecordingMode, RecordingModeBehavior, WaitingRoomAccessMode, Reminder, IsNewVia, EnrollmentType" +
                        ", LastModificationDate, LastModificationUser) " +
                                           " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                fields = new Object[] { apiActivityDuplicate.getActivityIDDuplicate(), activite.getTitle() + " - Copie", siteTo, activite.getDescription(), (activite.getDateBegin() == null? null:new Timestamp(dateStart.getTime()))
                                        , activite.getDuration(), eActivityState.Activer.getValue(), activite.getIsReplayAllowed(), activite.getRoomType().getValue(), activite.getAudioType().getValue()
                                        , activite.getActivityType().getValue(), activite.getProfilID(), activite.getRecordingMode().getValue(), activite.getRecordModeBehavior().getValue(), activite.getWaitingRoomAccessMode().getValue()
                                        ,activite.getReminder(), activite.getIsNewVia(), 1, new Timestamp(dateStart.getTime()), Uds.getUserEid(userSakaiID) };
                sqlService.dbWrite(connection, query, fields);
                connection.commit();
                
               addUserToActivity(apiActivityDuplicate.getActivityIDDuplicate(), userSakaiID, eParticipantType.Presentateur, Api, Uds, siteTo);
                Api.SendRemoveUserActivity(Api.getAdminID(), apiActivityDuplicate.getActivityIDDuplicate(), Api.getCieID(), Api.getApiID());
                
                //Add all users non students in site to the duplicated activity.
                Site destinationSite = apiTool.getSiteService().getSite(siteTo);

                for (Member siteMember : destinationSite.getMembers()){
                    if (!siteMember.getRole().getId().equalsIgnoreCase("student") ) {
                        addUserToActivity(apiActivityDuplicate.getActivityIDDuplicate(), siteMember.getUserId(), eParticipantType.Participant, Api, Uds, siteTo);
                    }
                }
            }
            catch (Exception e) 
            {
                log.error("cActivityaoImpl.DuplicateActivity : " + e.getMessage());
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
            log.error("cActivityaoImpl.DuplicateActivity : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateLastModified(String SakaiUserID,IcActivity activite)     {
        try 
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {  
                Date dt = new Date();
                query = "UPDATE via_activity SET LastModificationUser= ?, LastModificationDate= ? WHERE ActivityID= ?";
                fields = new Object[] {Uds.getUser(SakaiUserID).getEid(), new Timestamp(dt.getTime()),activite.getActivityID() };
                sqlService.dbWrite(connection, query, fields);
                connection.commit();
            }
            catch (Exception e) 
            {
            log.error("cActivityaoImpl.updateLastModified : " + e.getMessage());
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
            log.error("cActivityaoImpl.updateLastModified : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public void addGroupToActivity(String sGroupID, Site site, IcActivity activite)
        {
            final Site sakaiSite = site;
            final String sakaiGroupID = sGroupID;
            //Add to Sakai DB.
            try 
            {
                    final Connection connection = sqlService.borrowConnection();
                    boolean wasCommit = connection.getAutoCommit();
                    connection.setAutoCommit(false);
                    try 
                    {  
                        query = "INSERT INTO via_activitygroups (ActivityID, SakaiGroupID) VALUES (?, ?)";
                        fields = new Object[] { activite.getActivityID(), sakaiGroupID };
                        sqlService.dbWrite(connection, query, fields);
                        connection.commit();
                    }
                    catch (Exception e) 
                    {
                        log.error("cActivityaoImpl.addGroupToActivity : " + e.getMessage());
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
                log.error("cActivityaoImpl.addGroupToActivity : " + e.getMessage());
                e.printStackTrace();
            }
            
            try
            {
                    final Connection connection = sqlService.borrowConnection();
                    boolean wasCommit = connection.getAutoCommit();
                    connection.setAutoCommit(false);
                    try 
                    {   
                        query = "SELECT * FROM via_groupsusers WHERE SakaiGroupID = ?";
                        fields = new Object[] { sakaiGroupID };
                        
                        if(sqlService.dbRead(connection, query, fields, null).isEmpty()) 
                        { try 
                                {  
                                    //On ajoute les users du groupe a la table de synchronisation si le groupe n'est pas la.
                                     for (String userid : sakaiSite.getGroup(sakaiGroupID).getUsers())
                                        {
                                            query = "INSERT INTO via_groupsusers(SakaiGroupID, SakaiSiteID, SakaiUserID) VALUES (?, ?, ?)";
                                            fields = new Object[] { sakaiGroupID, sakaiSite.getId(), userid };
                                            sqlService.dbWrite(connection, query, fields);
                                            connection.commit();
                                            log.debug("INSERT INTO via_groupsusers(SakaiGroupID, SakaiSiteID, SakaiUserID) VALUES ("+sakaiGroupID+", "+sakaiSite.getId()+", "+userid+")");
                                        }
                                    
                                } catch (SQLException e) {
                                        log.error("cActivityDaoImpl.addGroupToActivity : " + e.getMessage());
                                        e.printStackTrace();
                                }
                            
                        };
                                                            
                    }
                    catch (Exception e) 
                    {
                            log.error("cActivityDaoImpl.addGroupToActivity : " + e.getMessage());
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
                    log.error("cActivityDaoImpl.addGroupToActivity : " + e.getMessage());
                    e.printStackTrace();
            }
        } 
    
    @Override
    public void removeGroupActivity(String sakaiGroupID,IcActivity activite)
    {
        //Remove from Sakai DB.
        try
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {  
                query = "DELETE FROM via_activitygroups WHERE ActivityID = ? AND SakaiGroupID = ?";
                fields = new Object[] { activite.getActivityID(), sakaiGroupID};
                sqlService.dbWrite(connection, query, fields);
                connection.commit();
            }
            catch (Exception e) 
            {
                log.error("cActivityDaoImpl.removeGroupActivity : " + e.getMessage());
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
            log.error("cActivityDaoImpl.removeGroupActivity : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void removeUserActivity(String sakaiUserID,IcActivity activite)
    {
        removeUserActivity(activite.getActivityID(), sakaiUserID, Api, Uds,activite.getSakaiSiteID(),activite);
    }
    
    public static void removeUserActivity(String activityID, String sakaiUserID, ApiRequestImpl Api, UserDirectoryService uds, String siteID,IcActivity activite)
    {
        String viaUserID = "";
        try 
        {
            viaUserID = getVIAUserID(uds.getUser(sakaiUserID), siteID);
        } catch (UserNotDefinedException e) {
            log.error("cActivityDaoImpl..removeUserActivity : " + e.getMessage());
            e.printStackTrace();
        }
        
        if(viaUserID.equals(""))
            return;

        //Remove from Via DB.
        Api.SendRemoveUserActivity(viaUserID, activityID, Api.getCieID(), Api.getApiID());
        
        //Remove from Sakai DB.
        try 
        {
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {  
                    query = "DELETE FROM via_activityusers WHERE ActivityID = ? AND SakaiUserID = ?";
                    fields = new Object[] { activityID, sakaiUserID};
                    sqlService.dbWrite(connection, query, fields);
                    connection.commit();
                }
                catch (Exception e) 
                {
                    log.error("cActivityDaoImpl..removeUserActivity : " + e.getMessage());
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
                log.error("cActivityDaoImpl..removeUserActivity : " + e.getMessage());
                e.printStackTrace();
        }
    }
    
    public static void loadLstUserList(IcActivity activite)     {
        if(Uds == null)
            return;
        
        getLstUsers(activite).clear();
        
        try
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {   
                query = "SELECT AU.SakaiUserID, VU.UserID, AU.ParticipantType FROM via_activityusers AU" + 
                                            " join via_users VU ON AU.SakaiUserID = VU.SakaiUserID" + 
                                            " WHERE ActivityID = ?" + 
                                            " ORDER BY ParticipantType";
                    fields = new Object[] { activite.getActivityID()};
                   sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                    {
                            public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                                    try 
                                    {  
                                                User user = null;
                                                try {
                                                    user = Uds.getUser(rs.getString("SakaiUserID"));
                                                } catch (UserNotDefinedException e) {
                                                    log.error("cActivityDaoImpl - loadUserList - getUser : " + e.getMessage());
                                                 }
                                                
                                                if(user != null)
                                                    getLstUsers(activite).add(new ActivityUser(rs.getString("UserID"), rs.getString("SakaiUserID"), user.getEid(), user.getEmail(), user.getLastName(), user.getFirstName(), rs.getString("ParticipantType")));
                                    } catch (SQLException e) {
                                            log.error("cActivityDaoImpl.loadLstUserList : " + e.getMessage());
                                    }
                                    return null;
                            }
                    });
                Collections.sort( getLstUsers(activite), new SortIgnoreCaseUser());                                
            }
            catch (Exception e) 
            {
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
            log.error("cActivityDaoImpl.loadLstUserList : " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public static void loadLstGroupList(IcActivity activite)     {
         activite.getLstGroup().clear();
        try
        {
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {   
                    String query = "SELECT SakaiGroupID from via_activitygroups" + 
                                    " WHERE ActivityID = ?";
                        fields = new Object[] { activite.getActivityID() };
                        
                        sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                        {
                            public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                                try 
                                {  
                                        activite.getLstGroup().add(rs.getString("SakaiGroupID"));
                                } catch (SQLException e) {
                                    log.error("cActivityDaoImpl.loadLstGroupList : " + e.getMessage());
                                }
                                return null;
                            }
                        });
                                                        
                }
                catch (Exception e) 
                {
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
            log.error("cActivityDaoImpl.loadLstGroupList : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void addUserToActivity(String sakaiUserID, eParticipantType participantType, IcActivity activite)
    {
         addUserToActivity(activite.getActivityID(), sakaiUserID, participantType, Api, Uds,activite.getSakaiSiteID());
    }

    
    public static void addUserToActivity(String activityID, String sakaiUserID, eParticipantType participantType, ApiRequestImpl Api, UserDirectoryService uds, String siteID)
    {
        String viaUserID = "";
        try {
            viaUserID = getVIAUserID(uds.getUser(sakaiUserID), siteID);
        } catch (UserNotDefinedException e) {
            log.error("cActivityDaoImpl.addUserToActivity : " + e.getMessage());
            return;
        }
        
        if(viaUserID == null || viaUserID.equals(""))
            return;
        
        try
        {
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {
                    query = "SELECT ActivityID from via_activityusers" + 
                                " WHERE ActivityID = ? AND SakaiUserID = ?";
                    fields = new Object[] { activityID, sakaiUserID };
                
                    doReturn = false;
                    
                    sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                    {
                        public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                                 doReturn = true;
                           return null;
                        }
                    });
                    
                    if (doReturn)
                        return;
                    
                    //Add to VIA DB.
                    cApiUserActivity_AddUser addUser = new cApiUserActivity_AddUser(activityID, String.valueOf(participantType.getValue()), viaUserID, Api.getCieID(), Api.getApiID());
                    Api.SendAddUserActivity(addUser);
                    
                    //Add to Sakai DB.
                    
                    query = "INSERT INTO via_activityusers (ActivityID, SakaiUserID, ParticipantType) VALUES (?, ?, ?)";
                    fields = new Object[] { activityID, sakaiUserID, participantType.getValue() };
                    sqlService.dbWrite(connection, query, fields);
                    connection.commit();           
                }
                catch (Exception e) 
                {
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
            log.error("cActivityDaoImpl.addUserToActivity : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void editUserActivity(String sakaiUserID, eParticipantType type,IcActivity activite)
    {
        editUserActivity(activite.getActivityID(), sakaiUserID, type, Api, Uds, activite.getSakaiSiteID());
    }    
    public void editUserActivity(String sakaiUserID, eParticipantType type,cActivity activite)
    {
        editUserActivity(activite.getActivityID(), sakaiUserID, type, Api, Uds, activite.getSakaiSiteID());
    }
    
    public static void editUserActivity(String activityID, String sakaiUserID, eParticipantType participantType, ApiRequestImpl Api, UserDirectoryService uds, String siteID)
    {
        String viaUserID = "";
        try {
            viaUserID = getVIAUserID(uds.getUser(sakaiUserID), siteID);
        } catch (UserNotDefinedException e) {
            log.error("cActivityDaoImpl.editUserActivity : " + e.getMessage());
        }
        
        if(viaUserID.equals(""))
            return;

        //Edit from Via DB.
        cApiUserActivity_AddUser editUser = new cApiUserActivity_AddUser(activityID, new ActivityUser(viaUserID, sakaiUserID, "", "", "", "", String.valueOf(participantType.getValue())), Api.getCieID(), Api.getApiID());
        Api.SendEditUserActivity(editUser);
        
        //Remove from Sakai DB.
        try 
        {
                final Connection connection = sqlService.borrowConnection();
                boolean wasCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                try 
                {  
                    query = "UPDATE via_activityusers SET ParticipantType = ? WHERE ActivityID = ? AND SakaiUserID = ?";
                        fields = new Object[] { participantType.getValue(), activityID, sakaiUserID };
                        sqlService.dbWrite(connection, query, fields);
                        connection.commit();
                }
                catch (Exception e) 
                {
                log.error("cActivityDaoImpl.editUserActivity : " + e.getMessage());
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
                log.error("cActivityDaoImpl.editUserActivity : " + e.getMessage());
                e.printStackTrace();
        }
    }
    
    @Override
    public String getActivityID() {
        return activityID;
    }
    
    @Override
    public eActivityState getActivityState() {
        return ActivityState;
    }
    
    public static void  load(UserDirectoryService uds, ApiRequestImpl api){
            Api= api;
            Uds=uds;
            log.warn("cActivityDaoImpl. test load ");}
    
    private static class SortIgnoreCaseUser implements Comparator<Object> {
            public int compare(Object o1, Object o2) {
                String s1 = ((ActivityUser)o1).getLastName();
                String s2 = ((ActivityUser)o2).getLastName();
                
                if(s1.toLowerCase().equals(s2.toLowerCase()))
                    return ((ActivityUser)o1).getFirstName().compareToIgnoreCase(((ActivityUser)o2).getFirstName());
                else
                    return s1.compareToIgnoreCase(s2);
            }
        }
    

    public static void loadActivity(IcActivity activite,String activityID,String sakaiUserID,UserDirectoryService uds, ApiRequestImpl api){
        Api= api;
        Uds= uds;
        activite.setActivityID(activityID);
        try {
            activite.setUserId( getVIAUserID(Uds.getUser(sakaiUserID), "_"));
                } catch (UserNotDefinedException e) {

                    log.error("cActivityDaoImpl - loadActivity - getViaUserId : " + e.getMessage());
                 }
        try
        {
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {   
                query = "SELECT va.*, ParticipantType FROM via_activity va LEFT JOIN via_activityusers vau ON va.ActivityID = vau.ActivityID and SakaiUserId = ? WHERE va.ActivityID = ?";
                fields = new Object[] { sakaiUserID, activityID };
                
                sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                {
                    public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                        try 
                        {
                                    activite.setSakaiSiteID(rs.getString("SakaiSiteID"));
                                    activite.setTitle(rs.getString("Title"));
                                    activite.setDescription(rs.getString("Description"));
                                    activite.setActivityState(eActivityState.GetActivityState(rs.getInt("SessionState")));
                                    activite.setIsReplayAllowed(rs.getBoolean("IsReplayAllowed"));
                                    activite.setRoomType(eRoomType.GetRoomType(rs.getInt("RoomType")));
                                    activite.setAudioType(eAudioType.GetAudioType(rs.getInt("AudioType")));
                                    activite.setActivityType(eActivityType.GetActivityType(rs.getInt("SessionType")));
                                    if(activite.getActivityType() == eActivityType.Normale)
                                    {
                                        activite.setDateBegin(rs.getTimestamp("SessionDate", sqlService.getCal()));
                                        activite.setDuration(rs.getInt("SessionDuration"));
                                    }
                                    
                                    activite.setProfilID(rs.getString("ProfilID"));
                                    activite.setRecordingMode(eRecordingMode.GetRecordingMode(rs.getInt("RecordingMode")));
                                    activite.setRecordModeBehavior(eRecordModeBehavior.GetRecordModeBehavior(rs.getInt("RecordingModeBehavior")));
                                    activite.setWaitingRoomAccessMode(eWaitingRoomAccessMode.GetWaitingRoomAccessModeType(rs.getInt("WaitingRoomAccessMode")));
                                    
                                    activite.setLastModificationDate(rs.getTimestamp("LastModificationDate", sqlService.getCal()));
                                    activite.setLastModificationUser( rs.getString("LastModificationUser") );
                                    
                                    if(rs.getObject("ParticipantType") == null)
                                        activite.setParticipantType(eParticipantType.Participant);
                                    else
                                        activite.setParticipantType(eParticipantType.GetParticipantType(rs.getInt("ParticipantType")));
                                    
                                    if(rs.getObject("Reminder") == null)
                                        activite.setReminder(0);
                                    else
                                       activite.setReminder(rs.getInt("Reminder"));
                                    
                                    if(rs.getObject("IsNewVia") == null)
                                        activite.setIsNewVia(false);
                                    else
                                        activite.setIsNewVia( rs.getBoolean("IsNewVia") );
                                    
                                    activite.setEnrollmentType(rs.getInt("EnrollmentType"));
                                    
                                    loadLstUserList(activite);
                                    loadLstGroupList(activite);
                                    
                                    lstPlayback = Api.getPlaybackList(activityID, Api.getCieID(), Api.getApiID());
                                    
                                   activite.editUserInformations(); /*Uds,lstUser,lstGroup,activityID,SakaiSiteID,Title,Description,ActivityState,
                                        IsReplayAllowed, RoomType,AudioType,ActivityType,DateBegin,Duration,ProfilID,
                                        RecordingMode,RecordModeBehavior,WaitingRoomAccessMode,LastModificationDate,
                                        ParticipantType,Reminder,IsNewVia, EnrollmentType);*/
                        } catch (SQLException e) {
                            log.error("cActivityDaoImpl.loadActivity : " + e.getMessage());
                        }
                        return null;
                    }
                });
                                
            }
            catch (Exception e) 
            {
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
            log.error("cActivityDaoImpl - loadActivity - select participantType Sql: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
    public void setActivitylstPlayback(cApiListPlayback lstPlayback,cActivity activite){
        activite.setlstPlayback(lstPlayback);
        }
    public static cApiListPlayback getActivitylstPlayback(){
        return lstPlayback;
        }
    
    public static ApiRequestImpl getApiRequest(){
        return Api;
        }
    public static List<ActivityUser> getLstUsers(boolean reloadList,IcActivity activite) {
        if(reloadList == true)
            loadLstUserList(activite);
        
            
        return activite.getLstUser();
    }
    public static List<ActivityUser> getLstUsers(IcActivity activite) {
        return getLstUsers(false,activite);
    }
    
    public List<ActivityUser> getLstUser(boolean reloadList,IcActivity activite) {
        if(reloadList == true)
            loadLstUserList(activite);

        return activite.getLstUser();
    }
    public List<ActivityUser> getLstUser(IcActivity activite) {
        return getLstUser(false,activite);
    }
    
     public static String getVIAUserID(User sUser, String sID)
    {
        final User sakaiUser = sUser;
        final String siteID = sID;
        try
        {   
            final Connection connection = sqlService.borrowConnection();
            boolean wasCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try 
            {   
                query = "SELECT UserID FROM via_users WHERE SakaiUserID = ?";
                fields = new Object[] { sakaiUser.getId() };
                uID = "";
                sqlService.dbRead(connection, query, fields, new SqlReader<Object>() 
                {
                    public Object readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {
                        try  
                        {  
                                    uID = rs.getString("UserID");
                                return null;

                        } catch (SQLException e) {
                                log.error("cActivityDaoImpl getVIAUserID : " + e.getMessage());
                        }
                        return null;
                    }
                });
                if(uID.equals("")){
                        if(!sakaiUser.getEmail().equals(""))
                            {
                                cApiUserSearch search = new cApiUserSearch(Api.getApiID(), Api.getCieID());
                                search.setEmail(sakaiUser.getEmail());                
                                List<Match> matches = Api.getUserSearchResult(search).getSearch();
                                if(matches.size() > 0)
                                {
                                    for (Match m : matches)
                                    {
                                        if(m.getEmail().toLowerCase().equals(sakaiUser.getEmail().toLowerCase()))
                                        {
                                            //FOUND A MATCH, ADD IN SAKAI.
                                            query = "INSERT INTO via_users(UserID, SakaiUserID) VALUES (?, ?)";
                                            fields = new Object[] { m.getUserID(), sakaiUser.getId()};
                                            sqlService.dbWrite(connection, query, fields);
                                            connection.commit();
                                            
                                            uID = m.getUserID();
                                            return null;
                                        }
                                    }
                                }
                            }
                            if(!sakaiUser.getEid().equals(""))
                            {
                                cApiUserSearch search = new cApiUserSearch(Api.getApiID(), Api.getCieID());
                                
                                String EID = apiToolImpl.normaliseString(sakaiUser.getEid());
                                search.setLogin(EID);
                                List<Match> matches = Api.getUserSearchResult(search).getSearch();
                                
                                if(matches.size() > 0)
                                {
                                    for (Match m : matches)
                                    {
                                        if(m.getLogin().toLowerCase().equals(EID.toLowerCase()))
                                        {
                                            //FOUND A MATCH, ADD IN SAKAI.
                                            query = "INSERT INTO via_users(UserID, SakaiUserID) VALUES (?, ?)";
                                            fields = new Object[] { m.getUserID(), sakaiUser.getId()};
                                            sqlService.dbWrite(connection, query, fields);
                                            connection.commit();
                                            
                                            uID = m.getUserID();
                                            return null;
                                        }
                                    }
                                }
                            }
                            
                            //Should not be needed but safety mesure incase user with site concatenated already exists.
                            if(!sakaiUser.getEid().equals(""))
                            {
                                cApiUserSearch search = new cApiUserSearch(Api.getApiID(),Api.getCieID());
                                
                                String EID = apiToolImpl.normaliseString(sakaiUser.getEid() + "_" + siteID);
                                search.setLogin(EID);
                                List<Match> matches = Api.getUserSearchResult(search).getSearch();
                                
                                if(matches.size() > 0)
                                {
                                    for (Match m : matches)
                                    {
                                        if(m.getLogin().toLowerCase().equals(EID.toLowerCase()))
                                        {
                                            //FOUND A MATCH, ADD IN SAKAI.
                                            query = "INSERT INTO via_users(UserID, SakaiUserID) VALUES (?, ?)";
                                            fields = new Object[] { m.getUserID(), sakaiUser.getId()};
                                            sqlService.dbWrite(connection, query, fields);
                                            connection.commit();
                                            
                                            uID = m.getUserID();
                                            return null;
                                        }
                                    }
                                }
                            }
                            
                            //Nothing interesting found. Creating a user in VIA.
                            cApiUsers user = new cApiUsers(Api.getCieID(), Api.getApiID());
                            if(!sakaiUser.getEmail().equals(""))
                                user.setEmail(sakaiUser.getEmail());
                            
                            user.setLogin(apiToolImpl.normaliseString(sakaiUser.getEid()));
                            user.setPassword(sakaiUser.getId());
                            
                            if(sakaiUser.getFirstName().equals(""))
                                user.setFirstName("N. A.");
                            else
                                user.setFirstName(sakaiUser.getFirstName());
                            
                            if(sakaiUser.getLastName().equals(""))
                                user.setLastName("N. A.");
                            else
                                user.setLastName(sakaiUser.getLastName());
                            
                            cApiUsers temp = user;
                            
                            user = Api.SendCreateUser(user);
                            
                            //User already exists in cie... we add the name of the site to the login
                            if(user == null)
                            {
                                temp.setLogin(apiToolImpl.normaliseString(sakaiUser.getEid() + "_" + siteID));                
                                user = Api.SendCreateUser(temp);
                            }
                            
                            //FOUND A MATCH, ADD IN SAKAI.

                            query = "INSERT INTO via_users(UserID, SakaiUserID) VALUES (?, ?)";
                            fields = new Object[] { user.getID(), sakaiUser.getId()};
                            sqlService.dbWrite(connection, query, fields);
                            connection.commit();
                            
                            uID = user.getID();
                            //return null;
                        }
                return uID;                         
            }
            catch (Exception e) 
            {
                log.error("cActivityDaoImpl.getVIAUserID : " + e.getMessage());
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
            log.error("cActivityDaoImpl .getVIAUserID : " + e.getMessage());
            e.printStackTrace();
        }
        
        return "";
    }
    
}
