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

import net.iGap.G;
import net.iGap.proto.ProtoChannelUpdateReactionStatus;
import net.iGap.realm.RealmChannelRoom;

public class ChannelUpdateReactionStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelUpdateReactionStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatusResponse.Builder builder = (ProtoChannelUpdateReactionStatus.ChannelUpdateReactionStatusResponse.Builder) message;
        RealmChannelRoom.updateReactionStatus(builder.getRoomId(), builder.getReactionStatus());

        if (G.onChannelUpdateReactionStatus != null) {
            G.onChannelUpdateReactionStatus.OnChannelUpdateReactionStatusResponse(builder.getRoomId(), builder.getReactionStatus());
        }

        if (G.onChannelUpdateReactionStatusChat != null) {
            G.onChannelUpdateReactionStatusChat.OnChannelUpdateReactionStatusResponse(builder.getRoomId(), builder.getReactionStatus());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onChannelUpdateReactionStatus != null) {
            G.onChannelUpdateReactionStatus.OnChannelUpdateReactionStatusError();
        }
    }

    @Override
    public void error() {
        super.error();
        if (G.onChannelUpdateReactionStatus != null) {
            G.onChannelUpdateReactionStatus.OnChannelUpdateReactionStatusError();
        }
    }
}


