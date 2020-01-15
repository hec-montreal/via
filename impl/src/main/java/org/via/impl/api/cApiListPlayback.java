package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

import org.via.impl.Playback;

@XStreamAlias("cApiListPlayback")
public class cApiListPlayback extends cBaseApi
{
    private String ActivityID = "";
    private List<Playback> PlaybackList = new ArrayList<Playback>();
    
    public cApiListPlayback(String activityID, String cieid, String apiid) 
    {
        super(cieid, apiid);
        ActivityID = activityID;
    }
    
    public cApiListPlayback()
    {}

    public List<Playback> getPlaybackList() {
        return PlaybackList;
    }
}
