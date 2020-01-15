package org.via.impl;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.via.IProfil;

@XStreamAlias("Profil")
public class Profil implements IProfil {
    @XStreamAsAttribute
    String ProfilID = "";
    
    @XStreamAsAttribute
    String ProfilName = "";
    
    public Profil() 
    {
    }
    
    public String getProfilID() {
        return ProfilID;
    }

    public String getProfilName() {
        return ProfilName;
    }
}
