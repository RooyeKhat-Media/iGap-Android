/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import net.iGap.WebSocketClient;

public class ReceiverBootPhone extends BroadcastReceiver {

    /**
     * after the user has finished booting perform  this method
     */

    @Override public void onReceive(Context context, Intent intent) {
        Log.i("OOO", "onReceive");
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.i("OOO", "ACTION_BOOT_COMPLETED");
            WebSocketClient.getInstance();
        }
    }
}