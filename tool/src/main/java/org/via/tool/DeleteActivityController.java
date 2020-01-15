package org.via.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import org.via.IcActivity;
import org.via.IcUsers;


public class DeleteActivityController extends BaseController {

    /**
     * Controller to delete Via activities
     *
     */

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);

        if (request.getParameter("action") != null) {
            
            try {
                HttpSession session = request.getSession();
                String id = (String) session.getAttribute("id");

                IcUsers user = apiTool.getCurrentUser();
                IcActivity activity = apiTool.getActivity(id, user.getSakaiID());
                activityDao.Delete(user.getSakaiID(),apiTool,activity);
                //activity.Delete(user.getSakaiID());
                session.removeAttribute("id");
                response.sendRedirect("index.htm");

            } catch (Exception e) {
                map.put("error", "Error_catch");
                map.put("etext", getErrorMessage(e));
            }
        }

        return new ModelAndView("activity_delete", map);
    }

}

