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

import com.iGap.proto.ProtoUserContactsEdit;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import io.realm.Realm;

public class RequestUserContactsEdit {

    public void contactsEdit(long phone, String first_name, String last_name) {
        Realm realm = Realm.getDefaultInstance();
        ProtoUserContactsEdit.UserContactsEdit.Builder builder =
                ProtoUserContactsEdit.UserContactsEdit.newBuilder();
        RealmContacts realmItem =
                realm.where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, phone).findFirst();

        if (realmItem != null) {

            builder.setPhone(phone);
            builder.setFirstName(first_name);
            builder.setLastName(last_name);
        }

        RequestWrapper requestWrapper = new RequestWrapper(109, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        realm.close();
    }
}