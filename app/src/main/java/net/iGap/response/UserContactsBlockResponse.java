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
import net.iGap.proto.ProtoUserContactsBlock;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmRegisteredInfo;

public class UserContactsBlockResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsBlockResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserContactsBlock.UserContactsBlockResponse.Builder builder = (ProtoUserContactsBlock.UserContactsBlockResponse.Builder) message;
        long userId = builder.getUserId();

        RealmRegisteredInfo.updateBlock(userId, true);
        RealmContacts.updateBlock(userId, true);

        if (G.onUserContactsBlock != null) {
            G.onUserContactsBlock.onUserContactsBlock(userId);
        }

        //+manually update (avoid use from realm-adapter)
        if (G.onBlockStateChanged != null) {
            G.onBlockStateChanged.onBlockStateChanged(true, userId);
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


