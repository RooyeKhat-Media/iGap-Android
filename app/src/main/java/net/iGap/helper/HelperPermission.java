/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.OnGetPermission;

import java.io.IOException;
import java.util.ArrayList;

/**
 * manage all permission in app
 */

public class HelperPermission {

    private static final int MY_PERMISSIONS_CAMERA = 201;
    private static final int MY_PERMISSIONS_STORAGE = 202;
    private static final int MY_PERMISSIONS_CONTACTS = 203;
    private static final int MY_PERMISSIONS_CALENDAR = 204;
    private static final int MY_PERMISSIONS_LOCATION = 205;
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 206;
    private static final int MY_PERMISSIONS_Phone = 207;
    private static final int MY_PERMISSIONS_Sms = 208;

    private static OnGetPermission ResultCamera;
    private static OnGetPermission ResultStorage;
    private static OnGetPermission ResultContact;
    private static OnGetPermission ResultCalendar;
    private static OnGetPermission ResultLocation;
    private static OnGetPermission ResultRecordAudio;
    private static OnGetPermission ResultPhone;
    private static OnGetPermission ResultSms;

    //************************************************************************************************************
    public static void getCameraPermission(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultCamera = onGetPermission;

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            getPermission(context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_CAMERA, context.getResources().getString(R.string.permission_camera), ResultCamera);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    //************************************************************************************************************
    public static void getStoragePermision(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultStorage = onGetPermission;

        ArrayList<String> needPermosion = null;

        int permissionReadStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteStorage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPermission(context, mStringArray, MY_PERMISSIONS_STORAGE, context.getResources().getString(R.string.permission_storage), ResultStorage);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    public static boolean grantedUseStorage() {
        return ContextCompat.checkSelfPermission(G.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //************************************************************************************************************
    public static void getContactPermision(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultContact = onGetPermission;

        ArrayList<String> needPermosion = null;

        int permissionReadContact = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        int permissionWriteContact = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS);
        //  int permissionWritGetAccunt = ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS);

        if (permissionReadContact != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.READ_CONTACTS);
        }

        if (permissionWriteContact != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.WRITE_CONTACTS);
        }

        //if (permissionWritGetAccunt != PackageManager.PERMISSION_GRANTED) {
        //    if (needPermosion == null) {
        //        needPermosion = new ArrayList<>();
        //    }
        //    needPermosion.add(Manifest.permission.GET_ACCOUNTS);
        //
        //}

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPermission(context, mStringArray, MY_PERMISSIONS_CONTACTS, context.getResources().getString(R.string.permission_contact), ResultContact);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    public static boolean grantedContactPermission() {
        return ContextCompat.checkSelfPermission(G.context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    //************************************************************************************************************
    public static void getCalendarPermision(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultCalendar = onGetPermission;

        ArrayList<String> needPermosion = null;

        int permissionReadCalendar = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR);
        int permissionWriteCaledar = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR);

        if (permissionReadCalendar != PackageManager.PERMISSION_GRANTED) {
            needPermosion = new ArrayList<>();
            needPermosion.add(Manifest.permission.READ_CALENDAR);
        }

        if (permissionWriteCaledar != PackageManager.PERMISSION_GRANTED) {
            if (needPermosion == null) {
                needPermosion = new ArrayList<>();
            }
            needPermosion.add(Manifest.permission.WRITE_CALENDAR);
        }

        if (needPermosion != null) {
            String[] mStringArray = new String[needPermosion.size()];
            mStringArray = needPermosion.toArray(mStringArray);
            getPermission(context, mStringArray, MY_PERMISSIONS_CALENDAR, context.getResources().getString(R.string.permission_calender), ResultCalendar);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    //************************************************************************************************************
    public static void getLocationPermission(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultLocation = onGetPermission;

        ArrayList<String> needPermission = null;

        int permissionFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoreseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionFineLocation != PackageManager.PERMISSION_GRANTED) {
            needPermission = new ArrayList<>();
            needPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionCoreseLocation != PackageManager.PERMISSION_GRANTED) {
            if (needPermission == null) {
                needPermission = new ArrayList<>();
            }
            needPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (needPermission != null) {
            String[] mStringArray = new String[needPermission.size()];
            mStringArray = needPermission.toArray(mStringArray);
            getPermission(context, mStringArray, MY_PERMISSIONS_LOCATION, context.getResources().getString(R.string.permission_location), ResultLocation);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    //************************************************************************************************************
    public static void getMicroPhonePermission(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultRecordAudio = onGetPermission;

        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            getPermission(context, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO, context.getResources().getString(R.string.permission_record_audio),
                    ResultRecordAudio);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    //************************************************************************************************************
    public static void getPhonePermision(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultPhone = onGetPermission;

        ArrayList<String> needPermission = null;

        int permissionCallPhone = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);

        if (permissionCallPhone != PackageManager.PERMISSION_GRANTED) {
            needPermission = new ArrayList<>();
            needPermission.add(Manifest.permission.CALL_PHONE);
        }

        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            if (needPermission == null) {
                needPermission = new ArrayList<>();
            }
            needPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (needPermission != null) {
            String[] mStringArray = new String[needPermission.size()];
            mStringArray = needPermission.toArray(mStringArray);
            getPermission(context, mStringArray, MY_PERMISSIONS_Phone, context.getResources().getString(R.string.permission_phone), ResultPhone);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    //************************************************************************************************************
    public static void getSmsPermision(Context context, OnGetPermission onGetPermission) throws IOException {

        if (checkApi()) {
            if (onGetPermission != null) onGetPermission.Allow();
            return;
        }

        ResultSms = onGetPermission;

        ArrayList<String> needPermission = null;

        int permissionReceiveSms = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);

        if (permissionReceiveSms != PackageManager.PERMISSION_GRANTED) {
            needPermission = new ArrayList<>();
            needPermission.add(Manifest.permission.RECEIVE_SMS);
        }

        if (needPermission != null) {
            String[] mStringArray = new String[needPermission.size()];
            mStringArray = needPermission.toArray(mStringArray);
            getPermission(context, mStringArray, MY_PERMISSIONS_Sms, context.getResources().getString(R.string.permission_sms), ResultSms);
        } else {
            if (onGetPermission != null) onGetPermission.Allow();
        }
    }

    //************************************************************************************************************
    //************************************************************************************************************
    private static boolean checkApi() {
        if (Build.VERSION.SDK_INT >= 23) {
            return false;
        }
        return true;
    }

    //************************************************************************************************************
    public static void getPermission(final Context context, final String[] needPermission, final int requestCode, String Text, final OnGetPermission onGetPermission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, needPermission[0])) {

            String message = context.getString(R.string.you_need_to_allow) + " " + Text;

            //final DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            //    @Override public void onClick(DialogInterface dialog, int which) {
            //
            //        ActivityCompat.requestPermissions((Activity) context, needPermission, requestCode);
            //    }
            //};
            //
            //final DialogInterface.OnClickListener onCancel = new DialogInterface.OnClickListener() {
            //    @Override public void onClick(DialogInterface dialog, int which) {
            //
            //        if (onGetPermission != null) onGetPermission.deny();
            //    }
            //};

            new MaterialDialog.Builder(context).cancelable(false).content(message).positiveText(context.getString(R.string.ok)).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    ActivityCompat.requestPermissions((Activity) context, needPermission, requestCode);
                }
            }).negativeText(context.getString(R.string.cancel)).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    if (onGetPermission != null) onGetPermission.deny();
                }
            }).show();


            //new AlertDialog.Builder(context).setMessage(message)
            //    .setPositiveButton(context.getString(R.string.ok), okListener)
            //    .setNegativeButton(context.getString(R.string.cancel), onCancel)
            //    .setCancelable(false)
            //    .create()
            //    .show();

            return;
        }

