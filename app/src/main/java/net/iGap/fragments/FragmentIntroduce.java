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

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.module.CustomCircleImage;
import net.iGap.request.RequestInfoLocation;
import net.iGap.request.RequestInfoPage;

import static net.iGap.G.context;

public class FragmentIntroduce extends BaseFragment {

    private static final String KEY_SAVE = "SAVE";
    private static int ONETIME = 1;
    private ViewPager viewPager;
    private CustomCircleImage circleButton;
    private boolean isOne0 = true;
    private boolean isOne1 = true;
    private boolean isOne2 = true;
    private boolean isOne3 = true;
    private boolean isOne4 = true;
    private boolean isOne5 = true;
    private boolean locationFound;
    private boolean registrationTry;
    private boolean enableRegistration = true;
    private ImageView logoIgap, logoSecurity, logoChat, transfer, call, boy;
    private TextView txt_p1_l2;
    private TextView txt_p1_l3;
    private TextView txt_p2_l1;
    private TextView txt_p2_l2;
    private TextView txt_p3_l1;
    private TextView txt_p3_l2;
    private TextView txt_p4_l1;
    private TextView txt_p4_l2;
    private TextView txt_p5_l1;
    private TextView txt_p5_l2;
    private TextView txt_p6_l1;
    private TextView txt_p6_l2;
    private TextView txtSkip;
    private Button btnStart;
    private ViewGroup layout_iGap;
    private String isoCode = "", countryName = "", pattern = "", regex = "", body = null;
    private int callingCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_introduce, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goToProgram(view, savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        boolean beforeState = G.isLandscape;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            G.isLandscape = false;
        }

        G.firstEnter = true;

