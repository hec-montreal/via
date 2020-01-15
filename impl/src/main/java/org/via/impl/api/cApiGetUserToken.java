package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.via.enums.eRedirectType;

@XStreamAlias("cApiGetUserToken")
public class cApiGetUserToken extends cBaseApi
{
    private String ID = "";
    private String RedirectType = "";
    private String ActivityID = "";
    private String PlaybackID = "";
    
    private String PortalAccess = "0";
    
    private String ForcedAccess = "";
    private String ForcedEditRights = "";
    
    private String TokenURL = "";
    
    
    public cApiGetUserToken(String userID, eRedirectType type, String activityID, String playbackID, String forcedAccess, String forcedEditRights, String cieid, String apiid) 
    {
        super(cieid, apiid);
        ID = userID;
        
        if(activityID != "")
            ActivityID = activityID;
      
       if(playbackID != "")
            PlaybackID = playbackID;
       
       if(forcedAccess != "")
           ForcedAccess = forcedAccess;
       
       if(forcedEditRights != "")
           ForcedEditRights = forcedEditRights;
       
        RedirectType = String.valueOf(type.getValue());
        type = null;
    }
    
    private cApiGetUserToken()
    {}

    public String getTokenURL() {
        return TokenURL;
    }

}
