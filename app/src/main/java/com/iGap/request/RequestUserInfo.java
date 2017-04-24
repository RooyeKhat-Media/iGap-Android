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

import com.iGap.proto.ProtoUserInfo;

public class RequestUserInfo {

    public void userInfo(long userId) {
        ProtoUserInfo.UserInfo.Builder builder = ProtoUserInfo.UserInfo.newBuilder();
        builder.setUserId(userId);

        RequestWrapper requestWrapper = new RequestWrapper(117, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * use this identity for when request for get member list and then get
     * user info for members .
     * should update view just when user info is for that group that send
     * request.
     * maybe send request in group profile and then go to another group
     * profile , that we don't send request get member list for that.
     *
     * @param userId
     * @param identity roomId
     */

    public void userInfo(long userId, String identity) {
        ProtoUserInfo.UserInfo.Builder builder = ProtoUserInfo.UserInfo.newBuilder();
        builder.setUserId(userId);

        RequestWrapper requestWrapper = new RequestWrapper(117, builder, identity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

