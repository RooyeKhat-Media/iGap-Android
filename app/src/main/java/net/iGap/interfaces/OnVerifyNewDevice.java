package net.iGap.interfaces;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import net.iGap.proto.ProtoGlobal;

public interface OnVerifyNewDevice {

    void verifyNewDevice(String appName, int appId, int appBuildVersion, String appVersion, ProtoGlobal.Platform platform, String platformVersion, ProtoGlobal.Device device, String deviceName, boolean twoStepVerification);

    void errorVerifyNewDevice(int majorCode, int minCode);
}
