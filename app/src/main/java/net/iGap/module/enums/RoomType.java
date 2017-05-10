/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module.enums;

import net.iGap.proto.ProtoGlobal;

public enum RoomType {
    CHAT, GROUP, CHANNEL;

    /**
     * convert ProtoGlobal.Room.Type to RoomType
     *
     * @param type ProtoGlobal.Room.Type
     * @return RoomType
     */
    public static RoomType convert(ProtoGlobal.Room.Type type) {
        return RoomType.valueOf(type.toString());
    }

    /**
     * convert ProtoGlobal.Room.Type to RoomType
     *
     * @param type ProtoGlobal.Room.Type
     * @return RoomType
     */
    public static ProtoGlobal.Room.Type convert(RoomType type) {
        return ProtoGlobal.Room.Type.valueOf(type.toString());
    }
}
