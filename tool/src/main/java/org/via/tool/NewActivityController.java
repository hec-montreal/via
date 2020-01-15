package org.via.tool;

import org.sakaiproject.authz.api.*;
import org.sakaiproject.site.api.*;
import org.sakaiproject.user.api.*;
import org.springframework.web.servlet.*;
import org.via.*;
import org.via.enums.*;

import javax.servlet.http.*;
import java.text.*;
import java.util.*;

public class NewActivityController extends BaseController {

    /**
     * Controller for Via - creating a new activity
     *
     */
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);
        IcUsers user = apiTool.getCurrentUser(); 
        String error = null;

        boolean canEditActivityOfSite = apiTool.canEditActivityOfSite();
        map.put("canEditActivityOfSite", canEditActivityOfSite);

        String enrollmentType = null;
        if (request.getParameter("save") != null) {
            HttpSession session = request.getSession();
            String id = (String) session.getAttribute("id");
            String title = request.getParameter("title");
            if(title != null)
            {
                if(title.matches("")){
                    error = "Error_title";
                }
            }
                       
            String description = request.getParameter("description");

            Integer activityType = 1;
            Date newStartDate = new Date();
            Integer duration = 0;
            if (request.getParameter("permanent") != null) {
                activityType = 2; //permanent activity
                newStartDate = null;
            } else {
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                String date = request.getParameter("date");
                String startTime = request.getParameter("startTime");
                String tempDate = date + " " + startTime;
                if(date != null && startTime != null)
                    newStartDate = formatter.parse(tempDate);
                if(request.getParameter("duration") != null)
                {
                    if(request.getParameter("duration").isEmpty()){
                        error = "Error_duration"; 
                    }else{
                        duration = Integer.parseInt(request.getParameter("duration"));
                }
            }
            }
            
            Integer roomType = 1;
            if (request.getParameter("roomType") != null) 
                roomType = Integer.parseInt(request.getParameter("roomType"));
            
            Integer reminder = 0;
            if (request.getParameter("ddlReminder") != null)
                reminder = Integer.parseInt(request.getParameter("ddlReminder"));
                
            String mediaQuality = request.getParameter("mediaQuality");

            Integer recordingMode = 0;
            if (request.getParameter("recordingMode") != null) 
                recordingMode = Integer.parseInt(request.getParameter("recordingMode"));

            Integer recordingBehavior = 1;
            if (request.getParameter("recordingBehavior") != null) {
                recordingBehavior = Integer.parseInt(request.getParameter("recordingBehavior"));
            }

            Boolean recordingPublic = false;
            if (request.getParameter("recordingIsPublic") != null) {
                recordingPublic = Boolean.parseBoolean(request.getParameter("recordingIsPublic"));
            }
            
            Integer waitingRoom = 0;
            if(request.getParameter("waitingRoom") != null)
                waitingRoom= Integer.parseInt(request.getParameter("waitingRoom"));
            
            Boolean isNewVia = true;
            if (request.getParameter("chkIsNewVia") != null)
            {
                isNewVia = !Boolean.parseBoolean(request.getParameter("chkIsNewVia"));
            }

            if(request.getParameter("enrollmentType") != null)
                enrollmentType = request.getParameter("enrollmentType");


            String presentor = request.getParameter("presentor");
            if(presentor.matches("") || presentor == null){
                error = "Error_presentor";
            }
            String[] animators = request.getParameterValues("hiddenAnimators");
            String[] participants = request.getParameterValues("hiddenParticipants");
            String[] groups = request.getParameterValues("groups");

            map.put("id", id);
            
            if (id == null) {
                
                //we create a new activity 
                try {
                   
                    IcActivity activityToCreate = null;
                    if(error == null){
                        activityToCreate = apiTool.createActivity(user.getSakaiID(), title, eActivityType.GetActivityType(activityType), newStartDate, duration); 
                    }else{
                        map.put("error", error); 
                    }
                    if(activityToCreate != null){
                        
                    activityToCreate.setDescription(description);
                    activityToCreate.setRoomType(eRoomType.GetRoomType(roomType));
                    activityToCreate.setProfilID(mediaQuality);
                    activityToCreate.setRecordingMode(eRecordingMode.GetRecordingMode(recordingMode));
                    activityToCreate.setRecordModeBehavior(eRecordModeBehavior.GetRecordModeBehavior(recordingBehavior));
                    activityToCreate.setIsReplayAllowed(recordingPublic);
                    activityToCreate.setWaitingRoomAccessMode(eWaitingRoomAccessMode.GetWaitingRoomAccessModeType(waitingRoom));
                    activityToCreate.setReminder(reminder);
                    activityToCreate.setIsNewVia(isNewVia);
                    activityToCreate.setEnrollmentType(Integer.parseInt(enrollmentType));
                    activityDao.Save(user.getSakaiID(),activityToCreate);
                    //activityToCreate.Save(user.getSakaiID());

                   if (presentor != null) {
                        activityDao.removeUserActivity(user.getSakaiID(), activityToCreate);
                        try{
                            activityDao.addUserToActivity(presentor, eParticipantType.Presentateur,activityToCreate);
                        } catch (Exception e) {
                            map.put("error", "Error_user");
                            map.put("etext", getErrorMessage(e));
                        }
                   }

                    if (animators != null && animators.length > 0) {
                        for (String animator : animators) {
                            try{
                                activityDao.addUserToActivity(animator, eParticipantType.Animator,activityToCreate);
                            } catch (Exception e) {
                                map.put("error", "Error_user");
                                map.put("etext", getErrorMessage(e));
                            }
                        }
                    }

                    if (participants != null && participants.length > 0) {
                        for (String participant : participants) {
                            try{
                                activityDao.addUserToActivity(participant, eParticipantType.Participant,activityToCreate);
                            } catch (Exception e) {
                                map.put("error", "Error_user");
                                map.put("etext", getErrorMessage(e));
                            }
                        }
                    }
                    if (groups != null && groups.length > 0) {
                        for (String group : groups) {
                            try{
                                activityDao.addGroupToActivity(group, apiTool.getSite(),activityToCreate);
                            } catch (Exception e) {
                                map.put("error", "Error_group");
                                map.put("etext", getErrorMessage(e));
                            }
                        }
                    }
                    
                    if(request.getParameter("chkSendInvitation") != null)
                    {
                        if(Boolean.parseBoolean(request.getParameter("chkSendInvitation")))
                            apiTool.sendActivityEmailNotification(activityToCreate);
                    }
                    

                    session.setAttribute("id", activityToCreate.getActivityID());
                    response.sendRedirect("activity_details.htm");
                    }

                } catch (Exception e) {
                    map.put("error", "Error_catch");
                    map.put("etext", getErrorMessage(e));
                }


            } else {

                //We update existing activity
                try {
                IcActivity activity = apiTool.getActivity(id, user.getSakaiID());
                    if(activity == null){
                       error = "Error_noActivity";
                    }else{
                        map.put("id", id);
        
                        if(activity.getActivityType().getValue() == 2 || !activity.getDateEnd().before(new Date()))
                        {
                            activity.setTitle(title);
                            activity.setDescription(description);
                            activity.setActivityType(eActivityType.GetActivityType(activityType));
                            activity.setDateBegin(newStartDate);
                            activity.setDuration(duration);
                            activity.setRecordingMode(eRecordingMode.GetRecordingMode(recordingMode));
                            activity.setRecordModeBehavior(eRecordModeBehavior.GetRecordModeBehavior(recordingBehavior));
                            activity.setIsReplayAllowed(recordingPublic);
                            activity.setWaitingRoomAccessMode(eWaitingRoomAccessMode.GetWaitingRoomAccessModeType(waitingRoom));
                            activity.setReminder(reminder);
                            activity.setIsNewVia(isNewVia);
                            if(roomType!=null)
                                activity.setRoomType(eRoomType.GetRoomType(roomType));
                            if(mediaQuality != null)
                                activity.setProfilID(mediaQuality);
                        }
                        
                        if(error == null){
                            activity.setEnrollmentType(Integer.parseInt(enrollmentType));
                            error = activityDao.Save(user.getSakaiID(),activity);

                            if (error != "")
                            {
                                map.put("error", error);
                                map.put("etext", "Vous ne pouvez pas changer la version de cette activité puisque des utilisateurs s'y sont connectés.");
                                
                                //Mapper les autres trucs pour le reload ici...
                                mapCurrentValues(request, request.getParameter("enrollmentType"));
                                
                                map.put("savedIsNewVia", !isNewVia);  
                            }
                            
                        }else{
                            map.put("error", error);
                        }
                        
                        if (error == null || error == "")
                        {
                            List<String> savedGroups = activity.getLstGroup();
                            
                            if(request.getParameterValues("groups") != null){
            
                                String[] newGroups = request.getParameterValues("groups");
                                List<String> newGroupList = Arrays.asList(newGroups);
                                List<String> groupsToKeep = new ArrayList<String>();
                
                                for (String newGroup : newGroupList) {
                                    //New user in the group, we need to add it to every single groups...
                                    if (savedGroups.indexOf(newGroup) == -1) {
                                        activityDao.addGroupToActivity(newGroup, apiTool.getSite(), activity);
                                    } else {
                                        groupsToKeep.add(newGroup);
                                    }
                                }
            
                                for (String saved : savedGroups) {
                                    if (groupsToKeep.indexOf(saved) == -1) {
                                        activityDao.removeGroupActivity(saved, activity);
                                    }
                                }
                            }else{
                                        //there are no groups we delete them all 
                                for (String saved : savedGroups) {
                                    activityDao.removeGroupActivity(saved, activity);
                                }
                            }
                            
                            
                            List<ActivityUser> savedUsers = activityDao.getLstUser(activity);
                            List<String> savedUsersList = new ArrayList<String>();
                            
                            for( int i = 0; i < savedUsers.size(); i++){
                                savedUsersList.add(savedUsers.get(i).getSakaiUserID());
                            }                
                            
                            if(presentor != null){
                            List<String> allNewUsers = new ArrayList<String>();
                            allNewUsers.add(presentor);
                            List<String> participantsList = null;
                            if (participants != null) {
                                allNewUsers.addAll(Arrays.asList(participants));
                                participantsList = Arrays.asList(participants);
                            }
            
                            if (animators != null) {
                                allNewUsers.addAll(Arrays.asList(animators));                   
                            }
            
                            List<String> usersToKeep = new ArrayList<String>();
                            List<String> usersToAdd = new ArrayList<String>();
                            
                            for (String newUser : allNewUsers) {
                                //New user in the group, we need to add it to every single groups...
                                if (savedUsersList.indexOf(newUser) == -1) {
                                   // Add user 
                                   // check role then add
                                    usersToAdd.add(newUser);
                                    if (newUser.equals(presentor)) {
                                        activityDao.addUserToActivity(newUser, eParticipantType.Presentateur,activity);
                                    }else {
                                        if (participantsList.indexOf(newUser) == -1) {
                                            activityDao.addUserToActivity(newUser, eParticipantType.Animator,activity);
                                       } else {
                                            activityDao.addUserToActivity(newUser, eParticipantType.Participant,activity);
                                       }
                                    }
                                } else {
                                    usersToKeep.add(newUser);
                                } 
                            }
                            
                            for (ActivityUser savedUser : savedUsers) {
                                
                                if (usersToKeep.indexOf(savedUser.getSakaiUserID()) == -1) {
                                    activityDao.removeUserActivity(savedUser.getSakaiUserID(),activity);
                                    
                                } else {
                                    if (savedUser.getSakaiUserID().equals(presentor) && savedUser.getParticipantType().toString() != "2") {
                                        activityDao.editUserActivity(savedUser.getSakaiUserID(), eParticipantType.Presentateur,activity);  
                                    } else {
                                                //if the user to keep is not in the participants list, then they are animator 
                                        if ((participantsList == null || participantsList.indexOf(savedUser.getSakaiUserID()) == -1) && savedUser.getParticipantType().toString() != "2") {
                                            if(!savedUser.getParticipantType().equals(eParticipantType.Animator)){
                                                activityDao.editUserActivity(savedUser.getSakaiUserID(), eParticipantType.Animator, activity);
                                            }
                                        } else {
                                            if(!savedUser.getParticipantType().equals(eParticipantType.Participant)){
                                                activityDao.editUserActivity(savedUser.getSakaiUserID(), eParticipantType.Participant, activity);
                                            }
                                        }
                                    }
                                }
                            }
                            }
                          
                            
                            if(request.getParameter("chkSendInvitation") != null)
                            {
                                if(Boolean.parseBoolean(request.getParameter("chkSendInvitation")))
                                    apiTool.sendActivityEmailNotification(activity);
                            }
                        
                        //to pass only the id
                        session.setAttribute("id", activity.getActivityID());
                        response.sendRedirect("activity_details.htm");

                        }
                    }
                

                } catch (Exception e) {
                    map.put("error", "Error_catch");
                    map.put("etext", getErrorMessage(e));
                }
            }

        } else if (request.getParameter("modify") != null) {    
        
            /* if action is different to null, we are modifiying an existing activity, we need to set the values to display */
            try {
                // reset error to null
                error = null;

                HttpSession session = request.getSession();
                String id = (String) session.getAttribute("id");
                map.put("id", id);
                map.put("modify", "modify");

                enrollmentType = request.getParameter("enrollmentType");

                //We load the current activity's attribute. They will be overwritten afterwards if needed by new form inputs.
                //====================================================================================================
                IcActivity activity = apiTool.getActivity(id, user.getSakaiID());
                map.put("title", activity.getTitle());
                map.put("description", activity.getDescription());

                Integer activityType = activity.getActivityType().getValue();
                map.put("activityType", activityType);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                /* set default is permanent activity */
                String startDate = dateFormat.format(new Date());
                String startTime = timeFormat.format(new Date());
                map.put("canModifyAll", 0);
                map.put("canModifyRoomType", 0);
                
                if (activityType == 1) { /* set values for none permanent activities */
                    startDate = dateFormat.format(activity.getDateBegin());
                    startTime = timeFormat.format(activity.getDateBegin());
                    Date now = new Date();

                    Calendar canModifyRoomType = Calendar.getInstance();
                    canModifyRoomType.setTime(activity.getDateBegin());
                    canModifyRoomType.add(Calendar.MINUTE, -30);

                    if (activity.getDateEnd().before(now)) {
                        map.put("canModifyAll", 1);
                        map.put("canModifyRoomType", 1);

                    } else if (canModifyRoomType.getTime().before(now)) {
                        map.put("canModifyRoomType", 1);
                    }
                }
                map.put("duration", activity.getDuration());
                map.put("roomType", activity.getRoomType().getValue());
                map.put("mediaQuality", apiTool.getLstProfils());
                map.put("savedMedia", activity.getProfilID().toString());
                map.put("savedMode", activity.getRecordingMode().getValue());
                map.put("savedBehavior", activity.getRecordModeBehavior().getValue());
                map.put("savedIsPublic", activity.getIsReplayAllowed().toString());
                map.put("savedWR", activity.getWaitingRoomAccessMode().getValue());
                map.put("savedReminder", activity.getReminder().toString());
                map.put("savedIsNewVia", ((Boolean)(!activity.getIsNewVia())).toString());
                map.put("enrollmentType", activity.getEnrollmentType().toString());
                
                map.put("startDate", startDate);
                map.put("startTime", startTime);

                
                /* we set the presentor to null, so that we may retrieve the presentor from the savedUserList */
                map.put("presentor", null);
                map.put("presentorID", null);
                //====================================================================================================
                
                /* we are reloading page as the enrollement type or groups have been changed */
                if (enrollmentType != null && (enrollmentType.equals("0") || enrollmentType.equals("1") || enrollmentType.equals("2"))) {
                    mapCurrentValues(request, enrollmentType);
                } else {
                    
                    map.put("savedUserList", activityDao.getLstUser(activity));
                    List savedGroupsList = activity.getLstGroup();

                    if (savedGroupsList.size() > 0) {
                        map.put("enrollmentType", "2");

                        ArrayList<GroupSelection> selectedGroups = null;
                        selectedGroups = new ArrayList<GroupSelection>();

                        List<Group> listGroup = apiTool.getUserGroupList(null);
                        for (int i = 0; i < listGroup.size(); i++) {
                            AuthzGroup g = listGroup.get(i);
                            selectedGroups.add(new GroupSelection((Group)g, false));
                        }
                        String[] savedGroups = new String[savedGroupsList.size()];
                        savedGroupsList.toArray(savedGroups);

                        List<String> lstGroups = new ArrayList<String>();
                        for (String group : savedGroups) {
                            lstGroups.add(group);
                            for (int i = 0; i < selectedGroups.size(); i++) {
                                GroupSelection g = selectedGroups.get(i);
                                if (group.equals(g.getGroup().getId())) {
                                    g.setIsSelected(true);
                                }
                            }
                        }

                        map.put("groupList", selectedGroups);

                    } else {
                        
                        if(activity.getEnrollmentType() == 1)
                        {
                            List<User> users = apiTool.getUserList();
                            
                            for(ActivityUser aUser : activityDao.getLstUser(activity)) 
                            {
                                users.remove(apiTool.getSakaiUserInfo(aUser.getSakaiUserID()));
                            }
                           
                            map.put("userList", users);        
                        }
                        
                        map.put("enrollmentType", activity.getEnrollmentType().toString());
                    }

                    session.removeAttribute(id);
                }

            } catch (Exception e) {
                map.put("error", "Error_catch");
                map.put("etext", getErrorMessage(e));
            }

        } else {

            /* if action is null, we are creating a new activity, we need to set the default values to display */
            try {
                HttpSession session = request.getSession();
                String sessionType = (String) session.getAttribute("new");
                if (sessionType != null &&  sessionType.equals("new")) {
                    session.removeAttribute("new");
                } else {
                   enrollmentType = request.getParameter("enrollmentType");
                }
                
                /* if an enrollmentType has been picked it means that we have reloaded the page */
                if (enrollmentType != null && (enrollmentType.equals("0") || enrollmentType.equals("1") || enrollmentType.equals("2"))) {
                    mapCurrentValues(request, enrollmentType);
                    
                } else {

                    /* no enrollment type has been picked yet */
                    /* default values & reset all values */
                    if (!canEditActivityOfSite) {
                        enrollmentType = "2";
                        ArrayList<GroupSelection> selectedGroups = null;

                        selectedGroups = new ArrayList<GroupSelection>();
                        List<Group> listGroup = apiTool.getUserGroupList(null);
                        for (int i = 0; i < listGroup.size(); i++) {
                            Group g = (Group) listGroup.get(i);
                            selectedGroups.add(new GroupSelection(g, false));
                        }

                        String[] groupEnrollment = request.getParameterValues("groups");
                        if (groupEnrollment != null && groupEnrollment.length > 0) {
                            List<String> lstGroups = new ArrayList<String>();
                            for (String addGroup : groupEnrollment) {
                                lstGroups.add(addGroup);
                                for (int i = 0; i < selectedGroups.size(); i++) {
                                    GroupSelection g = selectedGroups.get(i);
                                    if (addGroup.equals(g.getGroup().getId())) {
                                        g.setIsSelected(true);
                                    }
                                }
                            }
                            map.put("groupEnrollment", groupEnrollment);
                            }

                        if (selectedGroups != null)
                        {
                            map.put("groupList", selectedGroups);
                        }

                        map.put("userList", null);

                    }

                    else {
                        enrollmentType = "0";
                        map.put("groupList", null);
                        map.put("groupEnrollment", null);
                        map.put("userList", apiTool.getUserList());

                    }
                    map.put("canModifyAll", 0);
                    map.put("id", null);
                    map.put("title", null);
                    map.put("description", null);
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String startDate = dateFormat.format(new Date());
                    map.put("startDate", startDate);
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    String startTime = timeFormat.format(new Date());
                    map.put("startTime", startTime);
                    map.put("duration", 90);
                    map.put("roomType", 1);
                    map.put("mediaQuality", apiTool.getLstProfils());
                    map.put("savedMedia", apiTool.getLstProfils().get(1).getProfilID());
                    map.put("savedMode", null);
                    map.put("savedBehavior", null);
                    map.put("savedIsPublic", null);
                    map.put("savedWR", null);
                    map.put("savedReminder", null);
                    map.put("presentor", apiTool.getSakaiUserInfo(user.getSakaiID()));
                    map.put("presentorID", user.getSakaiID());

                    //JG Variables to put to null on first load when it's a new activity because modifying beforehand would make conflicts. Therefore, errors.
                    map.put("enrollmentType", enrollmentType); /* default value */
                   map.put("tempAnimators", null);
                    map.put("savedUserList", null);
                    map.put("activityType", null);
                    map.put("canModifyRoomType", 0);
                    

                }

            } catch (Exception e) {
                map.put("error", "Error_catch");
                map.put("etext", getErrorMessage(e));
            }
        }
        
        return new ModelAndView("activity_new", map);

    }
    
    private void mapCurrentValues(HttpServletRequest request, String enrollmentType)
    {
        map.put("mediaQuality", apiTool.getLstProfils());
        
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");


        /* load with users depending on which enrollement type has been picked */
        map.put("enrollmentType", enrollmentType);
        
        /* get values that were already added before the enrollement type was changed */
        if(request.getParameter("title") != null)
        {
            map.put("title", request.getParameter("title"));
        }
        
        if(request.getParameter("description") != null)
            map.put("description", request.getParameter("description"));
        
        if(request.getParameter("date") != null)
        {
            map.put("startDate", request.getParameter("date"));
        }
        
        if(request.getParameter("startTime") != null)
        {
            map.put("startTime", request.getParameter("startTime"));
        }
        
        if(request.getParameter("duration") != null)
            map.put("duration", request.getParameter("duration"));
        
        if (request.getParameter("permanent") != null) {
            map.put("activityType", 2);

            /* default values */
             map.put("startDate", dateFormat.format(new Date()));
             map.put("startTime", timeFormat.format(new Date()));
             map.put("duration", 90);
        }
        
        map.put("roomType", request.getParameter("roomType"));                 
        map.put("savedMedia", request.getParameter("mediaQuality"));
        map.put("savedMode", request.getParameter("recordingMode"));

        if (request.getParameter("recordingBehavior") != null) {
            map.put("savedBehavior", Integer.parseInt(request.getParameter("recordingBehavior")));
        }

        if (request.getParameter("recordingIsPublic") != null) {
            map.put("savedIsPublic", Boolean.parseBoolean(request.getParameter("recordingIsPublic")));
        }
        

        if (request.getParameter("waitingRoom") != null) {
            map.put("savedWR", Integer.parseInt(request.getParameter("waitingRoom")));
        }
        
        if (request.getParameter("ddlReminder") != null)
        {
            map.put("savedReminder", Integer.parseInt(request.getParameter("ddlReminder")));   
        }
        
        if (request.getParameter("chkIsNewVia") != null)
        {
            map.put("savedIsNewVia", Boolean.parseBoolean(request.getParameter("chkIsNewVia")));   
        }

        /* reset values before filling them again */
        
        map.put("savedUserList", null);
        map.put("userList", null);
        map.put("groupList", null);
        
        List<User> userList = new ArrayList<User>();

        if (enrollmentType.equals("0") || enrollmentType.equals("1")) 
        {
            userList = apiTool.getUserList();
            map.put("userList", userList);
        } 
        else if (enrollmentType.equals("2"))
        {
                                    
            ArrayList<GroupSelection> selectedGroups = null;

            selectedGroups = new ArrayList<GroupSelection>();
            List<Group> listGroup = apiTool.getUserGroupList(null);
            for (int i = 0; i < listGroup.size(); i++) {
                Group g = (Group) listGroup.get(i);
                selectedGroups.add(new GroupSelection(g, false));
            }
                                   
            String[] groupEnrollment = request.getParameterValues("groups");
            if (groupEnrollment != null && groupEnrollment.length > 0) {
                List<String> lstGroups = new ArrayList<String>();
                for (String addGroup : groupEnrollment) {
                    lstGroups.add(addGroup);
                    for (int i = 0; i < selectedGroups.size(); i++) {
                        GroupSelection g = selectedGroups.get(i);
                        if (addGroup.equals(g.getGroup().getId())) {
                            g.setIsSelected(true);
                        }
                    }
                }
                map.put("groupEnrollment", groupEnrollment);
                userList= apiTool.getGroupsUsers(lstGroups);
            }          
            
            if (selectedGroups != null) 
            {
                map.put("groupList", selectedGroups);    
            }                       
        }
        
        
        /* keep presentor */
        String presentor = request.getParameter("presentor");
        if(presentor != null)
        {
            map.put("presentor", apiTool.getSakaiUserInfo(presentor));
            map.put("presentorID", presentor);
        }
        
        
        /* We remove animators that have already been picked from the groups*/
        String[] hiddenAnimators = request.getParameterValues("hiddenAnimators");
        if(hiddenAnimators != null)
        { 
            List<User> tempAnimators = new ArrayList<User>();
            List<String> hiddenAnimatorsList = Arrays.asList(hiddenAnimators);
            for (String animator : hiddenAnimatorsList) {
                tempAnimators.add(apiTool.getSakaiUserInfo(animator));
            }
            List<User> usersToKeep = new ArrayList<User>();
            List<User> animatorsToKeep = new ArrayList<User>();
            for (User ulist : userList){
                if(tempAnimators.indexOf(ulist) == -1){
                    usersToKeep.add(ulist);
                }else{
                    animatorsToKeep.add(ulist);
                }
            }
            map.put("tempAnimators", animatorsToKeep);
            map.put("userList", usersToKeep);
        }
        else
        {
            map.put("userList", userList);
            map.put("tempAnimators", null);
        }
        
        
    }


}
