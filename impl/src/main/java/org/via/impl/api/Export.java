package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.via.IExport;

@XStreamAlias("Export")
public class Export implements IExport
{    
    @XStreamAsAttribute
    private String ExportEndDate = "";
    
    @XStreamAsAttribute
    private String RecordingType = "";
    
    @XStreamAsAttribute
    private String UserID = "";
    
    @XStreamAsAttribute
    private String ActivityID = "";
    
    @XStreamAsAttribute
    private String PlaybackTitle = "";
    
    public Export() 
    {
        super();
    }

    public Date getExportEndDate() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ExportEndDate);
        } catch (ParseException e) {
        }
        return null;
    }

    public String getRecordingType() {
        if(RecordingType.equals("1"))
            return "Vidéo";
        else if (RecordingType.equals("2"))
            return "Mobile";
        else if (RecordingType.equals("3"))
            return "Audio";
        else
            return "";
    }

    public String getUserID() {
        return UserID;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public String getPlaybackTitle() {
        return PlaybackTitle;
    }
}
