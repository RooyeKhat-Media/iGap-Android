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
import net.iGap.proto.ProtoUserContactsUnblock;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;

public class UserContactsUnblockResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsUnblockResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserContactsUnblock.UserContactsUnblockResponse.Builder builder = (ProtoUserContactsUnblock.UserContactsUnblockResponse.Builder) message;
        long userId = builder.getUserId();

        RealmRegisteredInfo.updateBlock(userId, false);
        RealmContacts.updateBlock(userId, false);

        if (G.onUserContactsUnBlock != null) {
            G.onUserContactsUnBlock.onUserContactsUnBlock(userId);
        }

        //+manually update (avoid use from realm-adapter)
        if (G.onBlockStateChanged != null) {
            G.onBlockStateChanged.onBlockStateChanged(false, userId);
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


