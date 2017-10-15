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
import net.iGap.proto.ProtoUserContactsBlock;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;

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
        long userId = builder.getUserId();

        Realm realm = Realm.getDefaultInstance();

        // set block to realm registeredInfo
        final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
        if (realmRegisteredInfo != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmRegisteredInfo.setBlockUser(true);
                }
            });
        }

        // set block to realm contact
        final RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
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

        //+manually update (avoid use from realm-adapter)
        if (G.onBlockStateChanged != null) {
            G.onBlockStateChanged.onBlockStateChanged(true, builder.getUserId());
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


