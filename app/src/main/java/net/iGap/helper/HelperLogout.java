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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import net.iGap.G;
import net.iGap.Theme;
import net.iGap.activities.ActivityRegisteration;
import net.iGap.module.LoginActions;
import net.iGap.module.SHP_SETTING;


/**
 * truncate realm and go to ActivityIntroduce for register again
 */
public final class HelperLogout {

    /**
     * truncate realm and go to ActivityIntroduce for register again
     */
    public static void logout() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                HelperRealm.realmTruncate();
                HelperNotificationAndBadge.cleanBadge();
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                resetStaticField();

                Intent intent = new Intent(G.context, ActivityRegisteration.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new LoginActions();
                if (G.currentActivity != null) {
                    G.currentActivity.finish();
                }
                G.context.startActivity(intent);


                try {
                    NotificationManager nMgr = (NotificationManager) G.context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }

    private static void resetStaticField() {
        Theme.setThemeColor();
        G.firstTimeEnterToApp = true;
        G.multiTab = false;
        G.isTimeWhole = false;
        G.isFirstPassCode = false;
        G.isPassCode = false;
        G.isDarkTheme = false;
        G.isAppRtl = false;
        G.isSaveToGallery = false;
        G.showSenderNameInGroup = false;
    }
}
