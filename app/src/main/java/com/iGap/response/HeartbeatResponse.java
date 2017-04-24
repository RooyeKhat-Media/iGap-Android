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

import android.util.Log;
import com.iGap.request.RequestHeartbeat;

import static com.iGap.G.latestHearBeatTime;

public class HeartbeatResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public HeartbeatResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        latestHearBeatTime = System.currentTimeMillis();
        Log.i("HHH", "latestHearBeatTime : " + latestHearBeatTime);
        new RequestHeartbeat().heartBeat();
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


