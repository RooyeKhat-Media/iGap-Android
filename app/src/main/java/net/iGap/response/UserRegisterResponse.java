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

import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;

import net.iGap.G;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserRegister;

public class UserRegisterResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserRegisterResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserRegister.UserRegisterResponse.Builder builder = (ProtoUserRegister.UserRegisterResponse.Builder) message;
        G.onUserRegistration.onRegister(builder.getUsername(), builder.getUserId(), builder.getMethod(), builder.getSmsNumberList(), builder.getVerifyCodeRegex(), builder.getVerifyCodeDigitCount(), builder.getAuthorHash());

        G.userId = builder.getUserId();
        G.authorHash = builder.getAuthorHash();
        G.displayName = builder.getUsername();

        PackageManager pm = G.context.getPackageManager();
        String installationSource = pm.getInstallerPackageName(G.context.getPackageName());

        if (installationSource == null) {
            installationSource = "(Unknown Market)";
        }

        Crashlytics.logException(new Exception("installationSource : " + installationSource));
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();
        final int getWait = errorResponse.getWait();

        G.onUserRegistration.onRegisterError(majorCode, minorCode, getWait);
    }
}
