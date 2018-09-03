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

import android.util.Log;

import net.iGap.interfaces.OnInfo;
import net.iGap.proto.ProtoUserContactsGetBlockedList;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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


        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    RealmRegisteredInfo.getRegistrationInfo(user.getUserId(), user.getCacheId(), new OnInfo() {
                        @Override
                        public void onInfo(final RealmRegisteredInfo registeredInfo) {
                            RealmRegisteredInfo.updateBlock(registeredInfo.getId(), true);
                            RealmContacts.updateBlock(registeredInfo.getId(), true);
                        }
                    });
                }
            }
        }).start();

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


