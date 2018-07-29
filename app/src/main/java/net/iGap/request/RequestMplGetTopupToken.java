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

import net.iGap.proto.ProtoMplGetTopupToken;

public class RequestMplGetTopupToken {

    public void mplGetTopupToken(long chargeMobileNumber, long amount, ProtoMplGetTopupToken.MplGetTopupToken.Type type) {
        ProtoMplGetTopupToken.MplGetTopupToken.Builder builder = ProtoMplGetTopupToken.MplGetTopupToken.newBuilder();
        builder.setChargeMobileNumber(chargeMobileNumber);
        builder.setAmount(amount);
        builder.setType(type);

        RequestWrapper requestWrapper = new RequestWrapper(9101, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
