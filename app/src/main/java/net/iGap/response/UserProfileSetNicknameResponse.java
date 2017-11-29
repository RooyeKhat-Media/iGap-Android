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
import net.iGap.proto.ProtoUserProfileNickname;
import net.iGap.realm.RealmUserInfo;

public class UserProfileSetNicknameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetNicknameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder userProfileNickNameResponse = (ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder) message;
        RealmUserInfo.updateNickname(userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getInitials());

        G.displayName = userProfileNickNameResponse.getNickname();

        if (G.onUserProfileSetNickNameResponse != null) {
            G.onUserProfileSetNickNameResponse.onUserProfileNickNameResponse(userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getInitials());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onUserProfileSetNickNameResponse != null) {
            G.onUserProfileSetNickNameResponse.onUserProfileNickNameTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        if (G.onUserProfileSetNickNameResponse != null) {
            G.onUserProfileSetNickNameResponse.onUserProfileNickNameError(majorCode, minorCode);
        }
    }
}


