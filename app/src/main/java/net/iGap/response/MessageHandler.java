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

import android.support.annotation.CallSuper;
import android.util.Log;
import android.widget.Toast;
import net.iGap.BuildConfig;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperError;
import net.iGap.proto.ProtoError;

import static net.iGap.G.latestResponse;
import static net.iGap.helper.HelperTimeOut.heartBeatTimeOut;

public abstract class MessageHandler {

    public Object message;
    int actionId;
    Object identity;

    public MessageHandler(int actionId, Object protoClass, Object identity) {
        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @CallSuper
    public void handler() throws NullPointerException {
        if (BuildConfig.DEBUG) {
            Log.i("MSGH", "MessageHandler handler : " + actionId + " || " + G.lookupMap.get(actionId) + " || " + message);
        }
        latestResponse = System.currentTimeMillis();
    }

    @CallSuper
    public void timeOut() {
        if (heartBeatTimeOut()) {
            if (BuildConfig.DEBUG) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(G.context, "MessageHandler HeartBeat TimeOut", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            WebSocketClient.reconnect(true);
        }
        error();
    }

    @CallSuper
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        HelperError.showSnackMessage(HelperError.getErrorFromCode(majorCode, minorCode));

        if (BuildConfig.DEBUG) {
            Log.i("MSGE", "MessageHandler error : " + actionId + " || " + G.lookupMap.get(actionId) + " || " + message);
        }
    }
}
