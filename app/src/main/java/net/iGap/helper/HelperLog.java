package net.iGap.helper;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import net.iGap.BuildConfig;

public class HelperLog {

    public static void setErrorLog(String message) {

        Crashlytics.logException(new Exception(message));

        if (BuildConfig.DEBUG) {
            Log.e("debug", message);
        }
    }
}
