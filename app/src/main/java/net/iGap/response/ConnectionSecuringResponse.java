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

import com.google.protobuf.ByteString;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperString;
import net.iGap.module.AESCrypt;
import net.iGap.proto.ProtoConnectionSecuring;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestWrapper;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class ConnectionSecuringResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ConnectionSecuringResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoConnectionSecuring.ConnectionSecuringResponse.Builder builder = (ProtoConnectionSecuring.ConnectionSecuringResponse.Builder) message;
        G.currentTime = builder.getResponse().getTimestamp();
        String publicKey = builder.getPublicKey();
        int symmetricKeyLength = builder.getSymmetricKeyLength();
        G.serverHeartBeatTiming = (builder.getHeartbeatInterval() * 1000);

        String key = HelperString.generateKey(symmetricKeyLength);
        if (G.symmetricKey != null) {
            return;
        }

        G.symmetricKey = HelperString.generateSymmetricKey(key);

        byte[] encryption = null;
        try {
            RSAPublicKey rsaPublicKeyServer = (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(publicKey);
            PublicKey pubKeyServer = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPublicKeyServer.getModulus(), rsaPublicKeyServer.getPublicExponent()));

            RSAPublicKey rsaPublicKeyClient = (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(Config.PUBLIC_KEY_CLIENT);
            PublicKey pubKeyClient = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPublicKeyClient.getModulus(), rsaPublicKeyClient.getPublicExponent()));

            encryption = AESCrypt.encryptSymmetricKey(pubKeyServer, pubKeyClient, G.symmetricKey.getEncoded(), builder.getSecondaryChunkSize());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        ProtoConnectionSecuring.ConnectionSymmetricKey.Builder connectionSymmetricKey = ProtoConnectionSecuring.ConnectionSymmetricKey.newBuilder();
        connectionSymmetricKey.setSymmetricKey(ByteString.copyFrom(encryption));
        connectionSymmetricKey.setVersion(2);
        RequestWrapper requestWrapper = new RequestWrapper(2, connectionSymmetricKey);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
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

