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

import net.iGap.proto.ProtoClientGetRoomHistory;

public class RequestClientGetRoomHistory {

    public void getRoomHistory(long roomId, long firstMessageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, String identity) {

        ProtoClientGetRoomHistory.ClientGetRoomHistory.Builder builder = ProtoClientGetRoomHistory.ClientGetRoomHistory.newBuilder();
        builder.setRoomId(roomId);
        builder.setFirstMessageId(firstMessageId);
        builder.setDirection(direction);

        RequestWrapper requestWrapper = new RequestWrapper(603, builder, identity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
