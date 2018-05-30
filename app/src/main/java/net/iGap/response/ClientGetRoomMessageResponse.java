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
import net.iGap.proto.ProtoClientGetRoomMessage;
import net.iGap.realm.RealmRoomMessage;

import io.realm.Realm;

public class ClientGetRoomMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoClientGetRoomMessage.ClientGetRoomMessageResponse.Builder builder = (ProtoClientGetRoomMessage.ClientGetRoomMessageResponse.Builder) message;
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage.putOrUpdate(builder.getMessage(), Long.parseLong(identity), false, true, realm);
            }
        });

        realm.close();


        if (G.onClientGetRoomMessage != null) {
            G.onClientGetRoomMessage.onClientGetRoomMessageResponse(builder.getMessage().getMessageId());
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


