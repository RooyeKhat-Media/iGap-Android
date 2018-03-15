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
 * call this interface after read from RealmAvatar
 */

public interface OnAvatarGet {
    /**
     * call this method if avatarPath is exist
     *
     * @param avatarPath path for show image from that
     */
    void onAvatarGet(String avatarPath, long roomId);

    /**
     * call this method if avatarPath not exist
     *
     * @param initials letters for show in imageView
     * @param color    color imageView background
     */
    void onShowInitials(String initials, String color);
}
