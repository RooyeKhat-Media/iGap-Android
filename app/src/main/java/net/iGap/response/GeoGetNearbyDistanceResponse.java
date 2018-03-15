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

        final ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Builder builder = (ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Builder) message;
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(final Realm realm) {
                        for (final ProtoGeoGetNearbyDistance.GeoGetNearbyDistanceResponse.Result result : builder.getResultList()) {
                            if (G.userId != result.getUserId()) { // don't show my account
                                RealmRegisteredInfo.getRegistrationInfo(result.getUserId(), new OnInfo() {
                                    @Override
                                    public void onInfo(RealmRegisteredInfo registeredInfo) {
                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                final Realm realm = Realm.getDefaultInstance();
                                                realm.executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        RealmGeoNearbyDistance geoNearbyDistance = realm.createObject(RealmGeoNearbyDistance.class, result.getUserId());
                                                        geoNearbyDistance.setHasComment(result.getHasComment());
                                                        geoNearbyDistance.setDistance(result.getDistance());
                                                    }
                                                }, new OnSuccess() {
                                                    @Override
                                                    public void onSuccess() {
                                                        if (G.onMapUsersGet != null) {
                                                            G.onMapUsersGet.onMapUsersGet(result.getUserId());
                                                        }
                                                        realm.close();
                                                    }
                                                }, new OnError() {
                                                    @Override
                                                    public void onError(Throwable error) {
                                                        realm.close();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
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


