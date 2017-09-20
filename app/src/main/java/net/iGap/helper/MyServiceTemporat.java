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
import net.iGap.G;

public class MyServiceTemporat extends Service {

    public MyServiceTemporat() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {

            Notification note = new Notification(0, null, System.currentTimeMillis());
            note.flags |= Notification.FLAG_NO_CLEAR;
            startForeground(142, note);

            Intent intentService = new Intent(G.context, MyService.class);
            intentService.putExtra("ACTION", MyService.STARTFOREGROUND_ACTION);
            startService(intentService);

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopSelf();
                }
            }, 100);
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
