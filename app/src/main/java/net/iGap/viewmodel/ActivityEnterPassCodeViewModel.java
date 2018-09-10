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

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.databinding.ObservableField;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.ActivityEnterPassCodeBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLogout;
import net.iGap.interfaces.FingerPrint;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.module.AppUtils;
import net.iGap.module.FingerprintHandler;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserSessionLogout;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.realm.Realm;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class ActivityEnterPassCodeViewModel {

    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private final int PIN = 0;
    private final int PASSWORD = 1;
    public ObservableField<String> edtSetPassword = new ObservableField<>("");
    public ObservableField<Integer> rippleOkVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> vsRootIsPassCode = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> vsRootIsEditText = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> vsRootIsPattern = new ObservableField<>(View.GONE);
    public ObservableField<Integer> edtSetPasswordInput = new ObservableField<>(TYPE_TEXT_VARIATION_PASSWORD);
    public ObservableField<Integer> onTextChangedMaxLine = new ObservableField<>(4);
    private Realm realm;
    private String password;
    private boolean isFingerPrint;
    private KeyStore keyStore;
    private Cipher cipher;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private int kindPassCode;
    private MaterialDialog dialog;
    private RealmUserInfo realmUserInfo;
    private MaterialDialog dialogForgot;
    private FingerprintHandler helper;
    private Context context;
    private TextView iconFingerPrint;
    private TextView textFingerPrint;
    private boolean isPattern;
    private boolean isPassCode;
    private ActivityEnterPassCodeBinding binding;
    private String passCodePattern = null;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {

            passCodePattern = PatternLockUtils.patternToString(binding.patternLockView, pattern);
            rippleOk(binding.getRoot());
            binding.patternLockView.clearPattern();

        }

        @Override
        public void onCleared() {
        }
    };

    public ActivityEnterPassCodeViewModel(Context context, ActivityEnterPassCodeBinding binding) {
        this.binding = binding;
        this.context = context;
        getInfo();
    }

    public static void closeKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    public void afterTextChanged(Editable s) {
        if (kindPassCode == PIN) {
            if (s.length() == 4) {
                rippleOk(binding.getRoot());
            }
        }
    }

    public void rippleOk(View v) {

        String enterPassword = null;

        if (edtSetPassword.get().length() > 0) {
            enterPassword = edtSetPassword.get();
        } else {
            enterPassword = passCodePattern;
        }

        if (enterPassword != null && enterPassword.length() > 0) {

            if (enterPassword.equals(password)) {
                ActivityMain.isLock = false;
                G.currentActivity.finish();

                //G.isPassCode = false;
                closeKeyboard(v);
            } else {
                closeKeyboard(v);
                AppUtils.error(G.context.getResources().getString(R.string.invalid_password));
                edtSetPassword.set("");
            }
        } else {
            closeKeyboard(v);
            AppUtils.error(G.context.getResources().getString(R.string.enter_a_password));
            edtSetPassword.set("");
        }

    }

    public void forgotPassword(final View v) {

        dialogForgot = new MaterialDialog.Builder(G.currentActivity).title(R.string.forgot_pin_title).content(R.string.forgot_pin_desc).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                G.isPassCode = false;

                if (ActivityMain.finishActivity != null) {
                    ActivityMain.finishActivity.finishActivity();
                }
                G.currentActivity.finish();
                closeKeyboard(v);
                logout(v);

            }
        }).negativeText(R.string.cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).build();

        dialogForgot.show();

    }

    private void getInfo() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) G.context.getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) G.context.getSystemService(KEYGUARD_SERVICE);
        }
        realm = Realm.getDefaultInstance();

        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (realmUserInfo != null) {
            password = realmUserInfo.getPassCode();
            isPassCode = realmUserInfo.isPassCode();
            isPattern = realmUserInfo.isPattern();
            isFingerPrint = realmUserInfo.isFingerPrint();
            kindPassCode = realmUserInfo.getKindPassCode();
        }
        binding.patternLockView.addPatternLockListener(mPatternLockViewListener);

        if (isPassCode) {
            if (isPattern){
                vsRootIsPassCode.set(View.GONE);
                vsRootIsPattern.set(View.VISIBLE);
            }else {
                vsRootIsPassCode.set(View.VISIBLE);
                vsRootIsPattern.set(View.GONE);
                if (kindPassCode == PIN) {
                    edtSetPasswordInput.set((InputType.TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD));
                    maxLengthEditText(4);
                    rippleOkVisibility.set(View.VISIBLE);
                } else {
                    edtSetPasswordInput.set(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
                    maxLengthEditText(20);
                    rippleOkVisibility.set(View.VISIBLE);
                }

                if (dialogForgot != null && dialogForgot.isShowing() && !(G.currentActivity).isFinishing()) {
                    dialogForgot.dismiss();
                }
            }
        }

        if (isFingerPrint) {

            dialog = new MaterialDialog.Builder(context).title(G.context.getString(R.string.FingerPrint)).customView(R.layout.dialog_finger_print, true).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        helper.stopListening();
                    }

                }
            }).negativeText(G.context.getResources().getString(R.string.B_cancel)).build();

            View viewDialog = dialog.getView();

            iconFingerPrint = (TextView) viewDialog.findViewById(R.id.iconDialogTitle);
            textFingerPrint = (TextView) viewDialog.findViewById(R.id.txtDialogTitle);

            if (!(G.currentActivity).isFinishing()) {
                dialog.show();
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = null;
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(context);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }

            G.fingerPrint = new FingerPrint() {
                @Override
                public void success() {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (dialog != null && dialog.isShowing() && !(G.currentActivity).isFinishing()) {
                                dialog.dismiss();
                            }

                            ActivityMain.isLock = false;
                            G.currentActivity.finish();
                            closeKeyboard(binding.getRoot());
                        }
                    });
                }

                @Override
                public void error() {
                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            if (iconFingerPrint != null && textFingerPrint != null) {
                                iconFingerPrint.setTextColor(G.context.getResources().getColor(R.color.red));
                                textFingerPrint.setTextColor(G.context.getResources().getColor(R.color.red));
                                textFingerPrint.setText(G.context.getResources().getString(R.string.Fingerprint_not_recognized));
                            }
                        }
                    }
                }
            };
        }
    }

    private void logout(final View v) {
        G.onUserSessionLogout = new OnUserSessionLogout() {
            @Override
            public void onUserSessionLogout() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onError() {

            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (v != null) {
                            HelperError.showSnackMessage(G.context.getResources().getString(R.string.error), false);
                        }
                    }
                });
            }
        };

        new RequestUserSessionLogout().userSessionLogout();
        HelperLogout.logout();
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC).setUserAuthenticationRequired(true).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void maxLengthEditText(int numberOfLenth) {
        onTextChangedMaxLine.set(numberOfLenth);
    }

    public void onStart() {
        ActivityMain.isActivityEnterPassCode = true;
    }

    public void onDestroy() {
        if (isFingerPrint) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (helper != null) helper.stopListening();
            }
        }
        realm.close();
        ActivityMain.isActivityEnterPassCode = false;
        closeKeyboard(binding.getRoot());
    }

    public void onResume() {
        if (isFingerPrint) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = null;
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    helper = new FingerprintHandler(context);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }
}
