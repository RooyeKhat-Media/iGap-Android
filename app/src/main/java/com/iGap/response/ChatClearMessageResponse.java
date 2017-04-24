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
import com.iGap.proto.ProtoChatClearMessage;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

public class ChatClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChatClearMessage.ChatClearMessageResponse.Builder chatClearMessage = (ProtoChatClearMessage.ChatClearMessageResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        if (chatClearMessage.getResponse().getId().isEmpty()) { // another account cleared message
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatClearMessage.getRoomId()).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.setClearId(chatClearMessage.getClearId());
                    }
                }
            });
            G.clearMessagesUtil.onChatClearMessage(chatClearMessage.getRoomId(), chatClearMessage.getClearId(), chatClearMessage.getResponse());
        }

        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, chatClearMessage.getRoomId()).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realmRoom != null) {
                    //  realmRoom.setUpdatedTime(chatClearMessage.getResponse().getTimestamp() * DateUtils.SECOND_IN_MILLIS);
                    realmRoom.setUnreadCount(0);
                    realmRoom.setLastMessage(null);
                }

                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, chatClearMessage.getRoomId()).
                        lessThan(RealmRoomMessageFields.MESSAGE_ID, chatClearMessage.getClearId()).findAll().deleteAllFromRealm();


            }
        });

        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


