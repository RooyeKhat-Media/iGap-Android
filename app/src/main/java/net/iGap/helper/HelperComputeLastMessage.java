/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/
package net.iGap.helper;

import android.content.res.Resources;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class HelperComputeLastMessage {

    /**
     * if last message was deleted , compute latest message that
     * exist in room and not deleted
     */

    public static String computeLastMessage(final long roomId, Resources resources, ProtoGlobal.Room.Type roomType, RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();
        String lastMessage = "";
        RealmResults<RealmRoomMessage> realmList =
            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        for (RealmRoomMessage realmRoomMessage : realmList) {
            if (realmRoomMessage != null && !realmRoomMessage.isDeleted()) {
                lastMessage = AppUtils.rightLastMessage(roomId, resources, roomType, realmRoomMessage, attachment);
            }
        }
        realm.close();
        return lastMessage;
    }
    /*public static void computeLastMessage(final long roomId) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmRoomMessage> realmList = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                for (RealmRoomMessage realmRoomMessage : realmList) {
                    if (realmRoom != null && realmRoomMessage != null && !realmRoomMessage.isDeleted()) {
                        realmRoom.setLastMessage(realmRoomMessage);
                        realmRoom.setUpdatedTime(realmRoomMessage.getUpdateOrCreateTime() / 1000);
                    }
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                realm.close();
            }
        });
    }*/
}
