/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import net.iGap.helper.HelperLog;
import net.iGap.module.SerializationUtils;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmWallpaper extends RealmObject {

    private long lastTimeGetList;
    private byte[] wallPaperList;
    private byte[] localList;

    public static void updateField(final List<ProtoGlobal.Wallpaper> protoList, final String localPath) {

        Realm realm = Realm.getDefaultInstance();

        final RealmWallpaper realmWallpaper = realm.where(RealmWallpaper.class).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmWallpaper item;

                if (realmWallpaper == null) {
                    final RealmWallpaper rw = new RealmWallpaper();
                    item = realm.copyToRealm(rw);
                } else {
                    item = realmWallpaper;
                }

                if (protoList != null) {
                    item.setWallPaperList(protoList);
                    item.setLastTimeGetList(TimeUtils.currentLocalTime());
                }

                if (localPath.length() > 0) {

                    ArrayList<String> lockalList = item.getLocalList();

                    if (lockalList == null) {

                        lockalList = new ArrayList<String>();
                        lockalList.add(localPath);
                        item.setLocalList(lockalList);
                    } else if (lockalList.indexOf(localPath) == -1) {
                        lockalList.add(0, localPath);
                        item.setLocalList(lockalList);
                    }
                }
            }
        });

        realm.close();
    }

    public List<ProtoGlobal.Wallpaper> getWallPaperList() {

        try {
            return wallPaperList == null ? null : (List<net.iGap.proto.ProtoGlobal.Wallpaper>) SerializationUtils.deserialize(wallPaperList);
        } catch (Exception e) {
            HelperLog.setErrorLog(" RealmWallpaper     getWallPaperList()       " + e.toString());
            return null;
        }

    }

    public void setWallPaperList(List<ProtoGlobal.Wallpaper> wallpaperListProto) {
        this.wallPaperList = SerializationUtils.serialize(wallpaperListProto);
    }

    public ArrayList<String> getLocalList() {
        return localList == null ? null : ((ArrayList<String>) SerializationUtils.deserialize(localList));
    }

    public void setLocalList(ArrayList<String> list) {
        this.localList = SerializationUtils.serialize(list);
    }

    public long getLastTimeGetList() {
        return lastTimeGetList;
    }

    public void setLastTimeGetList(long lastTimeGetList) {
        this.lastTimeGetList = lastTimeGetList;
    }
}
