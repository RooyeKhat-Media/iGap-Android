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
public class RealmOfflineListen extends RealmObject {

    @PrimaryKey private long id;
    private long offlineListen;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOfflineListen() {
        return offlineListen;
    }

    public void setOfflineListen(long offlineListen) {
        this.offlineListen = offlineListen;
    }

    public static RealmOfflineListen put(Realm realm, long messageId) {
        RealmOfflineListen realmOfflineListen = realm.createObject(RealmOfflineListen.class, SUID.id().get());
        realmOfflineListen.setOfflineListen(messageId);
        return realmOfflineListen;
    }
}
