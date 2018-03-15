package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.ObservableField;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperLog;
import net.iGap.module.AppUtils;
import net.iGap.module.DialogAnimation;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmUserInfo;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;

public class FragmentPassCodeViewModel {

    private final int PIN = 0;
    private final int PASSWORD = 1;
    public ObservableField<String> titlePassCode = new ObservableField<>(G.context.getResources().getString(R.string.two_step_pass_code));
    public ObservableField<String> autoLockText = new ObservableField<>("in 1 hour");
    public ObservableField<String> edtSetPasswordText = new ObservableField<>("");
    public ObservableField<String> edtSetPasswordHint = new ObservableField<>("");
    public ObservableField<String> txtSetPassword = new ObservableField<>(G.context.getResources().getString(R.string.enter_pass_code));
    public ObservableField<String> txtModePassCode = new ObservableField<>(G.context.getResources().getString(R.string.PIN));
    public ObservableField<Boolean> isTogglePassCode = new ObservableField<>(false);
    public ObservableField<Boolean> isFingerPrint = new ObservableField<>(false);
    public ObservableField<Boolean> isAllowScreenCapture = new ObservableField<>(false);
    public ObservableField<Boolean> txtChangePassCodeEnable = new ObservableField<>(false);
    public ObservableField<Integer> edtSetPasswordInput = new ObservableField<>(InputType.TYPE_TEXT_VARIATION_PASSWORD);
    public ObservableField<Integer> rootSettingPassword = new ObservableField<>(View.GONE);
    public ObservableField<Integer> rootEnterPassword = new ObservableField<>(View.GONE);
    public ObservableField<Integer> vgTogglePassCodeVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> txtChangePassCodeColor = new ObservableField<>(G.context.getResources().getColor(R.color.gray_5c));
    public ObservableField<Integer> titlePassCodeVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> edtSetPasswordMaxLength = new ObservableField<>(20);
    public ObservableField<Integer> rippleOkVisibility = new ObservableField<>(View.GONE);
    public ObservableField<Integer> layoutModePassCode = new ObservableField<>(View.GONE);
    public ObservableField<Integer> vgToggleFingerPrintVisibility = new ObservableField<>(View.GONE);
    private Realm realm;
    private boolean isPassCode;
    private boolean isFingerPrintCode;
    private String passCode;
    private String password;
    private int page = 0;
    private net.iGap.module.NumberPicker numberPickerMinutes;
    private boolean deviceHasFingerPrint;
    private int kindPassword = 0;
    private SharedPreferences sharedPreferences;
    private boolean screenShot;
    private RealmUserInfo realmUserInfo;

    public FragmentPassCodeViewModel() {
        getInfo();
    }