        try {
            if (beforeState != G.isLandscape) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (!isAdded() || G.fragmentActivity.isFinishing()) {
                            return;
                        }

                        G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentIntroduce.this).commitAllowingStateLoss();
                        FragmentIntroduce fragment = new FragmentIntroduce();
                        G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                    }
                });
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        super.onConfigurationChanged(newConfig);
    }

    private void goToProgram(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            ONETIME = savedInstanceState.getInt(KEY_SAVE);
            if (ONETIME != 1) {
                getInfo();
            }
        } else {
            getInfo();
        }

        layout_iGap = (ViewGroup) view.findViewById(R.id.int_layout_iGap);

        int[] layout = new int[]{
                R.layout.view_pager_introduce_1,
        };

        viewPager = (ViewPager) view.findViewById(R.id.int_viewPager_introduce);

        circleButton = (CustomCircleImage) view.findViewById(R.id.int_circleButton_introduce);
        if (circleButton != null) {
            circleButton.circleButtonCount(6);
        }

        Typeface titleTypeface;
        if (!HelperCalander.isPersianUnicode) {
            titleTypeface = G.typeface_neuropolitical;
        } else {
            titleTypeface = G.typeface_IRANSansMobile;
        }

        txtSkip = (TextView) view.findViewById(R.id.int_txt_skip);

        Drawable mDrawableSkip = ContextCompat.getDrawable(context, R.drawable.background_skip);
        mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            txtSkip.setBackground(mDrawableSkip);
        }

        logoIgap = (ImageView) view.findViewById(R.id.int_img_logo_introduce);

        TextView txt_i_p1_l1 = (TextView) view.findViewById(R.id.int_txt_i_p1_l1);
        TextView txt_p1_l1 = (TextView) view.findViewById(R.id.int_txt_p1_l1);

        txt_i_p1_l1.setTypeface(titleTypeface);
        txt_p1_l1.setTypeface(titleTypeface);

        txt_p1_l2 = (TextView) view.findViewById(R.id.int_txt_p1_l2);
        txt_p1_l3 = (TextView) view.findViewById(R.id.int_txt_p1_l3);
        txt_p1_l3.setText(G.fragmentActivity.getResources().getString(R.string.text_line_3_introduce_page1) + "\n" + G.fragmentActivity.getResources().getString(R.string.text_line_4_introduce_page1));

        txt_p1_l2.setText(R.string.text_line_2_introduce_page1);

        logoSecurity = (ImageView) view.findViewById(R.id.int_img_security_introduce);
        txt_p2_l1 = (TextView) view.findViewById(R.id.int_txt_p2_l1);
        txt_p2_l2 = (TextView) view.findViewById(R.id.int_txt_p2_l2);

        txt_p2_l2.setText(R.string.text_line_2_introduce_page2);

        logoChat = (ImageView) view.findViewById(R.id.int_img_chat_introduce);
        txt_p3_l1 = (TextView) view.findViewById(R.id.int_txt_p3_l1);
        txt_p3_l2 = (TextView) view.findViewById(R.id.int_txt_p3_l2);

        txt_p3_l2.setText(R.string.text_line_2_introduce_page3);

        transfer = (ImageView) view.findViewById(R.id.int_img_transfer_introduce);
        txt_p4_l1 = (TextView) view.findViewById(R.id.int_txt_p4_l1);
        txt_p4_l2 = (TextView) view.findViewById(R.id.int_txt_p4_l2);

        txt_p4_l2.setText(R.string.text_line_2_introduce_page4);

        call = (ImageView) view.findViewById(R.id.int_img_call_introduce);
        txt_p5_l1 = (TextView) view.findViewById(R.id.int_txt_p5_l1);
        txt_p5_l2 = (TextView) view.findViewById(R.id.int_txt_p5_l2);

        boy = (ImageView) view.findViewById(R.id.int_img_boy_introduce);
        txt_p6_l1 = (TextView) view.findViewById(R.id.int_txt_p6_l1);
        txt_p6_l2 = (TextView) view.findViewById(R.id.int_txt_p6_l2);
        txt_p6_l2.setText(R.string.text_line_2_introduce_page6);

        btnStart = (Button) view.findViewById(R.id.int_btnStart);
        Drawable mDrawable = ContextCompat.getDrawable(context, R.drawable.int_button_interduce);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnStart.setBackground(mDrawable);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistration();
            }
        });

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistration();
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) { //set animation for all page

                circleButton.percentScroll(positionOffset, position);

                switch (position) {

                    case 0://Igap 1
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {

                            isOne1 = true;
                            isOne2 = true;
                            isOne3 = true;
                            isOne4 = true;
                            isOne5 = true;

                            if (logoSecurity.getVisibility() == View.VISIBLE) {

                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }

                            if (isOne0) {
                                animationInPage1(logoIgap, layout_iGap, txt_p1_l2, txt_p1_l3);
                                isOne0 = false;
                            }
                        }

                        break;

                    case 1://Security 2
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne2 = true;
                            isOne3 = true;
                            isOne4 = true;
                            isOne5 = true;

                            if (logoIgap.getVisibility() == View.VISIBLE) {

                                animationOutPage1(logoIgap, layout_iGap, txt_p1_l2, txt_p1_l3);
                            }
                            if (logoChat.getVisibility() == View.VISIBLE) {
                                animationOut(logoChat, txt_p3_l1, txt_p3_l2);
                            }

                            if (isOne1) {

                                animationIn(logoSecurity, txt_p2_l1, txt_p2_l2);
                                isOne1 = false;
                            }
                        }
                        break;
                    case 2://Chat 3
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne1 = true;
                            isOne3 = true;
                            isOne4 = true;
                            isOne5 = true;

                            if (logoSecurity.getVisibility() == View.VISIBLE) {
                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }
                            if (transfer.getVisibility() == View.VISIBLE) {
                                animationOut(transfer, txt_p4_l1, txt_p4_l2);
                            }
                            if (isOne2) {

                                animationIn(logoChat, txt_p3_l1, txt_p3_l2);
                                isOne2 = false;
                            }
                        }
                        break;
                    case 3://boy 4
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne1 = true;
                            isOne2 = true;
                            isOne4 = true;
                            isOne5 = true;
                            if (viewPager.isFocusable()) {
                                if (logoChat.getVisibility() == View.VISIBLE) {
                                    animationOut(logoChat, txt_p3_l1, txt_p3_l2);
                                } else if (call.getVisibility() == View.VISIBLE) {
                                    animationOut(call, txt_p5_l1, txt_p5_l2);
                                }
                                if (isOne3) {

                                    animationIn(transfer, txt_p4_l1, txt_p4_l2);
                                    isOne3 = false;
                                }
                            }
                        }
                        break;
                    case 4://call 5
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne1 = true;
                            isOne2 = true;
                            isOne3 = true;
                            isOne5 = true;

                            if (viewPager.isFocusable()) {
                                if (transfer.getVisibility() == View.VISIBLE) {
                                    animationOut(transfer, txt_p4_l1, txt_p4_l2);
                                } else if (boy.getVisibility() == View.VISIBLE) {
                                    animationOutBoy(boy, txt_p6_l1, txt_p6_l2, btnStart);
                                }
                                if (isOne4) {
                                    animationIn(call, txt_p5_l1, txt_p5_l2);
                                    isOne4 = false;
                                }
                            }
                        }
                        break;
                    case 5://transfer1 6
                        txtSkip.bringToFront();
                        btnStart.bringToFront();
                        btnStart.getParent().requestLayout();

                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne1 = true;
                            isOne2 = true;
                            isOne3 = true;
                            isOne4 = true;

                            if (call.getVisibility() == View.VISIBLE) {

                                animationOut(call, txt_p5_l1, txt_p5_l2);
                            }
                            if (isOne5) {
                                animationInBoy(boy, txt_p6_l1, txt_p6_l2, btnStart);
                                isOne5 = false;
                            }
                        }
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        AdapterViewPager adapterViewPager = new AdapterViewPager(layout);
        viewPager.setAdapter(adapterViewPager);
        adapterViewPager.notifyDataSetChanged();

        //        loop fo image city
        final ImageView backgroundOne = (ImageView) view.findViewById(R.id.int_background_one);
        final ImageView backgroundTwo = (ImageView) view.findViewById(R.id.int_background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(20000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                assert backgroundOne != null;
                final float width = backgroundOne.getWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                assert backgroundTwo != null;
                backgroundTwo.setTranslationX(translationX - width);
            }
        });
        animator.start();

    }

    private void startRegistration() {

        if (!isAdded() || G.fragmentActivity.isFinishing()) {
            return;
        }

        try {
            registrationTry = true;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (G.socketConnection) {
                        if (body != null & enableRegistration & (!isoCode.equals("") || !locationFound)) {
                            enableRegistration = false;

                            FragmentRegister fragment = new FragmentRegister();
                            Bundle bundle = new Bundle();
                            bundle.putString("ISO_CODE", isoCode);
                            bundle.putInt("CALLING_CODE", callingCode);
                            bundle.putString("COUNTRY_NAME", countryName);
                            bundle.putString("PATTERN", pattern);
                            bundle.putString("REGEX", regex);
                            bundle.putString("TERMS_BODY", body);
                            fragment.setArguments(bundle);

                            G.fragmentActivity.getSupportFragmentManager().beginTransaction().add(R.id.ar_layout_root, fragment).setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left).commitAllowingStateLoss();
                            G.fragmentActivity.getSupportFragmentManager().beginTransaction().remove(FragmentIntroduce.this).commitAllowingStateLoss();
                        } else {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.Toast_waiting_fot_get_info), false);

                                }
                            });
                            getInfo();
                        }
                    } else {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {

                                HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.waiting_for_connection), false);

                            }
                        });
                    }

                }
            });
            thread.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void getInfo() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure) {
                    getTermsOfServiceBody();
                    //getInfoLocation();
                    ONETIME = 1;
                } else {
                    getInfo();
                }
            }
        }, 1000);
    }

    private void getInfoLocation() {

        G.onReceiveInfoLocation = new OnReceiveInfoLocation() {
            @Override
            public void onReceive(String isoCodeR, final int callingCodeR, final String countryNameR, String patternR, String regexR) {
                locationFound = true;
                isoCode = isoCodeR;
                callingCode = callingCodeR;
                countryName = countryNameR;
                pattern = patternR;
                regex = regexR;
                autoRegistration();
            }

            @Override
            public void onError(int majorCode, int minorCode) {
                if (majorCode == 500 && minorCode == 1) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            locationFound = false;
                        }
                    });
                }
            }
        };

        new RequestInfoLocation().infoLocation();
    }

    private void getTermsOfServiceBody() {

        G.onReceivePageInfoTOS = new OnReceivePageInfoTOS() {

            @Override
            public void onReceivePageInfo(final String bodyR) {
                body = bodyR;
                getInfoLocation();
            }

            @Override
            public void onError(int majorCode, int minorCode) {

            }
        };

        new RequestInfoPage().infoPage("TOS");
    }

    private void autoRegistration() { // if before user try for registration now after get data automatically go to registration page
        if (registrationTry & enableRegistration) {
            locationFound = false;
            startRegistration();
        }
    }

    private void animationInPage1(final ImageView logo, final ViewGroup txt1, final TextView txt2, final TextView txt3) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);

        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
        ObjectAnimator txt_fade3 = ObjectAnimator.ofFloat(txt3, "alpha", 0, 1);

        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX3 = ObjectAnimator.ofFloat(txt3, "scaleX", 0, 1);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY3 = ObjectAnimator.ofFloat(txt3, "scaleY", 0, 1);
        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_scaleX3).with(txt_scaleY3).with(txt_fade1).with(txt_fade2).with(txt_fade3);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                if (txt3 != null) {
                    txt3.setVisibility(View.VISIBLE);
                }

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scaleDown.start();
            }
        }, 500);
    }

    private void animationOutPage1(final ImageView logo, final ViewGroup txt1, final TextView txt2, final TextView txt3) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_fade3 = ObjectAnimator.ofFloat(txt3, "alpha", 1, 0);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX3 = ObjectAnimator.ofFloat(txt3, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY3 = ObjectAnimator.ofFloat(txt3, "scaleY", 1, 0);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_scaleX3).with(txt_scaleY3).with(txt_fade1).with(txt_fade2).with(txt_fade3);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //logo.setVisibility(View.VISIBLE);
                //txt1.setVisibility(View.VISIBLE);
                //txt2.setVisibility(View.VISIBLE);
                //if (txt3 != null) {
                //    txt3.setVisibility(View.VISIBLE);
                //}

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        scaleDown.start();
    }

    private void animationIn(final ImageView logo, final TextView txt1, final TextView txt2) {

        if (!logo.equals(boy)) {

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
            ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);
            ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
            ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
            ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
            ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
            ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
            ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
            final AnimatorSet scaleDown = new AnimatorSet();
            scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_fade1).with(txt_fade2);

            scaleDown.setDuration(500);
            scaleDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    logo.setVisibility(View.VISIBLE);
                    txt1.setVisibility(View.VISIBLE);
                    txt2.setVisibility(View.VISIBLE);

                    invisibleItems(logo);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scaleDown.start();
                }
            }, 500);
        }
    }

    private void animationOut(final ImageView logo, final TextView txt1, final TextView txt2) {

        viewPager.setEnabled(false);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        final ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);

        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(txt_fade1).with(txt_fade2);

        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                //invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.GONE);
                txt1.setVisibility(View.GONE);
                txt2.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleDown.start();
    }

    private void animationInBoy(final ImageView logo, final TextView txt1, final TextView txt2, final Button start) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0, 1);
        final ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 0, 1);

        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(txt1, "alpha", 0, 1);
        ObjectAnimator txt_fade2 = ObjectAnimator.ofFloat(txt2, "alpha", 0, 1);
        ObjectAnimator txt_scaleX1 = ObjectAnimator.ofFloat(txt1, "scaleX", 0, 1);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt1, "scaleY", 0, 1);
        ObjectAnimator txt_scaleY1 = ObjectAnimator.ofFloat(txt2, "scaleX", 0, 1);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 0, 1);
        ObjectAnimator btn_scaleX1 = ObjectAnimator.ofFloat(start, "scaleX", 0, 1);
        ObjectAnimator btn_scaleY1 = ObjectAnimator.ofFloat(start, "scaleY", 0, 1);
        ObjectAnimator btn_fade1 = ObjectAnimator.ofFloat(start, "alpha", 0, 1);
        final AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX1).with(txt_scaleY1).with(txt_scaleX2).with(txt_scaleY2).with(btn_scaleX1).with(btn_scaleY1).with(btn_fade1).with(txt_fade1).with(txt_fade2);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                logo.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.VISIBLE);
                txt2.setVisibility(View.VISIBLE);
                start.setVisibility(View.VISIBLE);

                invisibleItems(logo);

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                scaleDown.start();
            }
        }, 500);
    }

    private void animationOutBoy(final ImageView logo, final TextView txt1, final TextView txt2, final Button start) {

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 1, 0);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 1, 0);
        ObjectAnimator fade = ObjectAnimator.ofFloat(logo, "alpha", 1, 0);

        ObjectAnimator fade2 = ObjectAnimator.ofFloat(txt1, "alpha", 1, 0);
        ObjectAnimator fade3 = ObjectAnimator.ofFloat(txt2, "alpha", 1, 0);
        ObjectAnimator txt_scaleX = ObjectAnimator.ofFloat(txt1, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY = ObjectAnimator.ofFloat(txt1, "scaleY", 1, 0);
        ObjectAnimator txt_scaleX2 = ObjectAnimator.ofFloat(txt2, "scaleX", 1, 0);
        ObjectAnimator txt_scaleY2 = ObjectAnimator.ofFloat(txt2, "scaleY", 1, 0);
        ObjectAnimator btn_scaleX2 = ObjectAnimator.ofFloat(start, "scaleX", 1, 0);
        ObjectAnimator btn_scaleY2 = ObjectAnimator.ofFloat(start, "scaleY", 1, 0);
        ObjectAnimator txt_fade1 = ObjectAnimator.ofFloat(start, "alpha", 1, 0);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.play(scaleX).with(scaleY).with(fade).with(txt_scaleX).with(txt_scaleY).with(fade2).with(fade3).with(txt_scaleX2).with(txt_scaleY2).with(btn_scaleX2).with(btn_scaleY2).with(txt_fade1);
        scaleDown.setDuration(500);
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                invisibleItems(logo);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.GONE);
                txt1.setVisibility(View.GONE);
                txt2.setVisibility(View.GONE);
                start.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleDown.start();
    }

    private void invisibleItems(ImageView logo) {

        if (logo.equals(logoIgap)) { // 1

            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            call.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(logoSecurity)) { //2
            logoIgap.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            call.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            layout_iGap.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(logoChat)) { // 3

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            call.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            layout_iGap.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(transfer)) { // 4

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            call.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            layout_iGap.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(call)) { // 5

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            layout_iGap.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

            txt_p6_l1.setVisibility(View.GONE);
            txt_p6_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);
        }
        if (logo.equals(boy)) { //6

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            call.setVisibility(View.INVISIBLE);

            layout_iGap.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(KEY_SAVE, ONETIME);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayResult(final String result) {
        G.handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class AdapterViewPager extends PagerAdapter {
        int[] layout;

        public AdapterViewPager(int[] layout) {
            this.layout = layout;
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = G.inflater.inflate(R.layout.view_pager_introduce_1, container, false);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
