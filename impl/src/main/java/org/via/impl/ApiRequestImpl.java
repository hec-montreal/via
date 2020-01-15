package org.via.impl;

import java.io.OutputStreamWriter;


import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import org.via.impl.api.BaseXmlObj;
import org.via.impl.api.XmlReader;
import org.via.impl.api.cApiActivity;
import org.via.impl.api.cApiGetUserToken;
import org.via.impl.api.cApiListPlayback;
import org.via.impl.api.cApiListProfils;
import org.via.impl.api.cApiUserActivity_AddUser;
import org.via.impl.api.cApiUserActivity_RemoveUser;
import org.via.impl.api.cApiUserSearch;
import org.via.impl.api.cApiUsers;
import org.via.impl.api.cApiUsersListActivityGet;
import org.via.impl.api.cApiUsersSSO;
import org.via.impl.api.cApiActivityDuplicate;
import org.via.impl.api.cApiRecordDownload;
import org.via.impl.api.cApiGetLatestExports;
import org.via.enums.eRedirectType;


public class ApiRequestImpl {
    
    private static final Logger log = Logger.getLogger(ApiRequestImpl.class);

    private String ApiUrl = "";
    private String ApiID = "";
    private String CieID = "";
    private String AdminID = "";

    /**
     * Create a new object of ApiRequest
     *
     * @param  uri the url of the api ex : http://beta.sviesolutions.com/
     */
    public ApiRequestImpl(String uri) 
    {
        super();

        if (uri.charAt(uri.length() - 1) != '/')
            uri += "/";

        ApiUrl = uri + "application/viaapi.asmx/";
    }
    
    public ApiRequestImpl(String uri, String apiid, String cieid, String adminID) 
    {
        super();

        if (uri.charAt(uri.length() - 1) != '/')
            uri += "/";

        ApiUrl = uri + "application/viaapi.asmx/";
        ApiID = apiid;
        CieID = cieid;
        AdminID = adminID;
    }

