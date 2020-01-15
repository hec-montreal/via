package org.via.impl.jobs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlReaderFinishedException;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.*;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.via.IapiTool;
import org.via.IcActivity;
import org.via.enums.eParticipantType;
import org.via.impl.ApiRequestImpl;
import org.via.impl.api.cApiUserActivity_AddUser;
import org.via.impl.cActivity;
import org.sakaiproject.authz.api.Member;
import org.via.impl.cActivityDaoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SyncViaUsers2 implements Job {

    private static final Logger log = Logger.getLogger(SyncViaUsers2.class);

    private static boolean isRunning = false;

    @Setter
    @Getter
    protected IapiTool apiTool = null;

    @Setter
    private SqlService sqlService;
    @Setter
    private SiteService siteService;
    @Setter
    private UserDirectoryService userDirectoryService;
    @Setter
    private ServerConfigurationService serverConfigurationService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        if (isRunning) {
            log.warn("Stopping job since it's already running");
            return;
        }
        isRunning = true;
        log.debug("SyncViaUsers2 is running");

        try {
            List<IcActivity> activities = getActivitiesToSync();

            for (IcActivity activity : activities) {
                log.debug("Synchronizing activity: " + activity.getActivityID() + " Title: " + activity.getTitle() + " Site ID: " + activity.getSakaiSiteID());

                Site site = null;
                try {
                    site = siteService.getSite(activity.getSakaiSiteID());
                } catch (IdUnusedException e) {
                    e.printStackTrace();
                }

                if (site == null) {
                    continue;
                }

                // site enrollment
                if (activity.getEnrollmentType() == 0) {
                    Set<String> siteUsers = site.getUsers();
                    syncActivityUsers(activity, siteUsers);
                }
                // groups enrollment
                else if (activity.getEnrollmentType() == 2) {
                    List<String> groups = getActivityGroups(activity.getActivityID());
                    Set<String> totalMembersToSync = new HashSet<>();

                    Group group = null;
                    for (String groupId : groups) {
                        log.debug("Synchronize group: " + groupId);
                        group = site.getGroup(groupId);
                        if (group != null) {
                            totalMembersToSync.addAll(group.getUsers());
                        }
                    }
                    syncActivityUsers(activity, totalMembersToSync);
                }
            }
        }
        finally {
            isRunning = false;
            log.debug("SyncViaUsers finished");
        }
    }

    private void syncActivityUsers(IcActivity activity, Set<String> officialUsers) {
        List<ViaUser> viaUsers = getActivityUsers(activity.getActivityID());

        for (String userId : officialUsers) {
            ViaUser officialMemberAsViaUser = new ViaUser(userId);

            if (!viaUsers.contains(officialMemberAsViaUser)) {
                log.debug("Adding user: " + userId);
                addUserToActivity(activity.getActivityID(), userId, activity.getSakaiSiteID());
            }
            // remove official members from viaUsers so we know who to delete
            viaUsers.remove(officialMemberAsViaUser);
         }

        if (!viaUsers.isEmpty()) {
            for (ViaUser user : viaUsers) {
                // remove user if she is not presenter
                if (user.getParticipantType() != 2) {
                    log.debug("Removing user: " + user.getUserId());
                    removeUserFromActivity(activity.getActivityID(), user.getUserId(), activity.getSakaiSiteID());
                }
            }
        }
    }

    private void addUserToActivity(String activityID, String sakaiUserID, String siteID)
    {
        ApiRequestImpl api =
                new ApiRequestImpl(apiTool.getViaURL(), apiTool.getApiID() , apiTool.getCieID(), apiTool.getAdminID());

        String viaUserID;
        try {
            viaUserID = cActivityDaoImpl.getVIAUserID(userDirectoryService.getUser(sakaiUserID), siteID);

            //Add to VIA DB.
            cApiUserActivity_AddUser addUser = new cApiUserActivity_AddUser(activityID, String.valueOf(eParticipantType.Participant.getValue()), viaUserID, api.getCieID(), api.getApiID());
            api.SendAddUserActivity(addUser);

            //Add to Sakai DB.
            String query = "INSERT INTO via_activityusers (ActivityID, SakaiUserID, ParticipantType) VALUES (?, ?, ?)";
            Object[] fields = new Object[]{activityID, sakaiUserID, String.valueOf(eParticipantType.Participant.getValue())};
            sqlService.dbWrite(query, fields);
        }
        catch (UserNotDefinedException e) {
            log.debug("Unable to add user " + sakaiUserID + " - not found in UserDirectoryService");
            return;
        }
        catch (Exception e) {
            log.error("Error adding user " + sakaiUserID + " to via activity " + activityID);
            e.printStackTrace();
        }
    }

    private void removeUserFromActivity(String activityID, String sakaiUserID, String siteID)
    {
        ApiRequestImpl api =
                new ApiRequestImpl(apiTool.getViaURL(), apiTool.getApiID() , apiTool.getCieID(), apiTool.getAdminID());

        String viaUserID;
        try
        {
            viaUserID = cActivityDaoImpl.getVIAUserID(userDirectoryService.getUser(sakaiUserID), siteID);

            //Remove from Via DB.
            api.SendRemoveUserActivity(viaUserID, activityID, api.getCieID(), api.getApiID());

            //Remove from Sakai DB.
            String query = "DELETE FROM via_activityusers WHERE ActivityID = ? AND SakaiUserID = ?";
            Object[] fields = new Object[] { activityID, sakaiUserID};
            sqlService.dbWrite(query, fields);
        }
        catch (UserNotDefinedException e) {
            log.debug("Unable to remove user " + sakaiUserID + " - not found in UserDirectoryService");
            return;
        }
        catch (Exception e) {
            log.error("Error removing user " + sakaiUserID + " from via activity " + activityID);
            e.printStackTrace();
        }
    }

    private List<IcActivity> getActivitiesToSync() {
        String query;

        Integer pastDaysToSync = serverConfigurationService.getInt("viaPastDaysToSync", 30);

        query = "select * from VIA_ACTIVITY where SESSIONSTATE <> 2 and (sessiondate > SYSDATE - ? or LASTMODIFICATIONDATE  > SYSDATE - ?)";
        Object[] fields = new Object[2];
        fields[0] = pastDaysToSync;
        fields[1] = pastDaysToSync;

        List<IcActivity> activities = sqlService.dbRead(query, fields, new ActivityReader());

        return activities;
    }

    private List<String> getActivityGroups(String activityId) {
        String query;
        String[] fields;

        query = "select SAKAIGROUPID from VIA_ACTIVITYGROUPS where ACTIVITYID = ?";
        fields = new String[1];
        fields[0] = activityId;

        List<String> groups = sqlService.dbRead(query, fields, null);
        return groups;
    }

    private List<ViaUser> getActivityUsers(String activityId) {
        String query;
        String[] fields;

        query = "select SAKAIUSERID, PARTICIPANTTYPE from VIA_ACTIVITYUSERS where ACTIVITYID = ?";
        fields = new String[1];
        fields[0] = activityId;

        List<ViaUser> users = sqlService.dbRead(query, fields, new UserReader());
        return users;
    }

    private class UserReader implements SqlReader {

        @Override
        public Object readSqlResultRecord(ResultSet result) throws SqlReaderFinishedException {
            ViaUser user = new ViaUser();
            try {
                user.setUserId(result.getString("SakaiUserId"));
                user.setParticipantType(result.getInt("ParticipantType"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return user;
        }
    }

    @Data
    private class ViaUser {
        String userId;
        Integer participantType;

        public ViaUser(){}

        public ViaUser(String id) {
            userId = id;
        }

        @Override
        public boolean equals(Object o) {

            if (o == this) return true;
            if (!(o instanceof ViaUser)) {
                return false;
            }

            ViaUser user = (ViaUser) o;

            return user.getUserId().equals(userId);
        }
    }

    private class ActivityReader implements SqlReader {
        public IcActivity readSqlResultRecord(ResultSet rs) throws SqlReaderFinishedException {

            cActivity activity = new cActivity();
            try {
                activity.setActivityID(rs.getString("ActivityID"));
                activity.setTitle(rs.getString("Title"));
                activity.setSakaiSiteID(rs.getString("SakaiSiteID"));
                activity.setEnrollmentType(rs.getInt("EnrollmentType"));
            } catch (SQLException e) {
                log.error("Error reading activity from database: "+ e.getMessage());
                e.printStackTrace();
            }

            return activity;
        }
    }
}