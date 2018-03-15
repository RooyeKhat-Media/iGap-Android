/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import net.iGap.helper.HelperString;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmRoomDraft extends RealmObject {

    private String message;
    private long replyToMessageId;

    public static RealmRoomDraft put(Realm realm, String message, long replyToMessageId) {
        RealmRoomDraft draft = realm.createObject(RealmRoomDraft.class);
        draft.setMessage(message);
        draft.setReplyToMessageId(replyToMessageId);
        return draft;
    }

    public static RealmRoomDraft putOrUpdate(Realm realm, RealmRoomDraft draft, String message, long replyToMessageId) {
        if (draft == null) {
            draft = realm.createObject(RealmRoomDraft.class);
        }
        draft.setMessage(message);
        draft.setReplyToMessageId(replyToMessageId);
        return draft;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        try {
            this.message = message;
        } catch (Exception e) {
            this.message = HelperString.getUtf8String(message);
        }
    }

    public long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }
}
