package org.via.impl.api;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Match")
public class Match 
{
    @XStreamAsAttribute
    private String UserID="";

    @XStreamAsAttribute
    private String Login="";

    @XStreamAsAttribute
    private String FirstName="";

    @XStreamAsAttribute
    private String LastName="";

    @XStreamAsAttribute
    private String Email="";

    @XStreamAsAttribute
    private String UserType ="";

    @XStreamAsAttribute
    private String UniqueID="";
    
    @XStreamAsAttribute
    private String Status="";
    
    public Match() 
    {
        super();
    }

    public String getUserID() {
        return UserID;
    }

    public String getLogin() {
        return Login;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getEmail() {
        return Email;
    }
    public String getUserType() {
        return UserType;
    }

    public String getUniqueID() {
        return UniqueID;
    }

    public String getStatus() {
        return Status;
    }

}
