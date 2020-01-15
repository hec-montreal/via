package org.via.tool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.ModelAndView;

import org.via.IcUsers;

public class RecordDownloadController extends BaseController {


    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.handleRequest(request, response);

        String recordType = request.getParameter("recordType");
        String playbackID = request.getParameter("playback");
        IcUsers user = apiTool.getCurrentUser();
        String URL = apiTool.getRecordDownloadURL(user.getID(), playbackID, recordType);
        response.sendRedirect(URL);

        return new ModelAndView("record_download", map);

    }

}
