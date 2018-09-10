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
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperUploadFile;
import net.iGap.proto.ProtoFileUpload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestFileDownload;
import net.iGap.request.RequestFileUpload;

import io.realm.Realm;

public class FileUploadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;
    private RequestFileUpload.IdentityFileUpload identityFileUpload;

    public FileUploadResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
        identityFileUpload = ((RequestFileUpload.IdentityFileUpload) identity);
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUpload.FileUploadResponse.Builder fileUploadResponse = (ProtoFileUpload.FileUploadResponse.Builder) message;

        HelperUploadFile.onFileUpload.onFileUpload(fileUploadResponse.getProgress(), fileUploadResponse.getNextOffset(), fileUploadResponse.getNextLimit(), identityFileUpload.identify, fileUploadResponse.getResponse());
        boolean connectivityType = true;
        try {

            if (HelperCheckInternetConnection.currentConnectivityType != null) {


                if (HelperCheckInternetConnection.currentConnectivityType == HelperCheckInternetConnection.ConnectivityType.WIFI)
                    connectivityType = true;
                else
                    connectivityType = false;
            }

        } catch (Exception e) {
        }
        ;


        HelperDataUsage.progressUpload(connectivityType, fileUploadResponse.getNextLimit(), identityFileUpload.type);

        if (fileUploadResponse.getProgress() == 100)
            HelperDataUsage.insertDataUsage(HelperDataUsage.convetredUploadType, connectivityType, false);


    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    /**
     * make messages failed
     */
    private void makeFailed() {
        // message failed

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identityFileUpload.identify)).findFirst();
                        if (message != null) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identityFileUpload.identify)).findFirst();
                                if (message != null && message.isValid()) {
                                    G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                                }
                                realm.close();
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
    public void error() {
        super.error();
        HelperUploadFile.onFileUpload.onFileUploadTimeOut(identityFileUpload.identify);
        HelperSetAction.sendCancel(Long.parseLong(identityFileUpload.identify));
        makeFailed();
    }
}


