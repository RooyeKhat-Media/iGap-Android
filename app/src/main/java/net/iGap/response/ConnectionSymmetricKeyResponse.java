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

import com.neovisionaries.ws.client.WebSocket;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.module.LoginActions;
import net.iGap.proto.ProtoConnectionSecuring;

public class ConnectionSymmetricKeyResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ConnectionSymmetricKeyResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Builder builder = (ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Builder) message;
        ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Status status = builder.getStatus();
        int statusNumber = status.getNumber();

        //builder.getSecurityIssue(); true means reject

        if (statusNumber == Config.REJECT) { //go to upgrade page

            G.allowForConnect = false;
            WebSocket webSocket = WebSocketClient.getInstance();
            if (webSocket != null) {
                webSocket.disconnect();
            }

        } else if (statusNumber == Config.ACCEPT) {

            /**
             * when secure is false set useMask true otherwise set false
             */
            G.isSecure = true;
            WebSocket.useMask = false;

            G.ivSize = builder.getSymmetricIvSize();
            String sm = builder.getSymmetricMethod();
            G.symmetricMethod = sm.split("-")[2];
            if (G.onSecuring == null) {
                new LoginActions();
            }
            G.onSecuring.onSecure();
        }
    }

    @Override
    public void timeOut() {
        /**
         * disconnect socket for do securing action again.
         * if socket is not connect don't need to try for disconnect again because after establish
         * internet connection these steps will be done
         */
        if (WebSocketClient.isConnect()) {
            WebSocketClient.getInstance().disconnect();
        }
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}