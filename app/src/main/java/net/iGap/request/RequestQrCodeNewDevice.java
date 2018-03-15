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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoQrCodeNewDevice;

import java.util.Locale;

import static net.iGap.G.context;

public class RequestQrCodeNewDevice {


    private int AppBuildVersion;
    private String AppVersion;
    private String Device;
    private String Language;


    public void qrCodeNewDevice() {

        infoApp();

        ProtoQrCodeNewDevice.QrCodeNewDevice.Builder builder = ProtoQrCodeNewDevice.QrCodeNewDevice.newBuilder();
        builder.setAppName("iGap Android");
        builder.setAppId(2);
        builder.setAppBuildVersion(AppBuildVersion);
        builder.setAppVersion(AppVersion);
        builder.setPlatform(ProtoGlobal.Platform.ANDROID);
        builder.setPlatformVersion(Integer.toString(android.os.Build.VERSION.SDK_INT));
        builder.setDevice(typeMobile());
        builder.setDeviceName(Device);

        RequestWrapper requestWrapper = new RequestWrapper(802, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void infoApp() {

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pInfo != null) {
            AppVersion = pInfo.versionName;
            AppBuildVersion = pInfo.versionCode;
        }
        Device = Build.BRAND;
        Language = Locale.getDefault().getDisplayLanguage();
    }

    private ProtoGlobal.Language typeLanguage() {

        Language = Locale.getDefault().getDisplayLanguage();
        if (Language.equals("English")) {
            return ProtoGlobal.Language.FA_IR;
        } else {
            return ProtoGlobal.Language.EN_US;
        }
    }

    private ProtoGlobal.Device typeMobile() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            return ProtoGlobal.Device.TABLET;
        } else {
            // smaller device
            return ProtoGlobal.Device.MOBILE;
        }
    }

}
