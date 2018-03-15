/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import android.support.annotation.Nullable;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.HelperClassNamePreparation;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperString;
import net.iGap.module.AESCrypt;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.iGap.G.forcePriorityActionId;

public class RequestQueue {

    public static final CopyOnWriteArrayList<RequestWrapper> WAITING_REQUEST_WRAPPERS = new CopyOnWriteArrayList<>(); // if not logged-in
    private static final int QUEUE_LIMIT = 50;
    private static final int DEFAULT_PRIORITY = 100;
    public static CopyOnWriteArrayList<RequestWrapper> RUNNING_REQUEST_WRAPPERS = new CopyOnWriteArrayList<>(); // when logged-in and WAITING_REQUEST_WRAPPERS is full
    public static PriorityQueue<Integer> actionIdPriority = new PriorityQueue<>(1000, new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            if (a < b) {
                return 1;
            }
            if (a > b) {
                return -1;
            }
            return 0;
        }
    });
    private static ConcurrentHashMap<Integer, ArrayList<RequestWrapper[]>> priorityRequestWrapper = new ConcurrentHashMap<>();

    public static synchronized void sendRequest(RequestWrapper... requestWrappers) throws IllegalAccessException {
        int length = requestWrappers.length;
        String randomId = HelperString.generateKey();

        if (G.requestQueueMap.size() > QUEUE_LIMIT && !forcePriorityActionId.contains(requestWrappers[0].actionId)) {
            Object priority = G.priorityActionId.get(requestWrappers[0].actionId);
            if (priority == null) {
                priority = (int) DEFAULT_PRIORITY;
            }
            ArrayList<RequestWrapper[]> arrayWrapper = new ArrayList<>();
            if (actionIdPriority.contains((int) priority)) {
                arrayWrapper = priorityRequestWrapper.get((int) priority);
                arrayWrapper.add(requestWrappers);
                priorityRequestWrapper.put((int) priority, arrayWrapper);
            } else {
                arrayWrapper.add(requestWrappers);
                priorityRequestWrapper.put((int) priority, arrayWrapper);
            }
            actionIdPriority.offer((int) priority);
            return;
        }

        if (length == 1) {
            prepareRequest(randomId, requestWrappers[0]);
        } else if (length > 1) {
            ArrayList<Object> relationValue = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                relationValue.add(null);
            }

            G.requestQueueRelationMap.put(randomId, relationValue);

            for (int i = 0; i < requestWrappers.length; i++) {
                prepareRequest(randomId + "." + i, requestWrappers[i]);
            }
        } else if (length == 0) {
            Log.e("SOC_REQ", "RequestWrapper length should bigger than zero");
        }
    }

    public static synchronized void sendRequest() throws IllegalAccessException {
        if (actionIdPriority.size() <= 0) {
            return;
        }

        ArrayList<RequestWrapper[]> arrayWrapper = priorityRequestWrapper.get(actionIdPriority.poll());
        if (arrayWrapper != null && arrayWrapper.size() > 0) {
            RequestWrapper[] requestWrappers = arrayWrapper.get(0);
            arrayWrapper.remove(0);
            priorityRequestWrapper.put(requestWrappers[0].getActionId(), arrayWrapper);

            sendRequest(requestWrappers);
        }
    }

    private static synchronized void prepareRequest(String randomId, RequestWrapper requestWrapper) {
        if (!G.pullRequestQueueRunned.get()) {
            G.pullRequestQueueRunned.getAndSet(true);
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQueuePullFunction();
                }
            }, Config.TIME_OUT_DELAY_MS);
        }

        //requestWrapper.time = System.currentTimeMillis(); // here the client was previously recorded time
        requestWrapper.setRandomId(randomId);
        ProtoRequest.Request.Builder requestBuilder = ProtoRequest.Request.newBuilder();
        requestBuilder.setId(randomId);

        try {
            Object protoObject = requestWrapper.getProtoObject();
            Object protoInstance = null;
            try {
                Method setRequestMethod = protoObject.getClass().getMethod("setRequest", ProtoRequest.Request.Builder.class);
                protoInstance = setRequestMethod.invoke(protoObject, requestBuilder);
                Method method2 = protoInstance.getClass().getMethod("build");
                protoInstance = method2.invoke(protoInstance);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            G.requestQueueMap.put(randomId, requestWrapper);

            byte[] actionId = HelperNumerical.intToByteArray(requestWrapper.actionId);
            actionId = HelperNumerical.orderBytesToLittleEndian(actionId);

            Method toByteArrayMethod = protoInstance.getClass().getMethod("toByteArray");
            byte[] payload = (byte[]) toByteArrayMethod.invoke(protoInstance);
            byte[] message = HelperNumerical.appendByteArrays(actionId, payload);

            if (G.isSecure) {
                if (G.userLogin || G.unLogin.contains(requestWrapper.actionId + "")) {
                    message = AESCrypt.encrypt(G.symmetricKey, message);
                    WebSocket webSocket = WebSocketClient.getInstance();
                    if (webSocket != null) {
                        webSocket.sendBinary(message, requestWrapper);
                    }
                } else {
                    if (G.waitingActionIds.contains(requestWrapper.actionId + "")) {
                        /**
                         * add to waiting request wrappers while user not logged-in yet
                         */
                        WAITING_REQUEST_WRAPPERS.add(requestWrapper);
                    }
                }
            } else if (G.unSecure.contains(requestWrapper.actionId + "")) {
                WebSocket webSocket = WebSocketClient.getInstance();
                if (webSocket != null) {
                    webSocket.sendBinary(message, requestWrapper);
                }
            } else { //if (G.waitingActionIds.contains(requestWrapper.actionId + "")) {
                timeOutImmediately(randomId, false);
                /**
                 * add to waiting request wrappers while user not logged-in yet
                 */
                WAITING_REQUEST_WRAPPERS.add(requestWrapper);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void requestQueuePullFunction() {

        for (Iterator<Map.Entry<String, RequestWrapper>> it = G.requestQueueMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RequestWrapper> entry = it.next();
            String key = entry.getKey();
            RequestWrapper requestWrapper = entry.getValue();
            boolean delete = timeDifference(requestWrapper.getTime());
            if (delete) {
                if (key.contains(".")) {
                    String randomId = key.split("\\.")[0];

                    if (!G.requestQueueRelationMap.containsKey(randomId)) continue;

                    ArrayList<Object> array = G.requestQueueRelationMap.get(randomId);

                    G.requestQueueRelationMap.remove(randomId);

                    for (int i = 0; i < array.size(); i++) {
                        requestQueueMapRemover(randomId + "." + i);
                    }
                } else {
                    requestQueueMapRemover(key);
                }
            }
        }

        if (G.requestQueueMap.size() > 0) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQueuePullFunction();
                }
            }, Config.TIME_OUT_DELAY_MS);
        } else {
            G.pullRequestQueueRunned.getAndSet(false);
        }
    }

    private static void requestQueueMapRemover(String key) {

        try {

            RequestWrapper requestWrapper = G.requestQueueMap.get(key);
            G.requestQueueMap.remove(key);

            int actionId = requestWrapper.getActionId();
            String className = G.lookupMap.get(actionId + Config.LOOKUP_MAP_RESPONSE_OFFSET);
            String responseClassName = HelperClassNamePreparation.preparationResponseClassName(className);

            ProtoResponse.Response.Builder responseBuilder = ProtoResponse.Response.newBuilder();
            responseBuilder.setTimestamp((int) System.currentTimeMillis());
            responseBuilder.setId(key);
            responseBuilder.build();

            ProtoError.ErrorResponse.Builder errorBuilder = ProtoError.ErrorResponse.newBuilder();
            errorBuilder.setResponse(responseBuilder);
            errorBuilder.setMajorCode(5);
            errorBuilder.setMinorCode(1);
            errorBuilder.build();

            Class<?> c = Class.forName(responseClassName);
            Object object;
            try {
                object = c.getConstructor(int.class, Object.class, Object.class).newInstance(actionId, errorBuilder, requestWrapper.identity);
            } catch (NoSuchMethodException e) {
                object = c.getConstructor(int.class, Object.class, String.class).newInstance(actionId, errorBuilder, requestWrapper.identity);
            }
            Method setTimeoutMethod = object.getClass().getMethod("timeOut");
            setTimeoutMethod.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * timeOut request
     *
     * @param keyRandomId timeOut with this specific key
     * @param allRequest  timeOut all requests
     */
    public static void timeOutImmediately(@Nullable String keyRandomId, boolean allRequest) {
        for (Iterator<Map.Entry<String, RequestWrapper>> it = G.requestQueueMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RequestWrapper> entry = it.next();
            String key = entry.getKey();
            if (allRequest || (keyRandomId != null && key.equals(keyRandomId))) {
                if (key.contains(".")) {
                    String randomId = key.split("\\.")[0];

                    if (!G.requestQueueRelationMap.containsKey(randomId)) continue;

                    ArrayList<Object> array = G.requestQueueRelationMap.get(randomId);

                    G.requestQueueRelationMap.remove(randomId);

                    for (int i = 0; i < array.size(); i++) {
                        requestQueueMapRemover(randomId + "." + i);
                    }
                } else {
                    requestQueueMapRemover(key);
                }
            }
        }
    }

    public static void clearPriorityQueue() {
        if (actionIdPriority != null) {
            actionIdPriority.clear();
        }
    }

    private static boolean timeDifference(long beforeTime) {

        /**
         * if time not set yet don't set timeout
         */
        if (beforeTime == 0) {
            return false;
        }

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - beforeTime);

        if (difference >= Config.TIME_OUT_MS) {
            return true;
        }

        return false;
    }
}