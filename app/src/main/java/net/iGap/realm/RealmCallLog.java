/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import net.iGap.module.SerializationUtils;
import net.iGap.proto.ProtoSignalingGetLog;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class RealmCallLog extends RealmObject {

    @PrimaryKey
    private long id;
    private String name;
    private long time;
    private byte[] logProto;

    public static void addLog(ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog callLog, Realm realm) {
        RealmCallLog realmCallLog = realm.where(RealmCallLog.class).equalTo(RealmCallLogFields.ID, callLog.getId()).findFirst();
        if (realmCallLog == null) {
            realmCallLog = realm.createObject(RealmCallLog.class, callLog.getId());
        }

        realmCallLog.setLogProto(callLog);
        realmCallLog.setName(callLog.getPeer().getDisplayName());
        realmCallLog.setTime(callLog.getOfferTime());
    }

    public static void addLogList(final List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> list) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item : list) {
                    addLog(item, realm);
                }
            }
        });
        realm.close();
    }

    public static void clearCallLog(final long clearId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmCallLog.class).lessThanOrEqualTo(RealmCallLogFields.ID, clearId).findAll().deleteAllFromRealm();
            }
        });
        realm.close();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog getLogProto() {
        return logProto == null ? null : (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog) SerializationUtils.deserialize(logProto);
    }

    public void setLogProto(ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog logProto) {
        this.logProto = SerializationUtils.serialize(logProto);
    }
}
