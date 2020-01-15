package org.via.impl;

import java.sql.Connection;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlReaderFinishedException;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.user.api.User;

import org.sakaiproject.user.api.UserDirectoryService;

import org.via.IapiTool;
import org.via.IcActivity;
import org.via.cUserPermissions;

import org.via.IcUsers;
import org.via.enums.*;


public class cUser implements IcUsers 
{
    private static final Logger log = Logger.getLogger(cUser.class);

    private String ID = "";
    private String SakaiID = "";
    private String Email = "";
    
    private String LastName = "";
    private String FirstName = "";
    private String SakaiUserName = "";
    
    private String SakaiSiteID = "";
        
    private cUserPermissions permissions;
    private UserDirectoryService Uds;
    
    private String Type = "";
    
    ApiRequestImpl Api;
    
    @Getter
    @Setter
    private static SqlService sqlservice;
    
    
    private static String query;
    private static Object[] fields;
    
    public cUser(User user, String sakaiSiteID, ApiRequestImpl api, SecurityService securityService, String siteRef, UserDirectoryService uds) 
    {
        super();
        this.Api = api;
        this.SakaiSiteID = sakaiSiteID;
        this.Uds = uds;
        
        loadUserInfo(user, securityService, siteRef);
    }
    
    private void loadUserInfo(User user, SecurityService securityService, String siteRef)
    {
        try 
        {
           
            this.ID =cActivityDaoImpl.getVIAUserID(user, this.SakaiSiteID );
            
            this.SakaiID = user.getId();
            this.SakaiUserName = user.getEid();
            
            this.Email = user.getEmail();
            
            this.FirstName = user.getFirstName();  
            
            this.LastName = user.getLastName();
            
            this.Type = user.getType();
            
            this.permissions = new cUserPermissions(user, securityService, siteRef);
        } 
        catch (Exception e) 
        {
            log.error("cUser : " + e.getMessage());
            e.printStackTrace();
        }
    }    

    
    public String getSupportURL()
    {
        return Api.SendCreateTokenSSO(ID, "", "", eRedirectType.PageSupport_mn, "","", Api.getCieID(), Api.getApiID());
    }
    
    public String getConfigURL()
    {
        return Api.SendCreateTokenSSO(ID, "", "", eRedirectType.PageSetupWizard_nm, "","", Api.getCieID(), Api.getApiID());
    }
    
    public String getID() {
        return ID;
    }
    
    public String getSakaiID() {
        return SakaiID;
    }

    public String getEmail() {
        return Email;
    }

    public String getLastName() {
        return LastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public cUserPermissions getPermissions() {
        return permissions;
    }
    
@Override
    public String getSakaiSiteID() {
        return SakaiSiteID;
    }

}
