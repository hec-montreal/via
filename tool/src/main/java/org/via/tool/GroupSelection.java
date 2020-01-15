package org.via.tool;
import org.sakaiproject.site.api.Group;

public class GroupSelection {
    private Group group;
    private Boolean isSelected = false;
    
    public GroupSelection(Group g, Boolean selected) {
        super();
        group = g;
        isSelected = selected;    
    }
    public Group getGroup(){
        return group;    
    }
    public Boolean getIsSelected(){
        return isSelected;
    }
    public void setIsSelected(Boolean value){
        isSelected = value;    
    }
}
