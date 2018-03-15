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
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

import io.realm.Realm;

public class HelperUpdateMessageStatue {

    public static void updateStatus(final long roomId, final long messageId, final String authorHash, final ProtoGlobal.RoomMessageStatus status, final long statusVersion, ProtoResponse.Response.Builder response) {

        if (!response.getId().isEmpty()) { // I'm sender
            RealmClientCondition.deleteOfflineAction(messageId, status);
        } else {  // I'm recipient
            final Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    /**
                     * clear unread count if another account was saw this message
                     */
                    RealmRoom.clearUnreadCount(roomId, authorHash, status, messageId);

                    /**
                     * find message from database and update its status
                     */
                    RealmRoomMessage roomMessage = RealmRoomMessage.setStatusServerResponse(realm, messageId, statusVersion, status);
                    if (roomMessage != null) {
                        if (G.chatUpdateStatusUtil != null) {
                            G.chatUpdateStatusUtil.onChatUpdateStatus(roomId, messageId, status, statusVersion);
                        }
                    } else if (status == ProtoGlobal.RoomMessageStatus.SEEN) {
                        /**
                         * reason : getRoomList will be updated status in Realm and after that when
                         * client get status here and was in chat will not be updated status in second
                         * so i use from this block for avoid from this problem
                         */
                        if (G.chatUpdateStatusUtil != null) {
                            G.chatUpdateStatusUtil.onChatUpdateStatus(roomId, messageId, status, statusVersion);
                        }
                    }
                }
            });
            realm.close();
        }
    }
}
