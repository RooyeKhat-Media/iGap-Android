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

import net.iGap.module.SUID;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmOfflineDelete extends RealmObject {

    @PrimaryKey
    private long id;
    private long offlineDelete;
    private boolean both;

    public static RealmOfflineDelete setOfflineDeleted(Realm realm, long messageId, ProtoGlobal.Room.Type roomType, boolean both) {
        if (roomType != ProtoGlobal.Room.Type.CHAT) {
            both = false;
        }
        RealmOfflineDelete realmOfflineDelete = realm.createObject(RealmOfflineDelete.class, SUID.id().get());
        realmOfflineDelete.setOfflineDelete(messageId);
        realmOfflineDelete.setBoth(both);
        return realmOfflineDelete;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOfflineDelete() {
        return offlineDelete;
    }

    public void setOfflineDelete(long offlineDelete) {
        this.offlineDelete = offlineDelete;
    }

    public boolean isBoth() {
        return both;
    }

    public void setBoth(boolean both) {
        this.both = both;
    }
}
