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

import android.view.View;
import com.iGap.module.enums.SendingStep;
import com.iGap.module.structs.StructMessageInfo;
import com.iGap.realm.RealmRoomMessage;

public interface IMessageItem {
    /**
     * open means open for files and play for videos
     */
    void onOpenClick(View view, StructMessageInfo message, int pos);

    void onContainerClick(View view, StructMessageInfo message, int pos);

    void onSenderAvatarClick(View view, StructMessageInfo message, int pos);

    void onUploadOrCompressCancel(View view, StructMessageInfo message, int pos, SendingStep sendingStep);

    void onFailedMessageClick(View view, StructMessageInfo message, int pos);

    void onReplyClick(RealmRoomMessage replyMessage);

    void onDownloadAllEqualCashId(String token, String messageid);

    //void onVoteClick(StructMessageInfo message, String vote, ProtoGlobal.RoomMessageReaction reaction);
}
