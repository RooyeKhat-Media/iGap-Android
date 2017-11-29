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
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserVerifyNewDevice;

public class UserVerifyNewDeviceResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserVerifyNewDeviceResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserVerifyNewDevice.UserVerifyNewDeviceResponse.Builder builder = (ProtoUserVerifyNewDevice.UserVerifyNewDeviceResponse.Builder) message;

        if (G.onVerifyNewDevice != null) {
            G.onVerifyNewDevice.verifyNewDevice(builder.getAppName(), builder.getAppId(), builder.getAppBuildVersion(), builder.getAppVersion(), builder.getPlatform(), builder.getPlatformVersion(), builder.getDevice(), builder.getDeviceName(), builder.getTwoStepVerification());
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
        if (G.onVerifyNewDevice != null) {
            G.onVerifyNewDevice.errorVerifyNewDevice(majorCode, minorCode);
        }
    }
}


