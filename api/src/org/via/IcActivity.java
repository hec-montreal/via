package org.via;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.site.api.Site;

import org.sakaiproject.user.api.User;

import org.sakaiproject.user.api.UserDirectoryService;

import org.via.enums.eActivityState;
import org.via.enums.eActivityType;
import org.via.enums.eAudioType;
import org.via.enums.eParticipantType;
import org.via.enums.eRecordModeBehavior;
import org.via.enums.eRecordingMode;
import org.via.enums.eRoomType;
import org.via.enums.eWaitingRoomAccessMode;


public interface IcActivity {
    

    //void editUserActivity(String sakaiUserID, eParticipantType type);

    //void removeUserActivity(String sakaiUserID);

    void renamePlayback(String playbackID, String newName);

    void editPlaybackPublic(String playbackID, Boolean isPublic);

    String getPlaybackURL(String playbackID, Boolean forcedEditRights);

    String getVIAURL(Boolean forcedEditRights);

    String getActivityID();

    String getUserID();
    
    eParticipantType getParticipantType();

    String getTitle();

    void setTitle(String Title);

    eActivityState getActivityState();
    
    void setActivityState( eActivityState activityState);

    eAudioType getAudioType();

    void setAudioType(eAudioType AudioType);

    eActivityType getActivityType();

    void setActivityType(eActivityType ActivityType);

    Date getDateBegin();
    
    Date getDateEnd();
    
    Boolean getIsPreparation();

    void setDateBegin(Date DateBegin);

    Integer getDuration();

    void setDuration(Integer Duration);

    Integer getNeedConfirmation();

    void setNeedConfirmation(Integer NeedConfirmation);

    eRecordingMode getRecordingMode();

    void setRecordingMode(eRecordingMode RecordingMode);
    
    String getDescription();

    void setDescription(String Description);
    
    String getLastModificationUser();

    Date getLastModificationDate();
    
    List<ActivityUser> getLstUser();    
    //List<ActivityUser> getLstUser(boolean reloadList);
    
    List<String> getLstGroup();

    List<? extends IPlayback> getLstPlayback();
    
    Boolean getIsReplayAllowed();

    void setIsReplayAllowed(Boolean IsReplayAllowed);

    eRoomType getRoomType();

    void setRoomType(eRoomType RoomType);
    
    Integer getReminder();
    
    void setReminder(Integer reminder);    
    
    Boolean getIsNewVia();
    
    Boolean getIsNew();
    
    void setIsNewVia(Boolean isNewVia);
    
    Integer getEnrollmentType();

    void setEnrollmentType(Integer EnrollmentType);
    
    String getProfilID();

    void setProfilID(String ProfilID);
    
    eRecordModeBehavior getRecordModeBehavior();

    void setRecordModeBehavior(eRecordModeBehavior RecordModeBehavior);
    
    eWaitingRoomAccessMode getWaitingRoomAccessMode();

    void setWaitingRoomAccessMode(eWaitingRoomAccessMode WaitingRoomAccessMode);
    
    void setUserId (String sakaiUserID);
    
    void setActivityID (String activityID);
    
    void setSakaiSiteID (String SakaiSiteID);
    
    void setLastModificationDate (Date LastModificationDate);
    
    void setLastModificationUser ( String LastModificationUser);
    
    void setParticipantType (eParticipantType ParticipantType);
    
    public String getSakaiSiteID();
    
   void editUserInformations ();/*UserDirectoryService uds,List<ActivityUser> lstUser, List<String> lstGroup, String activityID,String sakaiSiteID,String title, String description, eActivityState activityState,
      Boolean isReplayAllowed,  eRoomType roomType, eAudioType audioType,eActivityType activityType, Date dateBegin,
      Integer duration, String profilID, eRecordingMode recordingMode, eRecordModeBehavior recordModeBehavior,
     eWaitingRoomAccessMode waitingRoomAccessMode, Date lastModificationDate, eParticipantType participantType,
    Integer reminder, Boolean isNewVia, Integer enrollmentType);*/
    
    
}
