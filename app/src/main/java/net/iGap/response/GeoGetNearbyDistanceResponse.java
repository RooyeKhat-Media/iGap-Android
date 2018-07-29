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
import net.iGap.interfaces.OnInfo;
import net.iGap.proto.ProtoGeoGetNearbyDistance;
import net.iGap.realm.RealmGeoNearbyDistance;
import net.iGap.realm.RealmRegisteredInfo;

import io.realm.Realm;

public class GeoGetNearbyDistanceResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GeoGetNearbyDistanceResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Builder builder = (ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Builder) message;

        for (final ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Result result : builder.getResultList()) {
            if (G.userId != result.getUserId()) { // don't show my account
                RealmRegisteredInfo.getRegistrationInfo(result.getUserId(), new OnInfo() {
                    @Override
                    public void onInfo(RealmRegisteredInfo registeredInfo) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmGeoNearbyDistance geoNearbyDistance = new RealmGeoNearbyDistance();
                                geoNearbyDistance.setUserId(result.getUserId());
                                geoNearbyDistance.setHasComment(result.getHasComment());
                                geoNearbyDistance.setDistance(result.getDistance());
                                realm.copyToRealmOrUpdate(geoNearbyDistance);
                            }
                        });
                        realm.close();
                    }
                });
            }
        }

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.onMapUsersGet != null) {
                    G.onMapUsersGet.onMapUsersGet();
                }
            }
        }, 250);


    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (G.onMapUsersGet != null) {
            G.onMapUsersGet.onMapUsersGet();
        }
    }
}


