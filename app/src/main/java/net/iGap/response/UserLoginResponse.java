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

import io.realm.Realm;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperConnectionState;
import net.iGap.module.enums.ConnectionState;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserLogin;
import net.iGap.realm.RealmCallConfig;
import net.iGap.request.RequestSignalingGetConfiguration;

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
        ProtoUserLogin.UserLoginResponse.Builder builder = (ProtoUserLogin.UserLoginResponse.Builder) message;
      /*builder.getDeprecatedClient();
        builder.getSecondaryNodeName();
        builder.getUpdateAvailable();*/
        G.currentServerTime = builder.getResponse().getTimestamp();
        G.bothChatDeleteTime = builder.getChatDeleteMessageForBothPeriod() * 1000;
        G.userLogin = true;

        /**
         * get Signaling Configuration
         * (( hint : call following request after set G.userLogin=true ))
         */

        Realm realm = Realm.getDefaultInstance();
        if (G.needGetSignalingConfiguration || realm.where(RealmCallConfig.class).findFirst() == null) {
            new RequestSignalingGetConfiguration().signalingGetConfiguration();
        }
        realm.close();

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


