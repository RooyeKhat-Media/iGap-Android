package net.iGap.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLog;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmUserInfo;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.context;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPassCode extends BaseFragment {
    private Realm realm;
    private boolean isPassCode;
    private boolean isFingerPrintCode;
    private String passCode;
    private EditText edtSetPassword;
    private TextView txtSetPassword;
    private TextView txtChangePassCode;
    private TextView titlePassCode;
    private TextView txtAutoLock;
    private RippleView rippleOk;
    private String password;
    private int page = 0;
    private net.iGap.module.NumberPicker numberPickerMinutes;
    private boolean deviceHasFingerPrint;
    private ViewGroup vgFingerPrint;
    private final int PIN = 0;
    private final int PASSWORD = 1;
    private int kindPassword = 0;
    SharedPreferences sharedPreferences;
    private ViewGroup layoutModePassCode;
    private TextView txtModePassCode;


    public FragmentPassCode() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_pass_code, container, false));
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        checkFingerPrint();

        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        layoutModePassCode = (ViewGroup) view.findViewById(R.id.layout_mode_passCode);
        final ViewGroup rootSettingPassword = (ViewGroup) view.findViewById(R.id.rootSettingPassword);
        final ViewGroup rootEnterPassword = (ViewGroup) view.findViewById(R.id.rootEnterPassword);
        final ViewGroup vgTogglePassCode = (ViewGroup) view.findViewById(R.id.vgTogglePassCode);
        final ViewGroup changePassCode = (ViewGroup) view.findViewById(R.id.st_layout_ChangePassCode);
        final ViewGroup vgAutoLock = (ViewGroup) view.findViewById(R.id.st_layout_autoLock);
        vgFingerPrint = (ViewGroup) view.findViewById(R.id.vgToggleFingerPrint);

        txtModePassCode = (TextView) view.findViewById(R.id.mode_passCode);
        txtChangePassCode = (TextView) view.findViewById(R.id.st_txt_ChangePassCode);
        edtSetPassword = (EditText) view.findViewById(R.id.setPassword_edtSetPassword);
        txtSetPassword = (TextView) view.findViewById(R.id.setPassword_txtSetPassword);
        titlePassCode = (TextView) view.findViewById(R.id.titlePassCode);
        txtAutoLock = (TextView) view.findViewById(R.id.st_txt_autoLock);
        rippleOk = (RippleView) view.findViewById(R.id.setPassword_rippleOk);

        final ViewGroup rootPassCode = (ViewGroup) view.findViewById(R.id.rootPassCode);
        rootPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView txtBack = (TextView) view.findViewById(R.id.stns_txt_back);
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //G.fragmentActivity.getSupportFragmentManager().popBackStack();

                popBackStackFragment();

                closeKeyboard(v);

            }
        });

        layoutModePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).items(R.array.modePassCode).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        switch (which) {
                            case 0:
                                // Whatever you want to happen when the first item gets selected
                                edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                                maxLengthEditText(4);
                                kindPassword = PIN;
                                txtModePassCode.setText(G.context.getString(R.string.PIN));
                                break;
                            case 1:
                                // Whatever you want to happen when the second item gets selected
                                edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                                maxLengthEditText(20);
                                kindPassword = PASSWORD;
                                txtModePassCode.setText(G.context.getString(R.string.password));
                                break;
                        }

                    }
                }).build();

                DialogAnimation.animationUp(dialog);
                dialog.show();

            }
        });

        final TextView txtPassCode = (TextView) view.findViewById(R.id.st_txt_st_toggle_passCode);
        final ToggleButton togglePassCode = (ToggleButton) view.findViewById(R.id.st_toggle_passCode);

        final TextView txtAutoFingerPrint = (TextView) view.findViewById(R.id.st_txt_st_toggle_FingerPrint);
        final ToggleButton toggleEnableFingerPrint = (ToggleButton) view.findViewById(R.id.st_toggle_FingerPrint);

        final TextView txtScreenShot = (TextView) view.findViewById(R.id.st_txt_st_tAllowScreenCapture);
        final ToggleButton toggleScreenShot = (ToggleButton) view.findViewById(R.id.st_toggle_AllowScreenCapture);

        realm = Realm.getDefaultInstance();

        final RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();

        if (realmUserInfo != null) {
            isPassCode = realmUserInfo.isPassCode();
            isFingerPrintCode = realmUserInfo.isFingerPrint();
            password = realmUserInfo.getPassCode();
            kindPassword = realmUserInfo.getKindPassCode();
        }


        if (kindPassword == PIN) {
            edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
            maxLengthEditText(4);
        } else {
            edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            maxLengthEditText(20);
        }


        if (isPassCode) {

            page = 3;
            vgTogglePassCode.setVisibility(View.GONE);
            rootEnterPassword.setVisibility(View.VISIBLE);
            rootSettingPassword.setVisibility(View.GONE);
            rippleOk.setVisibility(View.VISIBLE);
            txtSetPassword.setText(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
            txtChangePassCode.setEnabled(true);
            txtChangePassCode.setTextColor(G.context.getResources().getColor(R.color.black_register));
            togglePassCode.setChecked(true);

        } else {
            rootSettingPassword.setVisibility(View.GONE);
            togglePassCode.setChecked(false);
            txtChangePassCode.setEnabled(false);
            txtChangePassCode.setTextColor(G.context.getResources().getColor(R.color.gray_5c));
        }

        togglePassCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set password;

                edtSetPassword.setText("");
                if (realmUserInfo != null) isPassCode = realmUserInfo.isPassCode();

                if (!isPassCode) {
                    vgTogglePassCode.setVisibility(View.GONE);
                    rootEnterPassword.setVisibility(View.VISIBLE);
                    rootSettingPassword.setVisibility(View.GONE);
                    rippleOk.setVisibility(View.VISIBLE);
                    //txtSetPassword.setText(G.fragmentActivity.getResources().getString(R.string.enter_a_password));
                    //titlePassCode.setText("PIN");
                    titlePassCode.setVisibility(View.GONE);
                    layoutModePassCode.setVisibility(View.VISIBLE);
                    if (kindPassword == PIN) {
                        edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                    } else {
                        edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    }
                } else {
                    togglePassCode.setChecked(false);
                    vgTogglePassCode.setVisibility(View.VISIBLE);
                    rootEnterPassword.setVisibility(View.GONE);
                    rootSettingPassword.setVisibility(View.GONE);
                    rippleOk.setVisibility(View.GONE);
                    layoutModePassCode.setVisibility(View.GONE);
                    txtChangePassCode.setEnabled(false);
                    txtChangePassCode.setTextColor(G.context.getResources().getColor(R.color.gray_5c));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
                    editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
                    editor.apply();

                    G.isPassCode = false;
                    edtSetPassword.setText("");
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
        });

        txtPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePassCode.setChecked(!togglePassCode.isChecked());
            }
        });

        txtChangePassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 0;
                edtSetPassword.setText("");
                vgTogglePassCode.setVisibility(View.GONE);
                rootEnterPassword.setVisibility(View.VISIBLE);
                rootSettingPassword.setVisibility(View.GONE);
                rippleOk.setVisibility(View.VISIBLE);
                //titlePassCode.setText("PIN");
                titlePassCode.setVisibility(View.GONE);
                layoutModePassCode.setVisibility(View.VISIBLE);
                txtSetPassword.setText(G.fragmentActivity.getResources().getString(R.string.enter_change_pass_code));
                if (kindPassword == PIN) {
                    edtSetPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    edtSetPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        if (isFingerPrintCode) {
            toggleEnableFingerPrint.setChecked(true);
        } else {
            toggleEnableFingerPrint.setChecked(false);
        }

        toggleEnableFingerPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // set password;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (realmUserInfo != null) {
                            if (isFingerPrintCode) {
                                realmUserInfo.setFingerPrint(false);
                            } else {
                                realmUserInfo.setFingerPrint(true);
                            }
                        }
                    }
                });
            }
        });

        txtAutoFingerPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleEnableFingerPrint.setChecked(!toggleEnableFingerPrint.isChecked());
            }
        });

        final boolean screenShot = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);


        if (screenShot) {
            toggleScreenShot.setChecked(true);
        } else {
            toggleScreenShot.setChecked(false);
        }

        toggleScreenShot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (screenShot) {
                            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);

                            try {
                                if (G.currentActivity != null) {
                                    G.currentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                                }
                            } catch (Exception e) {
                                HelperLog.setErrorLog(e.toString());
                            }


                        } else {
                            editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);

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
        });

        txtScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleScreenShot.setChecked(!toggleScreenShot.isChecked());
            }
        });

        edtSetPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (kindPassword == PIN) {
                    if (s.length() == 4) {
                        rippleOk.performClick();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (page == 0 && edtSetPassword.getText().length() > 0) {

                    if (edtSetPassword.getText().length() >= 4) {
                        password = edtSetPassword.getText().toString();
                        edtSetPassword.setText("");
                        txtSetPassword.setText(G.fragmentActivity.getResources().getString(R.string.re_enter_pass_code));
                        page = 1;
                    } else {
                        closeKeyboard(v);
                        error(G.fragmentActivity.getResources().getString(R.string.limit_passcode));
                    }

                } else if (page == 1 && edtSetPassword.getText().length() > 0) {

                    if (edtSetPassword.getText().toString().equals(password)) {
                        vgTogglePassCode.setVisibility(View.VISIBLE);
                        rootEnterPassword.setVisibility(View.GONE);
                        rootSettingPassword.setVisibility(View.VISIBLE);
                        txtChangePassCode.setEnabled(true);
                        txtChangePassCode.setTextColor(G.context.getResources().getColor(R.color.black_register));
                        if (deviceHasFingerPrint) {
                            vgFingerPrint.setVisibility(View.VISIBLE);
                        } else {
                            vgFingerPrint.setVisibility(View.GONE);
                        }
                        rippleOk.setVisibility(View.GONE);
                        titlePassCode.setText(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                        titlePassCode.setVisibility(View.VISIBLE);
                        layoutModePassCode.setVisibility(View.GONE);

                        G.isPassCode = true;
                        ActivityMain.isLock = false;
                        closeKeyboard(v);

                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                if (realmUserInfo != null) {
                                    realmUserInfo.setPassCode(true);
                                    realmUserInfo.setPassCode(edtSetPassword.getText().toString());
                                    realmUserInfo.setKindPassCode(kindPassword);
                                }
                            }
                        });
                        edtSetPassword.setText("");
                    } else {
                        closeKeyboard(v);
                        error(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match));
                    }

                } else if (page == 3 && edtSetPassword.getText().length() > 0) {

                    if (edtSetPassword.getText().toString().equals(password)) {
                        vgTogglePassCode.setVisibility(View.VISIBLE);
                        rootEnterPassword.setVisibility(View.GONE);
                        rootSettingPassword.setVisibility(View.VISIBLE);
                        if (deviceHasFingerPrint) {
                            vgFingerPrint.setVisibility(View.VISIBLE);
                        } else {
                            vgFingerPrint.setVisibility(View.GONE);
                        }
                        rippleOk.setVisibility(View.GONE);
                        titlePassCode.setText(G.fragmentActivity.getResources().getString(R.string.two_step_pass_code));
                        titlePassCode.setVisibility(View.VISIBLE);
                        layoutModePassCode.setVisibility(View.GONE);
                        txtChangePassCode.setEnabled(true);
                        txtChangePassCode.setTextColor(G.context.getResources().getColor(R.color.black_register));
                        closeKeyboard(v);
                    } else {
                        closeKeyboard(v);
                        error(G.fragmentActivity.getResources().getString(R.string.invalid_password));
                        edtSetPassword.setText("");
                    }

                } else {
                    closeKeyboard(v);
                    error(G.fragmentActivity.getResources().getString(R.string.enter_pass_code));
                    edtSetPassword.setText("");
                }
            }
        });
        long valuNumberPic = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        if (valuNumberPic == 0) {
            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.Disable));
        } else if (valuNumberPic == 60) {
            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_1_minutes));
        } else if (valuNumberPic == 60 * 5) {
            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_5_minutes));
        } else if (valuNumberPic == 60 * 60) {
            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_1_hours));
        } else if (valuNumberPic == 60 * 60 * 5) {
            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_5_hours));
        }



        vgAutoLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.Disable));
                        } else if (which == 1) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60);
                            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_1_minutes));
                        } else if (which == 2) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 5);
                            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_5_minutes));
                        } else if (which == 3) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60);
                            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_1_hours));
                        } else if (which == 4) {
                            editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 60 * 60 * 5);
                            txtAutoLock.setText(G.fragmentActivity.getResources().getString(R.string.in_5_hours));
                        }
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkFingerPrint() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(G.fragmentActivity, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
                deviceHasFingerPrint = false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                deviceHasFingerPrint = false;
            } else {
                deviceHasFingerPrint = true;
            }
            // Everything is ready for fingerprint authentication
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {
        if (isAdded()) {
            try {

                HelperError.showSnackMessage(error, true);

            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }


    private void maxLengthEditText(int numberOfLenth) {
        edtSetPassword.setFilters(new InputFilter[]{new InputFilter.LengthFilter(numberOfLenth)});
    }
}
