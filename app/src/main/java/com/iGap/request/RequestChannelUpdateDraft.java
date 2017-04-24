/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.request;

import com.iGap.proto.ProtoChannelUpdateDraft;
import com.iGap.proto.ProtoGlobal;

public class RequestChannelUpdateDraft {

    public void channelUpdateDraft(long roomId, String message, long replyToMessageId) {
        ProtoChannelUpdateDraft.ChannelUpdateDraft.Builder builder = ProtoChannelUpdateDraft.ChannelUpdateDraft.newBuilder();

        ProtoGlobal.RoomDraft.Builder draft = ProtoGlobal.RoomDraft.newBuilder();
        draft.setMessage(message);
        draft.setReplyTo(replyToMessageId);

        builder.setRoomId(roomId);
        builder.setDraft(draft);

        RequestWrapper requestWrapper = new RequestWrapper(415, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
