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

import com.iGap.G;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

public final class HelperDeleteMessage {

    /**
     * update client condition with delete response and call onChatDeleteMessage
     */

    public static void deleteMessage(final long roomId, final long messageId, final long deleteVersion, final ProtoResponse.Response response) {

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                /**
                 * if another account deleted this message set deleted true
                 * otherwise before this state was set
                 */
                if (response.getId().isEmpty()) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);
                    }
                }

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setDeleteVersion(deleteVersion);
                    for (RealmOfflineDelete realmOfflineDeleted : realmClientCondition.getOfflineDeleted()) {
                        if (realmOfflineDeleted.getOfflineDelete() == messageId) {
                            realmOfflineDeleted.deleteFromRealm();
                            break;
                        }
                    }
                }
                if (G.onChatDeleteMessageResponse != null) {
                    G.onChatDeleteMessageResponse.onChatDeleteMessage(deleteVersion, messageId, roomId, response);
                }
            }
        });

        realm.close();
    }
}
