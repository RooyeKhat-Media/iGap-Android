/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.interfaces;

import com.iGap.proto.ProtoFileUploadStatus;
import com.iGap.proto.ProtoResponse;

public interface OnFileUploadStatusResponse {
    void onFileUploadStatus(ProtoFileUploadStatus.FileUploadStatusResponse.Status status,
                            double progress, int recheckDelayMS, String fileHashAsIdentity,
                            ProtoResponse.Response response);
}
