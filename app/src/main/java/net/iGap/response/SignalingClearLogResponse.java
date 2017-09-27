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
import net.iGap.proto.ProtoSignalingClearLog;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmCallLogFields;

public class SignalingClearLogResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public SignalingClearLogResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoSignalingClearLog.SignalingClearLogResponse.Builder builder = (ProtoSignalingClearLog.SignalingClearLogResponse.Builder) message;

        final long clearId = builder.getClearId();  // delete all call logs smaller then clearId

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmCallLog.class).lessThanOrEqualTo(RealmCallLogFields.ID, clearId).findAll().deleteAllFromRealm();
            }
        });
        realm.close();

        if (G.onCallLogClear != null) {
            G.onCallLogClear.onCallLogClear();
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


