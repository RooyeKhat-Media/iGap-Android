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

import com.iGap.G;
import com.iGap.proto.ProtoGroupUpdateStatus;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineSeen;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;
import io.realm.RealmList;

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

        Realm realm = Realm.getDefaultInstance();
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
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, builder.getMessageId()).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setStatus(builder.getStatus().toString());
                        realm.copyToRealmOrUpdate(roomMessage);

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
