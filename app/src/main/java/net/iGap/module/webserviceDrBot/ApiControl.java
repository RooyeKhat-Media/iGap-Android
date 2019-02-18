package net.iGap.module.webserviceDrBot;

import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

public class ApiControl {

    public static String apiKey = "unknown_webservice_key";
    public static String encryption(String text) {
        String encryptedMsg = null;
        try {
            byte[] expBytes = Base64.decode("AQAB".getBytes(), Base64.DEFAULT);
            byte[] modBytes = Base64.decode("oqWFs4s4Xvt3wQYT5f4M4JoEgjXALjHjAEqJgUYFkF71NI0DKZSAF8Zp8Ydz0HFsQqqnCf56GL9nAfZuDxF9xDsteHxGmkDLMbnymhJmXBSc0BlRgbLtWcX7c5AlryK35tj1ureAisbRYCc77bBsDoGTW8vSQ+il1EQMtUBxrFM=".getBytes(), Base64.DEFAULT);
            BigInteger modulus = new BigInteger(1, modBytes);
            BigInteger pubExp = new BigInteger(1, expBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(modulus, pubExp);
            PublicKey key = keyFactory.generatePublic(pubKeySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            if (text!=null) {
                byte[] cipherData = cipher.doFinal(text.getBytes("UTF-8"));
                encryptedMsg = new String(Base64.encode(cipherData, Base64.DEFAULT));
            } else {
                encryptedMsg = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return encryptedMsg;
    }
}
