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

/**
 * call this interface after delete Avatar from RealmAvatar
 */
public interface OnAvatarDelete {
    /**
     * latest avatarPath after delete an avatar if exist
     */
    void latestAvatarPath(String avatarPath);

    /**
     * return initials and color if after delete avatar not
     * anymore exist avatar
     */
    void showInitials(String initials, String color);
}
