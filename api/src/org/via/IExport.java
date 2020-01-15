package org.via;

import java.util.Date;

public interface IExport 
{
    Date getExportEndDate();
    
    String getRecordingType();
    
    String getUserID();
    
    String getActivityID();
    
    String getPlaybackTitle();
}
