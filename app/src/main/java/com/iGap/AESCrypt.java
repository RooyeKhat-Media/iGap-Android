/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap;

import com.iGap.helper.HelperNumerical;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt and decrypt messages using AES 256 bit encryption that are compatible with AESCrypt-ObjC
 * and AESCrypt Ruby.
 */
public final class AESCrypt {

    /**
     * More flexible AES encrypt that doesn't encode
     *
     * @param key AES key typically 128, 192 or 256 bit
     * @param message in bytes (assumed it's already been decoded)
     * @return Encrypted cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] encrypt(final SecretKeySpec key, final byte[] message) throws GeneralSecurityException {

        try {
            final Cipher cipher = Cipher.getInstance("AES/" + G.symmetricMethod + "/PKCS5Padding");
            SecureRandom r = new SecureRandom();
            byte[] ivBytes = new byte[G.ivSize];
            r.nextBytes(ivBytes);
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] encryptMessage = cipher.doFinal(message);
            return HelperNumerical.appendByteArrays(ivBytes, encryptMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * More flexible AES decrypt that doesn't encode
     *
     * @param key AES key typically 128, 192 or 256 bit
     * @param iv Initiation Vector
     * @param decodedCipherText in bytes (assumed it's already been decoded)
     * @return Decrypted message cipher text (not encoded)
     * @throws GeneralSecurityException if something goes wrong during encryption
     */
    public static byte[] decrypt(final SecretKeySpec key, final byte[] iv, final byte[] decodedCipherText) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance("AES/" + G.symmetricMethod + "/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher.doFinal(decodedCipherText);
    }

    /**
     * encrypt symmetricKey with PublicKey
     *
     * @param key publicKey that get from server
     * @param symmetricKey random String that generate in client
     */

    public static byte[] encryptSymmetricKey(PublicKey key, byte[] symmetricKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(symmetricKey);
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
