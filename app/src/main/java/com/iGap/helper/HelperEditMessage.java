/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.helper;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineEdited;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

/**
 * manage response for edited message for chat,group or channel
 */
public class HelperEditMessage {

    public static void editMessage(final long roomId, final long messageId, final long messageVersion, final ProtoGlobal.RoomMessageType messageType, final String message, final ProtoResponse.Response response) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    Log.i("EEE", "editMessage");
                    realmClientCondition.setMessageVersion(messageVersion);
                }

                if (!response.getId().isEmpty()) {
                    if (realmClientCondition != null) {
                        for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) {
                            if (realmOfflineEdited.getMessageId() == messageId) {
                                realmOfflineEdited.deleteFromRealm();
                                break;
                            }
                        }
                    }
                } else {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    if (roomMessage != null) {
                        /**
                         * update message text in database
                         */
                        roomMessage.setMessage(message);
                        roomMessage.setMessageVersion(messageVersion);
                        roomMessage.setEdited(true);
                        roomMessage.setMessageType(messageType);
                        if (G.onChatEditMessageResponse != null) {
                            G.onChatEditMessageResponse.onChatEditMessage(roomId, messageId, messageVersion, message, response);
                        }
                    }
                }
            }
        });
        realm.close();
    }

}
