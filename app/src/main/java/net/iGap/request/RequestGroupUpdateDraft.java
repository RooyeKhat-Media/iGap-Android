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

import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupUpdateDraft;

public class RequestGroupUpdateDraft {

    public void groupUpdateDraft(long roomId, String message, long replyToMessageId) {

        ProtoGroupUpdateDraft.GroupUpdateDraft.Builder builder = ProtoGroupUpdateDraft.GroupUpdateDraft.newBuilder();

        ProtoGlobal.RoomDraft.Builder roomDraft = ProtoGlobal.RoomDraft.newBuilder();
        roomDraft.setMessage(message);
        roomDraft.setReplyTo(replyToMessageId);

        builder.setRoomId(roomId);
        builder.setDraft(roomDraft);

        RequestWrapper requestWrapper = new RequestWrapper(315, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

