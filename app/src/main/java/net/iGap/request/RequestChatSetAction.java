/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoChatSetAction;
import net.iGap.proto.ProtoGlobal;

public class RequestChatSetAction {

    public void chatSetAction(long roomId, ProtoGlobal.ClientAction clientAction, int actionId) {
        ProtoChatSetAction.ChatSetAction.Builder builder = ProtoChatSetAction.ChatSetAction.newBuilder();
        builder.setRoomId(roomId);
        builder.setAction(clientAction);
        builder.setActionId(actionId);
        RequestWrapper requestWrapper = new RequestWrapper(210, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
