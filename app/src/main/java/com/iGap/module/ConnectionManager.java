package com.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;
import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperCheckInternetConnection;
import com.iGap.helper.HelperConnectionState;
import com.iGap.helper.HelperTimeOut;
import com.iGap.module.enums.ConnectionState;
import com.neovisionaries.ws.client.WebSocket;

import static com.iGap.G.context;
import static com.iGap.G.hasNetworkBefore;
import static com.iGap.G.latestConnectivityType;
import static com.iGap.G.latestMobileDataState;
import static com.iGap.WebSocketClient.allowForReconnecting;
import static com.iGap.WebSocketClient.reconnect;

public class ConnectionManager {

    public static void manageConnection() {
        getCurrentConnectionType();
        initializeReceiver();
    }

    /**
     * detect that current connection type is mobile data or wifi
     */
    private static void getCurrentConnectionType() {
        if (Connectivity.isConnectedMobile(context)) {
            HelperCheckInternetConnection.currentConnectivityType = HelperCheckInternetConnection.ConnectivityType.MOBILE;
            latestConnectivityType = HelperCheckInternetConnection.ConnectivityType.MOBILE;
            latestMobileDataState = true;
            hasNetworkBefore = true;
        } else if (Connectivity.isConnectedWifi(context)) {
            HelperCheckInternetConnection.currentConnectivityType = HelperCheckInternetConnection.ConnectivityType.WIFI;
            latestConnectivityType = HelperCheckInternetConnection.ConnectivityType.WIFI;
            latestMobileDataState = false;
            hasNetworkBefore = true;
        }
    }

    /**
     * initialize internet receiver for detect change connection state
     */
    private static void initializeReceiver() {
        BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (Connectivity.isConnectedMobile(context)) {
                    /**
                     * isConnectedMobile
                     */
                    HelperCheckInternetConnection.currentConnectivityType = HelperCheckInternetConnection.ConnectivityType.MOBILE;
                } else if (Connectivity.isConnectedWifi(context)) {
                    /**
                     * isConnectedWifi
                     */
                    HelperCheckInternetConnection.currentConnectivityType = HelperCheckInternetConnection.ConnectivityType.WIFI;
                }

                if (HelperCheckInternetConnection.hasNetwork()) {
                    /**
                     * Has Network
                     */
                    if (!hasNetworkBefore) {
                        /**
                         * before no network
                         */
                        latestConnectivityType = HelperCheckInternetConnection.currentConnectivityType;
                        hasNetworkBefore = true;
                        allowForReconnecting = true;
                        reconnect(true);
                    } else {
                        /**
                         * before has network
                         */
                        if (latestConnectivityType == null || latestConnectivityType != HelperCheckInternetConnection.currentConnectivityType) {
                            /**
                             * change connectivity type
                             */
                            latestConnectivityType = HelperCheckInternetConnection.currentConnectivityType;
                            allowForReconnecting = true;
                            WebSocket webSocket = WebSocketClient.getInstance();
                            if (webSocket != null) {
                                webSocket.disconnect();
                            }
                        } else {
                            /**
                             * not change connectivity type
                             * hint : if call twice or more this receiver , in second time will be called this section .
                             */
                            if (HelperTimeOut.heartBeatTimeOut()) {
                                Log.i("HHH", "Connection heartBeatTimeOut");
                                //reconnect(true);
                            } else {
                                Log.i("HHH", "Connection Not Time Out HeartBeat");
                            }
                        }
                    }
                } else {
                    /**
                     * No Network
                     */
                    hasNetworkBefore = false;
                    HelperConnectionState.connectionState(ConnectionState.WAITING_FOR_NETWORK);
                    G.socketConnection = false;
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkStateReceiver, filter);
    }
}
