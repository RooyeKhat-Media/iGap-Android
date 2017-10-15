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
import net.iGap.proto.ProtoPushUserInfoExpired;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserInfo;

public class PushUserInfoExpiredResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public PushUserInfoExpiredResponse(int actionId, Object protoClass, String identity) { // here identity is roomId and messageId
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoPushUserInfoExpired.PushUserInfoExpiredResponse.Builder builder = (ProtoPushUserInfoExpired.PushUserInfoExpiredResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, builder.getUserId());
        if (realmRegisteredInfo != null) {
            new RequestUserInfo().userInfo(builder.getUserId());
        }
        realm.close();
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


