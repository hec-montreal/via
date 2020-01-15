package org.via.impl;

import org.apache.log4j.*;
import org.sakaiproject.user.api.*;
import org.via.*;
import org.via.enums.*;
import org.via.impl.api.*;

import java.util.*;

public class cActivity implements IcActivity
{
    private static final Logger log = Logger.getLogger(cActivity.class);

    private String SakaiSiteID = "";
    private String ActivityID = "";
    private String UserID = "";
    private String Title = "";

    private String Description = "";
    private String LastModificationUser = "";
    private Date LastModificationDate;

    private eActivityState ActivityState = eActivityState.Activer;
    private eAudioType AudioType = eAudioType.VOIP;
    private eActivityType ActivityType = eActivityType.Normale;
    private Date DateBegin;
    private Integer Duration = 0;
    private Integer NeedConfirmation = 0;
    private eRecordingMode RecordingMode = eRecordingMode.Desactive;

    private eRecordModeBehavior RecordModeBehavior = eRecordModeBehavior.Automatique;
    private Boolean IsReplayAllowed = false;
    private eWaitingRoomAccessMode WaitingRoomAccessMode = eWaitingRoomAccessMode.Desactive;

    private String ProfilID = "";
    private eRoomType RoomType =  eRoomType.Standard;

    private List<ActivityUser> LstUser = new ArrayList<ActivityUser>();
    private List<String> LstGroup = new ArrayList<String>();
    private cApiListPlayback lstPlayback;

    private ApiRequestImpl Api;
    private UserDirectoryService Uds;

    private eParticipantType ParticipantType;

    private Integer Reminder = 0;
    private Boolean IsNewVia = false;
    private Integer EnrollmentType = 0;

    private Boolean isNew = false;

    private static String query;
    private static Object[] fields;

    private static boolean doReturn;
    private static String uID;

    /**
     * Used for Via Reminders jobs
     */
    public cActivity(){
        Api=cActivityDaoImpl.getApiRequest();

    }

    public void setUserId (String sakaiUserID){
        this.UserID = sakaiUserID;
    }

    //Constructor to create an activity.
    public cActivity(String sakaiUserID, String title, eActivityType activityType, Date dateBegin, int duration, ApiRequestImpl api, String sakaiSiteID, UserDirectoryService uds)
    {
        this.Api = api;
        this.SakaiSiteID = sakaiSiteID;
        this.Uds = uds;



        Title = title;
        ActivityType = activityType;

        if(activityType == eActivityType.Normale)
        {
            DateBegin = dateBegin;
            Duration = duration;

            if(Duration == 0)
                Duration = 60;
        }
        else
        {
            Duration = 0;
        }

        isNew = true;
    }


    private class SortIgnoreCaseUser implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = ((ActivityUser)o1).getLastName();
            String s2 = ((ActivityUser)o2).getLastName();

