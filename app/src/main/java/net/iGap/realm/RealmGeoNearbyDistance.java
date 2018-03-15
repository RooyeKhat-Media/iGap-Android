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

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmGeoNearbyDistance extends RealmObject {

    @PrimaryKey
    private long userId;
    private boolean hasComment;
    private int distance;
    private String comment;

    public static void updateComment(final long roomId, final String comment) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmGeoNearbyDistance realmGeoNearbyDistance = realm.where(RealmGeoNearbyDistance.class).equalTo(RealmGeoNearbyDistanceFields.USER_ID, roomId).findFirst();
                if (realmGeoNearbyDistance != null) {
                    realmGeoNearbyDistance.setComment(comment);
                }
            }
        });
        realm.close();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isHasComment() {
        return hasComment;
    }

    public void setHasComment(boolean hasComment) {
        this.hasComment = hasComment;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
