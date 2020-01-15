package org.via;

import java.util.Date;
import java.util.List;

import org.via.enums.eActivityType;

public interface IcUsers 
{
    String getID();

    String getEmail();

    String getLastName();

    String getFirstName();
    
    String getSakaiID();
    
    String getSupportURL();

    String getConfigURL();

    cUserPermissions getPermissions();
    
    public String getSakaiSiteID();
    
    //List<IcActivity> getActivityList();    
    
   // List<IcActivity> getActivityList(String groupFilter);
}
