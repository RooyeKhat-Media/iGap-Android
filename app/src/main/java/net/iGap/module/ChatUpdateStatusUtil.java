/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import net.iGap.interfaces.OnChatUpdateStatusResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestChatUpdateStatus;
import net.iGap.request.RequestGroupUpdateStatus;

/**
 * util for chat send messages
 * useful for having callback from different activities
 */
public class ChatUpdateStatusUtil implements OnChatUpdateStatusResponse {
    private OnChatUpdateStatusResponse onChatUpdateStatusResponse;
    private OnChatUpdateStatusResponse onChatUpdateStatusResponseFragmentMain;

    public void setOnChatUpdateStatusResponse(OnChatUpdateStatusResponse response) {
        this.onChatUpdateStatusResponse = response;
    }

    public void setOnChatUpdateStatusResponseFragmentMain(OnChatUpdateStatusResponse response) {
        this.onChatUpdateStatusResponseFragmentMain = response;
    }

    public void sendUpdateStatus(ProtoGlobal.Room.Type roomType, long roomId, long messageId, ProtoGlobal.RoomMessageStatus roomMessageStatus) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            new RequestChatUpdateStatus().updateStatus(roomId, messageId, roomMessageStatus);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            new RequestGroupUpdateStatus().groupUpdateStatus(roomId, messageId, roomMessageStatus);
        }
    }

    @Override
    public void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, long statusVersion) {
        if (onChatUpdateStatusResponse != null) {
            onChatUpdateStatusResponse.onChatUpdateStatus(roomId, messageId, status, statusVersion);
        }
        if (onChatUpdateStatusResponseFragmentMain != null) {
            onChatUpdateStatusResponseFragmentMain.onChatUpdateStatus(roomId, messageId, status, statusVersion);
        }
    }
}
