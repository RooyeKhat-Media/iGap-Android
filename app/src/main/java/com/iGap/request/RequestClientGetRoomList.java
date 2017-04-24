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
import com.iGap.proto.ProtoClientGetRoomList;
import com.iGap.proto.ProtoRequest;

public class RequestClientGetRoomList {

    public void clientGetRoomList() {
        ProtoClientGetRoomList.ClientGetRoomList.Builder clientGetRoomList = ProtoClientGetRoomList.ClientGetRoomList.newBuilder();
        clientGetRoomList.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        RequestWrapper requestWrapper = new RequestWrapper(601, clientGetRoomList);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}