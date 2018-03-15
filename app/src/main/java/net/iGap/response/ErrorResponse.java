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

import net.iGap.proto.ProtoError;

public class ErrorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ErrorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        double majorCode = errorResponse.getMajorCode();
        double minorCode = errorResponse.getMinorCode();
        int wait = errorResponse.getWait();
        String errorMessage = errorResponse.getMessage();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        double majorCode = errorResponse.getMajorCode();
        double minorCode = errorResponse.getMinorCode();
    }
}
