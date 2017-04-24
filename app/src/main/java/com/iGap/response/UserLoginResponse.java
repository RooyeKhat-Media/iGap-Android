/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperConnectionState;
import com.iGap.module.enums.ConnectionState;
import com.iGap.proto.ProtoError;

public class UserLoginResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserLoginResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        HelperConnectionState.connectionState(ConnectionState.IGAP);
        /*ProtoUserLogin.UserLoginResponse.Builder builder = (ProtoUserLogin.UserLoginResponse.Builder) message;
        builder.getDeprecatedClient();
        builder.getSecondaryNodeName();
        builder.getUpdateAvailable();*/

        G.userLogin = true;
        WebSocketClient.waitingForReconnecting = false;
        WebSocketClient.allowForReconnecting = true;
        G.onUserLogin.onLogin();
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
        G.onUserLogin.onLoginError(majorCode, minorCode);
    }
}


