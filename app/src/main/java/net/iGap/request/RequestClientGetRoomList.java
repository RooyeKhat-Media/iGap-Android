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

import net.iGap.proto.ProtoClientGetRoomList;

public class RequestClientGetRoomList {

    public void clientGetRoomList(int offset, int limit) {
        clientGetRoomList(offset, limit, false);
    }

    public void clientGetRoomList(int offset, int limit, boolean fromLogin) {
        ProtoClientGetRoomList.ClientGetRoomList.Builder clientGetRoomList = ProtoClientGetRoomList.ClientGetRoomList.newBuilder();
        //clientGetRoomList.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        clientGetRoomList.setPagination(new RequestPagination().pagination(offset, limit));

        RequestWrapper requestWrapper = new RequestWrapper(601, clientGetRoomList, fromLogin + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}