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
import com.iGap.module.enums.ChannelChatRole;
import com.iGap.proto.ProtoChannelAddAdmin;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class ChannelAddAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelAddAdmin.ChannelAddAdminResponse.Builder builder = (ProtoChannelAddAdmin.ChannelAddAdminResponse.Builder) message;

        RealmRoom.updateRole(ProtoGlobal.Room.Type.CHANNEL, builder.getRoomId(), builder.getMemberId(), ChannelChatRole.ADMIN.toString());
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
        if (realmRoom != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    RealmList<RealmMember> realmMemberRealmList = realmChannelRoom.getMembers();
                    for (RealmMember member : realmMemberRealmList) {
                        if (member.getPeerId() == builder.getMemberId()) {
                            member.setRole(ProtoGlobal.ChannelRoom.Role.ADMIN.toString());
                            if (G.onChannelAddAdmin != null) {
                                G.onChannelAddAdmin.onChannelAddAdmin(builder.getRoomId(), builder.getMemberId());
                            }
                            break;
                        }
                    }
                }
            });
        }

        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.onChannelAddAdmin != null) {
            G.onChannelAddAdmin.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onChannelAddAdmin != null) {
            G.onChannelAddAdmin.onError(majorCode, minorCode);
        }
    }
}


