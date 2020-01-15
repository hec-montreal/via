package org.via;

import java.util.Date;
import java.util.List;

public interface IPlayback {
    List<? extends IBreakOutPlayback> getBreackOutPlaybackList();

    String getTitle();

    void setTitle(String Title);

    String getDuration();

    Date getCreationDate();

    String getIsPublic();

    void setIsPublic(String IsPublic);

    String getPlaybackID();    
    
    String getHasFullVideoRecord();

    String getHasMobileVideoRecord();

    String getHasAudioRecord();
}
