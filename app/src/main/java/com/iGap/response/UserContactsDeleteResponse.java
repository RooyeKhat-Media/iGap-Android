/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserContactsDelete;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import io.realm.Realm;

public class UserContactsDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserContactsDelete.UserContactsDeleteResponse.Builder builder = (ProtoUserContactsDelete.UserContactsDeleteResponse.Builder) message;
        final long phone = builder.getPhone();

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmContacts realmUserContactsGetListResponse = realm.where(RealmContacts.class).equalTo("phone", phone).findFirst();
                if (realmUserContactsGetListResponse != null) {
                    realmUserContactsGetListResponse.deleteFromRealm();
                }

                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.PHONE_NUMBER, phone + "").findFirst();
                if (realmRegisteredInfo != null) {
                    realmRegisteredInfo.setMutual(false);
                }
            }
        });
        realm.close();

        G.onUserContactdelete.onContactDelete();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int MajorCode = errorResponse.getMajorCode();
        int MinorCode = errorResponse.getMinorCode();

        G.onUserContactdelete.onError(MajorCode, MinorCode);

    }
}


