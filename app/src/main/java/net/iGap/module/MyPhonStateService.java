package net.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhonStateService extends BroadcastReceiver {
    TelephonyManager telephony;


    /**
     * use when start or finish ringing
     */

    public void onReceive(Context context, Intent intent) {
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void onDestroy() {
        telephony.listen(null, PhoneStateListener.LISTEN_NONE);
    }
}