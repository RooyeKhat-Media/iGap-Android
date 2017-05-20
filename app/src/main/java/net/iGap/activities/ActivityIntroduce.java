/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.uncopt.android.widget.text.justify.JustifiedTextView;
import io.realm.Realm;
import java.io.File;
import java.io.IOException;
import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterViewPager;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnReceiveInfoLocation;
import net.iGap.interfaces.OnReceivePageInfoTOS;
import net.iGap.module.CustomCircleImage;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestInfoLocation;
import net.iGap.request.RequestInfoPage;

public class ActivityIntroduce extends ActivityEnhanced {

    static final String KEY_SAVE = "SAVE";
    static int ONETIME = 1;
    private ViewPager viewPager;
    private AdapterViewPager adapterViewPager;
    private int[] layout;
    private CustomCircleImage circleButton;
    private boolean isOne0 = true;
    private boolean isOne1 = true;
    private boolean isOne2 = true;
    private boolean isOne3 = true;
    private boolean isOne4 = true;
    private boolean locationFound;
    private boolean registrationTry;
    private boolean enableRegistration = true;
    private ImageView logoIgap, logoSecurity, logoChat, transfer, boy;
    private TextView txt_i_p1_l1, txt_p1_l1, txt_p1_l2, txt_p1_l3, txt_p2_l1, txt_p2_l2, txt_p3_l1, txt_p3_l2, txt_p4_l1, txt_p4_l2, txt_p5_l1, txt_p5_l2, txtSkip;
    private Button btnStart;
    private ViewGroup layout_test;
    private JustifiedTextView justifiedTextView;
    private String isoCode = "", countryName = "", pattern = "", regex = "", body = null;
    private int callingCode;
    private SharedPreferences sharedPreferences;
    private boolean isRealmDelete = true;
    //Licence Checking
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * set true mFirstRun for get room history after logout and login again
         */
        G.firstTimeEnterToApp = true;
        //licenceChecker();

