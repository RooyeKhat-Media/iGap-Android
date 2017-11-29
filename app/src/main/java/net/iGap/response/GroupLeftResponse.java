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

import net.iGap.G;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGroupLeft;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;

public class GroupLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupLeft.GroupLeftResponse.Builder builder = (ProtoGroupLeft.GroupLeftResponse.Builder) message;
        long roomId = builder.getRoomId();
        long memberId = builder.getMemberId();

        if (G.userId == memberId) {
            RealmRoom.deleteRoom(roomId);

            if (G.onGroupLeft != null) {
                G.onGroupLeft.onGroupLeft(roomId, memberId);
            }
            if (G.onGroupDeleteInRoomList != null) {
                G.onGroupDeleteInRoomList.onGroupDelete(roomId);
            }
        } else {
            RealmMember.kickMember(builder.getRoomId(), builder.getMemberId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onGroupLeft != null) {
            G.onGroupLeft.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onGroupLeft != null) {
            G.onGroupLeft.onError(majorCode, minorCode);
        }
    }
}
