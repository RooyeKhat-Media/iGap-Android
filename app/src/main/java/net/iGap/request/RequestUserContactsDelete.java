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

import net.iGap.proto.ProtoUserContactsDelete;

public class RequestUserContactsDelete {

    public void contactsDelete(String phone) {

        ProtoUserContactsDelete.UserContactsDelete.Builder builder = ProtoUserContactsDelete.UserContactsDelete.newBuilder();
        builder.setPhone(Long.parseLong(phone));
        RequestWrapper requestWrapper = new RequestWrapper(108, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}