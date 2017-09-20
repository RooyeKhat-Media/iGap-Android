package net.iGap.interfaces;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

public interface OnTwoStepPassword {

    void getPasswordDetail(String questionOne, String questionTwo, String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern);

    void errorGetPasswordDetail(int majorCode, int minorCode);

    void timeOutGetPasswordDetail();

    void checkPassword();

    void errorCheckPassword(int getWait);

    void unSetPassword();

    void changeRecoveryQuestion();

    void changeHint();

    void changeEmail(String unConfirmEmailPatern);

    void confirmEmail();

    void errorConfirmEmail();

    void errorInvalidPassword();
}
