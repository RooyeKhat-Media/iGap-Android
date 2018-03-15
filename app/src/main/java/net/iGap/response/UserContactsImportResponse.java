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

import net.iGap.request.RequestUserContactsGetList;

public class UserContactsImportResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserContactsImportResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        boolean getContactList = true;
        if (identity != null) {
            getContactList = (Boolean) identity;
        }

        if (getContactList) {
            new RequestUserContactsGetList().userContactGetList();
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        /**
         * even the import wasn't successful send request for get contacts list
         */
        new RequestUserContactsGetList().userContactGetList();
    }
}


