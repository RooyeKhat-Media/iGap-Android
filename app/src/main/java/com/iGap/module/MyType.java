/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.module;

public class MyType {
    public static final int sendLayot = 1;
    public static final int reciveLayout = 2;
    public static final int timeLayout = 3;

    public enum SendType {

        send(MyType.sendLayot),
        recvive(MyType.reciveLayout),
        timeLayout(MyType.timeLayout);

        private int value;

        SendType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
