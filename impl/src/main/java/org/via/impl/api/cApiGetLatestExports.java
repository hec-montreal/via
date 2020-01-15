package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("cApiGetLatestExports")
public class cApiGetLatestExports  extends cBaseApi
{
    private String DateFrom = "";
    private List<Export> ExportList = new ArrayList<Export>();
    
    public cApiGetLatestExports(String dateFrom, String cieid, String apiid) 
    {
        super(cieid, apiid);
        this.DateFrom = dateFrom;
    }
    
    public cApiGetLatestExports()
    {}
    
    public List<Export> getExportList()
    {
        return ExportList;    
    }
}
