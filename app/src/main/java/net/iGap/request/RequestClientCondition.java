/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.proto.ProtoClientCondition;

public class RequestClientCondition {

    public void clientCondition(ProtoClientCondition.ClientCondition.Builder clientCondition) {
        if (G.onUpdating != null) {
            G.onUpdating.onUpdating();
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    G.onUpdating.onCancelUpdating();
                }
            }, Config.UPDATING_TIME_SHOWING);
        }

        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}