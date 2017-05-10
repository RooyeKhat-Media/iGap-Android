/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.interfaces;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;

public interface OnChatSendMessageResponse {
    // message updated after send request sent
    void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage);

    void onMessageReceive(long roomId, String message, ProtoGlobal.RoomMessageType messageType, ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType);

    void onMessageFailed(long roomId, RealmRoomMessage roomMessage);
}
