/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import android.os.Handler;
import android.os.Looper;
import com.iGap.G;
import com.iGap.helper.HelperInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSendMessage;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestClientGetRoom;
import io.realm.Realm;
import io.realm.RealmResults;

public class GroupSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();


        final ProtoGroupSendMessage.GroupSendMessageResponse.Builder builder = (ProtoGroupSendMessage.GroupSendMessageResponse.Builder) message;
        Realm realm = Realm.getDefaultInstance();
        final ProtoGlobal.RoomMessage roomMessage = builder.getRoomMessage();
        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final String authorHash = realm.where(RealmUserInfo.class).findFirst().getAuthorHash();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                // because user may have more than one device, his another device should not be recipient
                // but sender. so I check current userId with room message user id, and if not equals
                // and response is null, so we sure recipient is another user

                if (userId != roomMessage.getAuthor().getUser().getUserId() && builder.getResponse().getId().isEmpty()) {
                    // i'm the recipient

                    HelperInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                    RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId());
                    G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.GROUP, builder.getRoomId());

                } else {
                    // i'm the sender
                    // update message fields into database
                    if (builder.getResponse().getId().isEmpty()) {
                        RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId());
                    } else {
                        RealmResults<RealmRoomMessage> realmRoomMessageRealmResults = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, builder.getRoomId()).findAll();
                        for (RealmRoomMessage realmRoomMessage : realmRoomMessageRealmResults) {
                            // find the message using identity and update it
                            if (realmRoomMessage != null && realmRoomMessage.getMessageId() == Long.parseLong(identity)) {
                                if (realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, roomMessage.getMessageId()).count() == 0) {
                                    RealmRoomMessage.updateId(Long.parseLong(identity), roomMessage.getMessageId());
                                }

                                RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId());
                                break;
                            }
                        }
                    }
                }

                // if first message received but the room doesn't exist, create new room
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                if (room == null) {
                    // make get room request
                    new RequestClientGetRoom().clientGetRoom(builder.getRoomId(), null);
                } else {
                    if (!roomMessage.getAuthor().getHash().equals(authorHash) && (room.getLastMessage() == null || (room.getLastMessage() != null && room.getLastMessage().getMessageId() < roomMessage.getMessageId()))) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                    }
                    // update last message sent/received in room table
                    if (room.getLastMessage() != null) {
                        if (room.getLastMessage().getMessageId() <= roomMessage.getMessageId()) {
                            room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId()));
                            //room.setUpdatedTime(roomMessage.getUpdateTime());
                        }
                    } else {
                        room.setLastMessage(RealmRoomMessage.putOrUpdate(roomMessage, builder.getRoomId()));
                        //room.setUpdatedTime(roomMessage.getUpdateTime());
                    }
                }
            }
        });
        // if (builder.getRoomMessage().getMessageId() > latestMessageId) {
            if (builder.getResponse().getId().isEmpty()) {
                // invoke following callback when i'm not the sender, because I already done
                // everything after sending message
                if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst() != null) {
                    G.chatSendMessageUtil.onMessageReceive(builder.getRoomId(), roomMessage.getMessage(), roomMessage.getMessageType(), roomMessage, ProtoGlobal.Room.Type.GROUP);
                }
            } else {
                // invoke following callback when I'm the sender and the message has updated
                G.chatSendMessageUtil.onMessageUpdate(builder.getRoomId(), roomMessage.getMessageId(), roomMessage.getStatus(), identity, roomMessage);
            }
        //}

        realm.close();
    }

    /**
     * make messages failed
     */
    private void makeFailed() {
        // message failed
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void error() {
        super.error();
        makeFailed();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
