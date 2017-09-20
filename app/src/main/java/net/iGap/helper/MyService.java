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

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {

    public static String STARTFOREGROUND_ACTION = "STARTFOREGROUND_ACTION";
    public static String STOPFOREGROUND_ACTION = "STOPFOREGROUND_ACTION";

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getExtras() != null) {

            String action = intent.getExtras().getString("ACTION");
            if (action != null) {

                if (action.equals(STARTFOREGROUND_ACTION)) {

                    Notification note = new Notification(0, null, System.currentTimeMillis());
                    note.flags |= Notification.FLAG_NO_CLEAR;
                    startForeground(142, note);
                } else if (action.equals(STOPFOREGROUND_ACTION)) {
                    stopSelf();
                }
            }
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
    }
}
