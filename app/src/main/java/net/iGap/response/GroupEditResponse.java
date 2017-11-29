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
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGroupEdit;
import net.iGap.realm.RealmRoom;

public class GroupEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupEdit.GroupEditResponse.Builder builder = (ProtoGroupEdit.GroupEditResponse.Builder) message;
        RealmRoom.editRoom(builder.getRoomId(), builder.getName(), builder.getDescription());

        if (G.onGroupEdit != null) {
            G.onGroupEdit.onGroupEdit(builder.getRoomId(), builder.getName(), builder.getDescription());
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onGroupEdit != null) {
            G.onGroupEdit.onError(majorCode, minorCode);
        }
    }
}
