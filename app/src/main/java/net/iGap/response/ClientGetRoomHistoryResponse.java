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
import net.iGap.proto.ProtoClientGetRoomHistory;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestClientGetRoomHistory;

import io.realm.Realm;

public class ClientGetRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientGetRoomHistoryResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        RequestClientGetRoomHistory.IdentityClientGetRoomHistory identityParams = ((RequestClientGetRoomHistory.IdentityClientGetRoomHistory) identity);
        final long roomId = identityParams.roomId;
        final long reachMessageId = identityParams.reachMessageId;
        final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction = identityParams.direction;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                final Realm realm = Realm.getDefaultInstance();
                final ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (ProtoGlobal.RoomMessage roomMessage : builder.getMessageList()) {
                            if (roomMessage.getAuthor().hasUser()) {
                                RealmRegisteredInfo.needUpdateUser(roomMessage.getAuthor().getUser().getUserId(), roomMessage.getAuthor().getUser().getCacheId());
                            }
                            RealmRoomMessage.putOrUpdate(roomMessage, roomId, false, true, realm);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        realm.close();

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.onClientGetRoomHistoryResponse.onGetRoomHistory(roomId, builder.getMessageList().get(0).getMessageId(), builder.getMessageList().get(builder.getMessageCount() - 1).getMessageId(), reachMessageId, direction);
                            }
                        });
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
        RequestClientGetRoomHistory.IdentityClientGetRoomHistory identityParams = ((RequestClientGetRoomHistory.IdentityClientGetRoomHistory) identity);
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        if (G.onClientGetRoomHistoryResponse != null) {
            G.onClientGetRoomHistoryResponse.onGetRoomHistoryError(errorResponse.getMajorCode(), errorResponse.getMinorCode(), identityParams.messageIdGetHistory, identityParams.direction);
        }
    }
}


