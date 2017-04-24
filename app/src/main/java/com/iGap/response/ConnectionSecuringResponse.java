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

import com.google.protobuf.ByteString;
import com.iGap.AESCrypt;
import com.iGap.G;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoConnectionSecuring;
import com.iGap.request.RequestQueue;
import com.iGap.request.RequestWrapper;
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
            RSAPublicKey rsaPublicKey = (RSAPublicKey) HelperString.getPublicKeyFromPemFormat(publicKey);
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent()));
            encryption = AESCrypt.encryptSymmetricKey(pubKey, G.symmetricKey.getEncoded());

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        ProtoConnectionSecuring.ConnectionSymmetricKey.Builder connectionSymmetricKey = ProtoConnectionSecuring.ConnectionSymmetricKey.newBuilder();
        connectionSymmetricKey.setSymmetricKey(ByteString.copyFrom(encryption));
        RequestWrapper requestWrapper = new RequestWrapper(2, connectionSymmetricKey);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void timeOut() {
        // disconnect socket for do securing action again
        //WebSocketClient.getInstance().disconnect();
        super.timeOut();
    }

    @Override
    public void error() {
        // disconnect socket for do securing action again
        //WebSocketClient.getInstance().disconnect();
        super.error();
    }
}

