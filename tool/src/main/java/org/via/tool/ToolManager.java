package org.via.tool;

import org.sakaiproject.util.*;

public class ToolManager {
    
    private ResourceLoader rb;
    
    public ToolManager() {
        rb = new ResourceLoader("via");
    }
    
    public String getText(String value){
        return rb.getString(value);
    }

    public String getLocale (){
        return rb.getLocale().toString();
    }
}
