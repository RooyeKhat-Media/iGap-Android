/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoChatSendMessage;
import net.iGap.proto.ProtoGlobal;

public class RequestChatSendMessage {
    // builder to force use newBuilder
    ProtoChatSendMessage.ChatSendMessage.Builder chatSendMessage;

    public RequestChatSendMessage newBuilder(ProtoGlobal.RoomMessageType messageType, long roomId) {
        chatSendMessage = ProtoChatSendMessage.ChatSendMessage.newBuilder();
        chatSendMessage.setMessageType(messageType);
        chatSendMessage.setRoomId(roomId);
        return this;
    }

    public RequestChatSendMessage message(String value) {
        chatSendMessage.setMessage(value);
        return this;
    }

    public RequestChatSendMessage attachment(String value) {
        chatSendMessage.setAttachment(value);
        return this;
    }

    public RequestChatSendMessage location(ProtoGlobal.RoomMessageLocation value) {
        chatSendMessage.setLocation(value);
        return this;
    }

    public RequestChatSendMessage contact(ProtoGlobal.RoomMessageContact value) {
        chatSendMessage.setContact(value);
        return this;
    }

    public RequestChatSendMessage forwardMessage(ProtoGlobal.RoomMessageForwardFrom forwardFrom) {
        chatSendMessage.setForwardFrom(forwardFrom);
        return this;
    }

    public RequestChatSendMessage replyMessage(long messageId) {
        chatSendMessage.setReplyTo(messageId);
        return this;
    }

    public RequestChatSendMessage sendMessage(String fakeMessageIdAsIdentity) {
        chatSendMessage.setRandomId(Long.parseLong(fakeMessageIdAsIdentity));
        RequestWrapper requestWrapper = new RequestWrapper(201, chatSendMessage, fakeMessageIdAsIdentity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }
}

