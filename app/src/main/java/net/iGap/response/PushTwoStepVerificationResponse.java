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
import net.iGap.proto.ProtoPushTwoStepVerification;

public class PushTwoStepVerificationResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public PushTwoStepVerificationResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoPushTwoStepVerification.PushTwoStepVerificationResponse.Builder builder = (ProtoPushTwoStepVerification.PushTwoStepVerificationResponse.Builder) message;
        if (G.onPushTwoStepVerification != null) {
            G.onPushTwoStepVerification.pushTwoStepVerification(builder.getUsername(), builder.getUserId(), builder.getAuthorHash());
        }
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


