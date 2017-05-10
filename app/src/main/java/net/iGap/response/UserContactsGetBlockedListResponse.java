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

    @Override public void handler() {
        super.handler();

        ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder builder = (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.Builder) message;
        List<ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.User> list = builder.getUserList();

        Realm realm = Realm.getDefaultInstance();

        // reset blocked user in RealmRegisteredInfo
        RealmResults<RealmRegisteredInfo> results = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        if (results != null) {
            for (final RealmRegisteredInfo item : results) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        item.setBlockUser(false);
                    }
                });
            }
        }

        // reset blocked user in RealmContacts
        RealmResults<RealmContacts> resultsContacts = realm.where(RealmContacts.class).equalTo(RealmRegisteredInfoFields.BLOCK_USER, true).findAll();
        if (resultsContacts != null) {
            for (final RealmContacts item : resultsContacts) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        item.setBlockUser(false);
                    }
                });
            }
        }

        // add blocked user to RealmRegisteredInfo and  realmContacts
        for (ProtoUserContactsGetBlockedList.UserContactsGetBlockedListResponse.User user : list) {

            final RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getUserId()).findFirst();
            if (realmRegisteredInfo != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        realmRegisteredInfo.setBlockUser(true);
                    }
                });
            }

            final RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, user.getUserId()).findFirst();
            if (realmContacts != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        realmContacts.setBlockUser(true);
                    }
                });
            }
        }

        realm.close();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


