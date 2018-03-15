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

import android.os.Handler;
import android.os.Looper;

import net.iGap.G;
import net.iGap.helper.HelperTimeOut;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserContactsGetList;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;

import io.realm.Realm;

public class UserContactsGetListResponse extends MessageHandler {

    private static long getListTime;
    public int actionId;
    public Object message;
    public String identity;

    public UserContactsGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserContactsGetList.UserContactsGetListResponse.Builder builder = (ProtoUserContactsGetList.UserContactsGetListResponse.Builder) message;

        /**
         * for avoid from multiple running in same time.
         * (( hint : we have an error for this class and now use from timeout.
         * in next version of app will be checked that any of users get this error again or no ))
         */
        if (HelperTimeOut.timeoutChecking(0, getListTime, 0)) {//Config.GET_CONTACT_LIST_TIME_OUT
            getListTime = System.currentTimeMillis();

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    final Realm realm = Realm.getDefaultInstance();

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.delete(RealmContacts.class);

                            for (ProtoGlobal.RegisteredUser registerUser : builder.getRegisteredUserList()) {
                                RealmRegisteredInfo.putOrUpdate(realm, registerUser);
                                RealmContacts.putOrUpdate(realm, registerUser);
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {

                            if (G.onContactsGetList != null) {
                                G.onContactsGetList.onContactsGetList();
                            }

                            realm.close();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            realm.close();
                        }
                    });
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


