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

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import net.iGap.module.SUID;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmOfflineEdited extends RealmObject {

    @PrimaryKey private long id;

    private long messageId;
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static RealmOfflineEdited put(Realm realm, long messageId, String message) {
        RealmOfflineEdited realmOfflineEdited = realm.createObject(RealmOfflineEdited.class, SUID.id().get());
        realmOfflineEdited.setMessageId(messageId);
        realmOfflineEdited.setMessage(message);
        realmOfflineEdited = realm.copyToRealm(realmOfflineEdited);

        return realmOfflineEdited;
    }
}
