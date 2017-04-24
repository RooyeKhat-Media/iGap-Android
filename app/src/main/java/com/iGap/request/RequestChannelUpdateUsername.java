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

import com.iGap.proto.ProtoChannelUpdateUsername;

public class RequestChannelUpdateUsername {

    public void channelUpdateUsername(long roomId, String username) {

        ProtoChannelUpdateUsername.ChannelUpdateUsername.Builder builder = ProtoChannelUpdateUsername.ChannelUpdateUsername.newBuilder();
        builder.setRoomId(roomId);
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(419, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
