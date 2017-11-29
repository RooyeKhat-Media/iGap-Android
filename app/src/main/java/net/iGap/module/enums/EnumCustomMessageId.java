/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module.enums;

public enum EnumCustomMessageId {
    FROM_NOW, FROM_BEGIN, CUSTOM_COUNT;

    public static EnumCustomMessageId convertType(String type) {
        if (type.equals("fromBegin")) {
            return EnumCustomMessageId.FROM_BEGIN;
        } else if (type.equals("fromNow")) {
            return EnumCustomMessageId.FROM_NOW;
        } else {
            return EnumCustomMessageId.CUSTOM_COUNT;
        }
    }
}
