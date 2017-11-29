/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import net.iGap.proto.ProtoClientPinRoom;
import net.iGap.realm.RealmRoom;

public class ClientPinRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientPinRoomResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoClientPinRoom.ClientPinRoomResponse.Builder builder = (ProtoClientPinRoom.ClientPinRoomResponse.Builder) message;
        if (builder.getPinId() > 0) {
            RealmRoom.updatePin(builder.getRoomId(), true, builder.getPinId());
        } else {
            RealmRoom.updatePin(builder.getRoomId(), false, builder.getPinId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


