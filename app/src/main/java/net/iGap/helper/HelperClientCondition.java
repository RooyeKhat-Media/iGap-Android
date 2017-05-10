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

import android.util.Log;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;
import net.iGap.proto.ProtoClientCondition;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmOfflineDelete;
import net.iGap.realm.RealmOfflineEdited;
import net.iGap.realm.RealmOfflineSeen;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

/**
 * helper client condition for set info to RealmClientCondition
 */
public class HelperClientCondition {

    public static ProtoClientCondition.ClientCondition.Builder computeClientCondition(Long roomid) {

        Realm realm = Realm.getDefaultInstance();
        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        RealmResults<RealmClientCondition> clientConditionList;

        if (roomid != null) {
            clientConditionList = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomid).findAll();
        } else {
            clientConditionList = realm.where(RealmClientCondition.class).findAll();
        }

        for (RealmClientCondition realmClientCondition : clientConditionList) {
            if (realmClientCondition.isManaged()) {
                ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
                room.setRoomId(realmClientCondition.getRoomId());

                long messageVersion = 0;
                long statusVersion = 0;
                List<RealmRoomMessage> allItemsMessageVersion = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId())
                    .findAll()
                    .sort(RealmRoomMessageFields.MESSAGE_VERSION, Sort.DESCENDING);
                for (RealmRoomMessage item : allItemsMessageVersion) {
                    if (item != null) {
                        messageVersion = item.getMessageVersion();
                        break;
                    }
                }
                List<RealmRoomMessage> allItemsStatusVersion = realm.where(RealmRoomMessage.class)
                    .equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId())
                    .findAll()
                    .sort(RealmRoomMessageFields.STATUS_VERSION, Sort.DESCENDING);
                for (RealmRoomMessage item : allItemsStatusVersion) {
                    if (item != null) {
                        statusVersion = item.getStatusVersion();
                        break;
                    }
                }
                room.setMessageVersion(messageVersion);
                room.setStatusVersion(statusVersion);

                room.setDeleteVersion(realmClientCondition.getDeleteVersion());

                for (RealmOfflineDelete offlineDeleted : realmClientCondition.getOfflineDeleted()) {
                    room.addOfflineDeleted(offlineDeleted.getOfflineDelete());
                }

                for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) { // server have problem
                    ProtoClientCondition.ClientCondition.Room.OfflineEdited.Builder offlineEdited = ProtoClientCondition.ClientCondition.Room.OfflineEdited.newBuilder();
                    offlineEdited.setMessageId(realmOfflineEdited.getMessageId());
                    offlineEdited.setMessage(realmOfflineEdited.getMessage());
                    room.addOfflineEdited(offlineEdited);
                }

                for (RealmOfflineSeen offlineSeen : realmClientCondition.getOfflineSeen()) { // DONE
                    room.addOfflineSeen(offlineSeen.getOfflineSeen());
                }

                room.setClearId(realmClientCondition.getClearId()); //DONE

                List<RealmRoomMessage> allItemsAscending =
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                for (RealmRoomMessage item : allItemsAscending) {
                    if (item != null) {
                        room.setCacheStartId(item.getMessageId());
                        break;
                    }
                }

                List<RealmRoomMessage> allItems =
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                for (RealmRoomMessage item : allItems) {
                    if (item != null) {
                        room.setCacheEndId(item.getMessageId());//Done
                        break;
                    }
                }

                if (realmClientCondition.getOfflineMute() != null) {
                    if (realmClientCondition.getOfflineMute().equals(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString())) {
                        room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
                    } else {
                        room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
                    }
                } else {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
                }

                clientCondition.addRooms(room);
                Log.i("CLI", "Condition : " + realmClientCondition);
                clearOffline(realmClientCondition, realm);
            }
        }
        realm.close();

        return clientCondition;
    }

    public static void setMessageAndStatusVersion(Realm realm, long roomId, ProtoGlobal.RoomMessage roomMessage) {
        RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();

        /**
         * if received new message set info to RealmClientCondition
         */
        if (realmClientCondition != null) {
            realmClientCondition.setMessageVersion(roomMessage.getMessageVersion());
            realmClientCondition.setStatusVersion(roomMessage.getStatusVersion());
        }
    }

    private static void clearOffline(final RealmClientCondition realmClientCondition, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                realmClientCondition.setOfflineEdited(new RealmList<RealmOfflineEdited>());
                realmClientCondition.setOfflineDeleted(new RealmList<RealmOfflineDelete>());
                realmClientCondition.setOfflineSeen(new RealmList<RealmOfflineSeen>());
            }
        });
    }

    private static void computeMessageVersion(long roomId, Realm realm) {
        long messageVersion = 0;
        long statusVersion = 0;

        for (RealmRoomMessage roomMessage : realm.where(RealmRoomMessage.class).findAll()) {
            if (roomMessage.getMessageVersion() > messageVersion) {
                messageVersion = roomMessage.getMessageVersion();
            }

            if (roomMessage.getStatusVersion() > statusVersion) {
                messageVersion = roomMessage.getStatusVersion();
            }
        }
    }

    private static void computeStatusVersion() {

    }

    private static void computeDeleteVersion() {

    }
}
