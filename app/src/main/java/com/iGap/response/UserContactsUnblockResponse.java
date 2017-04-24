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
import com.iGap.proto.ProtoUserContactsUnblock;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import io.realm.Realm;

public class UserContactsUnblockResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsUnblockResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsUnblock.UserContactsUnblockResponse.Builder builder = (ProtoUserContactsUnblock.UserContactsUnblockResponse.Builder) message;
        long userID = builder.getUserId();

        Realm realm = Realm.getDefaultInstance();

        // set Unblock to realm realmRegisteredInfo
        final RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userID).findFirst();
        if (realmRegisteredInfo != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmRegisteredInfo.setBlockUser(false);
                }
            });
        }

        // set Unblock to realm contact
        final RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userID).findFirst();
        if (realmContacts != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmContacts.setBlockUser(false);
                }
            });
        }

        realm.close();

        if (G.onUserContactsUnBlock != null) {
            G.onUserContactsUnBlock.onUserContactsUnBlock(builder.getUserId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


