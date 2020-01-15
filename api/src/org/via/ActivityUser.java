package org.via;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;


@XStreamAlias("ActivityUser")
public class ActivityUser {
    @XStreamAsAttribute
    private String UserID = "";
    
    @XStreamOmitField
    private String SakaiUserID = "";
    
    @XStreamOmitField
    private String SakaiUserName = "";
    
    @XStreamAsAttribute
    private String Email = "";
    
    @XStreamAsAttribute
    private String LastName = "";
    @XStreamAsAttribute
    private String FirstName = "";
    
    @XStreamAsAttribute
    private String ConfirmationStatus = "";
    @XStreamAsAttribute
    private String ParticipantType = "";
    @XStreamAsAttribute
    private String UniqueID = "";

    public ActivityUser() 
    {
        super();
    }
    
    public ActivityUser(String userID, String sakaiUserID, String sakaiUserName, String email, String lastName, String firstName, String participantType)
    {
        this.UserID = userID;
        this.SakaiUserID = sakaiUserID;
        this.SakaiUserName = sakaiUserName;
        this.Email = email;
        this.LastName = lastName;
        this.FirstName = firstName;
        this.ParticipantType = participantType;
    }

    public String getUserID() {
        return UserID;
    }
    
    public String getSakaiUserID() {
        return SakaiUserID;
    }    
    
    public String getSakaiUserName() {
        return SakaiUserName;
    }

    public String getEmail() {
        return Email;
    }

    public String getLastName() {
        return LastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getConfirmationStatus() {
        return ConfirmationStatus;
    }

    public String getParticipantType() {
        return ParticipantType;
    }

    public void setParticipantType(String ParticipantType) {
        this.ParticipantType = ParticipantType;
    }

    public String getUniqueID() {
        return UniqueID;
    }
}
