package net.iGap.helper;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class HelperLog {

    public static void setErrorLog(String message) {

        Crashlytics.logException(new Exception(message));

        Log.e("debug", message);
    }
}
