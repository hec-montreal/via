package org.via.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;


import org.sakaiproject.site.api.Group;

import org.springframework.web.servlet.ModelAndView;

import org.via.ActivityUser;
import org.via.IcActivity;
import org.via.IcUsers;
import org.via.enums.eActivityType;
import org.via.enums.eParticipantType;


public class ActivityController extends BaseController {

    /**
     * Controller for Via activity details page
     *
     */
    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);

        try {
            HttpSession session = request.getSession();
            String id = request.getParameter("id");
            if (id == null) {
                id = (String) session.getAttribute("id");
            }
            session.invalidate();
            
            IcUsers user = apiTool.getCurrentUser();
            IcActivity activity = apiTool.getActivity(id, user.getSakaiID());
               
            map.put("test",  activity.getActivityState().getValue());
            
            map.put("activity", activity);
            map.put("userList", activityDao.getLstUser(activity)); 

            Date now = new Date();

            map.put("now", now);

            if (activity.getActivityType().equals(eActivityType.Permanente)) {
                map.put("access", "true");
            } else {
               if (activity.getDateEnd().after(now)) {
                    map.put("access", "true");
                } else {
                    map.put("access", "finished");
                }
            }
            
            List<String> groupList = activity.getLstGroup();
            ArrayList<Group> groups = new ArrayList<Group>();
            if(groupList != null){
                for (int i = 0; i < groupList.size(); i++) {
                    groups.add(apiTool.getSakaiGroupInfo(groupList.get(i)));
                }
                
                map.put("groupList", groups);
            }
            map.put("recording", activity.getRecordingMode().getValue());
            map.put("participantType", activity.getParticipantType().getValue());
            map.put("playbackList", activity.getLstPlayback());

            boolean isPresenter = false;
            for (ActivityUser u : activity.getLstUser()) {
                if (u.getUserID().equals(user.getID()) &&
                        u.getParticipantType().equals(String.valueOf(eParticipantType.Presentateur.getValue()))) {
                    isPresenter = true;
                }
            }
            // if isPresenter or user can edit activities in the site
            map.put("userCanEdit", isPresenter || user.getPermissions().getCanEditActivity());
            
        } catch (Exception e) {
           map.put("error", "Error_catch");
           map.put("etext", getErrorMessage(e));
       }

        return new ModelAndView("activity_details", map);

    }

}
