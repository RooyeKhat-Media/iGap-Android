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

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import net.iGap.Config;
import net.iGap.module.Contacts;
import net.iGap.module.SHP_SETTING;

import static net.iGap.G.context;

public class ServiceContact extends Service {
    private MyContentObserver contentObserver;
    private long fetchContactTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        try {
            if (contentObserver != null) {
                getApplicationContext().getContentResolver().unregisterContentObserver(contentObserver);
                contentObserver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
            if (contentObserver == null) {
                contentObserver = new MyContentObserver();
                getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
            }
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (preferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1) == 1) {
            return Service.START_STICKY;
        }
        return Service.START_NOT_STICKY;
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {

            if ((ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)) {
                /**
                 * for avoid from run multiple this code at the same time
                 * because sometimes onChange was run multiple times
                 */
                if (HelperTimeOut.timeoutChecking(0, fetchContactTime, Config.FETCH_CONTACT_TIME_OUT)) {
                    fetchContactTime = System.currentTimeMillis();
                    new Contacts.FetchContactForServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }


    }

}
