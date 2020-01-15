package org.via;

import org.sakaiproject.authz.api.*;
import org.sakaiproject.entity.api.*;
import org.sakaiproject.site.api.*;
import org.sakaiproject.user.api.*;
import org.via.enums.*;

import java.util.*;

public interface IapiTool extends EntityProducer
{
    public IcActivity getActivity(String activityID, String sakaiUserID);
    
    public IcActivity createActivity(String sakaiUserID, String title, eActivityType activityType, Date dateBegin, int duration);
    
    public IcUsers getCurrentUser();
    
    public List<? extends IProfil> getLstProfils(); 
    
    public List<User> getUserList();    
    
    public List<Group> getGroupList();
    
    public List<User> getGroupsUsers(List<String> groupsToShow);
    
    public User getSakaiUserInfo(String sakaiUserID);

    public List<Group> getUserGroupList(String siteId);
    
    public Group getSakaiGroupInfo(String sakaiGroupID); 
    
    public void init();
    
    public String test(String str);
    
    public void sendActivityEmailNotification(IcActivity activity);        
    public void sendActivityEmailReminder(String activityTitle, Date activityDate, String participantEid, String siteID);
    public void sendExportNotification(String recordName, String audioType, String participantEid, String siteID);
    
    public void addParticipantToActivity(String activityID, String sakaiUserID, String siteID);
    
    public void removeParticipantFromActivity(String activityID, String sakaiUserID, String siteID);
    
    public void UpdateVIAUserType(String viaUserID, String SakaiUserID, String siteID);
    
    public String getRecordDownloadURL(String userID, String playbackID, String recordType);
    
    public List<? extends IExport> getLatestExports(String fromDate);
    
    /**
     * Get current siteid
     * @return
     */
    public String getCurrentSiteId();
    
    /**
     * Get current user id
     * @return
     */
    public String getCurrentUserId();
    
    /**
     * Get current user display name
     * @return
     */
    public String getCurrentUserDisplayName();
    
    /**
     * Is the current user a superUser? (anyone in admin realm)
     * @return
     */
    public boolean isSuperUser();
    
    public Site getSite();
    
    public SecurityService getSecurityService();
    
    public SiteService getSiteService();
    
    //public String getVIAUserID(User sUser, String sID);

    public String getViaURL() ;
    public String getApiID();
    public String getCieID() ;

    public String getAdminID() ;

    public boolean canEditActivityOfSite ();

    public boolean canCreateActivity (String userId);

    public boolean canEditActivity (IcActivity activity, String userId);
}
