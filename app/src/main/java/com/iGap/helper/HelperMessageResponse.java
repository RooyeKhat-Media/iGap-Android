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

import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * helper message response for get message and detect message is for
 * chat, group or channel and after set
 */
public class HelperMessageResponse {

    public static void handleMessage(final long roomId, final ProtoGlobal.RoomMessage roomMessage, final ProtoResponse.Response response, final String identity) {

        Realm realm = Realm.getDefaultInstance();
        long latestMessageId = computeLastMessageId(realm, roomId);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                String authorHash = realm.where(RealmUserInfo.class).findFirst().getAuthorHash();
                boolean isAuthorUser = false;
                if (roomMessage.getAuthor().hasUser()) {
                    isAuthorUser = true;
                }

                /**
                 * because user may have more than one device, his another device should not
                 * be recipient but sender. so I check current userId with room message user id,
                 * and if not equals and response is null, so we sure recipient is another user
                 */
                if (response.getId().isEmpty()) { // i'm recipient

                    if (isAuthorUser) {
                        HelperInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                    }
                    RealmRoomMessage.putOrUpdate(roomMessage, roomId);

                    /**
                     * show notification if this message isn't for another account
                     */
                    if (isAuthorUser) {
                        if (!roomMessage.getAuthor().getHash().equals(authorHash) && !roomMessage.getLog().getType().toString().equals("ROOM_CREATED")) {
                            G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.CHANNEL, roomId);
                        }
                    } else {
                        if (!roomMessage.getLog().getType().toString().equals("ROOM_CREATED")) {
                            G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.CHANNEL, roomId);
                        }
                    }

                } else { // i'm the sender

                    /**
                     * update message fields into database
                     */
                    RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll();
                    for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                        /**
                         * find the message using identity and update it
                         */
                        if (realmRoomMessage != null && realmRoomMessage.getMessageId() == Long.parseLong(identity)) {
                            if (realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).count() == 0) {
                                RealmRoomMessage.updateId(Long.parseLong(identity), roomMessage.getMessageId());
                            }
                            RealmRoomMessage.putOrUpdate(roomMessage, roomId);
                            break;
                        }
                    }
                }

                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (room == null) {
                    /**
                     * if first message received but the room doesn't exist, send request for create new room
                     */
                    new RequestClientGetRoom().clientGetRoom(roomId, null);
                } else {

                    if (!roomMessage.getAuthor().getHash().equals(authorHash) && (room.getLastMessage() == null || (room.getLastMessage() != null && room.getLastMessage().getMessageId() < roomMessage.getMessageId()))) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                    }

                    /**
                     * update last message sent/received in room table
                     */
                    if (room.getLastMessage() != null) {
                        if (room.getLastMessage().getMessageId() <= roomMessage.getMessageId()) {
                            room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, roomId));
                            //room.setUpdatedTime(roomMessage.getUpdateTime());
                        }
                    } else {
                        room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, roomId));
                        //room.setUpdatedTime(roomMessage.getUpdateTime());
                    }
                }
            }
        });

        if (roomMessage.getMessageId() > latestMessageId) {
            if (response.getId().isEmpty()) {
                /**
                 * invoke following callback when i'm not the sender, because
                 * I already done everything after sending message
                 */
                if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst() != null) {
                    G.chatSendMessageUtil.onMessageReceive(roomId, roomMessage.getMessage(), roomMessage.getMessageType(), roomMessage, ProtoGlobal.Room.Type.CHANNEL);
                }
            } else {
                /**
                 * invoke following callback when I'm the sender and the message has updated
                 */
                G.chatSendMessageUtil.onMessageUpdate(roomId, roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
            }
        }
    }

    /**
     * compute last messageId that exist in RealmRoomMessage for messages that
     * not in sending or failed state because that messages have fake messageId
     */
    public static long computeLastMessageId(Realm realm, long roomId) {
        long latestMessageId = 0;
        RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        for (RealmRoomMessage realmRoomMessage1 : realmRoomMessages) {
            if (realmRoomMessage1 != null && realmRoomMessage1.getStatus() != null) {
                if (realmRoomMessage1.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENT.toString()) || realmRoomMessage1.getStatus().equals(ProtoGlobal.RoomMessageStatus.DELIVERED.toString()) || realmRoomMessage1.getStatus().equals(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                    return realmRoomMessage1.getMessageId();
                }
            }
        }
        return latestMessageId;
    }

    //public static long computeLastMessageId(Realm realm, long roomId) {
    //    RealmRoomMessage realmRoomMessage = null;
    //    long latestMessageId = 0;
    //    RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
    //    if (realmRoomMessages.size() > 0) {
    //        realmRoomMessage = realmRoomMessages.last();
    //    }
    //    if (realmRoomMessage != null) {
    //        latestMessageId = realmRoomMessage.getMessageId();
    //    }
    //    return latestMessageId;
    //}

}
