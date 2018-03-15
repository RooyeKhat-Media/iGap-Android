/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.interfaces;

import net.iGap.proto.ProtoUserRegister;

import java.util.List;

public interface OnUserRegistration {

    void onRegister(String userName, long userId, ProtoUserRegister.UserRegisterResponse.Method methodValue, List<Long> smsNumbers, String regex, int verifyCodeDigitCount, String authorHash);

    void onRegisterError(int majorCode, int minorCode, int getWait);
}
