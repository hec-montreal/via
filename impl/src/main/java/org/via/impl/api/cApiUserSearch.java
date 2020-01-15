package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;


@XStreamAlias("cApiUserSearch")
public class cApiUserSearch extends cBaseApi
{
    private String Login = "";
    private String LastName = "";
    private String FirstName ="";
    private String Email = "";
    private String UserType = "";
    private String UniqueID = "";
    
    private String nbrResults = "";
    private List<Match> Search = new ArrayList<Match>();
    
    public cApiUserSearch(String apiid, String cieid) 
    {
        super(cieid, apiid);
    }

    private cApiUserSearch()
    {}

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setUserType(String UserType) {
        this.UserType = UserType;
    }

    public void setUniqueID(String UniqueID) {
        this.UniqueID = UniqueID;
    }

    public List<Match> getSearch() {
        return Search;
    }

}
