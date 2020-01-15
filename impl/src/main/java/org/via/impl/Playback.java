package org.via.impl;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.via.IPlayback;

@XStreamAlias("Playback")
public class Playback implements IPlayback {
    @XStreamAsAttribute
    private String PlaybackID= "";
    
    @XStreamAsAttribute    
    private String Title = "";
    
    @XStreamAsAttribute    
    private String Duration = "";
    
    @XStreamAsAttribute 
    private String CreationDate = "";
    
    @XStreamAsAttribute
    private String IsPublic = "";
    
    @XStreamAsAttribute
    private String HasFullVideoRecord = "";
    
    @XStreamAsAttribute
    private String HasMobileVideoRecord = "";
    
    @XStreamAsAttribute
    private String HasAudioRecord = "";
    
    
    private List<BreakOutPlayback> BreackOutPlaybackList = new ArrayList<BreakOutPlayback>();
    
    public Playback() 
    {}

    public List<? extends BreakOutPlayback> getBreackOutPlaybackList() {
        return BreackOutPlaybackList;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDuration() {
        return Duration;
    }

    public Date getCreationDate() 
    {
        //2014-05-12 14:16:51

        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(CreationDate);
        } catch (ParseException e) {
        }
        return null;
    }

    public String getIsPublic() {
        return IsPublic;
    }

    public void setIsPublic(String IsPublic) {
        this.IsPublic = IsPublic;
    }

    public String getPlaybackID() {
        return PlaybackID;
    }

    public String getHasFullVideoRecord() {
        return HasFullVideoRecord;
    }

    public String getHasMobileVideoRecord() {
        return HasMobileVideoRecord;
    }

    public String getHasAudioRecord() {
        return HasAudioRecord;
    }

}
