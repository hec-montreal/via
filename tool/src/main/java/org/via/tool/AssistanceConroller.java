package org.via.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import org.via.IcActivity;
import org.via.IcUsers;


public class AssistanceConroller extends BaseController {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);


        IcUsers user = apiTool.getCurrentUser();
        String type = request.getParameter("type");

        if (type.equals("config")) {

            try {
                response.sendRedirect(user.getConfigURL());

            } catch (Exception e) {
                map.put("error", "Error_catch");
                map.put("etext", getErrorMessage(e));
            }

        } else {

            try {
                response.sendRedirect(user.getSupportURL());
            } catch (Exception e) {
                map.put("error", "Error_catch");
                map.put("etext", getErrorMessage(e));
            }
        }

        return new ModelAndView("assistance", map);
    }

}
