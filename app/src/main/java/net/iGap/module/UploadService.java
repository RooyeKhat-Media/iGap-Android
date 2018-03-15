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

import net.iGap.helper.HelperUploadFile;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;

public class UploadService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String path = intent.getStringExtra("Path");
        Long roomId = intent.getLongExtra("Roomid", 0);

        sendVoice(path, roomId);

        return START_NOT_STICKY;
    }

    private void sendVoice(final String savedPath, final Long mRoomId) {
        ProtoGlobal.Room.Type chatType = RealmRoom.detectType(mRoomId);

        final long messageId = SUID.id().get();
        final long updateTime = TimeUtils.currentLocalTime();
        final long duration = AndroidUtils.getAudioDuration(getApplicationContext(), savedPath);

        RealmRoomMessage.makeVoiceMessage(mRoomId, messageId, duration, updateTime, savedPath, "");

        HelperUploadFile.startUploadTaskChat(mRoomId, chatType, savedPath, messageId, ProtoGlobal.RoomMessageType.VOICE, "", 0, null);
    }
}
