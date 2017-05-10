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
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserProfileUpdateUsername;
import net.iGap.realm.RealmUserInfo;

public class UserProfileUpdateUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileUpdateUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override public void handler() {
        super.handler();
        final ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder builder = (ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                realm.where(RealmUserInfo.class).findFirst().getUserInfo().setUsername(builder.getUsername());
            }
        });
        realm.close();

        if (G.onUserProfileUpdateUsername != null) G.onUserProfileUpdateUsername.onUserProfileUpdateUsername(builder.getUsername());
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        int getWait = errorResponse.getWait();

        if (G.onUserProfileUpdateUsername != null) G.onUserProfileUpdateUsername.Error(majorCode, minorCode, getWait);
    }
}


