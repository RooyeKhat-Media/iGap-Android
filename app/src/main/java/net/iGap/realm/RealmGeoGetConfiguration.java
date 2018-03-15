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

import net.iGap.fragments.FragmentiGapMap;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmGeoGetConfiguration extends RealmObject {
    private String mapCache;

    public static void putOrUpdate(final String mapCache) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmGeoGetConfiguration realmGeoGetConfiguration = realm.where(RealmGeoGetConfiguration.class).findFirst();
                if (realmGeoGetConfiguration == null) {
                    realmGeoGetConfiguration = realm.createObject(RealmGeoGetConfiguration.class);
                } else {
                    if (realmGeoGetConfiguration.getMapCache() != null && !realmGeoGetConfiguration.getMapCache().equals(mapCache)) {
                        FragmentiGapMap.deleteMapFileCash();
                    }
                }
                realmGeoGetConfiguration.setMapCache(mapCache);
            }
        });
        realm.close();
    }

    public String getMapCache() {
        return mapCache;
    }

    public void setMapCache(String mapCache) {
        this.mapCache = mapCache;
    }
}
