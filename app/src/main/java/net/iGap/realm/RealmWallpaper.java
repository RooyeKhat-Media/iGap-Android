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

import net.iGap.helper.HelperNumerical;
import net.iGap.module.SerializationUtils;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmWallpaper extends RealmObject {

    private long lastTimeGetList;
    private byte[] localList;
    private RealmList<RealmWallpaperProto> realmWallpaperProto;

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
                    item.setWallPaperList(realm, protoList);
                    item.setLastTimeGetList(TimeUtils.currentLocalTime());
                }

                if (localPath.length() > 0) {

                    ArrayList<String> localList = item.getLocalList();

                    if (localList == null) {

                        localList = new ArrayList<String>();
                        localList.add(localPath);
                        item.setLocalList(localList);
                    } else if (localList.indexOf(localPath) == -1) {
                        localList.add(0, localPath);
                        item.setLocalList(localList);
                    }
                }
            }
        });

        realm.close();
    }

    public RealmList<RealmWallpaperProto> getWallPaperList() {
        return realmWallpaperProto;
    }

    public void setWallPaperList(Realm realm, List<ProtoGlobal.Wallpaper> wallpaperListProto) {

        for (ProtoGlobal.Wallpaper wallpaper : wallpaperListProto) {
            RealmWallpaperProto wallProto = realm.createObject(RealmWallpaperProto.class);
            RealmAttachment realmAttachment;
            realmAttachment = RealmAttachment.putOrUpdate(realm, -HelperNumerical.getNanoTimeStamp(), null, wallpaper.getFile());
            wallProto.setColor(wallpaper.getColor());
            wallProto.setFile(realmAttachment);
            realmWallpaperProto.add(wallProto);
        }
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
