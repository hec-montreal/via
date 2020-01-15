package org.via.impl.api;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import com.thoughtworks.xstream.mapper.CannotResolveClassException;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import java.io.StringWriter;

import org.apache.log4j.Logger;

import org.via.ActivityUser;
import org.via.impl.BreakOutPlayback;
import org.via.impl.Playback;
import org.via.impl.Profil;


public class BaseXmlObj {    
    private static final Logger log = Logger.getLogger(BaseXmlObj.class);
    
    @XStreamAlias("Result")
    private ResultXmlObj Result = new ResultXmlObj();
    
    public BaseXmlObj() {
        super();
    }
  
    public void SetSuccess(){
        Result.SetSuccess();
    }
    
    public void SetError(String Message){
        Result.SetError(Message);
    }
    
    public boolean isSuccess(){
        return Result.isSuccess();
    }
    
    public void ClearResultXML()
    {
        Result.ClearResultXML();    
    }
    
    public String GetDetail(){
        return Result.GetDetail();
    }
     
    public String getXML(){
       return getXML(true);
    }
    public String getXML(boolean withHeader){
        StringBuilder str = new StringBuilder("");
        if(withHeader)
            str.append("<?xml version= \"1.0\" encoding= \"utf-8\"?>");
        StringWriter sw = new StringWriter();        
        XStream xstream = new XStream(new DomDriver("UTF-8"));
        xstream.processAnnotations(this.getClass());
        xstream.marshal(this,new CompactWriter(sw));
        //xstream.marshal(this,new PrettyPrintWriter(sw));
   
        str.append(sw.toString());
        //str.append(xstream.toXML(this));
        xstream = null;
        sw = null;
        return str.toString();
    }
    
    
    public static Object FromStringXMl(String xml,Class c)
    {
        XStream xstream = new XStream(new DomDriver("UTF-8")) {
                    protected MapperWrapper wrapMapper(MapperWrapper next) {
                        return new MapperWrapper(next) {
                            public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                                try {
                                    return definedIn != Object.class || realClass(fieldName) != null;
                                } catch(CannotResolveClassException cnrce) {
                                    return false;
                                }
                            }
                        };
                    }
                };
        
         xstream.alias(c.getSimpleName(), c);

        //Custom attributes.
        if(c == cApiUsersListActivityGet.class)
        {
            xstream.alias(ActivityUser.class.getSimpleName(), ActivityUser.class);
            xstream.processAnnotations(ActivityUser.class);
        }
        else if(c == cApiListProfils.class)
        {
            xstream.alias(Profil.class.getSimpleName(), Profil.class);
            xstream.processAnnotations(Profil.class);
        }
        else if (c == cApiListPlayback.class)
        {
            xstream.alias(Playback.class.getSimpleName(), Playback.class);
            xstream.processAnnotations(Playback.class);
            xstream.alias(BreakOutPlayback.class.getSimpleName(), BreakOutPlayback.class);
            xstream.processAnnotations(BreakOutPlayback.class);
        }
        else if (c == cApiUserSearch.class)
        {
            xstream.alias(Match.class.getSimpleName(), Match.class);
            xstream.processAnnotations(Match.class);
        }
        else if (c == cApiGetLatestExports.class) 
        {
            xstream.alias(Export.class.getSimpleName(), Export.class);
            xstream.processAnnotations(Export.class);   
        }
        
        Object obj = xstream.fromXML(xml);
        return obj;
    }
}