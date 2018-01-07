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

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.protobuf.ByteString;
import io.realm.Realm;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.AdapterDialog;
import net.iGap.fragments.FragmentRegister;
import net.iGap.fragments.FragmentRegistrationNickname;
import net.iGap.fragments.FragmentSecurityRecovery;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnPushLoginToken;
import net.iGap.interfaces.OnPushTwoStepVerification;
import net.iGap.interfaces.OnQrCodeNewDevice;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.interfaces.OnSecurityCheckPassword;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.module.AndroidUtils;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.SoftKeyboard;
import net.iGap.module.enums.Security;
import net.iGap.module.structs.StructCountry;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoUserRegister;
import net.iGap.proto.ProtoUserVerify;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoCountry;
import net.iGap.request.RequestQrCodeNewDevice;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserTwoStepVerificationGetPasswordDetail;
import net.iGap.request.RequestUserTwoStepVerificationVerifyPassword;
import net.iGap.request.RequestWrapper;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static net.iGap.G.context;
import static net.iGap.R.color.black_register;

public class FragmentRegisterViewModel implements OnSecurityCheckPassword, OnRecoverySecurityPassword {

    private static final String KEY_SAVE_CODENUMBER = "SAVE_CODENUMBER";
    private static final String KEY_SAVE_PHONENUMBER_MASK = "SAVE_PHONENUMBER_MASK";
    private static final String KEY_SAVE_PHONENUMBER_NUMBER = "SAVE_PHONENUMBER_NUMBER";
    private static final String KEY_SAVE_NAMECOUNTRY = "SAVE_NAMECOUNTRY";
    private static final String KEY_SAVE_REGEX = "KEY_SAVE_REGEX";
    private static final String KEY_SAVE_AGREEMENT = "KEY_SAVE_REGISTER";
    public static String isoCode = "IR";
    public static TextView btnOk;
    public static Dialog dialogChooseCountry;
    private MaterialDialog dialogRegistration;
    public static int positionRadioButton = -1;
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList();

    private Uri image_uriQrCode;
    private String _resultQrCode;
    private String phoneNumber;
    //Array List for Store List of StructCountry Object
    public String regex;
    private String userName;
    private String authorHash;
    private String token;
    private String regexFetchCodeVerification;
    private long userId;
    private boolean newUser;
    private ArrayList<StructCountry> items = new ArrayList<>();
    private AdapterDialog adapterDialog;
    private CountDownTimer CountDownTimerQrCode;
    private CountDownTimer countDownTimer;
    private SearchView edtSearchView;
    private Dialog dialog;
    private int digitCount;
    private MaterialDialog dialogWait;
    private String verifyCode;
    private boolean isRecoveryByEmail = false;
    private String securityPasswordQuestionOne = "";
    private String securityPasswordQuestionTwo = "";
    private String securityPaternEmail = "";
    private boolean isConfirmedRecoveryEmail;
    private MaterialDialog dialogQrCode;
    public boolean isVerify = false;
    private FragmentActivity mActivity;

    private ImageView imgQrCodeNewDevice;
    private ProgressBar prgQrCodeNewDevice;
    private FragmentRegister fragmentRegister;
    private View view;
    private int sendRequestRegister = 0;

