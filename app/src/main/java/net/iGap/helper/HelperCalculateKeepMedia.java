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

import android.content.Context;
import android.content.SharedPreferences;

import net.iGap.G;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmRoomMessage;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmResults;

public class HelperCalculateKeepMedia {

    /**
     * calculate time for delete media in after Specified time
     */

    public void calculateTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<RealmRoomMessage> mRealmList = realm.where(RealmRoomMessage.class).findAll();
                SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(SHP_SETTING.KEY_KEEP_MEDIA_TIME, G.currentTime);
                editor.apply();
                for (int i = 0; i < mRealmList.size(); i++) {
                    if (mRealmList.get(i).getAttachment() != null) {
                        String filePath = mRealmList.get(i).getAttachment().getLocalFilePath();
                        if (filePath != null) {
                            new File(filePath).delete();
                        }
                        String filePathThumbnail = mRealmList.get(i).getAttachment().getLocalThumbnailPath();
                        if (filePathThumbnail != null) {
                            new File(filePathThumbnail).delete();
                        }
                    }
                }
                realm.close();
            }
        }).start();
    }
}
