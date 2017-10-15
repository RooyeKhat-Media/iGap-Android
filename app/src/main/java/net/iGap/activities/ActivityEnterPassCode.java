package net.iGap.activities;
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
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperLogout;
import net.iGap.interfaces.FingerPrint;
import net.iGap.interfaces.OnUserSessionLogout;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.FingerprintHandler;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserSessionLogout;

import static android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;

public class ActivityEnterPassCode extends ActivityEnhanced {


    private Realm realm;
    private String password;
    private boolean isFingerPrint;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private int kindPassCode;
    private final int PIN = 0;
    private final int PASSWORD = 1;
    private MaterialDialog dialog;
    private TextView iconFingerPrint;
    private TextView textFingerPrint;
    private RealmUserInfo realmUserInfo;
    private MaterialDialog dialogForgot;
    private EditText edtPassword;
    private FingerprintHandler helper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enter_pass_code);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        }

        ViewGroup rootEnterPassword = (ViewGroup) findViewById(R.id.mainRootEnterPassword);
        final RippleView txtOk = (RippleView) findViewById(R.id.enterPassword_rippleOk);
        edtPassword = (EditText) findViewById(R.id.enterPassword_edtSetPassword);


        rootEnterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RippleView rippleView = (RippleView) findViewById(R.id.enterPassword_rippleOk);

        realm = Realm.getDefaultInstance();

        realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (realmUserInfo != null) {
            password = realmUserInfo.getPassCode();
            isFingerPrint = realmUserInfo.isFingerPrint();
            kindPassCode = realmUserInfo.getKindPassCode();
        }

        if (kindPassCode == PIN) {
            edtPassword.setInputType((InputType.TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD));
            maxLengthEditText(4);
            rippleView.setVisibility(View.GONE);
        } else {
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD);
            maxLengthEditText(20);
            rippleView.setVisibility(View.VISIBLE);
        }

        if (dialogForgot != null) {
            dialogForgot.dismiss();
        }


        if (isFingerPrint) {

            dialog = new MaterialDialog.Builder(this).title(getString(R.string.FingerPrint)).customView(R.layout.dialog_finger_print, true).onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        helper.stopListening();
                    }

                }
            }).negativeText(getResources().getString(R.string.B_cancel)).build();

            View viewDialog = dialog.getView();

            iconFingerPrint = (TextView) viewDialog.findViewById(R.id.iconDialogTitle);
            textFingerPrint = (TextView) viewDialog.findViewById(R.id.txtDialogTitle);

            dialog.show();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = null;
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }

            G.fingerPrint = new FingerPrint() {
                @Override
                public void success() {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (dialog != null) {
                                dialog.dismiss();
                            }

                            ActivityMain.isLock = false;
                            finish();

                        }
                    });
                }

                @Override
                public void error() {
                    if (dialog != null) {
                        if (dialog.isShowing()) {
                            if (iconFingerPrint != null && textFingerPrint != null) {
                                iconFingerPrint.setTextColor(getResources().getColor(R.color.red));
                                textFingerPrint.setTextColor(getResources().getColor(R.color.red));
                                textFingerPrint.setText(getResources().getString(R.string.Fingerprint_not_recognized));
                            }
                        }
                    }
                }
            };
        }

        final TextView txtForgotPassword = (TextView) findViewById(R.id.setPassword_forgotPassword);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dialogForgot = new MaterialDialog.Builder(ActivityEnterPassCode.this).title(R.string.forgot_pin_title).content(R.string.forgot_pin_desc).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        G.isPassCode = false;
                        logout(v);
                        finish();


                    }
                }).negativeText(R.string.cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    }
                }).build();

                dialogForgot.show();
            }
        });


        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (kindPassCode == PIN) {
                    if (s.length() == 4) {
                        txtOk.performClick();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterPassword = edtPassword.getText().toString();
                if (enterPassword.length() > 0) {

                    if (enterPassword.equals(password)) {
                        ActivityMain.isLock = false;
                        finish();

                        //G.isPassCode = false;
                        closeKeyboard(v);
                    } else {
                        closeKeyboard(v);
                        error(getString(R.string.invalid_password));
                        edtPassword.setText("");
                    }
                } else {
                    closeKeyboard(v);
                    error(getString(R.string.enter_a_password));
                    edtPassword.setText("");
                }
            }
        });
    }

    private void logout(final View v) {
        G.onUserSessionLogout = new OnUserSessionLogout() {
            @Override
            public void onUserSessionLogout() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HelperLogout.logout();
                    }
                });
            }

            @Override
            public void onError() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (v != null) {
                            final Snackbar snack = Snackbar.make(v.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (v != null) {
                            final Snackbar snack = Snackbar.make(v.findViewById(android.R.id.content), R.string.error, Snackbar.LENGTH_LONG);
                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    }
                });
            }
        };

        new RequestUserSessionLogout().userSessionLogout();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFingerPrint) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                generateKey();
                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = null;
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    helper = new FingerprintHandler(this);
                    helper.startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isFingerPrint) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (helper != null) helper.stopListening();
            }
        }
        realm.close();
        ActivityMain.isActivityEnterPassCode = false;
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

    private void closeKeyboard(View v) {

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }

    }

    private void error(String error) {

        try {
            Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
            vShort.vibrate(200);
            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG);
            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snack.dismiss();
                }
            });
            snack.show();
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ActivityMain.finishActivity != null) {
            ActivityMain.finishActivity.finishActivity();
        }
        finish();
        finishAffinity();

    }

    private void maxLengthEditText(int numberOfLenth) {
        edtPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(numberOfLenth)});
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityMain.isActivityEnterPassCode = true;
    }

}