    public ObservableField<String> callbackTxtAgreement = new ObservableField<>(G.context.getResources().getString(R.string.rg_agreement_text_register));
    public ObservableField<String> callbackBtnChoseCountry = new ObservableField<>("Iran");
    public ObservableField<String> callbackEdtCodeNumber = new ObservableField<>("+98");
    public ObservableField<String> callBackEdtPhoneNumber = new ObservableField<>("");
    public ObservableField<String> edtPhoneNumberMask = new ObservableField<>("###-###-####");
    public ObservableField<String> CallBackTxtVerifyConnect = new ObservableField<>(G.context.getResources().getString(R.string.rg_verify_register1));
    public ObservableField<String> callBackEdtCheckPassword = new ObservableField<>("");
    public ObservableField<String> edtCheckPasswordHint = new ObservableField<>("");
    public ObservableField<String> callBackTxtVerifyTimer = new ObservableField<>("05:00");
    public ObservableField<String> callBackTxtVerifySms = new ObservableField<>(G.context.getResources().getString(R.string.rg_verify_register2));
    public ObservableField<String> callBackTxtVerifyKey = new ObservableField<>(G.context.getResources().getString(R.string.rg_verify_register3));
    public ObservableField<String> callBackTxtIconVerifyConnect = new ObservableField<String>(G.context.getResources().getString(R.string.md_check_symbol));
    public ObservableInt prgVerifyConnectVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt txtVerifyTimerVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt prgVerifySmsVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt prgVerifyServerVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt imgVerifySmsVisibility = new ObservableInt(View.GONE);
    public ObservableInt imgVerifyServerVisibility = new ObservableInt(View.GONE);
    public ObservableInt txtIconVerifyConnectVisibility = new ObservableInt(View.GONE);
    public ObservableInt layoutVerifyAgreement = new ObservableInt(View.GONE);
    public ObservableInt txtOkVisibility = new ObservableInt(View.GONE);
    public ObservableInt qrCodeVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt rootMainLayoutVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt prgVerifyKeyVisibility = new ObservableInt(View.INVISIBLE);
    public ObservableInt imgVerifyKeyVisibility = new ObservableInt(View.GONE);
    public ObservableInt prgWaitingVisibility = new ObservableInt(View.GONE);
    public ObservableInt rootCheckPasswordVisibility = new ObservableInt(View.GONE);
    public ObservableInt txtAgreementVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt txtIconVerifyConnectColor = new ObservableInt(G.context.getResources().getColor(R.color.grayNew));
    public ObservableInt txtVerifyConnectColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_dark_verify));
    public ObservableInt txtVerifyTimerColor = new ObservableInt(G.context.getResources().getColor(R.color.black));
    public ObservableInt edtCodeNumberColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_black_register));
    public ObservableInt imgVerifySmsColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt txtVerifySmsColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt btnChoseCountryColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_black_register));
    public ObservableInt edtPhoneNumberColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_black_register));
    public ObservableInt txtVerifyKeColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt txtVerifyServerColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_text_verify));
    public ObservableInt btnStartColor = new ObservableInt(G.context.getResources().getColor(R.color.rg_whit_background));
    public ObservableInt btnStartBackgroundColor = new ObservableInt(Color.parseColor(G.appBarColor));
    public ObservableInt txtVerifyConnectAppearance = new ObservableInt();
    public ObservableInt txtVerifyServerAppearance = new ObservableInt();
    public ObservableInt txtVerifyKeyAppearance = new ObservableInt();
    public ObservableInt txtVerifySmsAppearance = new ObservableInt();
    public ObservableBoolean edtCodeNumberEnable = new ObservableBoolean(false);
    public ObservableBoolean btnChoseCountryEnable = new ObservableBoolean(true);
    public ObservableBoolean edtPhoneNumberEnable = new ObservableBoolean(true);
    public ObservableBoolean btnStartEnable = new ObservableBoolean(true);



    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }





    public FragmentRegisterViewModel(FragmentRegister fragmentRegister, View root, FragmentActivity mActivity) {
        this.fragmentRegister = fragmentRegister;
        view = root;
        this.mActivity = mActivity;

        getInfo();
    }



    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (s.toString().equals("0")) {
            Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.Toast_First_0), Toast.LENGTH_SHORT).show();
            callBackEdtPhoneNumber.set("");
        }

    }


    public void onClickQrCode(View v) {

        dialogQrCode = new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Login_with_QrCode)).customView(R.layout.dialog_qrcode, true).positiveText(R.string.share_item_dialog).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (_resultQrCode == null) {
                    return;
                }
                File file = new File(_resultQrCode);
                if (file.exists()) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    try {
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    G.fragmentActivity.startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
                }
            }
        }).negativeText(R.string.save).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (_resultQrCode == null) {
                    return;
                }
                File file = new File(_resultQrCode);
                if (file.exists()) {
                    HelperSaveFile.savePicToGallery(_resultQrCode, true);
                }
            }
        }).neutralText(R.string.cancel).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).build();

        imgQrCodeNewDevice = (ImageView) dialogQrCode.findViewById(R.id.imgQrCodeNewDevice);
        prgQrCodeNewDevice = (ProgressBar) dialogQrCode.findViewById(R.id.prgWaitQrCode);
        prgQrCodeNewDevice.setVisibility(View.VISIBLE);
        if (!(G.fragmentActivity).isFinishing()) {
            dialogQrCode.show();
        }

        dialogQrCode.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (CountDownTimerQrCode != null) {
                    CountDownTimerQrCode.cancel();
                }
            }
        });

        new RequestQrCodeNewDevice().qrCodeNewDevice();

        G.onQrCodeNewDevice = new OnQrCodeNewDevice() {
            @Override
            public void getQrCode(ByteString codeImage, final int expireTime) {

                _resultQrCode = G.DIR_TEMP + "/" + "QrCode" + ".jpg";

                File f = new File(_resultQrCode);
                if (f.exists()) {
                    f.delete();
                }
                AndroidUtils.writeBytesToFile(_resultQrCode, codeImage.toByteArray());
                image_uriQrCode = Uri.parse("file://" + _resultQrCode);
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        checkExpireTime(expireTime);
                        if (prgQrCodeNewDevice != null) {
                            prgQrCodeNewDevice.setVisibility(View.GONE);
                        }

                        if (imgQrCodeNewDevice != null) {
                            G.imageLoader.clearMemoryCache();
                            G.imageLoader.displayImage(AndroidUtils.suitablePath(_resultQrCode), imgQrCodeNewDevice);
                        }
                    }
                });
            }
        };


    }

    public void onClickChoseCountry(View v) {

        dialogChooseCountry = new Dialog(G.fragmentActivity);
        dialogChooseCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChooseCountry.setContentView(R.layout.rg_dialog);

        int setWidth = (int) (G.context.getResources().getDisplayMetrics().widthPixels * 0.9);
        int setHeight = (int) (G.context.getResources().getDisplayMetrics().heightPixels * 0.9);
        dialogChooseCountry.getWindow().setLayout(setWidth, setHeight);
        //
        final TextView txtTitle = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_titleToolbar);
        edtSearchView = (SearchView) dialogChooseCountry.findViewById(R.id.rg_edtSearch_toolbar);

        txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtSearchView.setIconified(false);
                edtSearchView.setIconifiedByDefault(true);
                txtTitle.setVisibility(View.GONE);
            }
        });

        edtSearchView.setOnCloseListener(new SearchView.OnCloseListener() { // close SearchView and show title again
            @Override
            public boolean onClose() {

                txtTitle.setVisibility(View.VISIBLE);

                return false;
            }
        });

        final ViewGroup root = (ViewGroup) dialogChooseCountry.findViewById(android.R.id.content);
        InputMethodManager im = (InputMethodManager) G.context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (edtSearchView.getQuery().toString().length() > 0) {
                            edtSearchView.setIconified(false);
                            edtSearchView.clearFocus();
                            txtTitle.setVisibility(View.GONE);
                        } else {
                            edtSearchView.setIconified(true);
                            txtTitle.setVisibility(View.VISIBLE);
                        }
                        adapterDialog.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {

                G.handler.post(new Runnable() {

                    @Override
                    public void run() {

                        txtTitle.setVisibility(View.GONE);
                    }
                });
            }
        });

        final ListView listView = (ListView) dialogChooseCountry.findViewById(R.id.lstContent);
        adapterDialog = new AdapterDialog(G.fragmentActivity, items);
        listView.setAdapter(adapterDialog);

        final View border = (View) dialogChooseCountry.findViewById(R.id.rg_borderButton);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                if (i > 0) {
                    border.setVisibility(View.VISIBLE);
                } else {
                    border.setVisibility(View.GONE);
                }
            }
        });

        AdapterDialog.mSelectedVariation = positionRadioButton;

        adapterDialog.notifyDataSetChanged();

        edtSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                adapterDialog.getFilter().filter(s);
                return false;
            }
        });

        btnOk = (TextView) dialogChooseCountry.findViewById(R.id.rg_txt_okDialog);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                G.onInfoCountryResponse = new OnInfoCountryResponse() {
                    @Override
                    public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callbackEdtCodeNumber.set("+" + callingCode);
                                edtPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                                regex = regexR;
                                btnStartBackgroundColor.set(Color.parseColor(G.appBarColor));
                                btnStartEnable.set(true);
                            }
                        });
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {
                        //empty
                    }
                };

                new RequestInfoCountry().infoCountry(isoCode);

                callBackEdtPhoneNumber.set("");
                dialogChooseCountry.dismiss();
            }
        });

        if (!(G.fragmentActivity).isFinishing()) {
            dialogChooseCountry.show();
        }

    }

    public void onClicksStart(View v) {

        if ((G.fragmentActivity).isFinishing()) {
            return;
        }

        if (callBackEdtPhoneNumber.get().length() > 0 && (regex.equals("") || (!regex.equals("") && callBackEdtPhoneNumber.get().replace("-", "").matches(regex)))) {

            phoneNumber = callBackEdtPhoneNumber.get();

            dialogRegistration = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.rg_mdialog_text, true).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_edit)).onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                    //txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());
                    //txtAgreement_register.startAnimation(trans_x_out);

                    if (FragmentRegister.onStartAnimationRegister != null) FragmentRegister.onStartAnimationRegister.start();

                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            btnStartBackgroundColor.set(G.context.getResources().getColor(R.color.rg_background_verify));
                            btnStartColor.set(G.context.getResources().getColor(R.color.rg_border_editText));
                            btnChoseCountryEnable.set(false);
                            btnChoseCountryColor.set(G.context.getResources().getColor(R.color.rg_border_editText));
                            edtPhoneNumberEnable.set(false);
                            edtPhoneNumberColor.set(G.context.getResources().getColor(R.color.rg_border_editText));
                            edtCodeNumberEnable.set(false);
                            edtCodeNumberColor.set(G.context.getResources().getColor(R.color.rg_border_editText));
                            txtAgreementVisibility.set(View.GONE);
                            //layoutVerifyAgreement.set(View.VISIBLE);
                            //layout_verify.startAnimation(trans_x_in);
                            isVerify = true;
                            checkVerify();

                        }
                    }, 600);

                }
            }).build();

            View view = dialogRegistration.getCustomView();
            assert view != null;
            TextView phone = (TextView) view.findViewById(R.id.rg_dialog_txt_number);
            phone.setText(callbackEdtCodeNumber.get() + "" + callBackEdtPhoneNumber.get());

            try {
                dialogRegistration.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        } else {
            if (callBackEdtPhoneNumber.get().replace("-", "").matches(regex)) {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).content(R.string.Toast_Minimum_Characters).positiveText(R.string.B_ok).show();
            } else {
                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).content(R.string.Toast_Enter_Phone_Number).positiveText(R.string.B_ok).show();
            }
        }
    }

    public void onClickTxtForgotPassword(View v) {

        int item;
        if (isConfirmedRecoveryEmail) {
            item = R.array.securityRecoveryPassword;
        } else {
            item = R.array.securityRecoveryPasswordWithoutEmail;
        }

        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.set_recovery_dialog_title).items(item).itemsCallback(new MaterialDialog.ListCallback() {

            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                if (text.equals(G.fragmentActivity.getResources().getString(R.string.recovery_by_email_dialog))) {
                    isRecoveryByEmail = true;
                } else {
                    isRecoveryByEmail = false;
                }

                FragmentSecurityRecovery fragmentSecurityRecovery = new FragmentSecurityRecovery();
                Bundle bundle = new Bundle();
                bundle.putSerializable("PAGE", Security.REGISTER);
                bundle.putString("QUESTION_ONE", securityPasswordQuestionOne);
                bundle.putString("QUESTION_TWO", securityPasswordQuestionTwo);
                bundle.putString("PATERN_EMAIL", securityPaternEmail);
                bundle.putBoolean("IS_EMAIL", isRecoveryByEmail);
                bundle.putBoolean("IS_CONFIRM_EMAIL", isConfirmedRecoveryEmail);
                fragmentSecurityRecovery.setArguments(bundle);

                G.fragmentActivity.getSupportFragmentManager().beginTransaction().addToBackStack(null).
                    setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).
                    replace(R.id.ar_layout_root, fragmentSecurityRecovery).commit();


            }
        }).show();


    }

    public void onClickTxtOk(View v) {
        if (callBackEdtCheckPassword.get().length() > 0) {

            prgWaitingVisibility.set(View.VISIBLE);

            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            new RequestUserTwoStepVerificationVerifyPassword().verifyPassword(callBackEdtCheckPassword.get());
        } else {
            error(G.fragmentActivity.getResources().getString(R.string.please_enter_code));
        }
    }

    private void getInfo() {
        G.onSecurityCheckPassword = this;
        G.onRecoverySecurityPassword = this;

        G.onPushLoginToken = new OnPushLoginToken() {
            @Override
            public void pushLoginToken(final String tokenQrCode, String userNameR, long userIdR, String authorHashR) {

                token = tokenQrCode;
                G.displayName = userName = userNameR;
                G.userId = userId = userIdR;
                G.authorHash = authorHash = authorHashR;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogQrCode != null && dialogQrCode.isShowing()) dialogQrCode.dismiss();

                        userLogin(token);
                    }
                });

            }
        };

        G.onPushTwoStepVerification = new OnPushTwoStepVerification() {
            @Override
            public void pushTwoStepVerification(String userNameR, long userIdR, String authorHashR) {

                userName = userNameR;
                userId = userIdR;
                authorHash = authorHashR;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogQrCode != null && dialogQrCode.isShowing()) dialogQrCode.dismiss();
                    }
                });
                checkPassword("", true);
            }
        };

        CountryReader countryReade = new CountryReader();
        StringBuilder fileListBuilder = countryReade.readFromAssetsTextFile("country.txt", G.context);

        String list = fileListBuilder.toString();
        // Split line by line Into array
        String listArray[] = list.split("\\r?\\n");
        final String countryNameList[] = new String[listArray.length];
        //Convert array
        for (int i = 0; listArray.length > i; i++) {
            StructCountry structCountry = new StructCountry();

            String listItem[] = listArray[i].split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryArrayList.add(structCountry);
        }

        Collections.sort(structCountryArrayList, new CountryListComparator());

        for (int i = 0; i < structCountryArrayList.size(); i++) {
            if (i < countryNameList.length) {
                countryNameList[i] = structCountryArrayList.get(i).getName();
                StructCountry item = new StructCountry();
                item.setId(i);
                item.setName(structCountryArrayList.get(i).getName());
                item.setCountryCode(structCountryArrayList.get(i).getCountryCode());
                item.setPhonePattern(structCountryArrayList.get(i).getPhonePattern());
                item.setAbbreviation(structCountryArrayList.get(i).getAbbreviation());
                items.add(item);
            }
        }
    }

    public void saveInstance(Bundle savedInstanceState, Bundle argument) {
        if (savedInstanceState != null) { // TODO: 12/16/2017
            // Restore value of members from saved state
            callbackEdtCodeNumber.set(savedInstanceState.getString(KEY_SAVE_CODENUMBER));
            edtPhoneNumberMask.set(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_MASK));
            callBackEdtPhoneNumber.set(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_NUMBER));
            callbackBtnChoseCountry.set(savedInstanceState.getString(KEY_SAVE_NAMECOUNTRY));
            callbackTxtAgreement.set(savedInstanceState.getString(KEY_SAVE_AGREEMENT));
            regex = (savedInstanceState.getString(KEY_SAVE_REGEX));
        } else {
            if (argument != null) {
                isoCode = argument.getString("ISO_CODE");
                callbackEdtCodeNumber.set("+" + argument.getInt("CALLING_CODE"));
                callbackBtnChoseCountry.set(argument.getString("COUNTRY_NAME"));
                String pattern = argument.getString("PATTERN");
                if (!pattern.equals("")) {
                    edtPhoneNumberMask.set(pattern.replace("X", "#").replace(" ", "-"));
                }
                regex = argument.getString("REGEX");
                String body = argument.getString("TERMS_BODY");
                if (body != null) {
                    callbackTxtAgreement.set(Html.fromHtml(body).toString());
                }
            }
        }
    }



    //======= process verify : check internet and sms
    private void checkVerify() {

        prgVerifyConnectVisibility.set(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (G.socketConnection) { //connection ok
                    //                        if (checkInternet()) { //connection ok
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            userRegister();
                            btnStartEnable.set(false);
                            long time = 0;
                            if (BuildConfig.DEBUG) {
                                time = 2 * DateUtils.SECOND_IN_MILLIS;
                            } else if (FragmentRegister.smsPermission) {
                                time = Config.COUNTER_TIMER;
                            } else {
                                time = 5 * DateUtils.SECOND_IN_MILLIS;
                            }

                            countDownTimer = new CountDownTimer(time, Config.COUNTER_TIMER_DELAY) { // wait for verify sms
                                public void onTick(long millisUntilFinished) {

                                    int seconds = (int) ((millisUntilFinished) / 1000);
                                    int minutes = seconds / 60;
                                    seconds = seconds % 60;

                                    txtVerifyTimerVisibility.set(View.VISIBLE);
                                    callBackTxtVerifyTimer.set("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

                                }

                                public void onFinish() {

                                    if (callBackTxtVerifyTimer != null) {
                                        callBackTxtVerifyTimer.set("00:00");
                                        txtVerifyTimerVisibility.set(View.INVISIBLE);
                                    }
                                    errorVerifySms(FragmentRegister.Reason.TIME_OUT); // open rg_dialog for enter sms code
                                }
                            };
                        }
                    });
                } else { // connection error
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            edtPhoneNumberEnable.set(true);
                            prgVerifyConnectVisibility.set(View.GONE);
                            callBackTxtIconVerifyConnect.set(G.context.getResources().getString(R.string.md_igap_alert));
                            txtIconVerifyConnectColor.set(G.context.getResources().getColor(R.color.rg_error_red));
                            txtIconVerifyConnectVisibility.set(View.VISIBLE);
                            txtVerifyTimerColor.set(G.context.getResources().getColor(R.color.rg_error_red));
                            callBackTxtVerifyTimer.set(G.context.getResources().getString(R.string.please_check_your_connenction));

                        }
                    });
                }
            }
        });
        thread.start();
    }


    // error verify sms and open rg_dialog for enter sms code
    private void errorVerifySms(FragmentRegister.Reason reason) { //when don't receive sms and open rg_dialog for enter code

        prgVerifySmsVisibility.set(View.GONE);
        //imgVerifySmsColor.set(R.mipmap.alert);
        imgVerifySmsVisibility.set(View.VISIBLE);
        imgVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_error_red));
        callBackTxtVerifySms.set(G.context.getResources().getString(R.string.errore_verification_sms));
        txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_error_red));

        dialog = new Dialog(G.fragmentActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rg_dialog_verify_code);
        dialog.setCanceledOnTouchOutside(false);

        final EditText edtEnterCodeVerify = (EditText) dialog.findViewById(R.id.rg_edt_dialog_verifyCode); //EditText For Enter sms cod

        TextView txtShowReason = (TextView) dialog.findViewById(R.id.txt_show_reason);

        if (reason == FragmentRegister.Reason.SOCKET) {
            txtShowReason.setText(G.fragmentActivity.getResources().getString(R.string.verify_socket_message));
        } else if (reason == FragmentRegister.Reason.TIME_OUT) {
            txtShowReason.setText(G.fragmentActivity.getResources().getString(R.string.verify_time_out_message));
        } else if (reason == FragmentRegister.Reason.INVALID_CODE) {
            txtShowReason.setText(G.fragmentActivity.getResources().getString(R.string.verify_invalid_code_message));
        }

        TextView btnCancel = (TextView) dialog.findViewById(R.id.rg_btn_cancelVerifyCode);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    txtVerifyTimerVisibility.set(View.INVISIBLE);

                    if (!edtEnterCodeVerify.getText().toString().equals("")) {
                        verifyCode = edtEnterCodeVerify.getText().toString();
                        userVerify(userName, verifyCode);
                        dialog.dismiss();
                    } else {
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.Enter_Code).content(R.string.Toast_Enter_Code).positiveText(R.string.B_ok).show();
                    }
                } catch (WindowManager.BadTokenException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView btnOk = (TextView) dialog.findViewById(R.id.rg_btn_dialog_okVerifyCode);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
                dialog.dismiss();
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        try {
            if (!(G.fragmentActivity).isFinishing()) {
                dialog.show();
                if (dialog.isShowing()) {
                    countDownTimer.cancel();
                }
            }
        } catch (WindowManager.BadTokenException e) {
            e.printStackTrace();
        }
    }

    private void userRegister() {

        G.onUserRegistration = new OnUserRegistration() {

            @Override
            public void onRegister(final String userNameR, final long userIdR, final ProtoUserRegister.UserRegisterResponse.Method methodValue, final List<Long> smsNumbersR, String regex, int verifyCodeDigitCount, final String authorHashR) {
                digitCount = verifyCodeDigitCount;
                countDownTimer.start();
                regexFetchCodeVerification = regex;
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        txtVerifyTimerVisibility.set(View.VISIBLE);

                        userName = userNameR;
                        userId = userIdR;
                        authorHash = authorHashR;
                        G.smsNumbers = smsNumbersR;

                        if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {
                            errorVerifySms(FragmentRegister.Reason.SOCKET);
                            countDownTimer.cancel();
                        }

                        prgVerifyConnectVisibility.set(View.GONE);
                        txtIconVerifyConnectVisibility.set(View.VISIBLE);
                        imgVerifySmsVisibility.set(View.GONE);
                        //txtVerifyConnectAppearance.set(R.style.RedHUGEText);
                        txtVerifyConnectColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                        prgVerifySmsVisibility.set(View.VISIBLE);
                        //txtVerifySmsAppearance.set(R.style.RedHUGEText);

                    }
                });
            }

            @Override
            public void onRegisterError(final int majorCode, int minorCode, int getWait) {
                final long time = getWait;
                if (majorCode == 100 && minorCode == 1) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid countryCode
                        }
                    });
                } else if (majorCode == 100 && minorCode == 2) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid phoneNumber
                        }
                    });
                } else if (majorCode == 101) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid phoneNumber
                        }
                    });
                } else if (majorCode == 135) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 136) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES, time, majorCode);
                        }
                    });
                } else if (majorCode == 137) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES_SEND, time, majorCode);
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) { // timeout
                    if (sendRequestRegister <= 2) {
                        requestRegister();
                        sendRequestRegister++;
                    }

                }
            }
        };

        requestRegister();
    }

    private void dialogWaitTime(int title, long time, int majorCode) {

        if (!G.fragmentActivity.hasWindowFocus()) {
            return;
        }


        boolean wrapInScrollView = true;
        dialogWait = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).cancelable(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                btnStartBackgroundColor.set(Color.parseColor(G.appBarColor));
                btnStartColor.set(G.context.getResources().getColor(R.color.white));
                btnStartEnable.set(true);
                btnChoseCountryEnable.set(true);
                btnChoseCountryColor.set(G.context.getResources().getColor(black_register));
                edtPhoneNumberEnable.set(true);
                edtPhoneNumberColor.set(G.context.getResources().getColor(black_register));
                edtCodeNumberColor.set(G.context.getResources().getColor(black_register));
                txtAgreementVisibility.set(View.VISIBLE);
                layoutVerifyAgreement.set(View.GONE);
                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
                dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            @Override
            public void onFinish() {
                dialogWait.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    private void requestRegister() {

        if (G.socketConnection) {
            phoneNumber = phoneNumber.replace("-", "");
            ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
            builder.setCountryCode(isoCode);
            builder.setPhoneNumber(Long.parseLong(phoneNumber));
            builder.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
            RequestWrapper requestWrapper = new RequestWrapper(100, builder);

            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestRegister();
                }
            }, 1000);
        }
    }

    /**
     * if the connection is established do verify otherwise start registration(step one) again
     */
    private void userVerify(final String userName, final String verificationCode) {
        if (G.socketConnection) {

            prgVerifyKeyVisibility.set(View.VISIBLE);
            //txtVerifyKeyAppearance.set(R.style.RedHUGEText);


            userVerifyResponse(verificationCode);
            ProtoUserVerify.UserVerify.Builder userVerify = ProtoUserVerify.UserVerify.newBuilder();
            userVerify.setCode(Integer.parseInt(verificationCode));
            userVerify.setUsername(userName);

            RequestWrapper requestWrapper = new RequestWrapper(101, userVerify);
            try {
                RequestQueue.sendRequest(requestWrapper);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            /**
             * return view for step one and two because now start registration again from step one
             */

            // return step one
            prgVerifyConnectVisibility.set(View.VISIBLE);
            txtIconVerifyConnectVisibility.set(View.GONE);
            txtVerifyConnectAppearance.set(Typeface.NORMAL);
            // clear step two
            prgVerifySmsVisibility.set(View.GONE);
            imgVerifySmsVisibility.set(View.INVISIBLE);
            txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

            //rg_txt_verify_sms.setTextAppearance(G.context, Typeface.NORMAL);

            requestRegister();
        }
    }

    private void userVerifyResponse(final String verificationCode) {
        G.onUserVerification = new OnUserVerification() {
            @Override
            public void onUserVerify(final String tokenR, final boolean newUserR) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBackTxtVerifySms.set(G.fragmentActivity.getResources().getString(R.string.rg_verify_register2));
                        prgVerifySmsVisibility.set(View.GONE);
                        imgVerifySmsVisibility.set(View.VISIBLE);
                        //imgVerifySmsColor.set(R.mipmap.check);
                        imgVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                        txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

                        newUser = newUserR;
                        token = tokenR;

                        prgVerifyKeyVisibility.set(View.GONE);
                        imgVerifyKeyVisibility.set(View.VISIBLE);
                        txtVerifyKeColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

                        userLogin(token);
                    }
                });
            }

            @Override
            public void onUserVerifyError(final int majorCode, int minorCode, final int time) {

                if (majorCode == 184 && minorCode == 1) {

                    checkPassword(verificationCode, false);

                } else if (majorCode == 102 && minorCode == 1) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            errorVerifySms(FragmentRegister.Reason.INVALID_CODE);
                        }
                    });
                } else if (majorCode == 102 && minorCode == 2) {
                    //empty
                } else if (majorCode == 103) {
                    //empty
                } else if (majorCode == 104) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // There is no registered user with given username
                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_GIVEN_USERNAME).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 105) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // User is blocked , You cannot verify the user

                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_BLOCKED_USER).content(R.string.Toast_Number_Block).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 106) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is invalid
                            errorVerifySms(FragmentRegister.Reason.INVALID_CODE);
                        }
                    });
                } else if (majorCode == 107) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is expired
                            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.USER_VERIFY_EXPIRED).content(R.string.Toast_Number_Block).onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            }).positiveText(R.string.B_ok).show();
                        }
                    });
                } else if (majorCode == 108) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Verification code is locked for a while due to too many tries

                            dialogWaitTime(R.string.USER_VERIFY_MANY_TRIES, time, majorCode);
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) {
                    userVerify(userName, verifyCode);
                }
            }
        };
    }

    private void checkPassword(final String verificationCode, final boolean isQrCode) {
        new RequestUserTwoStepVerificationGetPasswordDetail().getPasswordDetail();
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (!isQrCode) {

                    callBackTxtVerifySms.set((G.fragmentActivity.getResources().getString(R.string.rg_verify_register2)));
                    prgVerifySmsVisibility.set(View.GONE);
                    imgVerifySmsVisibility.set(View.VISIBLE);
                    //imgVerifySmsColor.set(R.mipmap.check);
                    imgVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                    txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));


                    //newUser = newUserR;
                    //token = tokenR;

                    prgVerifyKeyVisibility.set(View.GONE);
                    imgVerifyKeyVisibility.set(View.VISIBLE);
                    txtVerifyKeColor.set(G.context.getResources().getColor(R.color.rg_text_verify));
                    //userLogin(token);

                }
                rootMainLayoutVisibility.set(View.GONE);
                rootCheckPasswordVisibility.set(View.VISIBLE);
                txtOkVisibility.set(View.VISIBLE);
                qrCodeVisibility.set(View.GONE);

            }
        });
    }

    private void userLogin(final String token) {
        prgVerifyServerVisibility.set(View.VISIBLE);
        //txtVerifyServerAppearance.set(R.style.RedHUGEText);

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmUserInfo.putOrUpdate(realm, userId, userName, phoneNumber, token, authorHash);
                            }
                        });

                        prgVerifyServerVisibility.set(View.GONE);
                        imgVerifyServerVisibility.set(View.VISIBLE);

                        txtVerifyServerColor.set(G.context.getResources().getColor(R.color.rg_text_verify));



                        if (newUser) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
                                    fragment.setArguments(bundle);
                                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(fragmentRegister).commitAllowingStateLoss();
                                }
                            });
                        } else {
                            // get user info for set nick name and after from that go to ActivityMain
                            getUserInfo();
                            requestUserInfo();
                        }
                        realm.close();
                    }
                });
            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {
                if (majorCode == 111 && minorCode == 4) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {

                            HelperLogout.logout();
                        }
                    });
                } else if (majorCode == 111) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            requestLogin();
                        }
                    });
                } else if (majorCode == 5 && minorCode == 1) {
                    requestLogin();
                }
            }
        };

        requestLogin();
    }

    private void getUserInfo() {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        G.displayName = user.getDisplayName();

                        RealmUserInfo.putOrUpdate(realm, user);

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                G.onUserInfoResponse = null;
                                G.currentActivity.finish();
                                Intent intent = new Intent(context, ActivityMain.class);
                                intent.putExtra(FragmentRegistrationNickname.ARG_USER_ID, userId);
                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                G.context.startActivity(intent);
                            }
                        });
                    }
                });
                realm.close();
            }

            @Override
            public void onUserInfoTimeOut() {
                requestUserInfo();
            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
    }

    private void requestUserInfo() {
        if (G.socketConnection) {
            if (userId == 0) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    //finish();
                } else {
                    userId = realmUserInfo.getUserId();
                }
                realm.close();
            }
            new RequestUserInfo().userInfo(userId);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestUserInfo();
                }
            }, 1000);
        }
    }

    private void requestLogin() {
        if (G.socketConnection) {
            if (token == null) {
                Realm realm = Realm.getDefaultInstance();
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo == null) {
                    //finish();
                } else {
                    token = realmUserInfo.getToken();
                }
                realm.close();
            }
            new RequestUserLogin().userLogin(token);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLogin();
                }
            }, 1000);
        }
    }

    public void receiveVerifySms(String message) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        String verificationCode = HelperString.regexExtractValue(message, regexFetchCodeVerification);
        verifyCode = verificationCode;
        countDownTimer.cancel(); //cancel method CountDown and continue process verify

        prgVerifySmsVisibility.set(View.GONE);
        imgVerifySmsVisibility.set(View.VISIBLE);
        txtVerifySmsColor.set(G.context.getResources().getColor(R.color.rg_text_verify));

        userVerify(userName, verificationCode);
    }


    private void closeKeyboard(final View v) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

    }

    private void dialogWaitTimeVerifyPassword(long time) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialogWait = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.error_check_password).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(true).canceledOnTouchOutside(true).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                dialog.dismiss();
            }
        }).show();

        View v = dialogWait.getCustomView();

        final TextView remindTime = (TextView) v.findViewById(R.id.remindTime);
        CountDownTimer countWaitTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) ((millisUntilFinished) / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                remindTime.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));
            }

            @Override
            public void onFinish() {
                remindTime.setText("00:00");
            }
        };
        countWaitTimer.start();
    }

    private void error(String error) {
        HelperError.showSnackMessage(error, true);
    }

    /**
     * ****************************** Callbacks ******************************
     */

    @Override
    public void getDetailPassword(final String questionOne, final String questionTwo, final String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                edtCheckPasswordHint.set(hint);
            }
        });

        securityPasswordQuestionOne = questionOne;
        securityPasswordQuestionTwo = questionTwo;
        isConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
        String securityPasswordHint = hint;
        boolean hasConfirmedRecoveryEmail1 = hasConfirmedRecoveryEmail;
        String unconfirmedEmailPattern1 = unconfirmedEmailPattern;
    }

    @Override
    public void verifyPassword(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                closeKeyboard(view);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorVerifyPassword(final int wait) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                dialogWaitTimeVerifyPassword(wait);
            }
        });
    }

    @Override
    public void errorInvalidPassword() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                closeKeyboard(view);
                prgWaitingVisibility.set(View.GONE);
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    @Override
    public void recoveryByEmail(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                prgWaitingVisibility.set(View.GONE);

                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                rootCheckPasswordVisibility.set(View.GONE);
                txtOkVisibility.set(View.GONE);
                rootMainLayoutVisibility.set(View.VISIBLE);
                userLogin(token);
            }
        });

    }

    @Override
    public void getEmailPatern(final String patern) {

    }

    @Override
    public void errorRecoveryByEmail() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);

                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                closeKeyboard(view);
            }
        });
    }

    @Override
    public void recoveryByQuestion(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);

                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                rootCheckPasswordVisibility.set(View.GONE);
                txtOkVisibility.set(View.GONE);
                rootMainLayoutVisibility.set(View.VISIBLE);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorRecoveryByQuestion() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {

                prgWaitingVisibility.set(View.GONE);

                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                closeKeyboard(view);
            }
        });
    }

    public void onStop() {

        try {
            if (dialogRegistration != null && dialogRegistration.isShowing()) {
                dialogRegistration.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkExpireTime(int expireTime) {

        int time = (expireTime - 100) * 1000;
        if (CountDownTimerQrCode != null) {
            CountDownTimerQrCode.cancel();
        }
        CountDownTimerQrCode = new CountDownTimer(time, Config.COUNTER_TIMER_DELAY) { // wait for verify sms
            public void onTick(long millisUntilFinished) {

                //int seconds = (int) ((millisUntilFinished) / 1000);
                //int minutes = seconds / 60;
                //seconds = seconds % 60;
            }

            public void onFinish() {
                new RequestQrCodeNewDevice().qrCodeNewDevice();
            }
        };

        CountDownTimerQrCode.start();

    }
}



