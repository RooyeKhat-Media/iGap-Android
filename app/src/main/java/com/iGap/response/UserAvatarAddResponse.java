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
import com.iGap.proto.ProtoUserAvatarAdd;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;

public class UserAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoUserAvatarAdd.UserAvatarAddResponse.Builder userAvatarAddResponse = (ProtoUserAvatarAdd.UserAvatarAddResponse.Builder) message;

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    final long userId = realmUserInfo.getUserInfo().getId();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmAvatar.put(userId, userAvatarAddResponse.getAvatar(), true);
                        }
                    });
                }
                realm.close();
            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.onUserAvatarResponse != null) {
                    G.onUserAvatarResponse.onAvatarAdd(userAvatarAddResponse.getAvatar());
                }
            }
        }, 1000);
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onUserAvatarResponse != null) {
            G.onUserAvatarResponse.onAvatarAddTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        if (G.onUserAvatarResponse != null) {
            G.onUserAvatarResponse.onAvatarError();
        }
    }
}


