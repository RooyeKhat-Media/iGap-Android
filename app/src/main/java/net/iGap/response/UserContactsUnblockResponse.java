/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import io.realm.Realm;
import net.iGap.G;
import net.iGap.proto.ProtoUserContactsUnblock;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;

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
        long userId = builder.getUserId();

        Realm realm = Realm.getDefaultInstance();

        // set Unblock to realm realmRegisteredInfo
        final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
        if (realmRegisteredInfo != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmRegisteredInfo.setBlockUser(false);
                }
            });
        }

        // set Unblock to realm contact
        final RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
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

        //+manually update (avoid use from realm-adapter)
        if (G.onBlockStateChanged != null) {
            G.onBlockStateChanged.onBlockStateChanged(false, builder.getUserId());
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


