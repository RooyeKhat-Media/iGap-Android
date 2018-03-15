/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.os.Build;

import net.iGap.realm.RealmRoomMessage;

import java.util.Comparator;

public enum SortMessages implements Comparator<RealmRoomMessage> {
    ASC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(o1.getMessageId(), o2.getMessageId());
            } else {
                return Long.valueOf(o1.getMessageId()).compareTo(Long.valueOf(o2.getMessageId()));
            }
        }
    }, DESC {
        @Override
        public int compare(RealmRoomMessage o1, RealmRoomMessage o2) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Long.compare(o2.getMessageId(), o1.getMessageId());
            } else {
                return Long.valueOf(o2.getMessageId()).compareTo(Long.valueOf(o1.getMessageId()));
            }
        }
    }
}
