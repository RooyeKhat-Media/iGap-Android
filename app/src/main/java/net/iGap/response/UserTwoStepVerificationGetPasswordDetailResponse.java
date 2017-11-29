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
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserTwoStepVerificationGetPasswordDetail;

public class UserTwoStepVerificationGetPasswordDetailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationGetPasswordDetailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetailResponse.Builder builder = (ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetailResponse.Builder) message;

        if (G.onTwoStepPassword != null) {
            G.onTwoStepPassword.getPasswordDetail(builder.getQuestionOne(), builder.getQuestionTwo(), builder.getHint(), builder.getHasConfirmedRecoveryEmail(), builder.getUnconfirmedEmailPattern());
        }

        if (G.onSecurityCheckPassword != null) {
            G.onSecurityCheckPassword.getDetailPassword(builder.getQuestionOne(), builder.getQuestionTwo(), builder.getHint(), builder.getHasConfirmedRecoveryEmail(), builder.getUnconfirmedEmailPattern());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onTwoStepPassword != null) {
            G.onTwoStepPassword.timeOutGetPasswordDetail();
        }
    }


    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onTwoStepPassword != null) {
            G.onTwoStepPassword.errorGetPasswordDetail(majorCode, minorCode);
        }
    }
}


