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
import com.iGap.proto.ProtoUserContactsBlock;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmContactsFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import io.realm.Realm;

public class UserContactsBlockResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsBlockResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsBlock.UserContactsBlockResponse.Builder builder = (ProtoUserContactsBlock.UserContactsBlockResponse.Builder) message;
        long userID = builder.getUserId();

        Realm realm = Realm.getDefaultInstance();

        // set block to realm registeredInfo
        final RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userID).findFirst();
        if (realmRegisteredInfo != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmRegisteredInfo.setBlockUser(true);
                }
            });
        }

        // set block to realm contact
        final RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userID).findFirst();
        if (realmContacts != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmContacts.setBlockUser(true);
                }
            });
        }

        realm.close();

        if (G.onUserContactsBlock != null) {
            G.onUserContactsBlock.onUserContactsBlock(builder.getUserId());
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


