package org.via.tool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.via.IcActivity;
import org.via.IcUsers;

public class AccessActivityController extends BaseController {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);

        String id = request.getParameter("id");
        String playbackID = request.getParameter("playback");
        IcUsers user = apiTool.getCurrentUser();
        IcActivity activity = apiTool.getActivity(id, user.getSakaiID());

        if (playbackID == null) {

            try {
                String URL = activity.getVIAURL(user.getPermissions().getCanCreateActivity());
                response.sendRedirect(URL);

            } catch (Exception e) {
                map.put("error", "Error_VIAURL");
            }

        } else {

            try {

                String URL = activity.getPlaybackURL(playbackID, user.getPermissions().getCanCreateActivity());
                response.sendRedirect(URL);


            } catch (Exception e) {
                map.put("error", "Error_PlaybackURL");
            }

        }

        return new ModelAndView("activity_access", map);

    }

}