    public BaseXmlObj SendRequest(String requestUrl, String DataXml, Class c) 
    {

        //System.out.println("data Send = " + DataXml);
        String rep = "";
        try 
        {
            
            
            URL url = new URL(ApiUrl + requestUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF8");
            
            wr.write(DataXml);
            wr.flush();
            
            XmlReader resp = new XmlReader(conn.getInputStream(),"Envelope");  
            
            rep = resp.GetOuterXmlNode("Body/" + c.getSimpleName());     
            
            
            BaseXmlObj obj = (BaseXmlObj) BaseXmlObj.FromStringXMl(rep, c);
            
            
            resp.Clear();
            resp = null;

            if(obj.isSuccess() || requestUrl.equals("RecordDownload") || obj.GetDetail().equals("_CANNOT_CHANGE_ISNEWVIA"))
            {
              return obj;
            }
            else
            {   
                //Don't show the error if it's a UserCreate or UserEdit[When we need to increment the SiteID to the Login, we will get an error on the first call.]
                if(!(requestUrl.equals("UserCreate") && obj.GetDetail().equals("LOGIN_USED")) && !(requestUrl.equals("UserEdit") && obj.GetDetail().equals("CREATE_USER_ERROR_OR_EXISTS")))
                    log.error("Error on call API: " + requestUrl + "\n" + "data: " + DataXml + "\n" + "ResultState: " + obj.GetDetail());
                return null;
            }
        }
        catch(ConnectException ex)
        {
            //Retry to connect.
            log.error("Connection timed out. Retrying request.");
            return SendRequest(requestUrl, DataXml, c, true);
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
            return null;
        }
    }    
    
    //Overload for when we retry to connect to the API.
    public BaseXmlObj SendRequest(String requestUrl, String DataXml, Class c, Boolean isRetry) 
    {

        //System.out.println("data Send = " + DataXml);
        String rep = "";
        try 
        {
            
            
            URL url = new URL(ApiUrl + requestUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF8");
            
            wr.write(DataXml);
            wr.flush();
            
            XmlReader resp = new XmlReader(conn.getInputStream(),"Envelope");  
            
            rep = resp.GetOuterXmlNode("Body/" + c.getSimpleName());     
            
            
            BaseXmlObj obj = (BaseXmlObj) BaseXmlObj.FromStringXMl(rep, c);
            
            
            resp.Clear();
            resp = null;
            
            if(obj.isSuccess() || requestUrl.equals("RecordDownload"))
            {
              return obj;
            }
            else
            {   
                //Don't show the error if it's a UserCreate or UserEdit[When we need to increment the SiteID to the Login, we will get an error on the first call.]
                if(!(requestUrl.equals("UserCreate") && obj.GetDetail().equals("LOGIN_USED")) && !(requestUrl.equals("UserEdit") && obj.GetDetail().equals("CREATE_USER_ERROR_OR_EXISTS")))
                    log.error("Error on call API: " + requestUrl + "\n" + "data: " + DataXml + "\n" + "ResultState: " + obj.GetDetail());
                return null;
            }
        }
        catch(ConnectException ex)
        {
            log.error("Connection timed out again. Could not recconect to the API.");
            ex.printStackTrace();
            return null;
        }
        catch (Exception ex) 
        {
            ex.printStackTrace();
            return null;
        }
    }    
    
    public String SendCreateTokenSSO(String userID, String activityID,String playbackID,eRedirectType type, String forcedAccess, String forcedEditRights, String cieID, String apiID)
    {
        String xml = new cApiGetUserToken(userID, type, activityID, playbackID, forcedAccess, forcedEditRights, cieID, apiID).getXML(false);
        BaseXmlObj rep = SendRequest("UserGetSSOToken", FormatRequest(xml), cApiUsersSSO.class);
        return ((cApiUsersSSO)rep).getTokenURL();
    }
    
    public cApiUsers SendCreateUser(cApiUsers user)
    {
        String xml = user.getXML(false);
        BaseXmlObj rep = SendRequest("UserCreate", FormatRequest(xml), cApiUsers.class);
        
        return (cApiUsers)rep;
    }
    
    public cApiUsers SendEditUser(cApiUsers user)
    {
        String xml = user.getXML(false);
        BaseXmlObj rep = SendRequest("UserEdit", FormatRequest(xml), cApiUsers.class);
        
        return (cApiUsers)rep;
    }
    
    public cApiActivity SendCreateActivity(cApiActivity activity)
    {
        String xml = activity.getXML(false);  
        BaseXmlObj rep = SendRequest("ActivityCreate", FormatRequest(xml), cApiActivity.class);
        return (cApiActivity)rep;
    }
    
    public cApiActivity SendEditActivity(cApiActivity activity)
    {
        activity.ClearResultXML();
        String xml = activity.getXML(false);  
        BaseXmlObj rep = SendRequest("ActivityEdit", FormatRequest(xml), cApiActivity.class);
        return (cApiActivity)rep;
    }
    
    public cApiUsersListActivityGet getActivityUserList(String activityID, String cieID, String apiID)
    {
        String xml = new cApiUsersListActivityGet(activityID, cieID, apiID).getXML(false);
        BaseXmlObj rep = SendRequest("GetUsersListActivity", FormatRequest(xml), cApiUsersListActivityGet.class);
        return (cApiUsersListActivityGet)rep;
    }
    
    public cApiListProfils getProfilList(String cieID, String apiID)
    {
        try
        {
            String xml = new cApiListProfils(cieID, apiID).getXML(false);
            BaseXmlObj rep = SendRequest("ListProfils", FormatRequest(xml), cApiListProfils.class);
            return (cApiListProfils)rep;
        }
        catch(Exception e)
        {
            log.error(e.getMessage());
            return null;
        }
    }    
    
    public cApiListPlayback getPlaybackList(String activityID, String cieID, String apiID)
    {
        String xml = new cApiListPlayback(activityID, cieID, apiID).getXML(false);
        BaseXmlObj rep = SendRequest("ListPlayback", FormatRequest(xml), cApiListPlayback.class);
        return (cApiListPlayback)rep;
    }    
    
    public cApiGetLatestExports getLatestExports(String dateFrom, String cieID, String apiID)
    {
        String xml = new cApiGetLatestExports(dateFrom, cieID, apiID).getXML(false);
        BaseXmlObj rep = SendRequest("GetLatestExports", FormatRequest(xml), cApiGetLatestExports.class);
        return (cApiGetLatestExports)rep;
    }
    
    public cApiUserSearch getUserSearchResult(cApiUserSearch apiUS)
    {
        String xml = apiUS.getXML(false);
        BaseXmlObj rep = SendRequest("UserSearch", FormatRequest(xml), cApiUserSearch.class);
        return (cApiUserSearch)rep;
    }
    
    
    public boolean SendEditPlayback(cApiListPlayback alp)
    {
        alp.ClearResultXML();
        String xml = alp.getXML(false).replace("__", "_").replace("<BreackOutPlaybackList/>", "");
        BaseXmlObj rep = SendRequest("EditPlayback", FormatRequest(xml), cApiListPlayback.class);
        //return rep.isSuccess();
        return true;
    }
    
    public boolean SendEditUserActivity(cApiUserActivity_AddUser aua)
    {
        String xml = aua.getXML(false).replace("__", "_");
        BaseXmlObj rep = SendRequest("EditUserActivity", FormatRequest(xml), cApiUserActivity_AddUser.class);
        return rep.isSuccess();
    }
    
    public boolean SendAddUserActivity(cApiUserActivity_AddUser aua)
    {
        String xml = aua.getXML(false).replace("__","_");
        BaseXmlObj rep = SendRequest("AddUserActivity", FormatRequest(xml), cApiUserActivity_AddUser.class);
        return rep.isSuccess();
    }
    
    public cApiUserActivity_RemoveUser SendRemoveUserActivity(String userID, String activityID, String cieID, String apiID)
    {
        String xml = new cApiUserActivity_RemoveUser(userID, activityID, cieID, apiID).getXML(false).replace("__","_");
        BaseXmlObj rep = SendRequest("RemoveUser", FormatRequest(xml), cApiUserActivity_RemoveUser.class);
        return (cApiUserActivity_RemoveUser)rep;
    }
    
    public cApiActivityDuplicate SendActivityDuplicate(cApiActivityDuplicate aad)
    {
        String xml = aad.getXML(false).replace("__", "_");
        BaseXmlObj rep = SendRequest("ActivityDuplicate", FormatRequest(xml), cApiActivityDuplicate.class);
        return (cApiActivityDuplicate)rep;
    }    
    
    public String SendRecordDownload(String userID, String playbackID, String recordType)
    {
        String xml = new cApiRecordDownload(AdminID, recordType, playbackID, CieID, ApiID).getXML(false).replace("__","_");
        BaseXmlObj rep = SendRequest("RecordDownload", FormatRequest(xml), cApiRecordDownload.class);
        return ((cApiRecordDownload)rep).getDownloadToken();
    }

    public static String FormatRequest(String Myrequest) {
        String rq =
            "<?xml version= \"1.0\" encoding= \"utf-8\"?>" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchemainstance\">" +
            "<soap:Body>" + Myrequest + "</soap:Body>" + "</soap:Envelope> ";

        return rq;
    }

    public String getApiID() {
        return ApiID;
    }

    public void setApiID(String ApiID) {
        this.ApiID = ApiID;
    }

    public String getCieID() {
        return CieID;
    }

    public void setCieID(String CieID) {
        this.CieID = CieID;
    }

    public String getAdminID() {
        return AdminID;
    }
}
