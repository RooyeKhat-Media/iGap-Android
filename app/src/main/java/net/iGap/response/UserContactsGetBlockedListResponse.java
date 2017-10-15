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
import io.realm.RealmResults;
import java.util.List;
import net.iGap.interfaces.OnInfo;
import net.iGap.proto.ProtoUserContactsGetBlockedList;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmContactsFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;

public class UserContactsGetBlockedListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsGetBlockedListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder builder = (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder) message;
        final List<ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.User> list = builder.getUserList();

        Realm realm = Realm.getDefaultInstance();
        /**
         * reset blocked user in RealmRegisteredInfo
         */
        final RealmResults<RealmRegisteredInfo> results = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        if (results != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (final RealmRegisteredInfo item : results) {
                        item.setBlockUser(false);
                    }
                }
            });
        }

        /**
         * reset blocked user in RealmContacts
         */
        final RealmResults<RealmContacts> resultsContacts = realm.where(RealmContacts.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        if (resultsContacts != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (final RealmContacts item : resultsContacts) {
                        item.setBlockUser(false);
                    }
                }
            });
        }
        realm.close();

        for (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.User user : list) {
            RealmRegisteredInfo.getRegistrationInfo(user.getUserId(), new OnInfo() {
                @Override
                public void onInfo(final RealmRegisteredInfo registeredInfo) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, registeredInfo.getId());
                            RealmContacts realmContact = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, registeredInfo.getId()).findFirst();
                            if (realmRegisteredInfo != null) {
                                realmRegisteredInfo.setBlockUser(true);
                            }
                            if (realmContact != null) {
                                realmContact.setBlockUser(true);
                            }
                        }
                    });
                    realm.close();
                }
            });
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


