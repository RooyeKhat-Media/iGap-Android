/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.module.structs;

public class StructMessageOption {

    /**
     * if is gap we need set message id in {@link net.iGap.realm.RealmRoomMessage#previousMessageId}
     * for detect all message not exist in local database and we have to fetch message from server
     */
    private boolean isGap = false;

    /**
     * if is forward or reply we need create new message with new fake id for avoid from interference
     * forwarded or replied message with main message if exist in another room
     */
    private boolean isForwardOrReply = false;

    /**
     * if is from share media we need set gap if is new message and before not exist in realm
     */
    private boolean isFromShareMedia = false;


    public boolean isGap() {
        return isGap;
    }

    public StructMessageOption setGap() {
        isGap = true;
        return this;
    }

    public boolean isForwardOrReply() {
        return isForwardOrReply;
    }

    public StructMessageOption setForwardOrReply() {
        isForwardOrReply = true;
        return this;
    }

    public boolean isFromShareMedia() {
        return isFromShareMedia;
    }

    public StructMessageOption setFromShareMedia() {
        isFromShareMedia = true;
        return this;
    }
}
