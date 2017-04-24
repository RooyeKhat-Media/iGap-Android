/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelAvatarAdd;
import com.iGap.realm.RealmAvatar;
import io.realm.Realm;

public class ChannelAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelAvatarAdd.ChannelAvatarAddResponse.Builder builder = (ProtoChannelAvatarAdd.ChannelAvatarAddResponse.Builder) message;

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAvatar.put(builder.getRoomId(), builder.getAvatar(), true);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (G.onChannelAvatarAdd != null) {
                            G.onChannelAvatarAdd.onAvatarAdd(builder.getRoomId(), builder.getAvatar());
                        }
                    }
                });
                realm.close();
            }
        });

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


