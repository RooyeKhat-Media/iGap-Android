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

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder builder = (ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder) message;
        RealmUserInfo.updateUsername(builder.getUsername());

        if (G.onUserProfileUpdateUsername != null) {
            G.onUserProfileUpdateUsername.onUserProfileUpdateUsername(builder.getUsername());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onUserProfileUpdateUsername != null) {
            G.onUserProfileUpdateUsername.timeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        int getWait = errorResponse.getWait();

        if (G.onUserProfileUpdateUsername != null) {
            G.onUserProfileUpdateUsername.Error(majorCode, minorCode, getWait);
        }
    }
}


