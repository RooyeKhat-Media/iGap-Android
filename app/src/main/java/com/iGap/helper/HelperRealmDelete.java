/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * helper class for delete info from realm
 */

public class HelperRealmDelete {

    /**
     * delete room from realm and also delete all messages
     * from this room and finally delete RealmClientCondition
     */
    public static void deleteRoom(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst().deleteFromRealm();
            }
        });
        realm.close();
        deleteRoomMessages(roomId);
        deleteRoomClientCondition(roomId);
    }

    /**
     * delete messages from realm
     *
     * @param roomId roomId that want to delete messages
     */
    public static void deleteRoomMessages(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmRoomMessage> realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll();
                if (realmRoomMessage != null) {
                    realmRoomMessage.deleteAllFromRealm();
                }
            }
        });
        realm.close();
    }

    /**
     * delete client condition
     *
     * @param roomId roomId that want to delete client condition for that
     */
    public static void deleteRoomClientCondition(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteFromRealm();
                }
            }
        });
        realm.close();
    }
}
