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
import net.iGap.proto.ProtoUserContactsEdit;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;

public class UserContactsEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserContactsEdit.UserContactsEditResponse.Builder builder = (ProtoUserContactsEdit.UserContactsEditResponse.Builder) message;
        if (identity != null) {
            long userId = Long.parseLong(identity);
            RealmRegisteredInfo.updateName(userId, builder.getFirstName(), builder.getLastName(), builder.getInitials());
            RealmContacts.updateName(userId, builder.getFirstName(), builder.getLastName(), builder.getInitials());
            RealmRoom.updateChatTitle(userId, builder.getFirstName() + " " + builder.getLastName());
        }
        if (G.onUserContactEdit != null) {
            G.onUserContactEdit.onContactEdit(builder.getFirstName(), builder.getLastName(), builder.getInitials());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onUserContactEdit != null) {
            G.onUserContactEdit.onContactEditTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int MajorCode = errorResponse.getMajorCode();
        int MinorCode = errorResponse.getMinorCode();
        if (G.onUserContactEdit != null) {
            G.onUserContactEdit.onContactEditError(MajorCode, MinorCode);
        }
    }
}


