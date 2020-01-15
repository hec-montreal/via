package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cApiActivityDuplicate")
public class cApiActivityDuplicate extends cBaseApi 
{
    private String ActivityID = "";
    private String UserID = "";
    private String Title = "";
    private String DateBegin = "";
    private String Duration = "";
    private String ActivityType = "";
    
    private String IncludeUsers = "0";
    private String IncludeDocuments = "1";
    private String IncludeSurveyAndWBoards = "1";
    
    private String ActivityIDDuplicate = "";
    
    
    public cApiActivityDuplicate(String cieID, String apiID, String activityID, String userID, String title, String dateBegin, String duration, String activityType) 
    {
        super(cieID, apiID);
        
        ActivityID = activityID;
        UserID = userID;
        Title = title;
        DateBegin =dateBegin;
        Duration = duration;
        ActivityType = activityType;
    }
    
    private cApiActivityDuplicate() 
    {
    }

    public String getActivityIDDuplicate() {
        return ActivityIDDuplicate;
    }

}
