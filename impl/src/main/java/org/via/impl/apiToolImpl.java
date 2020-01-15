package org.via.impl;

import lombok.*;
import org.apache.log4j.*;
import org.sakaiproject.api.app.scheduler.*;
import org.sakaiproject.authz.api.*;
import org.sakaiproject.component.cover.*;
import org.sakaiproject.email.api.*;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.*;
import org.sakaiproject.exception.*;
import org.sakaiproject.site.api.*;
import org.sakaiproject.tool.api.*;
import org.sakaiproject.user.api.*;
import org.via.*;
import org.via.dao.*;
import org.via.enums.*;
import org.via.impl.api.*;
import org.w3c.dom.*;

import java.text.*;
import java.util.*;


public class apiToolImpl implements IapiTool, EntityTransferrer
{
    private static final Logger log = Logger.getLogger(apiToolImpl.class);
    
    @Getter @Setter
    private static IapiTool apiTool;

    @Getter @Setter
    private  ToolManager toolManager;

    @Getter @Setter
    private SessionManager sessionManager;

    @Getter @Setter
    private static UserDirectoryService userDirectoryService;

    @Getter @Setter
    private SecurityService securityService;

    @Setter
    private static AuthzGroupService authzGroupService;

    @Getter @Setter
    private SiteService siteService;

    @Getter @Setter
    private SchedulerManager schedulerManager;

    @Getter @Setter
    private FunctionManager functionManager;

    @Getter @Setter
    private EntityManager entityManager;

    @Getter @Setter
    private EmailService emailService;
    
    @Getter @Setter
    private cActivityDao activityDao;
    
    @Getter @Setter
    private cUserDao userDao;

    private String SiteID = "";
    private String UserID = "";

    //API
    private static ApiRequestImpl api;

    //Matches the bean id
    final static String beanId = "updateViaUsers";

    //Matches the jobName
    final static String jobName = "Updater VIA Users";

    String ViaSenderEmail = "";
    String ViaEmailWebsiteURL = "";
    private static String query;
    private static Object[] fields;
    
    private static boolean doReturn;
    private static String uID;

    public apiToolImpl()
    {
        super();
        activityDao= new cActivityDaoImpl();
        userDao= new cUserDaoImpl();
    }

    public apiToolImpl( String siteID, String userID)
    {
        super();
        this.SiteID = siteID;
        this.UserID = userID;
    }
    
    @Override
    public IcActivity getActivity(String activityID, String sakaiUserID)
    {
       IcActivity activite = new cActivity();
        
        cActivityDaoImpl.loadActivity(activite, activityID,sakaiUserID,userDirectoryService,api);
       return activite;
      
    }

    @Override
    public IcActivity createActivity(String sakaiUserID, String title, eActivityType activityType, Date dateBegin, int duration)
    {
        IcActivity activite = new cActivity(sakaiUserID, title, activityType, dateBegin, duration, api, getCurrentSiteId(), userDirectoryService);
        try {
            activite.setUserId( cActivityDaoImpl.getVIAUserID(userDirectoryService.getUser(sakaiUserID), "_"));
        } catch (UserNotDefinedException e) {

            log.error("cActivity : " + e.getMessage());
        }
        return activite;
    }

