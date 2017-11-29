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
import net.iGap.proto.ProtoChatGetRoom;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;

public class ChatGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatGetRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChatGetRoom.ChatGetRoomResponse.Builder chatGetRoomResponse = (ProtoChatGetRoom.ChatGetRoomResponse.Builder) message;

        /**
         * before client just get roomId from server and send that with receiver
         * and later get room info . but now client receive room with complete
         * info , but now i send roomId like before . i do that for don't change
         * other code . because i guess don't need for this actions.
         *
         * hint : we can set another interface for another state.
         */

        if ((chatGetRoomResponse.getRoom().getType() == ProtoGlobal.Room.Type.CHANNEL) || identity != null) {
            RealmRoom.putOrUpdate(chatGetRoomResponse.getRoom());
        } else {
            if (G.onChatGetRoom != null) {
                G.onChatGetRoom.onChatGetRoom(chatGetRoomResponse.getRoom());
            }
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onChatGetRoom != null) {
            G.onChatGetRoom.onChatGetRoomTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onChatGetRoom != null) {
            G.onChatGetRoom.onChatGetRoomError(majorCode, minorCode);
        }
    }
}


