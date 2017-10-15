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

import net.iGap.proto.ProtoChannelAddMessageReaction;
import net.iGap.proto.ProtoGlobal;

public class RequestChannelAddMessageReaction {

    public void channelAddMessageReaction(long roomId, long messageId, ProtoGlobal.RoomMessageReaction roomMessageReaction) {

        ProtoChannelAddMessageReaction.ChannelAddMessageReaction.Builder builder = ProtoChannelAddMessageReaction.ChannelAddMessageReaction.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);
        builder.setReaction(roomMessageReaction);

        RequestWrapper requestWrapper = new RequestWrapper(424, builder, new IdentityChannelAddMessageReaction(roomId, messageId, roomMessageReaction));
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void channelAddMessageReactionForward(long roomId, long messageId, ProtoGlobal.RoomMessageReaction roomMessageReaction, long forwardedMessageId) {
        ProtoChannelAddMessageReaction.ChannelAddMessageReaction.Builder builder = ProtoChannelAddMessageReaction.ChannelAddMessageReaction.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(forwardedMessageId);
        builder.setReaction(roomMessageReaction);

        RequestWrapper requestWrapper = new RequestWrapper(424, builder, new IdentityChannelAddMessageReaction(roomId, messageId, forwardedMessageId, roomMessageReaction));
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static class IdentityChannelAddMessageReaction {
        public long roomId;
        public long messageId;
        public long forwardedMessageId = 0;
        public ProtoGlobal.RoomMessageReaction roomMessageReaction;

        public IdentityChannelAddMessageReaction(long roomId, long messageId, ProtoGlobal.RoomMessageReaction roomMessageReaction) {
            this.roomId = roomId;
            this.messageId = messageId;
            this.roomMessageReaction = roomMessageReaction;
        }

        public IdentityChannelAddMessageReaction(long roomId, long messageId, long forwardedMessageId, ProtoGlobal.RoomMessageReaction roomMessageReaction) {
            this.roomId = roomId;
            this.messageId = messageId;
            this.forwardedMessageId = forwardedMessageId;
            this.roomMessageReaction = roomMessageReaction;
        }
    }
}
