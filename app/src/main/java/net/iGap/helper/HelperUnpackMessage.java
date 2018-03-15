/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.util.Log;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoResponse;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * unpack message after get response from server
 */
public class HelperUnpackMessage {

    /**
     * Automatically fetch message and create proto class and then create response class and fill
     * response class with proto class
     *
     * @param message byteArray message
     * @return true if all codes running is ok and return false if code have error
     */

    public static synchronized boolean unpack(byte[] message) {

        Object[] objects = fetchMessage(message);
        if (objects == null) {
            return false;
        }

        int actionId = (int) objects[0];

        if (!G.isSecure && !G.unSecureResponseActionId.contains(Integer.toString(actionId))) {
            return false;
        }

        byte[] payload = (byte[]) objects[1];
        String className = (String) objects[2];

        String protoClassName = HelperClassNamePreparation.preparationProtoClassName(className);
        Object protoObject = fillProtoClassData(protoClassName, payload);
        String responseId = getResponseId(protoObject);

        if (responseId == null) {
            if (actionId == 0) {
                instanceResponseClass(actionId, protoObject, null, "error");
            } else {
                instanceResponseClass(actionId, protoObject, null, "handler");
            }
        } else {
            if (!G.requestQueueMap.containsKey(responseId)) {
                Log.i("SOC", "HelperUnpackMessage responseId is not exist in requestQueueMap ");
                return false;
            }

            try {
                if (actionId == 0) { // error
                    if (responseId.contains(".")) {
                        String randomId = responseId.split("\\.")[0];
                        String indexString = responseId.split("\\.")[1];
                        int index = Integer.parseInt(indexString);
                        ArrayList<Object> values = G.requestQueueRelationMap.get(randomId);
                        G.requestQueueRelationMap.remove(randomId);
                        for (int i = 0; i < values.size(); i++) {
                            Object currentProto;
                            String currentResponseId = randomId + "." + i;
                            RequestWrapper currentRequestWrapper = G.requestQueueMap.get(currentResponseId);
                            if (i == index) {
                                currentProto = protoObject;
                            } else {
                                ProtoResponse.Response.Builder responseBuilder = ProtoResponse.Response.newBuilder();
                                responseBuilder.setId(getRequestId(currentRequestWrapper));
                                responseBuilder.build();

                                ProtoError.ErrorResponse.Builder errorResponse = ProtoError.ErrorResponse.newBuilder();
                                errorResponse.setMinorCode(6);
                                errorResponse.setMinorCode(1);
                                errorResponse.setResponse(responseBuilder);
                                errorResponse.build();

                                currentProto = errorResponse;
                            }
                            G.requestQueueMap.remove(currentResponseId);
                            instanceResponseClass(currentRequestWrapper.getActionId() + Config.LOOKUP_MAP_RESPONSE_OFFSET, currentProto, currentRequestWrapper.identity, "error");
                        }
                    } else {
                        RequestWrapper requestWrapper = G.requestQueueMap.get(responseId);
                        G.requestQueueMap.remove(responseId);

                        instanceResponseClass(requestWrapper.getActionId() + Config.LOOKUP_MAP_RESPONSE_OFFSET, protoObject, requestWrapper.identity, "error");
                    }
                } else {
                    if (responseId.contains(".")) {

                        String randomId = responseId.split("\\.")[0];
                        String indexString = responseId.split("\\.")[1];
                        int index = Integer.parseInt(indexString);

                        Object responseClass = instanceResponseClass(actionId, protoObject, G.requestQueueMap.get(responseId).identity, null);

                        ArrayList<Object> objectValues = G.requestQueueRelationMap.get(randomId);
                        objectValues.set(index, responseClass);

                        boolean runLoop = true;

                        for (int i = 0; i < objectValues.size(); i++) {
                            if (objectValues.get(i) == null) {
                                runLoop = false;
                                break;
                            }
                        }

                        if (runLoop) {
                            G.requestQueueRelationMap.remove(randomId);

                            for (int j = 0; j < objectValues.size(); j++) {
                                G.requestQueueMap.remove(randomId + "." + j);

                                Object object = objectValues.get(j);

                                Field fieldActionId = object.getClass().getDeclaredField("actionId");
                                Field fieldMessage = object.getClass().getDeclaredField("message");
                                Field fieldIdentity = object.getClass().getDeclaredField("identity");
                                int currentActionId = fieldActionId.getInt(object);
                                Object currentMessage = fieldMessage.get(object);
                                String currentIdentity = null;
                                if (fieldIdentity.get(object) != null) {
                                    currentIdentity = fieldIdentity.get(object).toString();
                                }
                                instanceResponseClass(currentActionId, currentMessage, currentIdentity, "handler");
                            }
                        }
                    } else {
                        RequestWrapper requestWrapper = G.requestQueueMap.get(responseId);
                        G.requestQueueMap.remove(responseId);
                        instanceResponseClass(actionId, protoObject, requestWrapper.identity, "handler");
                    }
                }
                RequestQueue.sendRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private static synchronized String getResponseId(Object protoObject) {
        String responseId = null;
        try {
            Method method = protoObject.getClass().getMethod("getResponse");
            ProtoResponse.Response response = (ProtoResponse.Response) method.invoke(protoObject);
            if (response.getId().equals("")) {
                return null;
            }
            responseId = response.getId();
        } catch (SecurityException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return responseId;
    }

    private static synchronized String getRequestId(RequestWrapper requestWrapper) {

        String requestId = null;
        try {
            Object protoObject = requestWrapper.getProtoObject();
            Method method = protoObject.getClass().getMethod("getRequest");
            ProtoRequest.Request request = (ProtoRequest.Request) method.invoke(protoObject);
            requestId = request.getId();
        } catch (SecurityException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return requestId;
    }

    /**
     * get actionId and payload from byteArray message and also fetch className with actionId
     *
     * @return objects[0] == actionId , objects[1] == className , objects[2] == payload
     */
    private static synchronized Object[] fetchMessage(byte[] message) {

        int actionId = getId(message);

        byte[] payload = getProtoInfo(message);

        String className = getClassName(actionId);

        if (className == null) {
            return null;
        }

        return new Object[]{actionId, payload, className};
    }

    public static synchronized int getId(byte[] byteArray) {

        byteArray = Arrays.copyOfRange(byteArray, 0, 2);
        byteArray = HelperNumerical.orderBytesToLittleEndian(byteArray);

        int value = 0;
        for (int i = 0; i < byteArray.length; i++) {
            value += ((int) byteArray[i] & 0xffL) << (8 * i);
        }
        return value;
    }

    private static synchronized String getClassName(int value) {

        if (!G.lookupMap.containsKey(value)) return null;

        return G.lookupMap.get(value);
    }

    private static synchronized byte[] getProtoInfo(byte[] byteArray) {
        byteArray = Arrays.copyOfRange(byteArray, 2, byteArray.length);
        return byteArray;
    }

    private static synchronized Object fillProtoClassData(String protoClassName, byte[] protoMessage) {
        Object object3 = null;
        try {

            Class<?> c = Class.forName(protoClassName);
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object object1 = constructor.newInstance();
            Method method1 = object1.getClass().getMethod("newBuilder");
            Object object2 = method1.invoke(object1);
            Method method2 = object2.getClass().getMethod("mergeFrom", byte[].class);
            object3 = method2.invoke(object2, protoMessage);
            Method method3 = object3.getClass().getMethod("build");
            method3.invoke(object3);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return object3;
    }

    private static synchronized Object instanceResponseClass(int actionId, Object protoObject, Object identity, String optionalMethod) {
        Object object = null;
        try {
            String className = getClassName(actionId);
            String responseClassName = HelperClassNamePreparation.preparationResponseClassName(className);
            Class<?> responseClass = Class.forName(responseClassName);
            Constructor<?> constructor;
            try {
                constructor = responseClass.getDeclaredConstructor(int.class, Object.class, Object.class);
            } catch (NoSuchMethodException e) {
                constructor = responseClass.getDeclaredConstructor(int.class, Object.class, String.class);
            }
            constructor.setAccessible(true);
            object = constructor.newInstance(actionId, protoObject, identity);
            if (optionalMethod != null) {
                responseClass.getMethod(optionalMethod).invoke(object);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }
}
