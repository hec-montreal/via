package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("cApiUsersSSO")
public class cApiUsersSSO extends cBaseApi 
{    
    private String TokenURL ="";
  
    public cApiUsersSSO() {
        super("","");
       
    }
    
    public String getTokenURL(){
        return TokenURL;
    }
    
  
}
