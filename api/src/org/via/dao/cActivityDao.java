package org.via.dao;


import java.util.List;

import org.sakaiproject.site.api.Site;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import org.via.ActivityUser;
import org.via.IapiTool;
import org.via.IcActivity;
import org.via.enums.eActivityState;
import org.via.enums.eParticipantType;

public interface cActivityDao {
    
     public String Save(String SakaiUserID, IcActivity activite);
     public void Delete(String SakaiUserID, IapiTool apiTool,IcActivity activite);
     public void DuplicateActivity(String siteFrom, String siteTo, String userSakaiID, IcActivity activite);
    public void updateLastModified(String SakaiUserID,IcActivity activite);
    public void addGroupToActivity(String sGroupID, Site site, IcActivity activite);
    public void removeGroupActivity(String sakaiGroupID,IcActivity activite);
    public void removeUserActivity(String sakaiUserID,IcActivity activite);
    public void editUserActivity(String sakaiUserID, eParticipantType type,IcActivity activite);
    //public void loadLstUserList();
   // public void loadLstGroupList();
    public void addUserToActivity(String sakaiUserID, eParticipantType participantType, IcActivity activite);
    public String getActivityID();
    public eActivityState getActivityState();
    public List<ActivityUser> getLstUser(IcActivity activite);
    public List<ActivityUser> getLstUser(boolean reloadList,IcActivity activite);
   // public IcActivity loadActivity(IcActivity activite,String activityID,String sakaiUserID);
   // public void loadActivity(String sakaiUserID,UserDirectoryService uds);.
    
}