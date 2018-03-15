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

import android.text.format.DateUtils;

import net.iGap.interfaces.OnInfo;
import net.iGap.proto.ProtoUserInfo;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RequestUserInfo {
    public static final int CLEAR_ARRAY_TIME = (int) (3 * DateUtils.SECOND_IN_MILLIS);
    public static HashMap<Long, OnInfo> infoHashMap = new HashMap<>();
    public static CopyOnWriteArrayList<String> userIdArrayList = new CopyOnWriteArrayList<>(); // ids that exist in list don't allowed to send request again

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

    /**
     * send userInfo request to server and avoid from resend request
     * at lower than {@link RequestUserInfo#CLEAR_ARRAY_TIME} again
     * <p>
     * HINT: haven't use this method for all get user info because
     * in some state maybe need send multiple request.
     * for example when client try for create new chat with a contact
     * we send a request when coming to ActivityChat and send a
     * request after send message
     */
    public void userInfoAvoidDuplicate(long userId) {
        if (!userIdArrayList.contains(String.valueOf(userId))) {
            userIdArrayList.add(String.valueOf(userId));
            ProtoUserInfo.UserInfo.Builder builder = ProtoUserInfo.UserInfo.newBuilder();
            builder.setUserId(userId);

            RequestWrapper requestWrapper = new RequestWrapper(117, builder, userId + "");
            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public enum InfoType {
        JUST_INFO, UPDATE_ROOM
    }
}

