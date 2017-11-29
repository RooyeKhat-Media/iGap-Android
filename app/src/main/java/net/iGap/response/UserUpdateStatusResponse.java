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
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.realm.RealmRegisteredInfo;

import static net.iGap.G.userId;

public class UserUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder builder = (ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder) message;

        RealmRegisteredInfo.updateStatus(builder.getUserId(), builder.getResponse().getTimestamp(), builder.getStatus().toString());

        if (builder.getUserId() == userId) {
            if (builder.getStatus() == ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE) {
                G.isUserStatusOnline = true;
            } else {
                G.isUserStatusOnline = false;
            }
        }

        if (G.onUserUpdateStatus != null) {
            G.onUserUpdateStatus.onUserUpdateStatus(builder.getUserId(), builder.getResponse().getTimestamp(), builder.getStatus().toString());
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


