package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cApiActivityGet")
public class cApiActivityGet extends cBaseApi 
{
    private String ActivityID = "";
    private String UserID = "";
    private String Title = "";
    private String ActivityState = "";
    private String AudioType = "";
    private String ActivityType = "";
    private String DateBegin = "";
    private String Duration = "";
    private String NeedConfirmation = "";
    private String RecordingMode = "";

    private String RecordModeBehavior = "";
    private String IsReplayAllowed = "";
    private String WaitingRoomAccessMode = "";
    
    private String ProfilID = "";
    private String RoomType = "";
    
    //Constructeur pour creer une activitee.
    public cApiActivityGet(String userID, String title, String audioType, String activityType, String dateBegin, String duration,  String cieid,String apiid) 
    {
        super(cieid, apiid);
        
        UserID = userID;
        
        Title = title;
        AudioType = audioType;
        ActivityType = activityType;
        DateBegin = dateBegin;
        Duration = duration;
        NeedConfirmation = "0";
        RecordingMode = "0";
    }
    
    private cApiActivityGet()
    {}
    
}
