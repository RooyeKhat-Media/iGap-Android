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

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;

public class StructContactInfo {
    public long peerId;
    public long lastSeen;
    public boolean isHeader;
    public String displayName;
    public String status;
    public boolean isSelected;
    public String phone;
    public String initials;
    public String color;
    public String role = ProtoGlobal.GroupRoom.Role.MEMBER.toString();
    public RealmAvatar avatar;
    public long userID;

    public StructContactInfo(long peerId, String displayName, String status, boolean isHeader, boolean isSelected, String phone) {
        this.peerId = peerId;
        this.isHeader = isHeader;
        this.displayName = displayName;
        this.status = status;
        this.isSelected = isSelected;
        this.phone = phone;
    }

    public StructContactInfo() {

    }
}
