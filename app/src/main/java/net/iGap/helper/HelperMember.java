/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import net.iGap.G;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestClientGetRoom;

public class HelperMember {

    public static void addMember(long roomId, long userId, String role) {
        if (userId == G.userId) {
            new RequestClientGetRoom().clientGetRoom(roomId, null);
        } else {
            RealmMember.addMember(roomId, userId, role);
            RealmRoom.updateMemberCount(roomId, true);
        }
    }

    public static void kickMember(long roomId, long memberId) {
        RealmMember.kickMember(roomId, memberId);
        RealmRoom.updateMemberCount(roomId, false);
    }

    public static void updateRole(long roomId, long memberId, String role) {
        RealmRoom.updateMineRole(roomId, memberId, role);
        RealmRoom.updateMemberRole(roomId, memberId, role);
    }
}
