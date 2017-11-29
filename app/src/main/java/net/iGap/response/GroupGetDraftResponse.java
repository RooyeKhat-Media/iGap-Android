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

import net.iGap.proto.ProtoGroupGetDraft;
import net.iGap.realm.RealmRoom;

public class GroupGetDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity; // here identity is roomId

    public GroupGetDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupGetDraft.GroupGetDraftResponse.Builder groupGetDraft = (ProtoGroupGetDraft.GroupGetDraftResponse.Builder) message;
        RealmRoom.convertAndSetDraft(Long.parseLong(identity), groupGetDraft.getDraft().getMessage(), groupGetDraft.getDraft().getReplyTo());
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


