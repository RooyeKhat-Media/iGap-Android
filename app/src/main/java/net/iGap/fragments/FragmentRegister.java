/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.protobuf.ByteString;
import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.BuildConfig;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.adapter.AdapterDialog;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperLogout;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperSaveFile;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnInfoCountryResponse;
import net.iGap.interfaces.OnPushLoginToken;
import net.iGap.interfaces.OnPushTwoStepVerification;
import net.iGap.interfaces.OnQrCodeNewDevice;
import net.iGap.interfaces.OnRecoverySecurityPassword;
import net.iGap.interfaces.OnSecurityCheckPassword;
import net.iGap.interfaces.OnSmsReceive;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.interfaces.OnUserRegistration;
import net.iGap.interfaces.OnUserVerification;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CountryListComparator;
import net.iGap.module.CountryReader;
import net.iGap.module.IncomingSms;
import net.iGap.module.MaterialDesignTextView;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static net.iGap.G.context;
import static net.iGap.R.color.black_register;

public class FragmentRegister extends BaseFragment implements OnSecurityCheckPassword, OnRecoverySecurityPassword {

    private static final String KEY_SAVE_CODENUMBER = "SAVE_CODENUMBER";
    private static final String KEY_SAVE_PHONENUMBER_MASK = "SAVE_PHONENUMBER_MASK";
    private static final String KEY_SAVE_PHONENUMBER_NUMBER = "SAVE_PHONENUMBER_NUMBER";
    private static final String KEY_SAVE_NAMECOUNTRY = "SAVE_NAMECOUNTRY";
    private static final String KEY_SAVE_REGEX = "KEY_SAVE_REGEX";
    private static final String KEY_SAVE_AGREEMENT = "KEY_SAVE_REGISTER";
    public static Button btnChoseCountry;
    public static EditText edtCodeNumber;
    public static MaskedEditText edtPhoneNumber;
    public static String isoCode = "IR";
    public static TextView btnOk;
    public static Dialog dialogChooseCountry;
    private MaterialDialog dialogRegistration;
    public static int positionRadioButton = -1;
    ArrayList<StructCountry> structCountryArrayList = new ArrayList();
    private SoftKeyboard softKeyboard;
    private Button btnStart;
    private TextView txtAgreement_register, txtTitleToolbar, txtTitleRegister, txtDesc;
    MaterialDesignTextView txtQrCode;

    private ProgressBar rg_prg_verify_connect, rg_prg_verify_sms, rg_prg_verify_generate, rg_prg_verify_register;
    private TextView rg_txt_verify_connect, rg_txt_verify_sms, rg_txt_verify_generate, rg_txt_verify_register, txtTimer;
    private ImageView rg_img_verify_connect, rg_img_verify_sms, rg_img_verify_generate, rg_img_verify_register;
    private ImageView imgQrCodeNewDevice;
    private ProgressBar prgQrCodeNewDevice;
    private Uri image_uriQrCode;
    private String _resultQrCode;
    private ViewGroup layout_verify;
    private String phoneNumber;
    //Array List for Store List of StructCountry Object
    private String regex;
    private String userName;
    private String authorHash;
    private String token;
    private String regexFetchCodeVerification;
    private long userId;
    private boolean newUser;
    private ArrayList<StructCountry> items = new ArrayList<>();
    private AdapterDialog adapterDialog;
    private Dialog dialogVerifyLandScape;
    private IncomingSms smsReceiver;
    private CountDownTimer CountDownTimerQrCode;
    private CountDownTimer countDownTimer;
    private SearchView edtSearchView;
    private Dialog dialog;
    private int digitCount;
    private MaterialDialog dialogWait;
    private Typeface titleTypeface;
    private TextView txtTimerLand;
    private String verifyCode;
    private boolean isRecoveryByEmail = false;
    private EditText editCheckPassword;
    private TextView txtRecovery;
    private ProgressBar prgWaiting;
    private TextView txtOk;
    private ViewGroup vgMainLayout;
    private ViewGroup vgCheckPassword;
    private String securityPasswordQuestionOne = "";
    private String securityPasswordQuestionTwo = "";
    private String securityPaternEmail = "";
    private String securityPasswordHint = "";
    private boolean hasConfirmedRecoveryEmail;
    private String unconfirmedEmailPattern;
    private boolean isConfirmedRecoveryEmail;
    private MaterialDialog dialogQrCode;
    private boolean smsPermission = true;
    private FragmentActivity mActivity;
    private boolean isVerify = false;
    private ScrollView scrollView;
    private int headerLayoutHeight;
    private LinearLayout headerLayout;

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");


        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        headerLayout = (LinearLayout) view.findViewById(R.id.headerLayout);