        sharedPreferences = getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        boolean deleteFolderBackground = sharedPreferences.getBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, true);

        if (deleteFolderBackground) {
            deleteContentFolderChatBackground();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SHP_SETTING.DELETE_FOLDER_BACKGROUND, false);
            editor.apply();
        }

        try {
            HelperPermision.getStoragePermision(this, new OnGetPermission() {
                @Override
                public void Allow() {
                    goToProgram(savedInstanceState);
                }

                @Override
                public void deny() {

                    DialogInterface.OnClickListener onOkListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            try {
                                HelperPermision.getStoragePermision(ActivityIntroduce.this, new OnGetPermission() {
                                    @Override
                                    public void Allow() {
                                        StartupActions.makeFolder();
                                        goToProgram(savedInstanceState);
                                    }

                                    @Override
                                    public void deny() {
                                        finish();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    DialogInterface.OnClickListener onCancelListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    };

                    new AlertDialog.Builder(ActivityIntroduce.this).setMessage(R.string.you_have_to_get_storage_permision_for_continue).setCancelable(false).
                            setPositiveButton(ActivityIntroduce.this.getString(R.string.ok), onOkListener).setNegativeButton(ActivityIntroduce.this.getString(R.string.cancel), onCancelListener).create().show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void licenceChecker() {
        //// Construct the LicenseCheckerCallback. The library calls this when done.
        //mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        //// Construct the LicenseChecker with a Policy.
        //String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        //mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(Config.SALT, BuildConfig.APPLICATION_ID, android_id)), Config.BASE64_PUBLIC_KEY);
    }

    private void goToProgram(Bundle savedInstanceState) {

        Realm realm = Realm.getDefaultInstance();

        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();

        if (userInfo != null && userInfo.getUserRegistrationState()) { // user registered before
            Intent intent = new Intent(G.context, ActivityMain.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_introduce);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            ONETIME = savedInstanceState.getInt(KEY_SAVE);
            if (ONETIME != 1) {
                getInfo();
            }
        } else {
            getInfo();
        }

        layout_test = (ViewGroup) findViewById(R.id.int_layout_test);

        layout = new int[]{
                R.layout.view_pager_introduce_1,
        };

        viewPager = (ViewPager) findViewById(R.id.int_viewPager_introduce);

        circleButton = (CustomCircleImage) findViewById(R.id.int_circleButton_introduce);
        if (circleButton != null) {
            circleButton.circleButtonCount(5);
        }

        txtSkip = (TextView) findViewById(R.id.int_txt_skip);
        txtSkip.setBackgroundColor(Color.parseColor(G.appBarColor));

        logoIgap = (ImageView) findViewById(R.id.int_img_logo_introduce);

        txt_i_p1_l1 = (TextView) findViewById(R.id.int_txt_i_p1_l1);
        //        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/neuropolitical.ttf");
        //        txt_i_p1_l1.setTypeface(type);

        txt_p1_l2 = (TextView) findViewById(R.id.int_txt_p1_l2);
        txt_p1_l3 = (TextView) findViewById(R.id.int_txt_p1_l3);

        txt_p1_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page1)));

        logoSecurity = (ImageView) findViewById(R.id.int_img_security_introduce);
        txt_p2_l1 = (TextView) findViewById(R.id.int_txt_p2_l1);
        txt_p2_l2 = (TextView) findViewById(R.id.int_txt_p2_l2);

        txt_p2_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page2)));

        logoChat = (ImageView) findViewById(R.id.int_img_chat_introduce);
        txt_p3_l1 = (TextView) findViewById(R.id.int_txt_p3_l1);
        txt_p3_l2 = (TextView) findViewById(R.id.int_txt_p3_l2);

        txt_p3_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page3)));

        transfer = (ImageView) findViewById(R.id.int_img_transfer_introduce);
        txt_p4_l1 = (TextView) findViewById(R.id.int_txt_p4_l1);
        txt_p4_l2 = (TextView) findViewById(R.id.int_txt_p4_l2);

        txt_p4_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page4)));

        boy = (ImageView) findViewById(R.id.int_img_boy_introduce);
        txt_p5_l1 = (TextView) findViewById(R.id.int_txt_p5_l1);
        txt_p5_l2 = (TextView) findViewById(R.id.int_txt_p5_l2);

        btnStart = (Button) findViewById(R.id.int_btnStart);
        txt_p5_l2.setText(Html.fromHtml(getResources().getString(R.string.text_line_2_introduce_page5)));

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

                    case 0:
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {

                            isOne1 = true;

                            if (logoSecurity.getVisibility() == View.VISIBLE) {

                                animationOut(logoSecurity, txt_p2_l1, txt_p2_l2);
                            }

                            if (isOne0) {
                                animationInPage1(logoIgap, layout_test, txt_p1_l2, txt_p1_l3);
                                isOne0 = false;
                            }
                        }

                        break;

                    case 1:
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne0 = true;
                            isOne2 = true;

                            if (logoIgap.getVisibility() == View.VISIBLE) {

                                animationOutPage1(logoIgap, layout_test, txt_p1_l2, txt_p1_l3);
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
                    case 2:
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne1 = true;
                            isOne3 = true;

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
                    case 3:
                        txtSkip.bringToFront();
                        if (positionOffset == 0) {
                            isOne2 = true;
                            isOne4 = true;
                            if (viewPager.isFocusable()) {

                                if (logoChat.getVisibility() == View.VISIBLE) {

                                    animationOut(logoChat, txt_p3_l1, txt_p3_l2);
                                } else if (boy.getVisibility() == View.VISIBLE) {
                                    animationOutBoy(boy, txt_p5_l1, txt_p5_l2, btnStart);
                                }
                                if (isOne3) {

                                    animationIn(transfer, txt_p4_l1, txt_p4_l2);
                                    isOne3 = false;
                                }
                            }
                        }
                        break;
                    case 4:
                        txtSkip.bringToFront();
                        btnStart.bringToFront();
                        btnStart.getParent().requestLayout();

                        if (positionOffset == 0) {
                            isOne3 = true;

                            if (transfer.getVisibility() == View.VISIBLE) {

                                animationOut(transfer, txt_p4_l1, txt_p4_l2);
                            }
                            if (isOne4) {
                                animationInBoy(boy, txt_p5_l1, txt_p5_l2, btnStart);
                                isOne4 = false;
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

        adapterViewPager = new AdapterViewPager(layout);
        viewPager.setAdapter(adapterViewPager);
        adapterViewPager.notifyDataSetChanged();

        //        loop fo image city
        final ImageView backgroundOne = (ImageView) findViewById(R.id.int_background_one);
        final ImageView backgroundTwo = (ImageView) findViewById(R.id.int_background_two);

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

        realm.close();
    }

    private void deleteContentFolderChatBackground() {

        // delete  content of folder chat background in the first registeration
        File root = new File(G.DIR_CHAT_BACKGROUND);
        File[] Files = root.listFiles();
        if (Files != null) {
            for (int j = 0; j < Files.length; j++) {
                Files[j].delete();
            }
        }
    }

    private void startRegistration() {

        registrationTry = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (G.socketConnection) {
                    if (body != null & enableRegistration & (!isoCode.equals("") || !locationFound)) {
                        enableRegistration = false;
                        Intent intent = new Intent(G.context, ActivityRegister.class);
                        intent.putExtra("ISO_CODE", isoCode);
                        intent.putExtra("CALLING_CODE", callingCode);
                        intent.putExtra("COUNTRY_NAME", countryName);
                        intent.putExtra("PATTERN", pattern);
                        intent.putExtra("REGEX", regex);
                        intent.putExtra("TERMS_BODY", body);
                        startActivity(intent);
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //                                Toast.makeText(G.context, "waiting fot get info", Toast.LENGTH_SHORT).show();

                                final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.Toast_waiting_fot_get_info), Snackbar.LENGTH_LONG);

                                snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        snack.dismiss();
                                    }
                                });
                                snack.show();
                            }
                        });
                        getInfo();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.waiting_for_connection), Snackbar.LENGTH_LONG);

                            snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    snack.dismiss();
                                }
                            });
                            snack.show();
                        }
                    });
                }
            }
        });
        thread.start();
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
                    runOnUiThread(new Runnable() {
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    locationFound = false;
                    //                    Toast.makeText(G.context, "autoRegistration", Toast.LENGTH_SHORT).show();
                    //
                    //                    final Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.Toast_Location_Not_Found), Snackbar.LENGTH_LONG);
                    //
                    //                    snack.setAction("CANCEL", new View.OnClickListener() {
                    //                        @Override
                    //                        public void onClick(View view) {
                    //                            snack.dismiss();
                    //                        }
                    //                    });
                    //                    snack.show();

                }
            });
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

                logoIgap.setVisibility(View.INVISIBLE);
                logoSecurity.setVisibility(View.INVISIBLE);
                logoChat.setVisibility(View.INVISIBLE);
                transfer.setVisibility(View.INVISIBLE);

                layout_test.setVisibility(View.GONE);
                txt_p1_l2.setVisibility(View.GONE);
                txt_p1_l3.setVisibility(View.GONE);

                txt_p2_l1.setVisibility(View.GONE);
                txt_p2_l2.setVisibility(View.GONE);

                txt_p3_l1.setVisibility(View.GONE);
                txt_p3_l2.setVisibility(View.GONE);

                txt_p4_l1.setVisibility(View.GONE);
                txt_p4_l2.setVisibility(View.GONE);
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

                invisibleItems(logo);
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

                logoIgap.setVisibility(View.INVISIBLE);
                logoSecurity.setVisibility(View.INVISIBLE);
                logoChat.setVisibility(View.INVISIBLE);
                transfer.setVisibility(View.INVISIBLE);

                layout_test.setVisibility(View.GONE);
                txt_p1_l2.setVisibility(View.GONE);
                txt_p1_l3.setVisibility(View.GONE);

                txt_p2_l1.setVisibility(View.GONE);
                txt_p2_l2.setVisibility(View.GONE);

                txt_p3_l1.setVisibility(View.GONE);
                txt_p3_l2.setVisibility(View.GONE);

                txt_p4_l1.setVisibility(View.GONE);
                txt_p4_l2.setVisibility(View.GONE);
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

        if (logo.equals(logoIgap)) {

            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);
        }
        if (logo.equals(logoSecurity)) {
            logoChat.setVisibility(View.INVISIBLE);
            logoIgap.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);
        }
        if (logo.equals(logoChat)) {

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);
        }
        if (logo.equals(transfer)) {

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            boy.setVisibility(View.GONE);

            txt_p5_l1.setVisibility(View.GONE);
            txt_p5_l2.setVisibility(View.GONE);
            btnStart.setVisibility(View.GONE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);
        }
        if (logo.equals(boy)) {

            logoIgap.setVisibility(View.INVISIBLE);
            logoSecurity.setVisibility(View.INVISIBLE);
            logoChat.setVisibility(View.INVISIBLE);
            transfer.setVisibility(View.INVISIBLE);

            layout_test.setVisibility(View.GONE);
            txt_p1_l2.setVisibility(View.GONE);
            txt_p1_l3.setVisibility(View.GONE);

            txt_p2_l1.setVisibility(View.GONE);
            txt_p2_l2.setVisibility(View.GONE);

            txt_p3_l1.setVisibility(View.GONE);
            txt_p3_l2.setVisibility(View.GONE);

            txt_p4_l1.setVisibility(View.GONE);
            txt_p4_l2.setVisibility(View.GONE);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(KEY_SAVE, ONETIME);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    //==================================Licence Checker
    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int reason) {
            /*if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }*/
            // Should allow user access.
            displayResult("Allow");
        }

        public void dontAllow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            displayResult("Don't Allow");

            if (reason == Policy.RETRY) {
                // If the reason received from the policy is RETRY, it was probably
                // due to a loss of connection with the service, so we should give the
                // user a chance to retry. So show a dialog to retry.
                //showDialog(DIALOG_RETRY);
            } else {
                // Otherwise, the user is not licensed to use this app.
                // Your response should always inform the user that the application
                // is not licensed, but your behavior at that point can vary. You might
                // provide the user a limited access version of your app or you can
                // take them to Google Play to purchase the app.
                //showDialog(DIALOG_GOTOMARKET);
            }
        }

        @Override
        public void applicationError(int errorCode) {

        }
    }

    private void displayResult(final String result) {
        G.handler.post(new Runnable() {
            public void run() {
                Toast.makeText(G.context, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mChecker.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            ActivityCompat.finishAffinity(this);
        }
    }
}