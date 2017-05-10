/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import io.realm.Realm;
import net.iGap.helper.HelperUploadFile;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;

public class UploadService extends Service {

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        String path = intent.getStringExtra("Path");
        Long roomId = intent.getLongExtra("Roomid", 0);

        sendVoice(path, roomId);

        return START_NOT_STICKY;
    }

    private void sendVoice(final String savedPath, final Long mRoomId) {

        Realm realm = Realm.getDefaultInstance();

        ProtoGlobal.Room.Type chatType = null;

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mRoomId).findFirst();
        if (realmRoom != null) {
            chatType = realmRoom.getType();
        }

        final long messageId = SUID.id().get();
        final long updateTime = System.currentTimeMillis();
        final long senderID = realm.where(RealmUserInfo.class).findFirst().getUserId();
        final long duration = AndroidUtils.getAudioDuration(getApplicationContext(), savedPath);

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);

                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE);
                //  roomMessage.setMessage(getWrittenMessage());
                roomMessage.setRoomId(mRoomId);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setAttachment(messageId, savedPath, 0, 0, 0, null, duration, LocalFileType.FILE);
                roomMessage.setUserId(senderID);
                roomMessage.setCreateTime(updateTime);
            }
        });

        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, "", 0, null);

        realm.close();
    }
}
