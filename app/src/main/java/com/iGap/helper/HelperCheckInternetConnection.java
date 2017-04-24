/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.iGap.G;

/**
 * Helper Class for checking internet connection
 */
public class HelperCheckInternetConnection {

    public static ConnectivityType currentConnectivityType;

    public enum ConnectivityType {
        MOBILE, WIFI
    }

    public static boolean hasNetwork() {

        try {
            ConnectivityManager cm = (ConnectivityManager) G.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && (netInfo.isConnectedOrConnecting() || netInfo.isAvailable())) {
                return true;
            }
            netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                //currentConnectivityType = ConnectivityType.MOBILE;
                return true;
            } else {
                netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    //currentConnectivityType = ConnectivityType.WIFI;
                    return true;
                }
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }
}
