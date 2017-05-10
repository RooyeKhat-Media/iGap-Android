/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.interfaces;

public interface OnChangeUserPhotoListener {
    /**
     * set null , imagePath . when user don't have image .
     */

    void onChangePhoto(String imagePath);

    /**
     * use this callback when user don't have avatar and after changed
     * nickname client have new initials and color for set instead of avatar
     */
    void onChangeInitials(String initials, String color);
}
