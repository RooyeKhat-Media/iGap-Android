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

import net.iGap.G;
import net.iGap.proto.ProtoClientResolveUsername;
import net.iGap.proto.ProtoError;

public class ClientResolveUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientResolveUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoClientResolveUsername.ClientResolveUsernameResponse.Builder builder = (ProtoClientResolveUsername.ClientResolveUsernameResponse.Builder) message;
        if (G.onClientResolveUsername != null) {
            G.onClientResolveUsername.onClientResolveUsername(builder.getType(), builder.getUser(), builder.getRoom());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onClientResolveUsername != null) {
            G.onClientResolveUsername.onError(majorCode, minorCode);
        }
    }
}


