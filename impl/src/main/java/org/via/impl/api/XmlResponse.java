package org.via.impl.api;

import java.io.InputStream;


public class XmlResponse {
    private XmlReader myXmlResponse = null;
    
   
    
    public XmlResponse(InputStream stream,String mainNode) {
        myXmlResponse = new XmlReader(stream,mainNode);
     }
    
    
    public String ReadNode(String node){
        return myXmlResponse.ReadString(node);
    }
   
    
    
}