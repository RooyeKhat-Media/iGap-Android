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

import android.os.Handler;
import com.iGap.G;
import com.iGap.helper.HelperInfo;
import com.iGap.proto.ProtoClientGetRoomHistory;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import io.realm.Realm;
import java.util.ArrayList;

public class ClientGetRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomHistoryResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final int[] i = {0};

        new Handler(G.currentActivity.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                final Realm realm = Realm.getDefaultInstance();
                final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;
                final ArrayList<RealmRoomMessage> realmRoomMessages = new ArrayList<>();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        final long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                        for (ProtoGlobal.RoomMessage roomMessage : builder.getMessageList()) {
                            if (roomMessage.getAuthor().hasUser()) {
                                HelperInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                            }
                            realmRoomMessages.add(RealmRoomMessage.putOrUpdate(roomMessage, Long.parseLong(identity)));
                            if (roomMessage.getAuthor().getUser().getUserId() != userId) { // show notification if this message isn't for another account
                                if (!G.isAppInFg) {
                                    G.helperNotificationAndBadge.checkAlert(true, ProtoGlobal.Room.Type.CHAT, Long.parseLong(identity));
                                }
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        G.onClientGetRoomHistoryResponse.onGetRoomHistory(Long.parseLong(identity), builder.getMessageList().get(0).getMessageId(), builder.getMessageList().get(builder.getMessageCount() - 1).getMessageId());
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

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onClientGetRoomHistoryResponse != null) {
            G.onClientGetRoomHistoryResponse.onGetRoomHistoryError(majorCode, minorCode);
        }
    }
}


