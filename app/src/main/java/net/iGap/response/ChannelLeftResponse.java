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
import net.iGap.proto.ProtoChannelLeft;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;

public class ChannelLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelLeft.ChannelLeftResponse.Builder builder = (ProtoChannelLeft.ChannelLeftResponse.Builder) message;

        if (G.userId == builder.getMemberId()) {
            RealmRoom.deleteRoom(builder.getRoomId());

            if (G.onChannelLeft != null) {
                G.onChannelLeft.onChannelLeft(builder.getRoomId(), builder.getMemberId());
            }
            if (G.onChannelDeleteInRoomList != null) {
                G.onChannelDeleteInRoomList.onChannelDelete(builder.getRoomId());
            }
        } else {
            RealmMember.kickMember(builder.getRoomId(), builder.getMemberId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onChannelLeft != null) {
            G.onChannelLeft.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onChannelLeft != null) {
            G.onChannelLeft.onError(majorCode, minorCode);
        }
    }
}


