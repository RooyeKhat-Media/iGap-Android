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

import android.os.Handler;
import android.os.Looper;

import net.iGap.G;
import net.iGap.proto.ProtoGroupAvatarAdd;
import net.iGap.realm.RealmAvatar;

import io.realm.Realm;

public class GroupAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder groupAvatarAddResponse = (ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder) message;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAvatar.putOrUpdate(realm, groupAvatarAddResponse.getRoomId(), groupAvatarAddResponse.getAvatar());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (G.onGroupAvatarResponse != null) {
                                    G.onGroupAvatarResponse.onAvatarAdd(groupAvatarAddResponse.getRoomId(), groupAvatarAddResponse.getAvatar());
                                }
                            }
                        });

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
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
        if (G.onGroupAvatarResponse != null) {
            G.onGroupAvatarResponse.onAvatarAddError();
        }
    }
}