    public void onClickTogglePassCode(View v) {

        edtSetPasswordText.set("");
        if (realmUserInfo != null) isPassCode = realmUserInfo.isPassCode();

        if (!isPassCode) {
            page = 0;
            vgTogglePassCodeVisibility.set(View.GONE);
            rootEnterPassword.set(View.VISIBLE);
            rootSettingPassword.set(View.GONE);
            rippleOkVisibility.set(View.VISIBLE);
            //txtSetPassword.setText(G.fragmentActivity.getResources().getString(R.string.enter_a_password));
            //titlePassCode.set("PIN");
            txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.enter_a_password));
            titlePassCodeVisibility.set(View.GONE);
            layoutModePassCode.set(View.VISIBLE);
            if (kindPassword == PIN) {
                edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
            } else {
                edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
            }
        } else {
            isTogglePassCode.set(false);
            vgTogglePassCodeVisibility.set(View.VISIBLE);
            rootEnterPassword.set(View.GONE);
            rootSettingPassword.set(View.GONE);
            rippleOkVisibility.set(View.GONE);
            layoutModePassCode.set(View.GONE);
            txtChangePassCodeEnable.set(false);
            txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.gray_5c));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
            editor.apply();

            G.isPassCode = false;
            edtSetPasswordText.set("");
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (realmUserInfo != null) {
                        realmUserInfo.setPassCode(false);
                        realmUserInfo.setPassCode("");
                    }
                }
            });
        }

    }

    public void onClickChangePassCode(View v) {

        edtSetPasswordHint.set(G.context.getString(R.string.PIN));
        page = 0;
        edtSetPasswordText.set("");
        vgTogglePassCodeVisibility.set(View.GONE);
        rootEnterPassword.set(View.VISIBLE);
        rootSettingPassword.set(View.GONE);
        rippleOkVisibility.set(View.VISIBLE);
        //titlePassCode.setText("PIN");
        titlePassCodeVisibility.set(View.GONE);
        layoutModePassCode.set(View.VISIBLE);
        txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.enter_change_pass_code));
        if (kindPassword == PIN) {
            edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
        } else {
            edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
        }

    }

    public void onClickChangeVgToggleFingerPrint(View v) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                if (realmUserInfo != null) {
                    if (isFingerPrintCode) {
                        realmUserInfo.setFingerPrint(false);
                        isFingerPrint.set(false);
                        isFingerPrintCode = false;
                    } else {
                        realmUserInfo.setFingerPrint(true);
                        isFingerPrint.set(true);
                        isFingerPrintCode = true;
                    }
                }
            }
        });

    }

    public void onClickRippleOk(View v) {

        if (page == 0 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().length() >= 4) {
                password = edtSetPasswordText.get();
                edtSetPasswordText.set("");
                txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.re_enter_pass_code));
                page = 1;
            } else {
                AppUtils.closeKeyboard(v);
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.limit_passcode));
            }

        } else if (page == 1 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                txtChangePassCodeEnable.set(true);
                txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.black_register));
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.set(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);

                G.isPassCode = true;
                ActivityMain.isLock = false;
                AppUtils.closeKeyboard(v);

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realmUserInfo != null) {
                            realmUserInfo.setPassCode(true);
                            realmUserInfo.setPassCode(edtSetPasswordText.get());
                            realmUserInfo.setKindPassCode(kindPassword);
                        }
                    }
                });

                isTogglePassCode.set(true);
                edtSetPasswordText.set("");
            } else {
                AppUtils.closeKeyboard(v);
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match));
            }

        } else if (page == 3 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.set(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);
                txtChangePassCodeEnable.set(true);
                txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.black_register));
                AppUtils.closeKeyboard(v);
            } else {
                AppUtils.closeKeyboard(v);
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
                edtSetPasswordText.set("");
            }

        } else {
            AppUtils.closeKeyboard(v);
            AppUtils.error(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
            edtSetPasswordText.set("");
        }

    }

    public void onClickChangeAutoLock(View v) {

        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.auto_lock)).customView(R.layout.dialog_auto_lock, wrapInScrollView).positiveText(R.string.B_ok).negativeText(R.string.B_cancel).build();

        View view1 = dialog.getCustomView();

        assert view1 != null;
        numberPickerMinutes = (net.iGap.module.NumberPicker) view1.findViewById(R.id.numberPicker);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(4);
        //numberPickerMinutes.setWrapSelectorWheel(true);
        //numberPickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        long valueNumberPic = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        if (valueNumberPic == 0) {
            numberPickerMinutes.setValue(0);
        } else if (valueNumberPic == 60) {
            numberPickerMinutes.setValue(1);
        } else if (valueNumberPic == 60 * 5) {
            numberPickerMinutes.setValue(2);
        } else if (valueNumberPic == 60 * 60) {
            numberPickerMinutes.setValue(3);
        } else if (valueNumberPic == 60 * 60 * 5) {
            numberPickerMinutes.setValue(4);
        }

        numberPickerMinutes.setFormatter(new net.iGap.module.NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                if (value == 0) {
                    return G.fragmentActivity.getResources().getString(R.string.Disable);
                } else if (value == 1) {
                    return G.fragmentActivity.getResources().getString(R.string.in_1_minutes);
                } else if (value == 2) {
                    return G.fragmentActivity.getResources().getString(R.string.in_5_minutes);
                } else if (value == 3) {
                    return G.fragmentActivity.getResources().getString(R.string.in_1_hours);
                } else if (value == 4) {
                    return G.fragmentActivity.getResources().getString(R.string.in_5_hours);
                }
                return "";
            }
        });

        View btnPositive = dialog.getActionButton(DialogAction.POSITIVE);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(SHP_SETTING.KEY_TIME_LOCK, numberPickerMinutes.getValue());
                editor.apply();

                int which = numberPickerMinutes.getValue();
                if (which == 0) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.Disable));
                } else if (which == 1) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_minutes));
                } else if (which == 2) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 5);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_minutes));
                } else if (which == 3) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_hours));
                } else if (which == 4) {
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60 * 5);
                    autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_hours));
                }
                editor.apply();
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public void onClickAllowScreenCapture(View v) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (screenShot) {
            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
            isAllowScreenCapture.set(false);
            screenShot = false;

            try {
                if (G.currentActivity != null) {
                    G.currentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                }
            } catch (Exception e) {
                HelperLog.setErrorLog(e.toString());
            }


        } else {
            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);
            isAllowScreenCapture.set(true);
            screenShot = true;
            try {
                if (G.currentActivity != null) {
                    G.currentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }
            } catch (Exception e) {
                HelperLog.setErrorLog(e.toString());
            }

        }
        editor.apply();

    }

    public void onClickModePassCode(View v) {


        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).items(R.array.modePassCode).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
                        edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
                        maxLengthEditText(4);
                        kindPassword = PIN;
                        edtSetPasswordHint.set(G.context.getString(R.string.PIN));
                        txtModePassCode.set(G.context.getString(R.string.PIN));
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
                        maxLengthEditText(20);
                        kindPassword = PASSWORD;
                        edtSetPasswordHint.set(G.context.getString(R.string.password));
                        txtModePassCode.set(G.context.getString(R.string.password));
                        break;
                }

            }
        }).build();

        DialogAnimation.animationUp(dialog);
        dialog.show();


    }

    private void getInfo() {

        checkFingerPrint();
        realm = Realm.getDefaultInstance();
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (realmUserInfo != null) {
            isPassCode = realmUserInfo.isPassCode();
            isFingerPrintCode = realmUserInfo.isFingerPrint();
            password = realmUserInfo.getPassCode();
            kindPassword = realmUserInfo.getKindPassCode();
        }


        if (kindPassword == PIN) {
            edtSetPasswordInput.set(InputType.TYPE_CLASS_NUMBER);
            maxLengthEditText(4);
        } else {
            edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT);
            maxLengthEditText(20);
        }


        if (isPassCode) {

            page = 3;
            vgTogglePassCodeVisibility.set(View.GONE);
            rootEnterPassword.set(View.VISIBLE);
            rootSettingPassword.set(View.GONE);
            rippleOkVisibility.set(View.VISIBLE);
            txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
            txtChangePassCodeEnable.set(true);
            txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.black_register));
            isTogglePassCode.set(true);

        } else {
            rootSettingPassword.set(View.GONE);
            isTogglePassCode.set(false);
            txtChangePassCodeEnable.set(false);
            txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.gray_5c));
        }


        isFingerPrint.set(isFingerPrintCode);

        screenShot = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);
        isAllowScreenCapture.set(screenShot);


        long valuNumberPic = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        if (valuNumberPic == 0) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.Disable));
        } else if (valuNumberPic == 60) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_minutes));
        } else if (valuNumberPic == 60 * 5) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_minutes));
        } else if (valuNumberPic == 60 * 60) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_1_hours));
        } else if (valuNumberPic == 60 * 60 * 5) {
            autoLockText.set(G.fragmentActivity.getResources().getString(R.string.in_5_hours));
        }

        edtSetPasswordText.set("");

        if (kindPassword == PIN) {
            if (edtSetPasswordText.get().length() == 4) {
                //rippleOk.performClick();
                buttonOk();

            }
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkFingerPrint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(G.fragmentActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    deviceHasFingerPrint = false;
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    deviceHasFingerPrint = false;
                } else {
                    deviceHasFingerPrint = true;
                }
            }
        }
    }

    private void maxLengthEditText(int numberOfLength) {
        edtSetPasswordMaxLength.set(numberOfLength);
    }

    public void onDestroy() {
        realm.close();
    }

    public void buttonOk() {

        if (page == 0 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().length() >= 4) {
                password = edtSetPasswordText.get();
                edtSetPasswordText.set("");
                txtSetPassword.set(G.fragmentActivity.getResources().getString(R.string.re_enter_pass_code));
                page = 1;
            } else {
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.limit_passcode));
            }

        } else if (page == 1 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                txtChangePassCodeEnable.set(true);
                txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.black_register));
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.set(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);

                G.isPassCode = true;
                ActivityMain.isLock = false;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realmUserInfo != null) {
                            realmUserInfo.setPassCode(true);
                            realmUserInfo.setPassCode(edtSetPasswordText.get());
                            realmUserInfo.setKindPassCode(kindPassword);
                        }
                    }
                });
                edtSetPasswordText.set("");
            } else {
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match));
            }

        } else if (page == 3 && edtSetPasswordText.get().length() > 0) {

            if (edtSetPasswordText.get().equals(password)) {
                vgTogglePassCodeVisibility.set(View.VISIBLE);
                rootEnterPassword.set(View.GONE);
                rootSettingPassword.set(View.VISIBLE);
                if (deviceHasFingerPrint) {
                    vgToggleFingerPrintVisibility.set(View.VISIBLE);
                } else {
                    vgToggleFingerPrintVisibility.set(View.GONE);
                }
                rippleOkVisibility.set(View.GONE);
                titlePassCode.set(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                titlePassCodeVisibility.set(View.VISIBLE);
                layoutModePassCode.set(View.GONE);
                txtChangePassCodeEnable.set(true);
                txtChangePassCodeColor.set(G.context.getResources().getColor(R.color.black_register));
            } else {
                AppUtils.error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
                edtSetPasswordText.set("");
            }

        } else {
            AppUtils.error(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
            edtSetPasswordText.set("");
        }
    }
}
