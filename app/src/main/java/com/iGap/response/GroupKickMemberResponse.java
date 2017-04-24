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
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupKickMember;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import io.realm.Realm;
import io.realm.RealmList;

public class GroupKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean isDeleted = false;

    public GroupKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupKickMember.GroupKickMemberResponse.Builder builder = (ProtoGroupKickMember.GroupKickMemberResponse.Builder) message;
        final long roomId = builder.getRoomId();
        final long memberId = builder.getMemberId();

        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();



        if (realmRoom != null) {
            final RealmList<RealmMember> realmMembers = realmRoom.getGroupRoom().getMembers();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < realmMembers.size(); i++) {
                        RealmMember member = realmMembers.get(i);
                        if (member.getPeerId() == memberId) {
                            member.deleteFromRealm();          //delete member from database
                            //realmRoom.getGroupRoom().setParticipantsCountLabel((Integer.parseInt(realmRoom.getGroupRoom().getParticipantsCountLabel()) - 1) + "");
                            isDeleted = true;
                            break;
                        }
                    }
                }
            });
        }

        realm.close();

        if (isDeleted) {
            if (G.onGroupKickMember != null) {
                G.onGroupKickMember.onGroupKickMember(roomId, memberId);
            }
        }


    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onGroupKickMember.onError(majorCode, minorCode);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onGroupKickMember.onTimeOut();
    }
}
