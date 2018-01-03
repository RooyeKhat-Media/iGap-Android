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

import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.vicmikhailau.maskededittext.MaskedEditText;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityRegisterBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnSmsReceive;
import net.iGap.module.AppUtils;
import net.iGap.module.IncomingSms;
import net.iGap.viewmodel.FragmentRegisterViewModel;

public class FragmentRegister extends BaseFragment {

    private static final String KEY_SAVE_CODENUMBER = "SAVE_CODENUMBER";
    private static final String KEY_SAVE_PHONENUMBER_MASK = "SAVE_PHONENUMBER_MASK";
    private static final String KEY_SAVE_PHONENUMBER_NUMBER = "SAVE_PHONENUMBER_NUMBER";
    private static final String KEY_SAVE_NAMECOUNTRY = "SAVE_NAMECOUNTRY";
    private static final String KEY_SAVE_REGEX = "KEY_SAVE_REGEX";
    private static final String KEY_SAVE_AGREEMENT = "KEY_SAVE_REGISTER";
    public static Button btnChoseCountry;
    public static EditText edtCodeNumber;
    public static MaskedEditText edtPhoneNumber;
    public static TextView btnOk;
    public static int positionRadioButton = -1;
    private TextView txtAgreement_register;
    private ViewGroup layout_verify;
    //Array List for Store List of StructCountry Object
    private IncomingSms smsReceiver;
    public static boolean smsPermission = true;
    private FragmentActivity mActivity;
    private ScrollView scrollView;
    private int headerLayoutHeight;
    private LinearLayout headerLayout;
    public static OnStartAnimationRegister onStartAnimationRegister;

    private FragmentRegisterViewModel fragmentRegisterViewModel;
    private ActivityRegisterBinding fragmentRegisterBinding;

    public enum Reason {
        SOCKET, TIME_OUT, INVALID_CODE
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentRegisterBinding = DataBindingUtil.inflate(inflater, R.layout.activity_register, container, false);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initBindView();

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");


        smsReceiver = new IncomingSms(new OnSmsReceive() {

            @Override
            public void onSmsReceive(final String phoneNumber, final String message) {
                try {
                    if (message != null && !message.isEmpty() && !message.equals("null") && !message.equals("")) {
                        //rg_txt_verify_sms.setText(message);
                        fragmentRegisterViewModel.receiveVerifySms(message);
                    }
                } catch (Exception e1) {
                    e1.getStackTrace();
                }
            }
        });

        try {
            HelperPermission.getSmsPermision(G.fragmentActivity, new OnGetPermission() {
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


        TextView txtTitleToolbar = fragmentRegisterBinding.rgTxtTitleToolbar;
        Typeface titleTypeface;
        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }

        txtTitleToolbar.setTypeface(titleTypeface);

        if (G.selectedLanguage.equals("fa") || G.selectedLanguage.equals("ar")) {

            TextView rg_txt_verify_connect = fragmentRegisterBinding.rgTxtVerifyConnect;
            rg_txt_verify_connect.setTypeface(titleTypeface);


            TextView rg_txt_verify_sms = fragmentRegisterBinding.rgTxtVerifySms;
            rg_txt_verify_sms.setTypeface(titleTypeface);

            TextView rg_txt_verify_generate = fragmentRegisterBinding.rgTxtVerifyKey;
            rg_txt_verify_generate.setTypeface(titleTypeface);

            TextView rg_txt_verify_register = fragmentRegisterBinding.rgTxtVerifyServer;
            rg_txt_verify_register.setTypeface(titleTypeface);

        }


        ProgressBar rg_prg_verify_connect = fragmentRegisterBinding.rgPrgVerifyConnect;
        AppUtils.setProgresColler(rg_prg_verify_connect);// TODO: 12/16/2017 - 1 -

        ProgressBar rg_prg_verify_sms = fragmentRegisterBinding.rgPrgVerifySms;
        AppUtils.setProgresColler(rg_prg_verify_sms);


        ProgressBar rg_prg_verify_generate = fragmentRegisterBinding.rgPrgVerifyKey;
        AppUtils.setProgresColler(rg_prg_verify_generate);

        ProgressBar rg_prg_verify_register = fragmentRegisterBinding.rgPrgVerifyServer;
        AppUtils.setProgresColler(rg_prg_verify_register);

        ProgressBar prgWaiting = (ProgressBar) fragmentRegisterBinding.prgWaiting;
        AppUtils.setProgresColler(prgWaiting);


        scrollView = fragmentRegisterBinding.scrollView;
        headerLayout = fragmentRegisterBinding.headerLayout;
        edtCodeNumber = fragmentRegisterBinding.rgEdtCodeNumber;
        btnChoseCountry = fragmentRegisterBinding.rgBtnChoseCountry;
        edtPhoneNumber = fragmentRegisterBinding.rgEdtPhoneNumber;
        btnOk = fragmentRegisterBinding.rgEdtPhoneNumber;

        txtAgreement_register = fragmentRegisterBinding.txtAgreementRegister;
        txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());
        layout_verify = fragmentRegisterBinding.rgLayoutVerifyAndAgreement;

        onStartAnimationRegister = new OnStartAnimationRegister() {
            @Override
            public void start() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        final Animation trans_x_in = AnimationUtils.loadAnimation(G.context, R.anim.rg_tansiton_y_in);
                        final Animation trans_x_out = AnimationUtils.loadAnimation(G.context, R.anim.rg_tansiton_y_out);
                        txtAgreement_register.setMovementMethod(new ScrollingMovementMethod());
                        txtAgreement_register.startAnimation(trans_x_out);
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragmentRegisterBinding.rgLayoutVerifyAndAgreement.setVisibility(View.VISIBLE);
                                layout_verify.startAnimation(trans_x_in);
                            }
                        }, 500);
                    }
                });

            }
        };



        fragmentRegisterViewModel.saveInstance(savedInstanceState, getArguments());


    }

    private void initBindView() {
        fragmentRegisterViewModel = new FragmentRegisterViewModel(this, fragmentRegisterBinding.getRoot(), mActivity);
        fragmentRegisterBinding.setFragmentRegisterViewModel(fragmentRegisterViewModel);
    }



    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(KEY_SAVE_CODENUMBER, edtCodeNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_MASK, edtPhoneNumber.getMask());
        savedInstanceState.putString(KEY_SAVE_PHONENUMBER_NUMBER, edtPhoneNumber.getText().toString());
        savedInstanceState.putString(KEY_SAVE_NAMECOUNTRY, btnChoseCountry.getText().toString());
        savedInstanceState.putString(KEY_SAVE_REGEX, fragmentRegisterViewModel.regex);
        savedInstanceState.putString(KEY_SAVE_AGREEMENT, txtAgreement_register.getText().toString());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        boolean beforeState = G.isLandscape;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            G.isLandscape = false;
        }

        if (G.isLandscape && fragmentRegisterViewModel.isVerify) {

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
        fragmentRegisterViewModel.onStop();
        super.onStop();
    }

    public interface OnStartAnimationRegister {
        void start();
    }
}
