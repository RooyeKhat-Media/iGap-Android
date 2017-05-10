/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module.structs;

import io.realm.Realm;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmUserInfo;
import org.parceler.Parcel;

@Parcel public class StructChannelExtra {

    public long messageId = 0;
    public String signature = "";
    public String viewsLabel = "1";
    public String thumbsUp = "0";
    public String thumbsDown = "0";

    public static StructChannelExtra convert(RealmChannelExtra realmChannelExtra) {
        StructChannelExtra structChannelExtra = new StructChannelExtra();

        /*if (realmChannelExtra.getSignature().isEmpty()) {
            if (showSignature(roomId)) {
                structChannelExtra.signature = getName();
            }
        } else {
            structChannelExtra.signature = realmChannelExtra.getSignature();
        }*/

        structChannelExtra.signature = realmChannelExtra.getSignature();
        structChannelExtra.thumbsUp = realmChannelExtra.getThumbsUp();
        structChannelExtra.thumbsDown = realmChannelExtra.getThumbsDown();
        structChannelExtra.viewsLabel = realmChannelExtra.getViewsLabel();
        return structChannelExtra;
    }

    private static boolean showSignature(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        boolean signature = false;
        if (realmRoom != null && realmRoom.getChannelRoom() != null) {
            signature = realmRoom.getChannelRoom().isSignature();
        }
        realm.close();
        return signature;
    }

    private static String getName() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        String name = realmUserInfo.getUserInfo().getDisplayName();
        realm.close();
        return name;
    }

    public static StructChannelExtra makeDefaultStructure(long messageId, long roomId) {
        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.messageId = messageId;
        structChannelExtra.thumbsUp = "0";
        structChannelExtra.thumbsDown = "0";
        structChannelExtra.viewsLabel = "1";
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
            structChannelExtra.signature = realm.where(RealmUserInfo.class).findFirst().getUserInfo().getDisplayName();
        } else {
            structChannelExtra.signature = "";
        }
        realm.close();
        return structChannelExtra;
    }
}
