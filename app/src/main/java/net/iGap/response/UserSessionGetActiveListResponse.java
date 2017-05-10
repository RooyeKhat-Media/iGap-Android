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
import net.iGap.proto.ProtoUserSessionGetActiveList;
import net.iGap.realm.RealmSessionGetActiveList;

public class UserSessionGetActiveListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserSessionGetActiveListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();
        final ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Builder builder = (ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                RealmSessionGetActiveList realmSessionGetActiveList = realm.createObject(RealmSessionGetActiveList.class);

                for (ProtoUserSessionGetActiveList.UserSessionGetActiveListResponse.Session b : builder.getSessionList()) {

                    realmSessionGetActiveList.setSessionId(b.getSessionId());
                    realmSessionGetActiveList.setName(b.getAppName());
                    realmSessionGetActiveList.setAppId(b.getAppId());
                    realmSessionGetActiveList.setBuildVersion(b.getAppBuildVersion());
                    realmSessionGetActiveList.setAppVersion(b.getAppVersion());
                    realmSessionGetActiveList.setPlatform(b.getPlatform().toString());
                    realmSessionGetActiveList.setPlatformVersion(b.getPlatformVersion());
                    realmSessionGetActiveList.setDevice(b.getDevice().toString());
                    realmSessionGetActiveList.setDeviceName(b.getDeviceName());
                    realmSessionGetActiveList.setLanguage(b.getLanguage().toString());
                    realmSessionGetActiveList.setCountry(b.getCountry());
                    realmSessionGetActiveList.setCurrent(b.getCurrent());
                    realmSessionGetActiveList.setCreatTime(b.getCreateTime());
                    realmSessionGetActiveList.setActiveTime(b.getActiveTime());
                    realmSessionGetActiveList.setIp(b.getIp());
                }
            }
        });

        realm.close();

        G.onUserSessionGetActiveList.onUserSessionGetActiveList(builder.getSessionList());
    }
}
