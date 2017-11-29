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

import net.iGap.helper.HelperMessageResponse;
import net.iGap.proto.ProtoChannelSendMessage;
import net.iGap.proto.ProtoGlobal;

import static net.iGap.realm.RealmRoomMessage.makeFailed;

public class ChannelSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.identity = identity;
        this.message = protoClass;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelSendMessage.ChannelSendMessageResponse.Builder channelSendMessageResponse = (ProtoChannelSendMessage.ChannelSendMessageResponse.Builder) message;
        HelperMessageResponse.handleMessage(channelSendMessageResponse.getRoomId(), channelSendMessageResponse.getRoomMessage(), ProtoGlobal.Room.Type.CHANNEL, channelSendMessageResponse.getResponse(), this.identity);
    }

    @Override
    public void error() {
        super.error();
        makeFailed(Long.parseLong(identity));
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }
}
