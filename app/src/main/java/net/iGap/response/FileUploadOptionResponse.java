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
import io.realm.Realm;
import net.iGap.G;
import net.iGap.helper.HelperSetAction;
import net.iGap.helper.HelperUploadFile;
import net.iGap.proto.ProtoFileUploadOption;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class FileUploadOptionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadOptionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUploadOption.FileUploadOptionResponse.Builder fp = (ProtoFileUploadOption.FileUploadOptionResponse.Builder) message;

        HelperUploadFile.onFileUpload.OnFileUploadOption(fp.getFirstBytesLimit(), fp.getLastBytesLimit(), fp.getMaxConnection(), this.identity, fp.getResponse());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        HelperUploadFile.onFileUpload.onFileUploadTimeOut(this.identity);
        HelperSetAction.sendCancel(Long.parseLong(this.identity));
        makeFailed();
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
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                                }
                            });
                        }

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
}


