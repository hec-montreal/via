package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

import org.via.impl.Profil;

@XStreamAlias("cApiListProfils")
public class cApiListProfils extends cBaseApi
{
    private List<Profil> ProfilList = new ArrayList<Profil>();
    
    public cApiListProfils(String cieid, String apiid) 
    {
        super(cieid, apiid);
    }
    
    public cApiListProfils()
    {}

    public List<Profil> getProfilList() {
        return ProfilList;
    }
}
