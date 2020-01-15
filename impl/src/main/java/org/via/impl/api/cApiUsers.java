package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.text.Normalizer;

import org.via.impl.apiToolImpl;


@XStreamAlias("cApiUsers")
public class cApiUsers extends cBaseApi{
    private String ID = "";
    
    private String Email = "";
    
    private String Login = "";
    
    private String Password = "";
    
    private String LastName = "";
    private String FirstName = "";
    
    private String UserType = "";
    
    public cApiUsers(String cieid,String apiid) 
    {
        super(cieid, apiid);
    }
    
    private cApiUsers()
    {}

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }
    
    public void setLogin(String Login) {
        this.Login = apiToolImpl.normaliseString(Login);
    }
    
    public void setID(String id) {
        this.ID = id;
    }
    
    public void setToCoordo()
    {
        UserType = "4";    
    }

    public String getID() {
        return ID;
    }

}
