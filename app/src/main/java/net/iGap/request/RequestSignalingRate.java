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

import net.iGap.proto.ProtoSignalingRate;

public class RequestSignalingRate {

    public void signalingRate(long id, int rate, String reason) {
        ProtoSignalingRate.SignalingRate.Builder builder = ProtoSignalingRate.SignalingRate.newBuilder();
        builder.setId(id);
        builder.setRate(rate);
        builder.setReason(reason);

        RequestWrapper requestWrapper = new RequestWrapper(909, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