        ActivityCompat.requestPermissions((Activity) context, needPermission, requestCode);
    }

    //************************************************************************************************************
    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) throws IOException {

        switch (requestCode) {
            case MY_PERMISSIONS_CAMERA:
                actionResultBack(grantResults, ResultCamera);
                break;
            case MY_PERMISSIONS_STORAGE:
                actionResultBack(grantResults, ResultStorage);
                break;
            case MY_PERMISSIONS_CONTACTS:
                actionResultBack(grantResults, ResultContact);
                break;
            case MY_PERMISSIONS_CALENDAR:
                actionResultBack(grantResults, ResultCalendar);
                break;
            case MY_PERMISSIONS_LOCATION:
                actionResultBack(grantResults, ResultLocation);
                break;
            case MY_PERMISSIONS_RECORD_AUDIO:
                actionResultBack(grantResults, ResultRecordAudio);
                break;
            case MY_PERMISSIONS_Phone:
                actionResultBack(grantResults, ResultPhone);
                break;
            case MY_PERMISSIONS_Sms:
                actionResultBack(grantResults, ResultSms);
                break;
        }
    }

    //************************************************************************************************************

    private static void actionResultBack(int[] grantResults, OnGetPermission onGetPermission) throws IOException {

        if (onGetPermission == null) return;

        boolean allOk = true;
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) allOk = false;
        }

        if (allOk) {
            onGetPermission.Allow();
        } else {
            onGetPermission.deny();
        }
    }

    //************************************************************************************************************
}
