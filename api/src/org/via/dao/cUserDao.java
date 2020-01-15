package org.via.dao;

import org.sakaiproject.user.api.*;
import org.via.*;

import java.util.*;

public interface cUserDao {
    public List<IcActivity> getActivityList(UserDirectoryService uds,IcUsers user);
    public List<IcActivity> getActivityList(String groupFilter,IapiTool apiTool,IcUsers user);
    public void setPermission (cUserPermissions Newpermissions);
    public void setSakaiSiteID (String SakaiSiteID);
    public void setSakaiID (String sakaiID);
    //public void setConn (SqlService conn);

    /**
     * Get a map of all the activities the user has access to and whether or not he can edit the activity.
     * @param groupFilter
     * @param ApiTool
     * @param user
     * @return
     */
    public Map<IcActivity, Boolean> getActivities(String groupFilter, IapiTool ApiTool, IcUsers user);


    
}
