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

import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.realm.RealmMember;

public class ChannelGetMemberListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelGetMemberListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder builder = (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder) message;
        RealmMember.convertProtoMemberListToRealmMember(builder, identity);
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


