/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.realm;

import com.iGap.module.structs.StructChannelExtra;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmChannelExtraRealmProxy;
import io.realm.RealmObject;
import org.parceler.Parcel;

@Parcel(implementations = {RealmChannelExtraRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmChannelExtra.class}) public class RealmChannelExtra extends RealmObject {

    private long messageId;
    private String signature;
    private String viewsLabel;
    private String thumbsUp;
    private String thumbsDown;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getViewsLabel() {
        return viewsLabel;
    }

    public void setViewsLabel(String viewsLabel) {
        this.viewsLabel = viewsLabel;
    }

    public String getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(String thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getThumbsDown() {
        return thumbsDown;
    }

    public void setThumbsDown(String thumbsDown) {
        this.thumbsDown = thumbsDown;
    }


    public static RealmChannelExtra convert(Realm realm, StructChannelExtra structChannelExtra) {
        RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
        realmChannelExtra.setMessageId(structChannelExtra.messageId);
        realmChannelExtra.setSignature(structChannelExtra.signature);
        realmChannelExtra.setThumbsUp(structChannelExtra.thumbsUp);
        realmChannelExtra.setThumbsDown(structChannelExtra.thumbsDown);
        realmChannelExtra.setViewsLabel(structChannelExtra.viewsLabel);
        return realmChannelExtra;
    }

    /**
     * get latest count for vote and increase it
     *
     * @param reaction Up or Down
     */
    public void setVote(ProtoGlobal.RoomMessageReaction reaction, String voteCount) {
        if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
            setThumbsUp(voteCount);
        } else if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
            setThumbsDown(voteCount);
        }
    }
}
