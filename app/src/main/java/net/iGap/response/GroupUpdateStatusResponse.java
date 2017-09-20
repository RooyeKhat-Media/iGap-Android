/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import io.realm.Realm;
import io.realm.RealmList;
import net.iGap.G;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupUpdateStatus;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmOfflineSeen;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class GroupUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder builder = (ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder) message;
        final ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(builder.getResponse());

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (!response.getId().isEmpty()) { // I'm sender

                    RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, builder.getRoomId()).findFirst();
                    RealmList<RealmOfflineSeen> offlineSeen = realmClientCondition.getOfflineSeen();
                    for (int i = offlineSeen.size() - 1; i >= 0; i--) {
                        RealmOfflineSeen realmOfflineSeen = offlineSeen.get(i);
                        realmOfflineSeen.deleteFromRealm();
                    }
                } else { // I'm recipient

                    /**
                     * clear unread count if another account was saw this message
                     */
                    RealmRoom.clearUnreadCount(builder.getRoomId(), builder.getUpdaterAuthorHash(), builder.getStatus());

                    /**
                     * find message from database and update its status
                     */
                    RealmRoomMessage roomMessage;
                    if (builder.getStatus() != ProtoGlobal.RoomMessageStatus.LISTENED) {
                        roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, builder.getMessageId()).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findFirst();
                    } else {
                        roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, builder.getMessageId()).findFirst();
                    }

                    if (roomMessage != null) {
                        roomMessage.setStatus(builder.getStatus().toString());
                        roomMessage.setStatusVersion(builder.getStatusVersion());
                        realm.copyToRealmOrUpdate(roomMessage);

                        if (G.chatUpdateStatusUtil != null) {
                            G.chatUpdateStatusUtil.onChatUpdateStatus(builder.getRoomId(), builder.getMessageId(), builder.getStatus(), builder.getStatusVersion());
                        }
                    } else if (builder.getStatus() == ProtoGlobal.RoomMessageStatus.SEEN) {
                        /**
                         * reason : getRoomList will be updated status in Realm and after that when
                         * client get status here and was in chat will not be updated status in second
                         * so i use from this block for avoid from this problem
                         */
                        if (G.chatUpdateStatusUtil != null) {
                            G.chatUpdateStatusUtil.onChatUpdateStatus(builder.getRoomId(), builder.getMessageId(), builder.getStatus(), builder.getStatusVersion());
                        }
                    }
                }
            }
        });
        realm.close();
    }

    @Override
    public void error() {
        super.error();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
