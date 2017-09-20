/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoGeoGetNearbyDistance;

public class RequestGeoGetNearbyDistance {

    public void getNearbyDistance(double latitude, double longitude) {
        ProtoGeoGetNearbyDistance.GeoGetNearbyDistance.Builder builder = ProtoGeoGetNearbyDistance.GeoGetNearbyDistance.newBuilder();
        builder.setLat(latitude);
        builder.setLon(longitude);

        RequestWrapper requestWrapper = new RequestWrapper(1005, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
