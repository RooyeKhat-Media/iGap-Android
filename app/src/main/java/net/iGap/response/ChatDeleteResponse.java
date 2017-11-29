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
import net.iGap.proto.ProtoChatDelete;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmRoom;

public class ChatDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChatDelete.ChatDeleteResponse.Builder builder = (ProtoChatDelete.ChatDeleteResponse.Builder) message;

        Long roomId = builder.getRoomId();

        RealmRoom.deleteRoom(roomId);
        if (G.onChatDelete != null) {
            G.onChatDelete.onChatDelete(builder.getRoomId());
        }

        if (G.onChatDeleteInRoomList != null) {
            G.onChatDeleteInRoomList.onChatDelete(builder.getRoomId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder builder = (ProtoError.ErrorResponse.Builder) message;
    }
}


