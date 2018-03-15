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

import com.google.protobuf.ByteString;

import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoFileUploadInit;
import net.iGap.proto.ProtoRequest;

import java.io.UnsupportedEncodingException;

public class RequestFileUploadInit {

    public void fileUploadInit(byte[] firstBytes, byte[] lastBytes, long size, byte[] fileHash, String identity, String fileName) throws UnsupportedEncodingException {

        ProtoFileUploadInit.FileUploadInit.Builder fileUploadInit = ProtoFileUploadInit.FileUploadInit.newBuilder();
        fileUploadInit.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        fileUploadInit.setFirstBytes(ByteString.copyFrom(firstBytes));
        fileUploadInit.setLastBytes(ByteString.copyFrom(lastBytes));
        fileUploadInit.setSize(size);
        fileUploadInit.setFileHash(ByteString.copyFrom(fileHash));
        fileUploadInit.setFileName(fileName);

        RequestWrapper requestWrapper = new RequestWrapper(701, fileUploadInit, identity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
