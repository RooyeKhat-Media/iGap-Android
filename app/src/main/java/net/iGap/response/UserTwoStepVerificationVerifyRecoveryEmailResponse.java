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
import net.iGap.proto.ProtoUserTwoStepVerificationVerifyRecoveryEmail;

public class UserTwoStepVerificationVerifyRecoveryEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationVerifyRecoveryEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmailResponse.Builder builder = (ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmailResponse.Builder) message;

        if (G.onTwoStepPassword != null) {
            G.onTwoStepPassword.confirmEmail();
        }

        if (G.twoStepSecurityConfirmEmail != null) {
            G.twoStepSecurityConfirmEmail.confirmEmail();
        }


    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.twoStepSecurityConfirmEmail != null && majorCode == 10113 && minorCode == 2) {
            G.twoStepSecurityConfirmEmail.errorInvalidConfirmCode();
            return;
        }

        if (G.onTwoStepPassword != null) {
            G.onTwoStepPassword.errorConfirmEmail();
        }

    }
}


