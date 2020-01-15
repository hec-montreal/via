package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cApiActivity")
public class cApiActivity extends cBaseApi 
{
    private String ActivityID = "";
    private String UserID = "";
    private String Title = "";
    private String ActivityState = "";
    private String AudioType = "";
    private String ActivityType = "";
    private String DateBegin = "";
    private String Duration = "";
    private String NeedConfirmation = "0";
    private String RecordingMode = "";
    
    private String RecordModeBehavior = "";
    private String IsReplayAllowed = "";
    private String WaitingRoomAccessMode = "";    
    
    private String ProfilID = "";
    private String RoomType = "";
    
    private String IsNewVia = "";
    
    //Constructeur pour creer une activitee.
    public cApiActivity(String userID, String title, String audioType, String activityType, String dateBegin, String duration, String isReplayAllowed, String profilID, String roomType,
                        String recordingMode, String recordingModeBehavior, String waitingRoomAccessMode, String isNewVia,  String cieid,String apiid)
    {
        super(cieid, apiid);

        UserID = userID;
        Title = title;
        
        AudioType = audioType;
        ActivityType = activityType;
        DateBegin = dateBegin;
        Duration = duration;
        RecordingMode = recordingMode;
        
        RecordModeBehavior = recordingModeBehavior;
        IsReplayAllowed = isReplayAllowed;
        WaitingRoomAccessMode = waitingRoomAccessMode;    
        
        ProfilID = profilID;
        RoomType = roomType;
        
        IsNewVia = isNewVia;
    }
    
    private cApiActivity()
    {}
    
    

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String ActivityID) {
        this.ActivityID = ActivityID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String UserID) {
        this.UserID = UserID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }


    public String getActivityState() {
        return ActivityState;
    }

    public void setActivityState(String ActivityState) {
        this.ActivityState = ActivityState;
    }

    public String getAudioType() {
        return AudioType;
    }

    public void setAudioType(String AudioType) {
        this.AudioType = AudioType;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String ActivityType) {
        this.ActivityType = ActivityType;
    }

    public String getDateBegin() {
        return DateBegin;
    }

    public void setDateBegin(String DateBegin) {
        this.DateBegin = DateBegin;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String Duration) {
        this.Duration = Duration;
    }

    public String getNeedConfirmation() {
        return NeedConfirmation;
    }

    public void setNeedConfirmation(String NeedConfirmation) {
        this.NeedConfirmation = NeedConfirmation;
    }

    public String getRecordingMode() {
        return RecordingMode;
    }

    public void setRecordingMode(String RecordingMode) {
        this.RecordingMode = RecordingMode;
    }

    public String getRecordModeBehavior() {
        return RecordModeBehavior;
    }

    public void setRecordModeBehavior(String RecordModeBehavior) {
        this.RecordModeBehavior = RecordModeBehavior;
    }

    public String getIsReplayAllowed() {
        return IsReplayAllowed;
    }

    public void setIsReplayAllowed(String IsReplayAllowed) {
        this.IsReplayAllowed = IsReplayAllowed;
    }

    public String getWaitingRoomAccessMode() {
        return WaitingRoomAccessMode;
    }

    public void setWaitingRoomAccessMode(String WaitingRoomAccessMode) {
        this.WaitingRoomAccessMode = WaitingRoomAccessMode;
    }

    public String getProfilID() {
        return ProfilID;
    }

    public void setProfilID(String ProfilID) {
        this.ProfilID = ProfilID;
    }

    public String getRoomType() {
        return RoomType;
    }

    public void setRoomType(String RoomType) {
        this.RoomType = RoomType;
    }
}
