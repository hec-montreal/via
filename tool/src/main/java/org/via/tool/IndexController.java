package org.via.tool;

import org.sakaiproject.site.api.*;
import org.springframework.web.servlet.*;
import org.via.*;

import javax.servlet.http.*;
import java.util.*;


public class IndexController extends BaseController {

    /**
     * Controller for Via activity list (main page)
     *
     */

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);

        String groupFilter = "";
        HttpSession session = request.getSession();
        session.setAttribute("new", "new");
        session.setAttribute("id", null);

        try {

            if (request.getParameter("group") != null) {
                groupFilter = request.getParameter("group");
            }

            if (request.getParameter("actions") != null) {
                String action = request.getParameter("actions");
                String id = request.getParameter("id");

                session.setAttribute("id", id);
                session.removeAttribute("new");

                if (action.equals("1")) {
                    response.sendRedirect("activity_new.htm?modify=1");
                } else if (action.equals("2")) {
                    response.sendRedirect("activity_delete.htm");
                }
            }

            IcUsers user = apiTool.getCurrentUser();
            List<Group> userGroup = apiTool.getUserGroupList(null);

            map.put("canEditActivityOfSite", apiTool.canEditActivityOfSite());
            map.put("userCanCreate",apiTool.canCreateActivity(user.getSakaiID()));
            userDao.setPermission (user.getPermissions());
            userDao.setSakaiSiteID(apiTool.getCurrentSiteId());
            userDao.setSakaiID(user.getSakaiID());

            Map<IcActivity, Boolean> treeMapActivities = new TreeMap<IcActivity, Boolean>(
                    new Comparator<IcActivity>() {

                        @Override
                        public int compare(IcActivity o1, IcActivity o2) {
                            if (o2.getDateBegin() == null || o1.getDateBegin() == null || o1.getDateBegin().equals(o2.getDateBegin()))
                                return o2.getTitle().compareTo(o1.getTitle());

                            return o2.getDateBegin().compareTo(o1.getDateBegin());
                        }

                    });
            treeMapActivities.putAll( userDao.getActivities(groupFilter, apiTool,user));
            map.put("activityList", treeMapActivities);
            map.put("groupList", userGroup);
            map.put("groupSelected", groupFilter);
            map.put("now", new Date());

        } catch (Exception e) {

            map.put("error", "Error_catch");
            map.put("etext", getErrorMessage(e));
        }

        return new ModelAndView("index", map);
    }

}
