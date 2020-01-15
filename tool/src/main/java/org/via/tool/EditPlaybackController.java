package org.via.tool;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import org.via.IcActivity;
import org.via.IcUsers;


public class EditPlaybackController extends BaseController {

    /**
     * Controller for Playback edit
     */

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);

        try {

            String id = request.getParameter("id");
            HttpSession session = request.getSession();
            if (id == null) {
                id = (String) session.getAttribute("id");
            } else {
                session.setAttribute("id", id);
            }
            IcUsers user = apiTool.getCurrentUser();
            IcActivity activity = apiTool.getActivity(id, user.getSakaiID());

            String playback = request.getParameter("playback");
            if (playback == null) {
                playback = (String) session.getAttribute("playback");
            } else {
                session.setAttribute("playback", playback);
            }
            String isPublic = request.getParameter("isPublic");

            if (request.getParameter("save") != null) {

                try {

                    String newTitle = request.getParameter("newTitle");
                    if (newTitle != null) {
                        activity.renamePlayback(playback, newTitle);
                    }

                    response.sendRedirect("activity_details.htm");

                } catch (Exception e) {
                    map.put("error", "Error_catch");
                    map.put("etext", getErrorMessage(e));
                }

            } else if (isPublic != null) {

                try {
                    Boolean newValue = false;
                    if (isPublic.equals("0")) {
                        newValue = true;
                    }

                    activity.editPlaybackPublic(playback, newValue);

                    response.sendRedirect("activity_details.htm");

                } catch (Exception e) {
                    map.put("error", "Error_catch");
                    map.put("etext", getErrorMessage(e));
                }
            }

            map.put("title", request.getParameter("title"));

        } catch (Exception e) {
            map.put("error", "Error_catch");
            map.put("etext", getErrorMessage(e));
        }

        return new ModelAndView("playback_edit", map);

    }

}
