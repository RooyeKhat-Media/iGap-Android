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

import net.iGap.helper.HelperUpdateMessageStatue;
import net.iGap.proto.ProtoGroupUpdateStatus;
import net.iGap.proto.ProtoResponse;

public class GroupUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder builder = (ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder) message;
        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(builder.getResponse());

        HelperUpdateMessageStatue.updateStatus(builder.getRoomId(), builder.getMessageId(), builder.getUpdaterAuthorHash(), builder.getStatus(), builder.getStatusVersion(), response);
    }

    @Override
    public void error() {
        super.error();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
