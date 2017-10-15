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
import net.iGap.request.RequestFileDownload;

public class FileDownloadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;
    public boolean isFromHelperDownload = false;

    public FileDownloadResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        super.handler();
        ProtoFileDownload.FileDownloadResponse.Builder builder = (ProtoFileDownload.FileDownloadResponse.Builder) message;
        RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
        String cacheId = identityFileDownload.cacheId;
        long fileSize = identityFileDownload.size;
        String filePath = identityFileDownload.filepath;
        int previousOffset = (int) identityFileDownload.offset;
        isFromHelperDownload = identityFileDownload.isFromHelperDownload;

        long nextOffset = previousOffset + builder.getBytes().size();
        long progress = (nextOffset * 100) / fileSize;

        AndroidUtils.writeBytesToFile(filePath, builder.getBytes().toByteArray());

        if (isFromHelperDownload) {
            if (G.onFileDownloadResponse != null) {
                G.onFileDownloadResponse.onFileDownload(cacheId, nextOffset, identityFileDownload.selector, (int) progress);
            }
        } else {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onFileDownload(filePath, cacheId, fileSize, nextOffset, identityFileDownload.selector, (int) progress);
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
        RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
        isFromHelperDownload = identityFileDownload.isFromHelperDownload;

        if (isFromHelperDownload) {
            if (G.onFileDownloadResponse != null) {
                G.onFileDownloadResponse.onError(majorCode, minorCode, identityFileDownload.cacheId, identityFileDownload.selector);
            }
        } else {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onError(majorCode, identity);
            }
        }
    }
}


