/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoClientMuteRoom;
import net.iGap.proto.ProtoGlobal;

public class RequestClientMuteRoom {

    public void muteRoom(long roomId, ProtoGlobal.RoomMute mute) {

        //RealmRoom.roomMute(roomId, mute); // just in response update this value

        ProtoClientMuteRoom.ClientMuteRoom.Builder builder = ProtoClientMuteRoom.ClientMuteRoom.newBuilder();
        builder.setRoomId(roomId);
        builder.setRoomMute(mute);

        RequestWrapper requestWrapper = new RequestWrapper(614, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void muteRoom(long roomId, boolean mute) {
        if (mute) {
            muteRoom(roomId, ProtoGlobal.RoomMute.MUTE);
        } else {
            muteRoom(roomId, ProtoGlobal.RoomMute.UNMUTE);
        }
    }
}
