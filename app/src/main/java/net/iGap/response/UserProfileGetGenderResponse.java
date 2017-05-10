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
import net.iGap.proto.ProtoUserProfileGetGender;
import net.iGap.realm.RealmUserInfo;

public class UserProfileGetGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();
        final ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder builder = (ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                realmUserInfo.setGender(builder.getGender());
            }
        });
        realm.close();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


