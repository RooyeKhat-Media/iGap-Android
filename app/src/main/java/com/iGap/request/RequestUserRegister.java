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

import android.os.Bundle;
import com.iGap.activities.ActivityEnhanced;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoUserRegister;

public class RequestUserRegister extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // build proto class
        ProtoUserRegister.UserRegister.Builder builder =
                ProtoUserRegister.UserRegister.newBuilder();
        builder.setCountryCode("IR");
        builder.setPhoneNumber(1123456789L);
        ProtoUserRegister.UserRegister userRegister = builder.build();

        String currentClassName = this.getClass().getSimpleName();
        int actionId = HelperString.getActionId(currentClassName);

        // initialize request wrapper
        RequestWrapper requestWrapper = new RequestWrapper(actionId, userRegister);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
