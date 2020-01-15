package org.via.impl;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.via.IBreakOutPlayback;

@XStreamAlias("BreakOutPlayback")
public class BreakOutPlayback implements IBreakOutPlayback {
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
    private String PlaybackRefID = "";    
    
    @XStreamAsAttribute
    private String HasFullVideoRecord = "";
    
    @XStreamAsAttribute
    private String HasMobileVideoRecord = "";
    
    @XStreamAsAttribute
    private String HasAudioRecord = "";
    
    public BreakOutPlayback() 
    {
        super();
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

    public Date getCreationDate() {
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