            if(s1.toLowerCase().equals(s2.toLowerCase()))
                return ((ActivityUser)o1).getFirstName().compareToIgnoreCase(((ActivityUser)o2).getFirstName());
            else
                return s1.compareToIgnoreCase(s2);
        }
    }

    public void renamePlayback(String playbackID, String newName)
    {
        for(int i =0;i< lstPlayback.getPlaybackList().size(); i++)
        {
            if(lstPlayback.getPlaybackList().get(i).getPlaybackID().equals(playbackID))
            {
                lstPlayback.getPlaybackList().get(i).setTitle(newName);
                Api.SendEditPlayback(lstPlayback);
                return;
            }

            if(lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList() != null)
            {
                for(int j =0;j< lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList().size(); j++)
                {
                    if(lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList().get(j).getPlaybackID().equals(playbackID))
                    {
                        lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList().get(j).setTitle(newName);
                        Api.SendEditPlayback(lstPlayback);
                        return;
                    }
                }
            }
        }
    }

    public void editPlaybackPublic(String playbackID, Boolean isPublic)
    {
        for(int i =0;i< lstPlayback.getPlaybackList().size(); i++)
        {
            if(lstPlayback.getPlaybackList().get(i).getPlaybackID().equals(playbackID))
            {
                lstPlayback.getPlaybackList().get(i).setIsPublic(isPublic ? "1" : "0");
                Api.SendEditPlayback(lstPlayback);
                return;
            }

            if(lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList() != null)
            {
                for(int j =0;j< lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList().size(); j++)
                {
                    if(lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList().get(j).getPlaybackID().equals(playbackID))
                    {
                        lstPlayback.getPlaybackList().get(i).getBreackOutPlaybackList().get(j).setIsPublic(isPublic ? "1" : "0");
                        Api.SendEditPlayback(lstPlayback);
                        return;
                    }
                }
            }
        }
    }

    public String getPlaybackURL(String playbackID, Boolean forcedEditRights)
    {
        return Api.SendCreateTokenSSO(UserID, ActivityID, playbackID,
                eRedirectType.PageVia,"", (forcedEditRights? "1" : ""), Api.getCieID(), Api.getApiID());
    }

    public String getVIAURL(Boolean forcedEditRights)
    {
        return Api.SendCreateTokenSSO(UserID, ActivityID, "", eRedirectType.PageVia, "", (forcedEditRights? "1" : ""), Api.getCieID(), Api.getApiID());
    }

    public String getActivityID() {
        return ActivityID;
    }

    public String getUserID() {
        return UserID;
    }

    public eParticipantType getParticipantType() {
        return ParticipantType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public eActivityState getActivityState() {
        return ActivityState;
    }

    public eAudioType getAudioType() {
        return AudioType;
    }

    public void setAudioType(eAudioType AudioType) {
        this.AudioType = AudioType;
    }

    public eActivityType getActivityType() {
        return ActivityType;
    }

    public void setActivityType(eActivityType ActivityType) {
        this.ActivityType = ActivityType;
    }

    public Date getDateBegin() {
        return DateBegin;
    }

    public Date getDateEnd() {
        if(DateBegin == null)
            return null;

        Calendar cal = Calendar.getInstance();
        cal.setTime(DateBegin);
        cal.add(Calendar.MINUTE, Duration);

        return cal.getTime();
    }

    public Boolean getIsPreparation()
    {
        if(DateBegin == null)
            return false;

        Calendar cal = Calendar.getInstance();
        cal.setTime(DateBegin);
        cal.add(Calendar.MINUTE, -30);

        return new Date().before(cal.getTime());
    }

    public void setDateBegin(Date DateBegin) {
        this.DateBegin = DateBegin;
    }

    public Integer getDuration() {
        return Duration;
    }

    public void setDuration(Integer Duration) {
        this.Duration = Duration;
    }

    public Integer getNeedConfirmation() {
        return NeedConfirmation;
    }

    public void setNeedConfirmation(Integer NeedConfirmation) {
        this.NeedConfirmation = NeedConfirmation;
    }

    public eRecordingMode getRecordingMode() {
        return RecordingMode;
    }

    public void setRecordingMode(eRecordingMode RecordingMode) {
        this.RecordingMode = RecordingMode;
    }





    public List<String> getLstGroup() {
        return LstGroup;
    }

    public List<? extends IPlayback> getLstPlayback() {
        return lstPlayback.getPlaybackList();
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getLastModificationUser() {
        return LastModificationUser;
    }

    public Date getLastModificationDate() {
        return LastModificationDate;
    }

    public Boolean getIsReplayAllowed() {
        return IsReplayAllowed;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsReplayAllowed(Boolean IsReplayAllowed) {
        this.IsReplayAllowed = IsReplayAllowed;
    }

    public eRoomType getRoomType() {
        return RoomType;
    }

    public void setRoomType(eRoomType RoomType) {
        this.RoomType = RoomType;
    }

    public Integer getReminder()
    {
        return this.Reminder;
    }

    public void setReminder(Integer reminder)
    {
        this.Reminder = reminder;
    }

    public Boolean getIsNewVia()
    {
        return this.IsNewVia;
    }

    public void setIsNewVia(Boolean isNewVia)
    {
        this.IsNewVia = isNewVia;
    }

    public Integer getEnrollmentType() {
        return EnrollmentType;
    }

    public void setEnrollmentType(Integer EnrollmentType) {
        this.EnrollmentType = EnrollmentType;
    }

    public String getProfilID() {
        return ProfilID;
    }

    public void setProfilID(String ProfilID) {
        this.ProfilID = ProfilID;
    }

    public eRecordModeBehavior getRecordModeBehavior() {
        return RecordModeBehavior;
    }

    public void setRecordModeBehavior(eRecordModeBehavior RecordModeBehavior) {
        this.RecordModeBehavior = RecordModeBehavior;
    }

    public eWaitingRoomAccessMode getWaitingRoomAccessMode() {
        return WaitingRoomAccessMode;
    }

    public void setWaitingRoomAccessMode(eWaitingRoomAccessMode WaitingRoomAccessMode) {
        this.WaitingRoomAccessMode = WaitingRoomAccessMode;
    }
    @Override
    public String getSakaiSiteID() {
        return SakaiSiteID;
    }
    @Override
    public void setSakaiSiteID(String sakaiSiteID) {
        SakaiSiteID = sakaiSiteID;
    }

    @Override
    public void editUserInformations (){lstPlayback= cActivityDaoImpl.getActivitylstPlayback();}/*UserDirectoryService uds,List<ActivityUser> lstUser, List<String> lstGroup,
      String activityID,String sakaiSiteID,String title, String description, eActivityState activityState,
      Boolean isReplayAllowed,  eRoomType roomType, eAudioType audioType,eActivityType activityType, Date dateBegin,
      Integer duration, String profilID, eRecordingMode recordingMode, eRecordModeBehavior recordModeBehavior,
     eWaitingRoomAccessMode waitingRoomAccessMode, Date lastModificationDate, eParticipantType participantType,
    Integer reminder, Boolean isNewVia, Integer enrollmentType){
            Uds= uds;
            LstUser = lstUser;
            LstGroup= lstGroup;
            ActivityID= activityID;
            SakaiSiteID=sakaiSiteID;
            Title=title;
            Description=description;
            ActivityState= activityState;
            IsReplayAllowed= isReplayAllowed;
            RoomType= roomType;
            AudioType= audioType;
            ActivityType= activityType;
            DateBegin= dateBegin;
            Duration=  duration;
            ProfilID= profilID;
            RecordingMode=  recordingMode;
            RecordModeBehavior=  recordModeBehavior;
            WaitingRoomAccessMode= waitingRoomAccessMode;
            LastModificationDate= lastModificationDate;
            ParticipantType= participantType;
            Reminder= reminder;
            IsNewVia= isNewVia;
            EnrollmentType= enrollmentType;

            lstPlayback= cActivityDaoImpl.getActivitylstPlayback();
            Api=cActivityDaoImpl.getApiRequest();

            log.info("cActivity editUserInformation DateBegin: " + DateBegin);
        }*/


    public List<ActivityUser> getLstUser() {
        return LstUser;
    }

    public void setlstPlayback(cApiListPlayback LstPlayback){
        lstPlayback= LstPlayback;
    }
    @Override
    public void setActivityState( eActivityState activityState){
        ActivityState=activityState;
    };
    @Override
    public void setActivityID (String activityID){
        ActivityID= activityID;
    };


    @Override
    public void setLastModificationDate(Date lastModificationDate) {
        LastModificationDate = lastModificationDate;
    };
    @Override
    public void setLastModificationUser(String lastModificationUser) {
        LastModificationUser = lastModificationUser;

    };
    @Override
    public void setParticipantType(eParticipantType participantType) {
        ParticipantType = participantType;
    };

}
