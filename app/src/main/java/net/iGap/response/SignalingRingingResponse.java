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

import android.widget.Toast;
import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoSignalingRinging;

public class SignalingRingingResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public SignalingRingingResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoSignalingRinging.SignalingRingingResponse.Builder builder = (ProtoSignalingRinging.SignalingRingingResponse.Builder) message;
        if (builder.getResponse().getId().isEmpty()) {

            if (G.iSignalingRinging != null) {
                G.iSignalingRinging.onRinging();
            }

            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(G.context, G.context.getResources().getString(R.string.ringing), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


