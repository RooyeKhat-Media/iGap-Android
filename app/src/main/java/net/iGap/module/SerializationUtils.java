/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializationUtils {

    /**
     * @param obj - object to serialize to a byte array
     * @return byte array containing the serialized obj
     */
    public static byte[] serialize(Object obj) {
        byte[] result = null;
        ByteArrayOutputStream fos = null;

        try {
            fos = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(obj);
            result = fos.toByteArray();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * @param arr - the byte array that holds the serialized object
     * @return the deserialized object
     */
    public static Object deserialize(byte[] arr) {
        InputStream fis = null;

        try {
            fis = new ByteArrayInputStream(arr);
            ObjectInputStream o = new ObjectInputStream(fis);
            return o.readObject();
        } catch (IOException e) {
            System.err.println(e);
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * @param obj - object to be cloned
     * @return a clone of obj
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneObject(T obj) {
        return (T) deserialize(serialize(obj));
    }
}






