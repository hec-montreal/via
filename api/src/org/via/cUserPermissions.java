package org.via;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.User;

public class cUserPermissions 
{
    Boolean canCreateActivity = false;
    public final static String canCreateActivityFN = "via.perm.canCreateActivity";
    
    Boolean canEditActivity = false;
    public final static String canEditActivityFN = "via.perm.canEditActivity";
    
    Boolean canSeeAllActivities = false;
    public final static String canSeeAllActivityFN = "via.perm.canSeeAllActivity";
    
    public cUserPermissions(User user, SecurityService securityService, String siteRef)
    {
        canCreateActivity = securityService.unlock(user, canCreateActivityFN, siteRef);
        canEditActivity = securityService.unlock(user, canEditActivityFN, siteRef);
        canSeeAllActivities = securityService.unlock(user, canSeeAllActivityFN, siteRef);
    }
    /*
    public cUserPermissions(String userType, Boolean cActivity, Boolean eActivity, Boolean sActivity, Boolean cPermissions) 
    {
        super();
        
        canCreateActivity = cActivity;
        canEditActivity = eActivity;
        canSeeAllActivities = sActivity;
        canEditPermissions = cPermissions;
        UserType = userType;
    }
*/

    public Boolean getCanCreateActivity() {
        return canCreateActivity;
    }

    public Boolean getCanEditActivity() {
        return canEditActivity;
    }

    public Boolean getCanSeeAllActivities() {
        return canSeeAllActivities;
    }

    public void setCanCreateActivity(Boolean canCreateActivity) {
        this.canCreateActivity = canCreateActivity;
    }

    public void setCanEditActivity(Boolean canEditActivity) {
        this.canEditActivity = canEditActivity;
    }

    public void setCanSeeAllActivities(Boolean canSeeAllActivities) {
        this.canSeeAllActivities = canSeeAllActivities;
    }

    public static List<String> getPermissionNames()
    {
        List<String> lstPermissions = new ArrayList<String>();
        
        lstPermissions.add("canCreateActivity");        
        lstPermissions.add("canEditActivity");
        lstPermissions.add("canSeeAllActivities");
        
        return lstPermissions;
    }
}
