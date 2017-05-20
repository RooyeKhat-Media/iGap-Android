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

import net.iGap.proto.ProtoSignalingSessionHold;

public class RequestSignalingSessionHold {

    public void signalingSessionHold(boolean hold) {

        ProtoSignalingSessionHold.SignalingSessionHold.Builder builder = ProtoSignalingSessionHold.SignalingSessionHold.newBuilder();
        builder.setHold(hold);

        RequestWrapper requestWrapper = new RequestWrapper(906, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
