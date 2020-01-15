package org.via.tool;

import java.io.OutputStreamWriter;

import java.net.URL;
import java.net.URLConnection;

import javax.sql.rowset.spi.XmlReader;

public class ApiRequest {
    
    private String ApiUrl = "";
    /**
     * Create a new object of ApiRequest 
     * 
     * @param  uri the url of the api ex : http://beta.sviesolutions.com/
     */      
    public ApiRequest(String uri) {
          super();
          
         if(uri.charAt(uri.length()-1) != '/')
             uri+="/";
          
          ApiUrl = uri +  "application/viaapi.asmx";
      }
    
    private Object SendRequest(String requestUrl,Class objectName,String DataXml) {
      
        //System.out.println("data Send = " + DataXml);
        String rep = "";
        try{
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(),"UTF8");
            wr.write(DataXml);
            wr.flush();
           
           
           //Response = conn.getInputStream();
           return null;
        }catch (Exception ex) {
          ex.printStackTrace();
          return null;
        }
       
    }
    
    public static String FormatRequest(String Myrequest){
      String rq = "<?xml version= \"1.0\" encoding= \"utf-8\"?>" +
        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchemainstance\">" + 
          "<soap:Body>" + Myrequest +
          "</soap:Body>" +
          "</soap:Envelope> ";
      
      return rq;
    }
}
