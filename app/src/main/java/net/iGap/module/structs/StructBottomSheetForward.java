package net.iGap.module.structs;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import net.iGap.proto.ProtoGlobal;

public class StructBottomSheetForward {

    private long id;
    private long peer_id;
    private String displayName;
    private ProtoGlobal.Room.Type type;
    private boolean isContactList;
    private boolean isNotExistRoom;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeer_id() {
        return peer_id;
    }

    public void setPeer_id(long peer_id) {
        this.peer_id = peer_id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ProtoGlobal.Room.Type getType() {
        return type;
    }

    public void setType(ProtoGlobal.Room.Type type) {
        this.type = type;
    }

    public boolean isContactList() {
        return isContactList;
    }

    public void setContactList(boolean contactList) {
        isContactList = contactList;
    }

    public boolean isNotExistRoom() {
        return isNotExistRoom;
    }

    public void setNotExistRoom(boolean notExistRoom) {
        isNotExistRoom = notExistRoom;
    }
}

