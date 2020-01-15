package org.via;

import java.util.Date;

public interface IBreakOutPlayback {
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
