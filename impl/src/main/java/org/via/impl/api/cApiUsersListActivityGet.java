package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

import org.via.ActivityUser;


@XStreamAlias("cApiUsersListActivityGet")
public class cApiUsersListActivityGet extends cBaseApi
{
    private String ActivityID = "";
    private List<ActivityUser> ActivityUsersList = new ArrayList<ActivityUser>();
    
    public cApiUsersListActivityGet(String activityID, String cieid, String apiid) 
    {
        super(cieid, apiid);
        
        ActivityID  = activityID;
    }

    private cApiUsersListActivityGet()
    {}

    public String getActivityID() {
        return ActivityID;
    }

    public List<ActivityUser> getActivityUsersList() {
        return ActivityUsersList;
    }
}

