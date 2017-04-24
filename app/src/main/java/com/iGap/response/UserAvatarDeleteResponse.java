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
import com.iGap.proto.ProtoUserAvatarDelete;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import io.realm.Realm;

public class UserAvatarDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoUserAvatarDelete.UserAvatarDeleteResponse.Builder userAvatarDeleteResponse = (ProtoUserAvatarDelete.UserAvatarDeleteResponse.Builder) message;
        if (G.onUserAvatarDelete != null) {
            G.onUserAvatarDelete.onUserAvatarDelete(userAvatarDeleteResponse.getId(), identity);
        } else {

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, userAvatarDeleteResponse.getId()).findFirst();
                    if (realmAvatar != null) {
                        realmAvatar.deleteFromRealm();
                    }
                }
            });

            realm.close();

        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (G.onUserAvatarDelete != null) {
            G.onUserAvatarDelete.onUserAvatarDeleteError();
        }
    }
}


