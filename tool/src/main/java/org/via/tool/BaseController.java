package org.via.tool;

import java.io.FileInputStream;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import org.via.IapiTool;
import org.via.dao.cUserDao;
import org.via.dao.cActivityDao;

public class BaseController implements Controller{
    
    @Setter
    @Getter
    protected IapiTool apiTool = null;
    
    @Setter
    @Getter
    protected cUserDao userDao = null;
    
    @Setter
    @Getter
    protected cActivityDao activityDao = null;
    
    protected Map<String, Object> map = new HashMap<String,Object>();
    
    public BaseController() 
    {
        map.put("ToolManager", new ToolManager());
    }
    
    protected String getErrorMessage(Exception e) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);

        return sw.getBuffer().toString();
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest,
                                      HttpServletResponse httpServletResponse) throws Exception {
        // TODO Implement this method
        return null;
    }
}
