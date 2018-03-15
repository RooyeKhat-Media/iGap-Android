/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import net.iGap.G;
import net.iGap.interfaces.OnGetUserInfo;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestUserInfo;

public class HelperGetUserInfo implements OnUserInfoResponse {
    OnGetUserInfo onGetUserInfo;

    public HelperGetUserInfo(OnGetUserInfo onGetUserInfo) {
        this.onGetUserInfo = onGetUserInfo;
        G.onUserInfoResponse = this;
    }

    public void getUserInfo(long userId) {
        new RequestUserInfo().userInfo(userId);
    }

    @Override
    public void onUserInfo(ProtoGlobal.RegisteredUser user, String identity) {
        onGetUserInfo.onGetUserInfo(user);
    }

    @Override
    public void onUserInfoTimeOut() {

    }

    @Override
    public void onUserInfoError(int majorCode, int minorCode) {

    }
}
