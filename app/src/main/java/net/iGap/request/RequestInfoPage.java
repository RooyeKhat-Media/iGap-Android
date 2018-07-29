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

import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoInfoPage;
import net.iGap.proto.ProtoRequest;

public class RequestInfoPage {

    public void infoPage(String id) {
        ProtoInfoPage.InfoPage.Builder infoPage = ProtoInfoPage.InfoPage.newBuilder();
        infoPage.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        infoPage.setId(id);

        RequestWrapper requestWrapper = new RequestWrapper(503, infoPage, id);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
