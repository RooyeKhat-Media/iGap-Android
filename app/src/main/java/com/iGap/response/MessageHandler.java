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

import android.support.annotation.CallSuper;
import android.util.Log;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperError;
import com.iGap.proto.ProtoError;

import static com.iGap.helper.HelperTimeOut.heartBeatTimeOut;

public abstract class MessageHandler {

    public Object message;
    int actionId;
    String identity;

    public MessageHandler(int actionId, Object protoClass, String identity) {
        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @CallSuper
    public void handler() throws NullPointerException {
        Log.i("MSGH", "MessageHandler handler : " + actionId + " || " + message);
        //Log.i("LLL", "MessageHandler handler : " + actionId + " || Response => " + G.lookupMap.get(actionId));
    }

    @CallSuper
    public void timeOut() {
        if (heartBeatTimeOut()) {
            Log.i("HHH", "heartBeatTimeOut");
            WebSocketClient.reconnect(true);
        } else {
            Log.i("HHH", "Not Time Out HeartBeat");
        }
        Log.i("MSGT", "MessageHandler timeOut : " + actionId + " || " + message);
        error();
    }

    @CallSuper
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        HelperError.showSnackMessage(HelperError.getErrorFromCode(majorCode, minorCode));

        Log.i("MSGE", "MessageHandler error : " + actionId + " || " + message);
        //Log.i("LLL", "MessageHandler timeOut/error : " + actionId + " || Response => " + G.lookupMap.get(actionId) + " || code : " + majorCode + "," + minorCode + " || reason => " + errorResponse.getMessage());
    }
}
