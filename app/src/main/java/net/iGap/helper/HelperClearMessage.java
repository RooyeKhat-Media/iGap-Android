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
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

public class HelperClearMessage {

    public static void clearMessage(long roomId, long clearId) {
        RealmClientCondition.setClearId(roomId, clearId);
        RealmRoom.clearMessage(roomId, clearId);
        RealmRoomMessage.deleteAllMessageLessThan(roomId, clearId);

        if (G.clearMessagesUtil != null) {
            G.clearMessagesUtil.onChatClearMessage(roomId, clearId);
        }
    }
}
