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

import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserProfileEmail;

public class RequestUserProfileSetEmail {

    public void setUserProfileEmail(String email) {
        ProtoUserProfileEmail.UserProfileSetEmail.Builder userProfileEmail = ProtoUserProfileEmail.UserProfileSetEmail.newBuilder();
        userProfileEmail.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userProfileEmail.setEmail(email);

        RequestWrapper requestWrapper = new RequestWrapper(103, userProfileEmail);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
