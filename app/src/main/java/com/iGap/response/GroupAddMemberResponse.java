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
import com.iGap.module.SUID;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupAddMember;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class GroupAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoGroupAddMember.GroupAddMemberResponse.Builder response = (ProtoGroupAddMember.GroupAddMemberResponse.Builder) message;

        Long roomId = response.getRoomId();
        Long userId = response.getUserId();


        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
            if (realmGroupRoom != null) {
                final RealmList<RealmMember> members = realmGroupRoom.getMembers();

                final RealmMember realmMember = new RealmMember();
                realmMember.setId(SUID.id().get());
                realmMember.setPeerId(userId);
                realmMember.setRole(response.getRole().toString());

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        members.add(realmMember);
                    }
                });
                if (G.onGroupAddMember != null) {
                    G.onGroupAddMember.onGroupAddMember(roomId, userId);
                }
            }
        }


        realm.close();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onGroupAddMember.onError(majorCode, minorCode);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onGroupAddMember.onError(0, 0);
    }
}
