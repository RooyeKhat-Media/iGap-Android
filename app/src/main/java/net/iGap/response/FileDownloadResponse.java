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

import net.iGap.G;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoFileDownload;

public class FileDownloadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean isFromHelperDownload = false;

    public FileDownloadResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        super.handler();
        ProtoFileDownload.FileDownloadResponse.Builder builder = (ProtoFileDownload.FileDownloadResponse.Builder) message;
        String[] identityParams = identity.split("\\*");
        String cacheId = identityParams[0];
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.valueOf(identityParams[1]);
        long fileSize = Long.parseLong(identityParams[2]);
        String filePath = identityParams[3];

        int previousOffset = Integer.parseInt(identityParams[4]);
        if (identityParams[5].equals("true")) isFromHelperDownload = true;

        //
        //boolean avatarRequested = false;
        //long userId = -1;
        //RoomType roomType = RoomType.CHAT;
        //if (identityParams.length == 8) {
        //    avatarRequested = Boolean.parseBoolean(identityParams[5]);
        //    userId = Long.parseLong(identityParams[6]);
        //    roomType = RoomType.GROUP;
        //}

        long nextOffset = previousOffset + builder.getBytes().size();
        long progress = (nextOffset * 100) / fileSize;

        AndroidUtils.writeBytesToFile(filePath, builder.getBytes().toByteArray());

        if (isFromHelperDownload) {
            if (G.onFileDownloadResponse != null) {
                G.onFileDownloadResponse.onFileDownload(cacheId, nextOffset, selector, (int) progress);
            }
        } else {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onFileDownload(filePath, cacheId, fileSize, nextOffset, selector, (int) progress);
            }
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        String[] identityParams = identity.split("\\*");
        String cacheId = identityParams[0];
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.valueOf(identityParams[1]);
        if (identityParams[5].equals("true")) isFromHelperDownload = true;

        if (isFromHelperDownload) {
            if (G.onFileDownloadResponse != null) {
                G.onFileDownloadResponse.onError(majorCode, minorCode, cacheId, selector);
            }
        } else {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onError(majorCode, identity);
            }
        }
    }
}