    @Override
    public IcUsers getCurrentUser()
    {
        try {
            return new cUser(userDirectoryService.getUser(getCurrentUserId()), getCurrentSiteId(), api, securityService, siteService.siteReference(getCurrentSiteId()), userDirectoryService);
        } catch (UserNotDefinedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public IcUsers getCurrentUser(String SiteID)
    {
        try {
            return new cUser(userDirectoryService.getUser(getCurrentUserId()), SiteID, api, securityService, siteService.siteReference(SiteID), userDirectoryService);
        } catch (UserNotDefinedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<? extends IProfil> getLstProfils()
    {
        return api.getProfilList(api.getCieID(), api.getApiID()).getProfilList();
    }

    public List<User> getUserList(){
    return getUserList(toolManager.getCurrentPlacement().getContext());
        }
        
        public List<User> getUserList(String siteId)
        {
        try
        {
            List<String> userListStr = new ArrayList<String>();
            List<User> users = new ArrayList<User>();
            Set<String> usersToRetrieve = null;
            Site currentSite = siteService.getSite(siteId);
            if (canEditActivityOfSite()){
                usersToRetrieve = currentSite.getUsers();
            }else{
                IcUsers currentUser = getCurrentUser();
                usersToRetrieve = new HashSet<String>();
                for (Group group: currentSite.getGroupsWithMember(currentUser.getSakaiID())){
                    usersToRetrieve.addAll(group.getUsers());
                }
            }

            for (String str : usersToRetrieve)
            {
                try
                {
                    if(!userListStr.contains(str))
                    {
                        userListStr.add(str);
                        users.add(userDirectoryService.getUser(str));
                    }
                } catch (UserNotDefinedException e) {
                    //log.error("apiToolImpl : " + e.getMessage());

                }

            }

            Collections.sort(users, new SortIgnoreCaseUser());

            return users;
        } catch (IdUnusedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Group> getUserGroupList(String siteId){
        String userId = getCurrentUserId();
        try
        {
            List<Group> groups = new ArrayList<Group>();
            Site site  = null;
            if (siteId == null)
               site = siteService.getSite(toolManager.getCurrentPlacement().getContext());
            else
                site = siteService.getSite(siteId);

            //Super user
            if (securityService.isSuperUser(userId) ||
                    //has permissions on site
                    (site.isAllowed(userId, cUserPermissions.canCreateActivityFN) || site.isAllowed(userId, cUserPermissions.canEditActivityFN))) {
                groups.addAll(site.getGroups());
             }
            //  user is member of one group at least
            else if (site.getGroupsWithMember(userId).size() > 0) {

                for (Group group : site.getGroupsWithMember(userId)) {
                    groups.add(group);
               }
            }else {
                //Check permission site.upd of user role in site
                if (securityService.unlock(userId, "site.upd", site.getReference()) && site.getMember(userId) == null) {
                    groups.addAll(site.getGroups());
                }
            }
            Collections.sort(groups, new SortIgnoreCaseGroup());
            return groups;
        } catch (IdUnusedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean canCreateActivity (String userId){
        Site site = null;
        if (securityService.isSuperUser(userId))
            return true;
        try {
            site = siteService.getSite(toolManager.getCurrentPlacement().getContext());
            // Check role in site
            if (site.isAllowed(userId, cUserPermissions.canCreateActivityFN )
                    || site.isAllowed(userId, cUserPermissions.canEditActivityFN )) {
                return true;
            }
            //Check group and sections of the site
            for (Group group: site.getGroups()){
                if (securityService.unlock(userId, cUserPermissions.canCreateActivityFN, group.getReference()))
                    return true;
            }

            //Check permission site.upd of user role in site
            if (securityService.unlock(userId, "site.upd", site.getReference()) && site.getMember(userId) == null){
                return true;
            }


        } catch (IdUnusedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean canEditActivity (IcActivity activity, String userId){
        Site site  = null;
          try {

            User user = userDirectoryService.getUser(userId);
              if (activity.getLastModificationUser().equalsIgnoreCase(user.getEid()))
                  return true;

              site = siteService.getSite(activity.getSakaiSiteID());

            if (securityService.isSuperUser(userId)) {
                return true;
            }

            Role userRole = site.getUserRole(userId);
            //User is in site
            if (userRole != null) {
                if ((site.isAllowed(userId, cUserPermissions.canCreateActivityFN) || site.isAllowed(userId, cUserPermissions.canEditActivityFN))) {
                    return true;
                } else {
                    List<String> activityGroups = activity.getLstGroup();
                    if (activityGroups != null){
                        Group group = null;
                        for (String groupId: activityGroups){
                            group = site.getGroup(groupId);
                            if (group != null &&
                                    (group.isAllowed(userId, cUserPermissions.canCreateActivityFN) ||
                                    group.isAllowed(userId, cUserPermissions.canEditActivityFN))){
                                return true;
                            }
                        }
                    }

                }
            } else{
                //Check permission site.upd of user role in site
                if (securityService.unlock(userId, "site.upd", site.getReference()) && site.getMember(userId) == null){
                    return true;
                }
            }

          } catch (IdUnusedException e) {
            e.printStackTrace();
        } catch (UserNotDefinedException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean canEditActivityOfSite (){
        String userId = getCurrentUserId();
        Site site  = null;
        try {
            site = siteService.getSite(toolManager.getCurrentPlacement().getContext());
            if (securityService.isSuperUser(userId) ||
                    //has permissions on site
                    (site.isAllowed(userId, cUserPermissions.canCreateActivityFN) || site.isAllowed(userId, cUserPermissions.canEditActivityFN))) {
                return true;
            }

            //Check permission site.upd of user role in site
            if (securityService.unlock(userId, "site.upd", site.getReference()) && site.getMember(userId) == null){
                return true;
            }

        } catch (IdUnusedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Group> getGroupList()
    {
        try
        {
            List<Group> groups = new ArrayList<Group>();
            for (Group group : siteService.getSite(toolManager.getCurrentPlacement().getContext()).getGroups())
            {
                groups.add(group);
            }
            Collections.sort(groups, new SortIgnoreCaseGroup());

            return groups;
        } catch (IdUnusedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getGroupsUsers(List<String> groupsToShow)
    {
        try
        {
            Site site = siteService.getSite(toolManager.getCurrentPlacement().getContext());
            List<User> users = new ArrayList<User>();

            for (String group : groupsToShow)
            {
                for(String user : site.getGroup(group).getUsers())
                {
                    try
                    {
                        User u = userDirectoryService.getUser(user);
                        if(users.indexOf(u) == -1)
                            users.add(u);
                    }
                    catch (UserNotDefinedException e) {
                        //log.error("apiToolImpl : " + e.getMessage());
                    }
                }
            }

            Collections.sort(users, new SortIgnoreCaseUser());

            return users;
        } catch (IdUnusedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void transferCopyEntities(String string, String string2, List<String> list) {
        //FUSION DE DONNEE

        try
        {
            Set<IcActivity> activities =  userDao.getActivities("",this,getCurrentUser(string)).keySet();
            //for (IcActivity act : getCurrentUser(string).getActivityList())
            for (IcActivity act :activities)
            {
                    activityDao.DuplicateActivity(string, string2, getCurrentUser(string).getSakaiID(), act);
            }
        }
        catch(Exception ex)
        {
            log.error("transferCopyEntities : " + ex.getMessage());
        }

    }

    @Override
    public void transferCopyEntities(String string, String string2, List<String> list, boolean b) {
        //REMPLACEMENT DE DONNEE

        Set<IcActivity> activities = userDao.getActivities("",this,getCurrentUser(string2)).keySet();
        //for (IcActivity act : getCurrentUser().getActivityList())
        for (IcActivity act : activities)
        {
            activityDao.Delete(getCurrentUser(string2).getSakaiID(), apiTool, act);
        }

        transferCopyEntities(string, string2, list);
    }

    @Override
    public String[] myToolIds() {
        String[] toolIDs = {"sakai.via"};
        return  toolIDs;
    }

    @Override
    public String getLabel() {
        // TODO Implement this method
        return "ACTIVITY";
    }

    @Override
    public boolean willArchiveMerge() {
        // TODO Implement this method
        return false;
    }

    @Override
    public String archive(String string, Document document, Stack<Element> stack, String string2,
                          List<Reference> list) {
        // TODO Implement this method
        return null;
    }

    @Override
    public String merge(String string, Element element, String string2, String string3, Map<String, String> map,
                        Map<String, String> map2, Set<String> set) {
        // TODO Implement this method
        return null;
    }

    @Override
    public boolean parseEntityReference(String string, Reference reference) {
        // TODO Implement this method
        return false;
    }

    @Override
    public String getEntityDescription(Reference reference) {
        // TODO Implement this method
        return null;
    }

    @Override
    public ResourceProperties getEntityResourceProperties(Reference reference) {
        // TODO Implement this method
        return null;
    }

    @Override
    public Entity getEntity(Reference reference) {
        // TODO Implement this method
        return null;
    }

    @Override
    public String getEntityUrl(Reference reference) {
        // TODO Implement this method
        return null;
    }

    @Override
    public Collection<String> getEntityAuthzGroups(Reference reference, String string) {
        // TODO Implement this method
        return Collections.emptySet();
    }

    @Override
    public HttpAccess getHttpAccess() {
        // TODO Implement this method
        return null;
    }

    private class SortIgnoreCaseUser implements Comparator<Object> {
            public int compare(Object o1, Object o2) {
                String s1 = ((User)o1).getLastName();
                String s2 = ((User)o2).getLastName();

                if(s1.toLowerCase().equals(s2.toLowerCase()))
                    return ((User)o1).getFirstName().compareToIgnoreCase(((User)o2).getFirstName());
                else
                    return s1.compareToIgnoreCase(s2);
            }
        }

    private class SortIgnoreCaseGroup implements Comparator<Object> {
            public int compare(Object o1, Object o2) {
            String s2 = ((Group) o2).getTitle();
            String s1 = ((Group) o1).getTitle();

            return s1.compareToIgnoreCase(s2);
            }
        }

    public User getSakaiUserInfo(String sakaiUserID)
    {
        try {
            return userDirectoryService.getUser(sakaiUserID);
        } catch (UserNotDefinedException e) {
            //log.warn ("The user " + sakaiUserID + " could not be found");
        }
        return null;
    }

    public Group getSakaiGroupInfo(String sakaiGroupID)
    {
        try {
            return siteService.getSite(toolManager.getCurrentPlacement().getContext()).getGroup(sakaiGroupID);
        } catch (IdUnusedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void init()
    {
        initConfig();

        functionManager.registerFunction(cUserPermissions.canCreateActivityFN);
        functionManager.registerFunction(cUserPermissions.canEditActivityFN);
        functionManager.registerFunction(cUserPermissions.canSeeAllActivityFN);

        entityManager.registerEntityProducer(this, "via");
    }


    public String test(String str)
    {
        log.warn(str);
        return "";
    }

    private void sendEmail(String from, String to, String subject, String content)
    {
        //log.error("SendEmail From : " + from + " TO : " + to + " SUBJECT : " + subject + " CONTENT : " + content);

        emailService.send(from, to, subject, content, to, null, null);

    }
    
    public ApiRequestImpl getApi(){
        return api;
        }

    public void sendActivityEmailNotification(IcActivity activity)
    {
        for(ActivityUser activityuser : cActivityDaoImpl.getLstUsers(true, activity))
        {
            //Envois email a tout le monde sauf le presentateur.
            if(!activityuser.getParticipantType().equals(eParticipantType.Presentateur))
            {
                sendActivityEmail(activity.getTitle(), activity.getDateBegin(), activityuser.getEmail(), getCurrentSiteId(), false);
            }
        }
    }

    public void sendActivityEmailReminder(String activityTitle, Date activityDate, String participantEid, String siteID)
    {
        sendActivityEmail(activityTitle, activityDate, participantEid, siteID, true);
    }

    private void sendActivityEmail(String activityTitle, Date activityDate, String emailAddress, String siteID, Boolean isReminder)
    {
        if (emailAddress == null || emailAddress.equals(""))
            return;

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy, HH:mm");

        String subject = "Invitation à une activité Via";

        if(isReminder && activityDate != null)
            subject = "Rappel de l'activité Via du " + sdf.format(activityDate);

        String content = "Bonjour,\nVous avez été invité à l'activité Via « " + activityTitle + " »";

        if(activityDate != null)
        {
            subject = "Invitation à l'activité Via du " + sdf.format(activityDate);
            content += " qui aura lieu le " + sdf.format(activityDate);
        }

        content += ". Veuillez y accéder en cliquant sur l'outil « Activités Via » dans le menu vertical du site ZoneCours « "+siteID+" ».\n\n"
            + "Pour accéder à ZoneCours, cliquez ici: " + ViaEmailWebsiteURL + "/portal/site/" + siteID;

        sendEmail(ViaSenderEmail, emailAddress, subject, content);
    }
    
    public void sendExportNotification(String recordName, String audioType, String emailAddress, String siteID) 
    {
        if (emailAddress == null || emailAddress.equals(""))
            return;
        
        String subject = "Exportation terminée";

        String content = "Bonjour,\nLe présent courriel est pour vous informer que l'exportation de l'enregistrement «";

        content += recordName;
        content += "» en format «";
        content += audioType;
        content += "» est terminé.";
        content += " Veuillez y accéder en cliquant sur l'outil « Activités Via » dans le menu vertical du site ZoneCours « "+siteID+" ».\n\n";
        content += "Pour accéder à ZoneCours, cliquez ici: " + ViaEmailWebsiteURL + "/portal/site/" + siteID;
        
        log.warn("emailAddress : " + emailAddress);
        log.warn("content : " + content);

        sendEmail(ViaSenderEmail, emailAddress, subject, content);
    }


    public static String normaliseString(String str)
    {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("[\\.\\-!#$%&'*+-/=?<>:;^_`{|}~]", "").replace(" ", "");
    }

    public static Logger getLog() {
        return log;
    }

    public String getViaURL() {
        return ViaURL;
    }

    public String getApiID() {
        return ApiID;
    }

    public String getCieID() {
        return CieID;
    }

    public String getAdminID() {
        return AdminID;
    }

    String ViaURL;
    String ApiID;
    String CieID;
    String AdminID;

    private void initConfig()
    {
        try
        {
             ViaURL = ServerConfigurationService.getString("viaUrl");
            if(ViaURL.equals(""))
            {
                log.error("Missing viaUrl in sakai.properties");
                return;
            }

             ApiID = ServerConfigurationService.getString("viaApiID");
            if(ApiID.equals(""))
            {
                log.error("Missing viaApiID in sakai.properties");
                return;
            }

            CieID = ServerConfigurationService.getString("viaCieID");
            if(CieID.equals(""))
            {
                log.error("Missing viaCieID in sakai.properties");
                return;
            }

            AdminID = ServerConfigurationService.getString("viaAdminID");
            if(AdminID.equals(""))
            {
                log.error("Missing viaAdminID in sakai.properties");
                return;
            }

            api = new ApiRequestImpl(ViaURL, ApiID, CieID, AdminID);

            String viaSenderEmail = ServerConfigurationService.getString("viaSenderEmail");
            if(!viaSenderEmail.equals(""))
            {
                ViaSenderEmail = viaSenderEmail;
            }
            else
            {
                log.error("Missing viaSenderEmail in sakai.properties");
            }

            String viaEmailWebsiteURL = ServerConfigurationService.getString("viaEmailWebsiteURL");
            if(!viaEmailWebsiteURL.equals(""))
            {
                ViaEmailWebsiteURL = viaEmailWebsiteURL;
            }
            else
            {
                log.error("Missing viaEmailWebsiteURL in sakai.properties");
            }
        }
        catch (Exception e)
        {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
    private void initCronJob()
    {
        log.info("init()");
            //Is there some other way to schedule this?!?
        try {
            Scheduler sched = schedulerManager.getScheduler();
            if (sched == null) {
              log.error("Scheduler is down!");
              return;
            }
            JobDetail jobDetail = new JobDetail( "Sync VIA Users", Scheduler.DEFAULT_GROUP, SpringConfigurableJobBeanWrapper.class);
            jobDetail.getJobDataMap().put("org.sakaiproject.api.app.scheduler.JobBeanWrapper.bean", "syncViaUsers");
            jobDetail.getJobDataMap().put("org.sakaiproject.api.app.scheduler.JobBeanWrapper.jobType","Sync VIA Users");
            CronTrigger trigger =  new CronTrigger( "Sync VIA Users", Scheduler.DEFAULT_GROUP);
            trigger.setCronExpression( "0 * * * * ?" );
            //0 0/5 * * * ? chaque 5minutes
            //0 0 * * * ? chaque heure
            sched.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            //This can probably just be a debug
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
    }
    */

    public void addParticipantToActivity(String activityID, String sakaiUserID, String siteID)
    {
        activityDao.addUserToActivity(sakaiUserID, eParticipantType.Participant, getActivity(activityID, sakaiUserID));
    }

    public void removeParticipantFromActivity(String activityID, String sakaiUserID, String siteID)
    {
         activityDao.removeUserActivity(sakaiUserID, getActivity(activityID, sakaiUserID));
    }

    public void UpdateVIAUserType(String viaUserID, String SakaiUserID, String siteID)
    {
        cApiUsers user = new cApiUsers(api.getCieID(), api.getApiID());

        User sakaiUser = getSakaiUserInfo(SakaiUserID);

        if(!sakaiUser.getEmail().equals(""))
            user.setEmail(sakaiUser.getEmail());


        user.setLogin(apiToolImpl.normaliseString(sakaiUser.getEid()));
        user.setPassword(sakaiUser.getId());

        if(sakaiUser.getFirstName().equals(""))
            user.setFirstName("N. A.");
        else
            user.setFirstName(sakaiUser.getFirstName());

        if(sakaiUser.getLastName().equals(""))
            user.setLastName("N. A.");
        else
            user.setLastName(sakaiUser.getLastName());

        user.setID(viaUserID);

        user.setToCoordo();

        cApiUsers temp = user;

        user = api.SendEditUser(user);

        //User already exists in cie... we add the name of the site to the login
        if(user == null)
        {
            temp.setLogin(apiToolImpl.normaliseString(sakaiUser.getEid() + "_" + siteID));
            user = api.SendEditUser(temp);
        }
    }

    public String getRecordDownloadURL(String userID, String playbackID, String recordType)
    {
        return api.SendRecordDownload(userID, playbackID, recordType);
    }
    
    public List<? extends IExport> getLatestExports(String dateFrom) 
   {
        return api.getLatestExports(dateFrom, api.getCieID(), api.getApiID()).getExportList();
   }


    /**
    * {@inheritDoc}
    */
    public String getCurrentSiteId()
    {
        if(this.SiteID != "")
            return SiteID;
        else
        { 
            return toolManager.getCurrentPlacement().getContext();}
    }

    /**
    * {@inheritDoc}
    */
    public String getCurrentUserId()
    {
        if(this.UserID != "")
            return UserID;
        else
            return sessionManager.getCurrentSessionUserId();
    }

    /**
    * {@inheritDoc}
    */
    public String getCurrentUserDisplayName() {
       return userDirectoryService.getCurrentUser().getDisplayName();
    }

    /**
    * {@inheritDoc}
    */
    public boolean isSuperUser() {
            return securityService.isSuperUser();
    }

    public Site getSite()
    {
        try {
            return siteService.getSite(toolManager.getCurrentPlacement().getContext());
        } catch (IdUnusedException e) {
            log.error("apiToolImpl : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public SecurityService getSecurityService()
    {
        return securityService;
    }

    public SiteService getSiteService()
    {
        return siteService;
    }


}
