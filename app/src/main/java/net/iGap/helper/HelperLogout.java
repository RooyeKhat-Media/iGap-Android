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
            @Override public void run() {
                HelperRealm.realmTruncate();
                HelperNotificationAndBadge.updateBadgeOnly();
                Intent intent = new Intent(G.context, ActivityRegisteration.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new LoginActions();
                G.context.startActivity(intent);
                if (G.currentActivity != null) {
                    G.currentActivity.finish();
                }

                G.firstTimeEnterToApp = true;
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();

                try {
                    NotificationManager nMgr = (NotificationManager) G.context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });
    }
}
