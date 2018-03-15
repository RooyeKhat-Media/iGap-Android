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

import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmChatRoom extends RealmObject {

    @PrimaryKey
    private long peer_id;

    private RealmNotificationSetting realmNotificationSetting;

    /**
     * convert ProtoGlobal.ChatRoom to RealmChatRoom
     *
     * @param room ProtoGlobal.ChatRoom
     * @return RealmChatRoom
     */
    public static RealmChatRoom convert(ProtoGlobal.ChatRoom room) {
        Realm realm = Realm.getDefaultInstance();
        RealmChatRoom realmChatRoom = realm.where(RealmChatRoom.class).equalTo(RealmChatRoomFields.PEER_ID, room.getPeer().getId()).findFirst();
        if (realmChatRoom == null) {
            realmChatRoom = realm.createObject(RealmChatRoom.class, room.getPeer().getId());
        }
        realm.close();
        return realmChatRoom;
    }

    public long getPeerId() {
        return peer_id;
    }

    public void setPeerId(long peer_id) {
        this.peer_id = peer_id;
    }

    public RealmNotificationSetting getRealmNotificationSetting() {
        return realmNotificationSetting;
    }

    public void setRealmNotificationSetting(RealmNotificationSetting realmNotificationSetting) {
        this.realmNotificationSetting = realmNotificationSetting;
    }
}
