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
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;
import net.iGap.proto.ProtoClientCondition;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmOfflineDelete;
import net.iGap.realm.RealmOfflineEdited;
import net.iGap.realm.RealmOfflineListen;
import net.iGap.realm.RealmOfflineSeen;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

/**
 * helper client condition for set info to RealmClientCondition
 */
public class HelperClientCondition {

    public static ProtoClientCondition.ClientCondition.Builder computeClientCondition(Long roomId) {

        Realm realm = Realm.getDefaultInstance();
        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll().size() == 0) {
            return clientCondition;
        }

        RealmResults<RealmClientCondition> clientConditionList;

        if (roomId != null) {
            clientConditionList = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findAll();
        } else {
            /**
             * find all client condition that deleted is false
             *
             * hint: we use {@link net.iGap.realm.RealmRoom#putChatToDatabase(List, boolean, boolean)} for add room to realm
             * and in this method also we called {@link net.iGap.realm.RealmRoom#putChatToClientCondition(Realm, ProtoGlobal.Room)}
             * so for each room exist a RealmClientCondition, but we just need RealmClientCondition for rooms that aren't deleted.
             *
             * it is better that client just create RealmClientCondition for rooms that need really.
             */
            RealmQuery<RealmClientCondition> conditionQuery = realm.where(RealmClientCondition.class);
            if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll().size() > 1) {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
                    conditionQuery.equalTo(RealmClientConditionFields.ROOM_ID, realmRoom.getId()).or();
                }
            } else {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
                    conditionQuery.equalTo(RealmClientConditionFields.ROOM_ID, realmRoom.getId());
                }
            }
            clientConditionList = conditionQuery.findAll();
        }

        for (RealmClientCondition realmClientCondition : clientConditionList) {
            if (realmClientCondition.isManaged()) {
                ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
                room.setRoomId(realmClientCondition.getRoomId());

                Number messageVersion = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().max(RealmRoomMessageFields.MESSAGE_VERSION);
                if (messageVersion != null) {
                    room.setMessageVersion((long) messageVersion);
                }

                Number statusVersion = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().max(RealmRoomMessageFields.STATUS_VERSION);
                if (statusVersion != null) {
                    room.setStatusVersion((long) statusVersion);
                }

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

                for (RealmOfflineListen offlineListen : realmClientCondition.getOfflineListen()) { // DONE
                    room.addOfflineListened(offlineListen.getOfflineListen());
                }

                room.setClearId(realmClientCondition.getClearId()); //DONE

                List<RealmRoomMessage> allItemsAscending = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                for (RealmRoomMessage item : allItemsAscending) {
                    if (item != null) {
                        room.setCacheStartId(item.getMessageId());
                        break;
                    }
                }

                List<RealmRoomMessage> allItems = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
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
                Log.i("CLI", "room : " + room);
                clearOffline(realmClientCondition, realm);
            }
        }
        realm.close();

        return clientCondition;
    }

    private static void clearOffline(final RealmClientCondition realmClientCondition, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmClientCondition.setOfflineEdited(new RealmList<RealmOfflineEdited>());
                realmClientCondition.setOfflineDeleted(new RealmList<RealmOfflineDelete>());
                realmClientCondition.setOfflineSeen(new RealmList<RealmOfflineSeen>());
                realmClientCondition.setOfflineListen(new RealmList<RealmOfflineListen>());
            }
        });
    }
}
