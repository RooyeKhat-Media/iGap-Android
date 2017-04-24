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
import com.iGap.helper.HelperSetAction;
import com.iGap.helper.HelperUploadFile;
import com.iGap.proto.ProtoFileUpload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

public class FileUploadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUpload.FileUploadResponse.Builder fileUploadResponse = (ProtoFileUpload.FileUploadResponse.Builder) message;
        HelperUploadFile.onFileUpload.onFileUpload(fileUploadResponse.getProgress(), fileUploadResponse.getNextOffset(), fileUploadResponse.getNextLimit(), this.identity,
            fileUploadResponse.getResponse());
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

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(identity)).findFirst();
                        if (message != null) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void error() {
        super.error();
        HelperUploadFile.onFileUpload.onFileUploadTimeOut(this.identity);
        HelperSetAction.sendCancel(Long.parseLong(this.identity));
        makeFailed();
    }
}


