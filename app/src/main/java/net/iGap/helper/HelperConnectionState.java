/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.support.design.widget.Snackbar;
import android.view.View;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnterPassCode;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityManageSpace;
import net.iGap.activities.ActivityRegisteration;
import net.iGap.module.enums.ConnectionState;

/**
 * manage connection state for showing state in main page
 */
public class HelperConnectionState {

    public static Snackbar snack = null;

    public static void connectionState(final ConnectionState connectionState) {

        if (connectionState != ConnectionState.IGAP) {
            if (G.onCallLeaveView != null) {
                G.onCallLeaveView.onLeaveView("");
            }
        }


        if (HelperCheckInternetConnection.hasNetwork()) {
            if (G.onConnectionChangeState != null) {
                G.onConnectionChangeState.onChangeState(connectionState);
            }
            if (G.onConnectionChangeStateChat != null) {
                G.onConnectionChangeStateChat.onChangeState(connectionState);
            }

            G.connectionState = connectionState;
        } else {
            if (G.onConnectionChangeState != null) {
                G.onConnectionChangeState.onChangeState(ConnectionState.WAITING_FOR_NETWORK);
            }
            if (G.onConnectionChangeStateChat != null) {
                G.onConnectionChangeStateChat.onChangeState(ConnectionState.WAITING_FOR_NETWORK);
            }
            G.connectionState = ConnectionState.WAITING_FOR_NETWORK;
        }
        if (G.currentActivity != null && !G.currentActivity.getLocalClassName().equals(G.latestActivityName)) {
            G.latestConnectionState = ConnectionState.UPDATING;
        }

        if ((G.currentActivity instanceof ActivityMain || G.currentActivity instanceof ActivityEnterPassCode || G.currentActivity instanceof ActivityRegisteration || G.currentActivity instanceof ActivityManageSpace) && (!G.isFragmentMapActive
            || connectionState == ConnectionState.IGAP
            || connectionState == ConnectionState.UPDATING)) {

            if (snack != null) {
                if (snack.isShown()) {
                    snack.dismiss();
                }
                snack = null;
            }
        } else {

            if (G.latestConnectionState != G.connectionState) {

                if (G.currentActivity != null) {
                    G.latestActivityName = G.currentActivity.getLocalClassName();
                }
                G.latestConnectionState = G.connectionState;
                String message = G.context.getResources().getString(R.string.waiting_for_network);

                if (G.connectionState == ConnectionState.WAITING_FOR_NETWORK) {
                    message = G.context.getResources().getString(R.string.waiting_for_network);

                    if (G.iCallFinishChat != null) {
                        G.iCallFinishChat.onFinish();
                    }
                    if (G.iCallFinishMain != null) {
                        G.iCallFinishMain.onFinish();
                    }

                } else if (G.connectionState == ConnectionState.CONNECTING) {
                    message = G.context.getResources().getString(R.string.connecting);
                }

                final String finalMessage = message;
                if (G.currentActivity != null) {
                    final String finalMessage2 = message;
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            snack = null;
                            snack = Snackbar.make(G.currentActivity.findViewById(android.R.id.content), finalMessage, 600000);
                            snack.setAction(R.string.cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (snack != null) {
                                        snack.dismiss();
                                    }
                                }
                            });
                            snack.setText(finalMessage2);
                            snack.show();
                        }
                    });
                }
            }
        }
    }
}
