package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.via.ActivityUser;
import org.via.enums.eParticipantType;

@XStreamAlias("cApiUserActivity_AddUser")
public class cApiUserActivity_AddUser extends cBaseApi
{
    private String UserID = "";
    private String ActivityID = "";
    
    private String ParticipantType = "";
    
    //To add a user to an activity.
    public cApiUserActivity_AddUser(String activityID, String type, String userID, String cieID ,String apiID) 
    {
        super(cieID, apiID);
        
        ActivityID = activityID;
        UserID = userID;
        ParticipantType = type;
    }    
    
    //To modify a user's data.
    public cApiUserActivity_AddUser(String activityID, ActivityUser au, String cieID, String apiID) 
    {
        super(cieID, apiID);
        
        UserID = au.getUserID();
        ParticipantType = au.getParticipantType();
        
        ActivityID = activityID;
    }

    private cApiUserActivity_AddUser()
    {}

    public String getParticipantType() {
        return ParticipantType;
    }

    public void setParticipantType(String participantType) {
        this.ParticipantType = participantType;
    }
}
