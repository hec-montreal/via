package org.via.tool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import org.via.cUserPermissions;


public class PermissionsController extends BaseController {

    /**
     * Controller for Via permissions page
     *
     */

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);
    /*
        List<cUserPermissions> lstPermissions = apiTool.getListPermissions();

        try {
            map.put("listPermissions", lstPermissions);
        } catch (Exception e) {
            map.put("error", e.toString());
        }
        
        map.put("adminType", apiTool.getViaAdminType());

        if (request.getParameter("submit") != null) {

            try {

                for (int i = 0; i < lstPermissions.size(); i++) {
                    String type = lstPermissions.get(i).getUserType();

                    boolean canCreate = false;
                    if (request.getParameter("c_" + type) != null) {
                        canCreate = request.getParameter("c_" + type).toLowerCase().equals("on");
                    }
                    lstPermissions.get(i).setCanCreateActivity(canCreate);

                    boolean canEdit = false;
                    if (request.getParameter("e_" + type) != null) {
                        canEdit = request.getParameter("e_" + type).equals("on");
                    }
                    lstPermissions.get(i).setCanEditActivity(canEdit);

                    boolean canSee = false;
                    if (request.getParameter("s_" + type) != null) {
                        canSee = request.getParameter("s_" + type).equals("on");
                    }
                    lstPermissions.get(i).setCanSeeAllActivities(canSee);
                    
                    boolean canEditPerm = false;
                    if (request.getParameter("p_" + type) != null) {
                        canEditPerm = request.getParameter("p_" + type).equals("on");
                    }
                    lstPermissions.get(i).setCanEditPermissions(canEditPerm);
                }

                apiTool.savePermissions(lstPermissions);
                response.sendRedirect("index.htm");
                
            } catch (Exception e) {
                map.put("error", getErrorMessage(e));
            }

        } else if (request.getParameter("cancel") != null) {

            response.sendRedirect("index.htm");
        }
        */

        return new ModelAndView("permissions", map);

    }


}
