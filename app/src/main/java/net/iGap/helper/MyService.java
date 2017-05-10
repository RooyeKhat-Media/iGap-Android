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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {

    public MyService() {
    }

    @Override public void onCreate() {
        super.onCreate();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        if (startId == 24) {
            return Service.START_NOT_STICKY;
        } else {
            return Service.START_STICKY;
        }
    }

    @Override public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override public void onDestroy() {
        super.onDestroy();

        Intent i = new Intent("stop");
        onStartCommand(i, 12, 24);
        stopSelf();
    }
}
