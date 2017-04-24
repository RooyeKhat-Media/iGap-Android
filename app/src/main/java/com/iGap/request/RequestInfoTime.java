/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoInfoTime;
import com.iGap.proto.ProtoRequest;

public class RequestInfoTime {

    public void infoTime() {

        ProtoInfoTime.InfoTime.Builder infoTime = ProtoInfoTime.InfoTime.newBuilder();
        infoTime.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        RequestWrapper requestWrapper = new RequestWrapper(502, infoTime);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
