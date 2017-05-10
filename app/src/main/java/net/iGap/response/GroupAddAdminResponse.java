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
import net.iGap.module.enums.GroupChatRole;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupAddAdmin;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class GroupAddAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();

        final ProtoGroupAddAdmin.GroupAddAdminResponse.Builder builder = (ProtoGroupAddAdmin.GroupAddAdminResponse.Builder) message;
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();

        RealmRoom.updateRole(ProtoGlobal.Room.Type.GROUP, builder.getRoomId(), builder.getMemberId(), GroupChatRole.ADMIN.toString());

        if (realmRoom != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    RealmList<RealmMember> realmMemberRealmList = realmGroupRoom.getMembers();
                    for (RealmMember member : realmMemberRealmList) {
                        if (member.getPeerId() == builder.getMemberId()) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.ADMIN.toString());
                            if (G.onGroupAddAdmin != null) {
                                G.onGroupAddAdmin.onGroupAddAdmin(builder.getRoomId(), builder.getMemberId());
                            }
                            break;
                        }
                    }
                }
            });
        }

        realm.close();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onGroupAddAdmin.onError(majorCode, minorCode);
    }
}
