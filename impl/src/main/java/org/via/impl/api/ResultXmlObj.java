package org.via.impl.api;

public class ResultXmlObj {
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_ERROR = "ERROR";
                  
    private String ResultState;
    private String ResultDetail;
    
    public ResultXmlObj() {
        super();
    }
    
    public boolean isSuccess(){
        return (ResultState.equals(STATUS_SUCCESS));
    }
    
    public String GetDetail(){
        return ResultDetail;
    }
    
    public void SetSuccess(){
        ResultState = STATUS_SUCCESS;
        ResultDetail = "";
    }
    
    public void SetError(String message){
        ResultState = STATUS_ERROR;
        this.ResultDetail = message;
    }
    
    public void ClearResultXML()
    {
        ResultState = null;
        ResultDetail = null;
    }
}