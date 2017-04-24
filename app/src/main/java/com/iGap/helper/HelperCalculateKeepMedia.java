/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import com.iGap.G;
import com.iGap.realm.RealmRoomMessage;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;


public class HelperCalculateKeepMedia {
    private RealmResults<RealmRoomMessage> mRealmList;

    public void calculateTime() { // calculate time for delete media in after 7 days

        Realm realm = Realm.getDefaultInstance();
        mRealmList = realm.where(RealmRoomMessage.class).findAll();
        for (int i = 0; i < mRealmList.size(); i++) {

            if (mRealmList.get(i).getAttachment() != null) {
                long timeMedia = mRealmList.get(i).getUpdateTime() / 1000;
                long currentTime = G.currentTime;
                long oneWeeks = (24L * 60L * 60L * 1000L);
                long b = currentTime - timeMedia;
                long last = b / oneWeeks;
                if (last >= 7) {
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
        }
    }
}
