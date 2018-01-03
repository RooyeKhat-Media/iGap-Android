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

import net.iGap.proto.ProtoChannelCreate;

public class RequestChannelCreate {

    public void channelCreate(String name, String description) {
        ProtoChannelCreate.ChannelCreate.Builder builder = ProtoChannelCreate.ChannelCreate.newBuilder();
        builder.setName(name);
        builder.setDescription(description.trim());

        RequestWrapper requestWrapper = new RequestWrapper(400, builder, name); // set name as identity because client need channel name for show correct log message.
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
