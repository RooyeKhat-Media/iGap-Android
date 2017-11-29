

package net.iGap.response;

import net.iGap.proto.ProtoChannelGetDraft;
import net.iGap.realm.RealmRoom;

public class ChannelGetDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelGetDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelGetDraft.ChannelGetDraftResponse.Builder builder = (ProtoChannelGetDraft.ChannelGetDraftResponse.Builder) message;
        RealmRoom.convertAndSetDraft(Long.parseLong(identity), builder.getDraft().getMessage(), builder.getDraft().getReplyTo());
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


