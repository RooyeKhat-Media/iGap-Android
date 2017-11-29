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

public enum GroupChatRole {
    MEMBER, MODERATOR, ADMIN, OWNER, UNRECOGNIZED;

    /**
     * convert ProtoGlobal.GroupRoom.Role to GroupChatRole
     *
     * @param role ProtoGlobal.GroupRoom.Role
     * @return GroupChatRole
     */
    public static GroupChatRole convert(ProtoGlobal.GroupRoom.Role role) {
        switch (role) {
            case ADMIN:
                return GroupChatRole.ADMIN;
            case MEMBER:
                return GroupChatRole.MEMBER;
            case MODERATOR:
                return GroupChatRole.MODERATOR;
            case OWNER:
                return GroupChatRole.OWNER;
            default:
                return UNRECOGNIZED;
        }
    }
}
