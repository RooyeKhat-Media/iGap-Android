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

import com.iGap.proto.ProtoGroupAddMember;

public class RequestGroupAddMember {

    public void groupAddMember(long roomId, long userId, long startMessageId) {

        ProtoGroupAddMember.GroupAddMember.Member.Builder member = ProtoGroupAddMember.GroupAddMember.Member.newBuilder();
        member.setUserId(userId);
        member.setStartMessageId(startMessageId);

        ProtoGroupAddMember.GroupAddMember.Builder builder = ProtoGroupAddMember.GroupAddMember.newBuilder();
        builder.setRoomId(roomId);
        builder.setMember(member);

        RequestWrapper requestWrapper = new RequestWrapper(301, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

