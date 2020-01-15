package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cApiRecordDownload")
public class cApiRecordDownload extends cBaseApi 
{
    private String ID = "";
    private String RecordType = "";
    private String PlaybackID = "";
    
    private String DownloadToken = "";
    
    public cApiRecordDownload(String id, String recordType, String playbackID, String cieID, String apiID) 
    {
        CieID = cieID;
        ApiID = apiID;
        
        ID = id;
        RecordType = recordType;
        PlaybackID = playbackID;
    }
    
    public cApiRecordDownload() 
    {
        
    }

    public String getDownloadToken() {
        return DownloadToken;
    }
}
