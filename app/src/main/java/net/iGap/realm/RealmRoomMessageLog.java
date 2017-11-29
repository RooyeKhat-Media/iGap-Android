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
import io.realm.RealmRoomMessageLogRealmProxy;
import io.realm.annotations.PrimaryKey;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoGlobal;
import org.parceler.Parcel;

@Parcel(implementations = {RealmRoomMessageLogRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmRoomMessageLog.class}) public class RealmRoomMessageLog extends RealmObject {
    private String type;
    @PrimaryKey private long id;

    public static RealmRoomMessageLog put(final ProtoGlobal.RoomMessageLog input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageLog messageLocation = realm.createObject(RealmRoomMessageLog.class, SUID.id().get());
        messageLocation.setType(input.getType());
        realm.close();

        return messageLocation;
    }

    public ProtoGlobal.RoomMessageLog.Type getType() {
        return ProtoGlobal.RoomMessageLog.Type.valueOf(type);
    }

    public void setType(ProtoGlobal.RoomMessageLog.Type type) {
        this.type = type.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
