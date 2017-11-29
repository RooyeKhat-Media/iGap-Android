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

import net.iGap.proto.ProtoUserContactsEdit;

public class RequestUserContactsEdit {

    public void contactsEdit(long userId, long phone, String first_name, String last_name) {
        ProtoUserContactsEdit.UserContactsEdit.Builder builder = ProtoUserContactsEdit.UserContactsEdit.newBuilder();
        builder.setPhone(phone);
        builder.setFirstName(first_name);
        builder.setLastName(last_name);

        RequestWrapper requestWrapper = new RequestWrapper(109, builder, userId + "");
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}