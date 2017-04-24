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
import com.iGap.proto.ProtoChannelAddMember;
import com.iGap.proto.ProtoError;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class ChannelAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddMember.ChannelAddMemberResponse.Builder builder = (ProtoChannelAddMember.ChannelAddMemberResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();

        if (realmRoom != null) {
            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
            if (realmChannelRoom != null) {
                final RealmList<RealmMember> members = realmChannelRoom.getMembers();

                final RealmMember realmMember = new RealmMember();
                realmMember.setId(SUID.id().get());
                realmMember.setPeerId(builder.getUserId());
                realmMember.setRole(builder.getRole().toString());
                // realmMember = realm.copyToRealm(realmMember);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        members.add(realmMember);
                    }
                });

                if (G.onChannelAddMember != null) {
                    G.onChannelAddMember.onChannelAddMember(builder.getRoomId(), builder.getUserId(), builder.getRole());
                }
            }
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.onChannelAddMember != null) {
            G.onChannelAddMember.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        if (G.onChannelAddMember != null) {
            G.onChannelAddMember.onError(majorCode, minorCode);
        }
    }
}


