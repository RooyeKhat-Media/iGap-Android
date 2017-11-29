/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import net.iGap.G;
import net.iGap.module.enums.ClientConditionOffline;
import net.iGap.module.enums.ClientConditionVersion;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRoomMessage;

/**
 * manage response for edited message for chat,group or channel
 */
public class HelperEditMessage {

    public static void editMessage(final long roomId, final long messageId, final long messageVersion, final ProtoGlobal.RoomMessageType messageType, final String message, final ProtoResponse.Response response) {

        RealmClientCondition.setVersion(roomId, messageVersion, ClientConditionVersion.EDIT);
        if (!response.getId().isEmpty()) {
            RealmClientCondition.deleteOfflineAction(messageId, ClientConditionOffline.EDIT);
        } else {
            RealmRoomMessage.editMessageServerResponse(messageId, messageVersion, message, messageType);

            if (G.onChatEditMessageResponse != null) {
                G.onChatEditMessageResponse.onChatEditMessage(roomId, messageId, messageVersion, message, response);
            }
        }
    }
}
