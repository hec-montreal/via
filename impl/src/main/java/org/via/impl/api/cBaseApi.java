package org.via.impl.api;

public class cBaseApi extends BaseXmlObj {
    protected String CieID="";
    protected String ApiID="";
    
    public cBaseApi(String cieid,String apiid) {
        super();
        
        CieID = cieid;
    
        ApiID = apiid;
    }
    
    public cBaseApi()
    {}
}