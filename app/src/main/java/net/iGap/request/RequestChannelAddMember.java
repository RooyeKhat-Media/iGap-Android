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

import net.iGap.proto.ProtoChannelAddMember;

public class RequestChannelAddMember {

    public void channelAddMember(long roomId, long userId) {

        ProtoChannelAddMember.ChannelAddMember.Member.Builder member = ProtoChannelAddMember.ChannelAddMember.Member.newBuilder();
        member.setUserId(userId);

        ProtoChannelAddMember.ChannelAddMember.Builder builder = ProtoChannelAddMember.ChannelAddMember.newBuilder();
        builder.setRoomId(roomId);
        builder.setMember(member);

        RequestWrapper requestWrapper = new RequestWrapper(401, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
