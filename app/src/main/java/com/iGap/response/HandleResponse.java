/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import com.iGap.AESCrypt;
import com.iGap.G;
import com.iGap.helper.HelperNumerical;
import com.iGap.helper.HelperUnpackMessage;
import java.security.GeneralSecurityException;

public class HandleResponse extends Thread {

    byte[] binary;

    public HandleResponse(byte[] binary) {
        this.binary = binary;
    }

    @Override
    public void run() {
        super.run();
        if (G.isSecure) {
            byte[] iv = HelperNumerical.getIv(binary, G.ivSize);
            byte[] binaryDecode = HelperNumerical.getMessage(binary);

            try {
                binaryDecode = AESCrypt.decrypt(G.symmetricKey, iv, binaryDecode);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            HelperUnpackMessage.unpack(binaryDecode);
        } else {
            HelperUnpackMessage.unpack(binary);
        }
    }
}
