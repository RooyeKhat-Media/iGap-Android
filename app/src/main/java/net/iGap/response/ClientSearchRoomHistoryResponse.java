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

import net.iGap.G;
import net.iGap.proto.ProtoClientSearchRoomHistory;
import net.iGap.proto.ProtoError;

public class ClientSearchRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientSearchRoomHistoryResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        if (G.onClientSearchRoomHistory != null) {
            ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder builder = (ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder) message;
            G.onClientSearchRoomHistory.onClientSearchRoomHistory(builder.getTotalCount(), builder.getNotDeletedCount(), builder.getResultList(), ((ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter) identity));
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onClientSearchRoomHistory != null) {
            G.onClientSearchRoomHistory.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        if (G.onClientSearchRoomHistory != null) {
            ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
            G.onClientSearchRoomHistory.onError(errorResponse.getMajorCode(), errorResponse.getMinorCode(), ((ProtoClientSearchRoomHistory.ClientSearchRoomHistory.Filter) identity));
        }
    }
}