        smsReceiver = new IncomingSms(new OnSmsReceive() {

            @Override
            public void onSmsReceive(final String phoneNumber, final String message) {
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        //rg_txt_verify_sms.setText(message);
                        receiveVerifySms(message);
                    }
                } catch (Exception e1) {
                    e1.getStackTrace();
                }
            }
        });

        try {
            HelperPermision.getSmsPermision(G.fragmentActivity, new OnGetPermission() {
                @Override
                public void Allow() {
                    smsPermission = true;
                    G.fragmentActivity.registerReceiver(smsReceiver, filter);
                }

                @Override
                public void deny() {
                    smsPermission = false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        G.onSecurityCheckPassword = this;
        G.onRecoverySecurityPassword = this;

        edtCodeNumber = (EditText) view.findViewById(R.id.rg_edt_CodeNumber);
        btnChoseCountry = (Button) view.findViewById(R.id.rg_btn_choseCountry);
        edtPhoneNumber = (MaskedEditText) view.findViewById(R.id.rg_edt_PhoneNumber);
        txtAgreement_register = (TextView) view.findViewById(R.id.txtAgreement_register);
        txtQrCode = (MaterialDesignTextView) view.findViewById(R.id.rg_qrCode);

        txtQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                            startActivity(Intent.createChooser(intent, G.fragmentActivity.getResources().getString(R.string.share_image_from_igap)));
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
                dialogQrCode.show();

                dialogQrCode.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (CountDownTimerQrCode != null) {
                            CountDownTimerQrCode.cancel();
                        }
                    }
                });

                new RequestQrCodeNewDevice().qrCodeNewDevice();

            }
        });


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

        G.onPushLoginToken = new OnPushLoginToken() {
            @Override
            public void pushLoginToken(final String tokenQrCode, String userNameR, long userIdR, String authorHashR) {

                token = tokenQrCode;
                userName = userNameR;
                userId = userIdR;
                authorHash = authorHashR;

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dialogQrCode != null && dialogQrCode.isShowing())
                            dialogQrCode.dismiss();

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
                        if (dialogQrCode != null && dialogQrCode.isShowing())
                            dialogQrCode.dismiss();
                    }
                });
                checkPassword("", true);
            }
        };

        view.findViewById(R.id.ar_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            edtCodeNumber.setText(savedInstanceState.getString(KEY_SAVE_CODENUMBER));
            edtPhoneNumber.setMask(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_MASK));
            edtPhoneNumber.setText(savedInstanceState.getString(KEY_SAVE_PHONENUMBER_NUMBER));
            btnChoseCountry.setText(savedInstanceState.getString(KEY_SAVE_NAMECOUNTRY));
            txtAgreement_register.setText(savedInstanceState.getString(KEY_SAVE_AGREEMENT));
            regex = (savedInstanceState.getString(KEY_SAVE_REGEX));
        } else {
            Bundle extras = getArguments();
            if (extras != null) {
                isoCode = extras.getString("ISO_CODE");
                edtCodeNumber.setText("+" + extras.getInt("CALLING_CODE"));
                btnChoseCountry.setText(extras.getString("COUNTRY_NAME"));
                String pattern = extras.getString("PATTERN");
                if (!pattern.equals("")) {
                    edtPhoneNumber.setMask(pattern.replace("X", "#").replace(" ", "-"));
                }
                regex = extras.getString("REGEX");
                String body = extras.getString("TERMS_BODY");
                if (body != null & txtAgreement_register != null) {
                    txtAgreement_register.setText(Html.fromHtml(body));
                }
            }
        }
        int getHeight = context.getResources().getDisplayMetrics().heightPixels;

        txtTitleRegister = (TextView) view.findViewById(R.id.rg_txt_title_register);
        txtDesc = (TextView) view.findViewById(R.id.rg_txt_text_descRegister);

        txtTitleToolbar = (TextView) view.findViewById(R.id.rg_txt_titleToolbar);

        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }

        txtTitleToolbar.setTypeface(titleTypeface);

        edtPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


                if (editable.toString().equals("0")) {
                    Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.Toast_First_0), Toast.LENGTH_SHORT).show();
                    edtPhoneNumber.setText("");
                }
            }
        });

        layout_verify = (ViewGroup) view.findViewById(R.id.rg_layout_verify_and_agreement);

        /**
         * list of country
         */

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

        btnChoseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                        edtCodeNumber.setText("+" + callingCode);
                                        edtPhoneNumber.setMask(pattern.replace("X", "#").replace(" ", "-"));
                                        regex = regexR;
                                        btnStart.setBackgroundColor(Color.parseColor(G.appBarColor));
                                        btnStart.setEnabled(true);
                                    }
                                });
                            }

                            @Override
                            public void onError(int majorCode, int minorCode) {
                                //empty
                            }
                        };

                        new RequestInfoCountry().infoCountry(isoCode);

                        edtPhoneNumber.setText("");
                        dialogChooseCountry.dismiss();
                    }
                });

                dialogChooseCountry.show();
            }
        });

        //=============================================================================================================== click button for start verify

        final Animation trans_x_in = AnimationUtils.loadAnimation(context, R.anim.rg_tansiton_y_in);
        final Animation trans_x_out = AnimationUtils.loadAnimation(context, R.anim.rg_tansiton_y_out);
        btnStart = (Button) view.findViewById(R.id.rg_btn_start); //check phone and internet connection
        btnStart.setBackgroundColor(Color.parseColor(G.appBarColor));
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isAdded() || mActivity.isFinishing()) {
                    return;
                }

                if (edtPhoneNumber.getText().length() > 0 && (regex.equals("") || (!regex.equals("") && edtPhoneNumber.getText().toString().replace("-", "").matches(regex)))) {

                    phoneNumber = edtPhoneNumber.getText().toString();

                    dialogRegistration = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.rg_mdialog_text, true).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_edit)).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());


                            txtAgreement_register.startAnimation(trans_x_out);
                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    btnStart.setBackgroundColor(G.context.getResources().getColor(R.color.rg_background_verify));
                                    btnStart.setTextColor(G.context.getResources().getColor(R.color.rg_border_editText));
                                    btnChoseCountry.setEnabled(false);
                                    btnChoseCountry.setTextColor(G.context.getResources().getColor(R.color.rg_border_editText));
                                    edtPhoneNumber.setEnabled(false);
                                    edtPhoneNumber.setTextColor(G.context.getResources().getColor(R.color.rg_border_editText));

                                    edtCodeNumber.setEnabled(false);
                                    edtCodeNumber.setTextColor(G.context.getResources().getColor(R.color.rg_border_editText));
                                    txtAgreement_register.setVisibility(View.GONE);
                                    layout_verify.setVisibility(View.VISIBLE);
                                    layout_verify.startAnimation(trans_x_in);

                                    isVerify = true;
                                    checkVerify();
                                }
                            }, 600);

                        }
                    }).build();

                    View view = dialogRegistration.getCustomView();
                    assert view != null;
                    TextView phone = (TextView) view.findViewById(R.id.rg_dialog_txt_number);
                    phone.setText(edtCodeNumber.getText().toString() + "" + edtPhoneNumber.getText().toString());

                    try {
                        dialogRegistration.show();
                    } catch (WindowManager.BadTokenException e) {
                        e.printStackTrace();
                    }

                } else {

                    if (edtPhoneNumber.getText().toString().replace("-", "").matches(regex)) {
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).content(R.string.Toast_Minimum_Characters).positiveText(R.string.B_ok).show();
                    } else {
                        new MaterialDialog.Builder(G.fragmentActivity).title(R.string.phone_number).content(R.string.Toast_Enter_Phone_Number).positiveText(R.string.B_ok).show();
                    }
                }
            }
        });
        // enable scroll text view
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        boolean beforeState = G.isLandscape;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            G.isLandscape = false;
        }

        if (G.isLandscape && isVerify) {

            ViewTreeObserver observer = headerLayout.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    // TODO Auto-generated method stub
                    headerLayoutHeight = headerLayout.getHeight();
                    scrollView.scrollTo(0, headerLayoutHeight);
                    headerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }

        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (FragmentActivity) context;
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (smsPermission) {
                G.fragmentActivity.unregisterReceiver(smsReceiver);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dialogRegistration != null) {
            dialogRegistration.dismiss();
        }
        super.onStop();
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

    //======= process verify : check internet and sms
    private void checkVerify() {

        setItem(); // invoke object

        if (rg_prg_verify_connect == null) {
            rg_prg_verify_connect = (ProgressBar) G.fragmentActivity.findViewById(R.id.rg_prg_verify_connect);
        }
        if (rg_prg_verify_connect != null) {
            rg_prg_verify_connect.setVisibility(View.VISIBLE);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (G.socketConnection) { //connection ok
                    //                        if (checkInternet()) { //connection ok
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            userRegister();
                            btnStart.setEnabled(false);
                            long time = 0;
                            if (BuildConfig.DEBUG) {
                                time = 2 * DateUtils.SECOND_IN_MILLIS;
                            } else if (smsPermission) {
                                time = Config.COUNTER_TIMER;
                            } else {
                                time = 5 * DateUtils.SECOND_IN_MILLIS;
                            }


                            txtTimer = (TextView) G.fragmentActivity.findViewById(R.id.rg_txt_verify_timer);


                            countDownTimer = new CountDownTimer(time, Config.COUNTER_TIMER_DELAY) { // wait for verify sms
                                public void onTick(long millisUntilFinished) {

                                    int seconds = (int) ((millisUntilFinished) / 1000);
                                    int minutes = seconds / 60;
                                    seconds = seconds % 60;

                                    if (txtTimer != null) {
                                        txtTimer.setVisibility(View.VISIBLE);
                                        txtTimer.setText("" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

                                    }
                                }

                                public void onFinish() {

                                    if (txtTimer != null) {
                                        txtTimer.setText("00:00");
                                        txtTimer.setVisibility(View.INVISIBLE);
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
                            edtPhoneNumber.setEnabled(true);
                            rg_prg_verify_connect.setVisibility(View.GONE);
                            rg_img_verify_connect.setImageResource(R.mipmap.alert);
                            rg_img_verify_connect.setColorFilter(G.context.getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
                            rg_img_verify_connect.setVisibility(View.VISIBLE);
                            rg_txt_verify_connect.setTextColor(G.context.getResources().getColor(R.color.rg_error_red));
                            rg_txt_verify_connect.setText(R.string.please_check_your_connenction);
                            if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                                rg_txt_verify_connect.setTypeface(titleTypeface);
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void setItem() { //invoke object

        rg_prg_verify_connect = (ProgressBar) G.fragmentActivity.findViewById(R.id.rg_prg_verify_connect);
        AppUtils.setProgresColler(rg_prg_verify_connect);

        rg_txt_verify_connect = (TextView) G.fragmentActivity.findViewById(R.id.rg_txt_verify_connect);
        rg_img_verify_connect = (ImageView) G.fragmentActivity.findViewById(R.id.rg_img_verify_connect);

        rg_prg_verify_sms = (ProgressBar) G.fragmentActivity.findViewById(R.id.rg_prg_verify_sms);
        AppUtils.setProgresColler(rg_prg_verify_sms);

        rg_txt_verify_sms = (TextView) G.fragmentActivity.findViewById(R.id.rg_txt_verify_sms);
        rg_img_verify_sms = (ImageView) G.fragmentActivity.findViewById(R.id.rg_img_verify_sms);

        rg_prg_verify_generate = (ProgressBar) G.fragmentActivity.findViewById(R.id.rg_prg_verify_key);
        AppUtils.setProgresColler(rg_prg_verify_generate);

        rg_txt_verify_generate = (TextView) G.fragmentActivity.findViewById(R.id.rg_txt_verify_key);
        rg_img_verify_generate = (ImageView) G.fragmentActivity.findViewById(R.id.rg_img_verify_key);

        rg_prg_verify_register = (ProgressBar) G.fragmentActivity.findViewById(R.id.rg_prg_verify_server);
        AppUtils.setProgresColler(rg_prg_verify_register);
        rg_txt_verify_register = (TextView) G.fragmentActivity.findViewById(R.id.rg_txt_verify_server);
        rg_img_verify_register = (ImageView) G.fragmentActivity.findViewById(R.id.rg_img_verify_server);

    }

    // error verify sms and open rg_dialog for enter sms code
    private void errorVerifySms(FragmentRegister.Reason reason) { //when don't receive sms and open rg_dialog for enter code

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setImageResource(R.mipmap.alert);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_img_verify_sms.setColorFilter(G.context.getResources().getColor(R.color.rg_error_red), PorterDuff.Mode.SRC_ATOP);
        rg_txt_verify_sms.setText(R.string.errore_verification_sms);
        rg_txt_verify_sms.setTextColor(G.context.getResources().getColor(R.color.rg_error_red));
        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
            rg_txt_verify_sms.setTypeface(titleTypeface);
        }

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
                    txtTimer.setVisibility(View.INVISIBLE);

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
            if (!mActivity.isFinishing() && !mActivity.isRestricted()) {
                if (isAdded()) {
                    dialog.show();
                    if (dialog.isShowing()) {
                        countDownTimer.cancel();
                    }
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

                        txtTimer.setVisibility(View.VISIBLE);

                        userName = userNameR;
                        userId = userIdR;
                        authorHash = authorHashR;
                        G.smsNumbers = smsNumbersR;

                        if (methodValue == ProtoUserRegister.UserRegisterResponse.Method.VERIFY_CODE_SOCKET) {
                            errorVerifySms(FragmentRegister.Reason.SOCKET);
                            countDownTimer.cancel();
                        }

                        rg_prg_verify_connect.setVisibility(View.GONE);
                        rg_img_verify_connect.setVisibility(View.VISIBLE);
                        if (rg_img_verify_sms != null) rg_img_verify_sms.setVisibility(View.GONE);
                        rg_txt_verify_connect.setTextAppearance(G.context, R.style.RedHUGEText);
                        rg_txt_verify_connect.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            rg_txt_verify_connect.setTypeface(titleTypeface);
                        }

                        rg_prg_verify_sms.setVisibility(View.VISIBLE);
                        rg_txt_verify_sms.setTextAppearance(G.context, R.style.RedHUGEText);
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            rg_txt_verify_sms.setTypeface(titleTypeface);
                        }
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
                            requestRegister();
                        }
                    });
                } else if (majorCode == 100 && minorCode == 2) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid phoneNumber
                            requestRegister();
                        }
                    });
                } else if (majorCode == 101) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Invalid phoneNumber
                            requestRegister();
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
                    requestRegister();
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
        dialogWait = new MaterialDialog.Builder(G.fragmentActivity).title(title).customView(R.layout.dialog_remind_time, wrapInScrollView).positiveText(R.string.B_ok).autoDismiss(false).canceledOnTouchOutside(false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                btnStart.setBackgroundColor(Color.parseColor(G.appBarColor));
                btnStart.setTextColor(G.context.getResources().getColor(R.color.white));
                btnStart.setEnabled(true);
                btnChoseCountry.setEnabled(true);
                btnChoseCountry.setTextColor(G.context.getResources().getColor(black_register));
                edtPhoneNumber.setEnabled(true);
                edtPhoneNumber.setTextColor(G.context.getResources().getColor(black_register));
                edtCodeNumber.setTextColor(G.context.getResources().getColor(black_register));
                txtAgreement_register.setVisibility(View.VISIBLE);
                layout_verify.setVisibility(View.GONE);
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

            rg_prg_verify_generate = (ProgressBar) G.fragmentActivity.findViewById(R.id.rg_prg_verify_key);
            AppUtils.setProgresColler(rg_prg_verify_generate);

            rg_txt_verify_generate = (TextView) G.fragmentActivity.findViewById(R.id.rg_txt_verify_key);
            rg_img_verify_generate = (ImageView) G.fragmentActivity.findViewById(R.id.rg_img_verify_key);


            if (rg_prg_verify_generate != null) rg_prg_verify_generate.setVisibility(View.VISIBLE);
            if (rg_txt_verify_generate != null) {
                rg_txt_verify_generate.setTextAppearance(G.context, R.style.RedHUGEText);
                if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                    rg_txt_verify_generate.setTypeface(titleTypeface);
                }
            }

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
            rg_prg_verify_connect.setVisibility(View.VISIBLE);
            rg_img_verify_connect.setVisibility(View.GONE);
            //rg_txt_verify_connect.setTextAppearance(G.context, Typeface.NORMAL);
            // clear step two
            rg_prg_verify_sms.setVisibility(View.GONE);
            rg_img_verify_sms.setVisibility(View.INVISIBLE);
            rg_txt_verify_sms.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
            if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                rg_txt_verify_sms.setTypeface(titleTypeface);
            }
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
                        rg_txt_verify_sms.setText(G.fragmentActivity.getResources().getString(R.string.rg_verify_register2));
                        rg_prg_verify_sms.setVisibility(View.GONE);
                        rg_img_verify_sms.setVisibility(View.VISIBLE);
                        rg_img_verify_sms.setImageResource(R.mipmap.check);
                        rg_img_verify_sms.setColorFilter(G.context.getResources().getColor(R.color.rg_text_verify), PorterDuff.Mode.SRC_ATOP);
                        rg_txt_verify_sms.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            rg_txt_verify_sms.setTypeface(titleTypeface);
                        }

                        newUser = newUserR;
                        token = tokenR;

                        if (rg_prg_verify_generate != null) {
                            rg_prg_verify_generate.setVisibility(View.GONE);
                            rg_img_verify_generate.setVisibility(View.VISIBLE);
                            rg_txt_verify_generate.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
                            if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                                rg_txt_verify_generate.setTypeface(titleTypeface);
                            }
                        }
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

                    rg_txt_verify_sms.setText((G.fragmentActivity.getResources().getString(R.string.rg_verify_register2)));
                    rg_prg_verify_sms.setVisibility(View.GONE);
                    rg_img_verify_sms.setVisibility(View.VISIBLE);
                    rg_img_verify_sms.setImageResource(R.mipmap.check);
                    rg_img_verify_sms.setColorFilter(G.context.getResources().getColor(R.color.rg_text_verify), PorterDuff.Mode.SRC_ATOP);
                    rg_txt_verify_sms.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
                    if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                        rg_txt_verify_sms.setTypeface(titleTypeface);
                    }

                    //newUser = newUserR;
                    //token = tokenR;
                    if (rg_prg_verify_generate != null) {
                        rg_prg_verify_generate.setVisibility(View.GONE);
                        rg_img_verify_generate.setVisibility(View.VISIBLE);
                        rg_txt_verify_generate.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
                        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                            rg_txt_verify_generate.setTypeface(titleTypeface);
                        }
                    }
                    //userLogin(token);

                }
                vgMainLayout = (ViewGroup) G.fragmentActivity.findViewById(R.id.rg_rootMainLayout);
                vgMainLayout.setVisibility(View.GONE);
                vgCheckPassword = (ViewGroup) G.fragmentActivity.findViewById(R.id.rg_rootCheckPassword);
                vgCheckPassword.setVisibility(View.VISIBLE);
                editCheckPassword = (EditText) G.fragmentActivity.findViewById(R.id.rg_edtCheckPassword);
                txtRecovery = (TextView) G.fragmentActivity.findViewById(R.id.rg_txtForgotPassword);
                prgWaiting = (ProgressBar) G.fragmentActivity.findViewById(R.id.prgWaiting);
                AppUtils.setProgresColler(prgWaiting);
                txtOk = (TextView) G.fragmentActivity.findViewById(R.id.rg_txtOk);
                txtOk.setVisibility(View.VISIBLE);
                txtQrCode.setVisibility(View.GONE);
                txtRecovery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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
                });
                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editCheckPassword.length() > 0) {
                            if (prgWaiting != null) {
                                prgWaiting.setVisibility(View.VISIBLE);
                            }
                            G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            new RequestUserTwoStepVerificationVerifyPassword().verifyPassword(editCheckPassword.getText().toString());
                        } else {
                            error(G.fragmentActivity.getResources().getString(R.string.please_enter_code));
                        }
                    }
                });
            }
        });
    }

    private void userLogin(final String token) {
        if (rg_prg_verify_register != null) rg_prg_verify_register.setVisibility(View.VISIBLE);
        if (rg_txt_verify_register != null) {
            rg_txt_verify_register.setTextAppearance(G.context, R.style.RedHUGEText);
            if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                rg_txt_verify_register.setTypeface(titleTypeface);
            }
        }
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

                        if (rg_prg_verify_register != null)
                            rg_prg_verify_register.setVisibility(View.GONE);
                        if (rg_img_verify_register != null)
                            rg_img_verify_register.setVisibility(View.VISIBLE);
                        if (rg_txt_verify_register != null) {
                            rg_txt_verify_register.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
                            if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
                                rg_txt_verify_register.setTypeface(titleTypeface);
                            }
                        }

                        if (newUser) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(FragmentRegistrationNickname.ARG_USER_ID, userId);
                                    fragment.setArguments(bundle);
                                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                                    G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentRegister.this).commitAllowingStateLoss();
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

    private void receiveVerifySms(String message) {

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

        String verificationCode = HelperString.regexExtractValue(message, regexFetchCodeVerification);
        verifyCode = verificationCode;
        countDownTimer.cancel(); //cancel method CountDown and continue process verify

        rg_prg_verify_sms.setVisibility(View.GONE);
        rg_img_verify_sms.setVisibility(View.VISIBLE);
        rg_txt_verify_sms.setTextColor(G.context.getResources().getColor(R.color.rg_text_verify));
        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {
            rg_txt_verify_sms.setTypeface(titleTypeface);
        }
        userVerify(userName, verificationCode);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(KEY_SAVE_CODENUMBER, edtCodeNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_MASK, edtPhoneNumber.getMask());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_NUMBER, edtPhoneNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_NAMECOUNTRY, btnChoseCountry.getText().toString());
        savedInstanceState.putString(KEY_SAVE_REGEX, regex);
        savedInstanceState.putString(KEY_SAVE_AGREEMENT, txtAgreement_register.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
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
                if (editCheckPassword != null) editCheckPassword.setHint(hint);
            }
        });

        securityPasswordQuestionOne = questionOne;
        securityPasswordQuestionTwo = questionTwo;
        isConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
        securityPasswordHint = hint;
        this.hasConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
        this.unconfirmedEmailPattern = unconfirmedEmailPattern;
    }

    @Override
    public void verifyPassword(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {

                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                closeKeyboard(txtOk);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorVerifyPassword(final int wait) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
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
                closeKeyboard(txtOk);
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });
    }

    @Override
    public void recoveryByEmail(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                vgCheckPassword.setVisibility(View.GONE);
                txtOk.setVisibility(View.GONE);
                vgMainLayout.setVisibility(View.VISIBLE);
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
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                closeKeyboard(txtOk);
            }
        });
    }

    @Override
    public void recoveryByQuestion(final String tokenR) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                token = tokenR;
                vgCheckPassword.setVisibility(View.GONE);
                txtOk.setVisibility(View.GONE);
                vgMainLayout.setVisibility(View.VISIBLE);
                userLogin(token);
            }
        });
    }

    @Override
    public void errorRecoveryByQuestion() {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (prgWaiting != null) {
                    prgWaiting.setVisibility(View.GONE);
                }
                G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                closeKeyboard(txtOk);
            }
        });
    }
}
