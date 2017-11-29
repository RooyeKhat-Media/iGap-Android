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

public enum ChannelChatRole {
    MEMBER, MODERATOR, ADMIN, OWNER, UNRECOGNIZED;

    /**
     * convert ProtoGlobal.ChannelRoom.Role to ChannelChatRole
     *
     * @param role ProtoGlobal.ChannelRoom.Role
     * @return ChannelChatRole
     */
    public static ChannelChatRole convert(ProtoGlobal.ChannelRoom.Role role) {
        switch (role) {
            case ADMIN:
                return ChannelChatRole.ADMIN;
            case MEMBER:
                return ChannelChatRole.MEMBER;
            case MODERATOR:
                return ChannelChatRole.MODERATOR;
            case OWNER:
                return ChannelChatRole.OWNER;
            default:
                return UNRECOGNIZED;
        }
    }
}
