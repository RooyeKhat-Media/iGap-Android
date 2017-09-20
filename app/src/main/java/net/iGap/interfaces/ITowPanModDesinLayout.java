package net.iGap.interfaces;

import net.iGap.activities.ActivityMain;

/**
 * Created by android3 on 9/11/2017.
 */

public interface ITowPanModDesinLayout {

    void onLayout(ActivityMain.chatLayoutMode mode);

    boolean getBackChatVisibility();

    void setBackChatVisibility(boolean visibility);
}
