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
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGroupDelete;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import static net.iGap.module.MusicPlayer.roomId;

public class GroupDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();
        ProtoGroupDelete.GroupDeleteResponse.Builder builder = (ProtoGroupDelete.GroupDeleteResponse.Builder) message;
        final long id = builder.getRoomId();

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(final Realm realm) {

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }
                RealmResults<RealmRoomMessage> realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, id).findAll();
                if (realmRoomMessage != null) {
                    realmRoomMessage.deleteAllFromRealm();
                }

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteFromRealm();
                }
            }
        });
        realm.close();
        if (G.onGroupDelete != null) {
            G.onGroupDelete.onGroupDelete(id);
        }
    }

    @Override public void timeOut() {
        super.timeOut();
        G.onGroupDelete.onTimeOut();
    }

    @Override public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onGroupDelete.Error(majorCode, minorCode);
    }
}


