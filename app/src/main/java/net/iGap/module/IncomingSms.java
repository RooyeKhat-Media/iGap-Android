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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import net.iGap.G;
import net.iGap.interfaces.OnSmsReceive;

/**
 * get sms from igap sms center for register user in program
 */

public class IncomingSms extends BroadcastReceiver {

    private OnSmsReceive listener;

    public IncomingSms() {
        super();
    }

    public IncomingSms(OnSmsReceive listener) {
        this.listener = listener;
    }

    public static void markMessageRead(String number, String body) {

        //        Uri uri = Uri.parse("content://sms/inbox");
        //        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        //        try {
        //
        //            while (cursor.moveToNext()) {
        //                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
        //                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
        //                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
        //                        ContentValues values = new ContentValues();
        //                        values.put("read", 1);
        //                        context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
        //                        return;
        //                    }
        //                }
        //            }
        //        } catch (Exception e) {
        //            Log.e("Mark Read", "Error in Read: " + e.toString());
        //        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    for (Long number : G.smsNumbers) {
                        if (phoneNumber.contains(number.toString())) {
                            listener.onSmsReceive("" + phoneNumber, message);
                            //markMessageRead(phoneNumber, message);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
