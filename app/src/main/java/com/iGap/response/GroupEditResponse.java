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

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupEdit;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;

public class GroupEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupEdit.GroupEditResponse.Builder builder =
                (ProtoGroupEdit.GroupEditResponse.Builder) message;
        long roomId = builder.getRoomId();
        final String name = builder.getName();
        final String descriptions = builder.getDescription();


        Intent intent = new Intent("Intent_filter_on_change_group_name");
        intent.putExtra("Name", name);
        intent.putExtra("Description", descriptions);
        intent.putExtra("RoomId", builder.getRoomId());
        LocalBroadcastManager.getInstance(G.context).sendBroadcast(intent);


        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom =
                realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

        if (realmRoom != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realmRoom.setTitle(name);
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    realmGroupRoom.setDescription(descriptions);

                }
            });

            G.onGroupEdit.onGroupEdit(builder.getRoomId(), builder.getName(),
                    builder.getDescription());

        }

        realm.close();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onGroupEdit.onError(majorCode, minorCode);

    }
}
