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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import net.iGap.G;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.proto.ProtoFileDownload;

public class RequestFileDownload {

    public static int maxLimitDownload = 0;
    private final int KB_10 = 10 * 1024;
    private final int KB_30 = 30 * 1024;
    private final int KB_50 = 50 * 1024;
    private final int KB_100 = 100 * 1024;

    public void download(String token, long offset, int maxLimit, ProtoFileDownload.FileDownload.Selector selector, Object identity) {
        ProtoFileDownload.FileDownload.Builder builder = ProtoFileDownload.FileDownload.newBuilder();

        if (token == null) {
            return;
        }

        builder.setToken(token);
        builder.setOffset(offset);
        builder.setMaxLimit(getMaxLimitDownload());
        builder.setSelector(selector);

        try {
            RequestWrapper requestWrapper = new RequestWrapper(705, builder, identity);
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the connection is fast
     */
    public int getMaxLimitDownload() {
        if (maxLimitDownload > 0) {
            return maxLimitDownload;
        }

        int maxLimit;

        if (HelperCheckInternetConnection.connectivityType == -1) {
            HelperCheckInternetConnection.detectConnectionTypeForDownload();
        }

        if (HelperCheckInternetConnection.connectivityType == ConnectivityManager.TYPE_WIFI) {
            int linkSpeed = 10;

            WifiManager wifiManager = (WifiManager) G.context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    linkSpeed = wifiInfo.getLinkSpeed();  // Mbps
                    linkSpeed = (linkSpeed * 1024);   // Kbps
                    //linkSpeed = (linkSpeed * 1024) / 8; // KByte per second
                }
            }

            if (linkSpeed < 100) {
                maxLimit = KB_10;
            } else if (linkSpeed < 1000) {
                maxLimit = KB_30;
            } else if (linkSpeed < 1500) {
                maxLimit = KB_50;
            } else {
                maxLimit = KB_100;
            }

        } else if (HelperCheckInternetConnection.connectivityType == ConnectivityManager.TYPE_MOBILE) {
            switch (HelperCheckInternetConnection.connectivitySubType) {
                case TelephonyManager.NETWORK_TYPE_CDMA:  // ~ 14-64  kbps
                case TelephonyManager.NETWORK_TYPE_IDEN:  // ~ 25     kbps -> API level 8
                case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:  // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:  // ~ 100    kbps
                    maxLimit = KB_10;
                    break;


                case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
                    maxLimit = KB_30;
                    break;


                case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
                    maxLimit = KB_50;
                    break;


                case TelephonyManager.NETWORK_TYPE_EHRPD:  // ~ 1-2   Mbps -> API level 11
                case TelephonyManager.NETWORK_TYPE_HSUPA:  // ~ 1-23  Mbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:  // ~ 2-14  Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5     Mbps -> API level 9
                case TelephonyManager.NETWORK_TYPE_HSPAP:  // ~ 10-20 Mbps -> API level 13
                case TelephonyManager.NETWORK_TYPE_LTE:    // ~ 10+   Mbps -> API level 11
                    maxLimit = KB_100;
                    break;


                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    maxLimit = KB_10;
            }
        } else {
            maxLimit = KB_10;
        }

        maxLimitDownload = maxLimit;
        return maxLimit;
    }

    public static class IdentityFileDownload {
        public String cacheId;
        public String filepath;
        public ProtoFileDownload.FileDownload.Selector selector;
        public long size;
        public long offset;
        public boolean isFromHelperDownload;

        public IdentityFileDownload(String cacheId, String filepath, ProtoFileDownload.FileDownload.Selector selector, long size, long offset, boolean isFromHelperDownload) {
            this.cacheId = cacheId;
            this.filepath = filepath;
            this.selector = selector;
            this.size = size;
            this.offset = offset;
            this.isFromHelperDownload = isFromHelperDownload;
        }
    }
}