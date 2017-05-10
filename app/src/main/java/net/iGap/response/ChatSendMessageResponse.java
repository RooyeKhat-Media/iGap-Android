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
import net.iGap.helper.HelperMessageResponse;
import net.iGap.proto.ProtoChatSendMessage;
import net.iGap.proto.ProtoError;

import static net.iGap.realm.RealmRoomMessage.makeFailed;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.identity = identity;
        this.message = protoClass;
    }

    @Override public void handler() {
        super.handler();
        final ProtoChatSendMessage.ChatSendMessageResponse.Builder builder = (ProtoChatSendMessage.ChatSendMessageResponse.Builder) message;
        HelperMessageResponse.handleMessage(builder.getRoomId(), builder.getRoomMessage(), builder.getResponse(), this.identity);
    }

    @Override public void error() {
        super.error();
        makeFailed(Long.parseLong(identity));

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        int waitTime = errorResponse.getWait();

        if (majorCode == 234 && G.onChatSendMessage != null) {
            G.onChatSendMessage.Error(majorCode, minorCode, waitTime);
        }
    }

    @Override public void timeOut() {
        super.timeOut();
    }
}
