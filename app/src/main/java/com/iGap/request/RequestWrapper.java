/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.request;

public class RequestWrapper {

    public long time = 0;
    public String identity;
    protected Object protoObject;
    protected int actionId;

    public RequestWrapper(int actionId, Object protoObject, String identity) {
        this.actionId = actionId;
        this.protoObject = protoObject;
        this.identity = identity;
    }

    public RequestWrapper(int actionId, Object protoObject) {
        this.actionId = actionId;
        this.protoObject = protoObject;
    }

    public int getActionId() {
        return actionId;
    }

    public long getTime() {
        return time;
    }

    public Object getProtoObject() {
        return protoObject;
    }

    public String getIdentity() {
        return identity;
    }
}
