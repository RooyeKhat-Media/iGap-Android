/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.interfaces;

import net.iGap.proto.ProtoResponse;

/**
 * use this interface for each activities which need callbacks
 */
public interface OnFileUpload {
    void OnFileUploadOption(int firstBytesLimit, int lastBytesLimit, int maxConnection, String fileHashAsIdentity, ProtoResponse.Response response);

    void OnFileUploadInit(String token, double progress, long offset, int limit, String fileHashAsIdentity, ProtoResponse.Response response);

    void onFileUpload(double progress, long nextOffset, int nextLimit, String fileHashAsIdentity, ProtoResponse.Response response);

    void onFileUploadComplete(String fileHashAsIdentity, ProtoResponse.Response response);

    void onFileUploadTimeOut(String identity);
}
