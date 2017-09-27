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
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupKickModerator;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class GroupKickModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupKickModerator.GroupKickModeratorResponse.Builder builder = (ProtoGroupKickModerator.GroupKickModeratorResponse.Builder) message;

        RealmRoom.updateRole(ProtoGlobal.Room.Type.GROUP, builder.getRoomId(), builder.getMemberId(), ProtoGlobal.GroupRoom.Role.MEMBER.toString());
        Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();

            for (final RealmMember member : realmMembers) {
                if (member.getPeerId() == builder.getMemberId()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            member.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                        }
                    });
                    if (G.onGroupKickModerator != null) {
                        G.onGroupKickModerator.onGroupKickModerator(builder.getRoomId(), builder.getMemberId());
                    }
                    break;
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

        if (G.onGroupKickModerator != null) {
            G.onGroupKickModerator.onError(majorCode, minorCode);
        }
    }
}
