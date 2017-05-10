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
import net.iGap.G;
import net.iGap.proto.ProtoChannelEdit;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class ChannelEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();

        final ProtoChannelEdit.ChannelEditResponse.Builder builder = (ProtoChannelEdit.ChannelEditResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                if (realmRoom != null) {
                    realmRoom.setTitle(builder.getName());
                    realmRoom.getChannelRoom().setDescription(builder.getDescription());
                }
            }
        });

        realm.close();

        if (G.onChannelEdit != null) {
            G.onChannelEdit.onChannelEdit(builder.getRoomId(), builder.getName(), builder.getDescription());
        }
    }

    @Override public void timeOut() {
        super.timeOut();
        if (G.onChannelEdit != null) {

            G.onChannelEdit.onTimeOut();
        }
    }

    @Override public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onChannelEdit.onError(majorCode, minorCode);
    }
}


