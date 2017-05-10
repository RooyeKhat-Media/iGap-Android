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
import io.realm.RealmResults;
import net.iGap.G;
import net.iGap.proto.ProtoChatDelete;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class ChatDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();
        ProtoChatDelete.ChatDeleteResponse.Builder builder = (ProtoChatDelete.ChatDeleteResponse.Builder) message;

        final Long roomId = builder.getRoomId();

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteFromRealm();
                }

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }
                RealmResults<RealmRoomMessage> realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll();
                if (realmRoomMessage != null) {
                    realmRoomMessage.deleteAllFromRealm();
                }
            }
        });

        realm.close();

        if (G.onChatDelete != null) {
            G.onChatDelete.onChatDelete(builder.getRoomId());
        }
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder builder = (ProtoError.ErrorResponse.Builder) message;
    }
}


