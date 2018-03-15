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

import net.iGap.G;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmRoom;

import org.parceler.Parcel;

@Parcel
public class StructChannelExtra {

    public long messageId = 0;
    public String signature = "";
    public String viewsLabel = "1";
    public String thumbsUp = "0";
    public String thumbsDown = "0";

    public static StructChannelExtra convert(RealmChannelExtra realmChannelExtra) {
        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.signature = realmChannelExtra.getSignature();
        structChannelExtra.thumbsUp = realmChannelExtra.getThumbsUp();
        structChannelExtra.thumbsDown = realmChannelExtra.getThumbsDown();
        structChannelExtra.viewsLabel = realmChannelExtra.getViewsLabel();
        return structChannelExtra;
    }

    public static StructChannelExtra makeDefaultStructure(long messageId, long roomId) {
        StructChannelExtra structChannelExtra = new StructChannelExtra();
        structChannelExtra.messageId = messageId;
        structChannelExtra.thumbsUp = "0";
        structChannelExtra.thumbsDown = "0";
        structChannelExtra.viewsLabel = "1";
        if (RealmRoom.showSignature(roomId)) {
            structChannelExtra.signature = G.displayName;
        } else {
            structChannelExtra.signature = "";
        }
        return structChannelExtra;
    }
}
