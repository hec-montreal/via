package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("cApiUserActivity_RemoveUser")
public class cApiUserActivity_RemoveUser extends cBaseApi
{
    private String UserID = "";
    private String ActivityID = "";
    
    public cApiUserActivity_RemoveUser(String userID, String activityID, String cieID, String apiID) 
    {
        super(cieID, apiID);
        
        UserID = userID;
        ActivityID = activityID;
    }
    
    private cApiUserActivity_RemoveUser()
    {}
}
