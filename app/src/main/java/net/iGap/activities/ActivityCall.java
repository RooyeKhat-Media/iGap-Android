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

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wang.avi.AVLoadingIndicatorView;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.request.RequestUserInfo;


public class ActivityCall extends ActivityEnhanced {

    public static final String UserIdStr = "USERID";
    public static final String INCOMONGCALL_STR = "INCOMONGCALL_STR";

    long userID;
    boolean isIncomingCall = false;

    boolean canClick = false;
    boolean canTuch = false;

    boolean down = false;

    VerticalSwip verticalSwip;

    TextView txtName;
    TextView txtStatus;
    AVLoadingIndicatorView avLoadingIndicatorView;
    ImageView userCallerPicture;
    LinearLayout layoutCaller;
    LinearLayout layoutOption;
    MaterialDesignTextView btnCircleChat;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnCall;
    MaterialDesignTextView btnMic;
    MaterialDesignTextView btnChat;
    MaterialDesignTextView btnSpeaker;

    MediaPlayer player;

    //************************************************************************

    @Override protected void onDestroy() {
        super.onDestroy();
        G.isInCall = false;
        G.iSignalingCallBack = null;
        cancelRigtone();
    }

    @Override public void onCreate(Bundle savedInstanceState) {

        G.isInCall = true;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(
            LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        userID = getIntent().getExtras().getLong(UserIdStr);
        isIncomingCall = getIntent().getExtras().getBoolean(INCOMONGCALL_STR);

        initComponent();

        initCallBack();
    }

    @Override public boolean dispatchTouchEvent(MotionEvent ev) {

        verticalSwip.dispatchTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }

    //***************************************************************************************

    private void initCallBack() {

        G.iSignalingCallBack = new ISignalingCallBack() {

            @Override public void onStatusChanged(final String status) {

                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        txtStatus.setText(status);

                        if (status.contains("end")) {
                            endVoiceAndFinish();
                        }
                    }
                });
            }
        };
    }

    private void initComponent() {

        verticalSwip = new VerticalSwip();

        txtName = (TextView) findViewById(R.id.fcr_txt_name);
        txtStatus = (TextView) findViewById(R.id.fcr_txt_status);
        avLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.fcr_txt_avi);
        userCallerPicture = (ImageView) findViewById(R.id.fcr_imv_background);
        layoutCaller = (LinearLayout) findViewById(R.id.fcr_layout_caller);
        layoutOption = (LinearLayout) findViewById(R.id.fcr_layout_option);

        //************************************

        final FrameLayout layoutCallEnd = (FrameLayout) findViewById(R.id.fcr_layout_chat_call_end);
        layoutCallEnd.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (canClick) {
                    endVoiceAndFinish();
                    layoutCallEnd.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnEndCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_end);
        btnEndCall.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                setUpSwap(layoutCallEnd);
                return false;
            }
        });

        //************************************

        final FrameLayout layoutChat = (FrameLayout) findViewById(R.id.fcr_layout_chat_call);
        layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (canClick) {
                    btnChat.performClick();
                    layoutChat.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnCircleChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_circle_chat);
        btnCircleChat.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                setUpSwap(layoutChat);
                return false;
            }
        });

        //************************************

        final FrameLayout layoutCall = (FrameLayout) findViewById(R.id.fcr_layout_answer_call);
        layoutCall.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (canClick) {
                    layoutOption.setVisibility(View.VISIBLE);
                    layoutCall.setVisibility(View.GONE);
                    layoutChat.setVisibility(View.GONE);

                    cancelRigtone();
                }
            }
        });

        btnCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_call);
        btnCall.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                setUpSwap(layoutCall);
                return false;
            }
        });

        //************************************

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                HelperPublicMethod.goToChatRoom(userID, null, null);
                endVoiceAndFinish();
            }
        });

        btnSpeaker = (MaterialDesignTextView) findViewById(R.id.fcr_btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnSpeaker.getText().toString().equals(G.context.getResources().getString(R.string.md_muted))) {
                    btnSpeaker.setText(R.string.md_unMuted);
                } else {
                    btnSpeaker.setText(R.string.md_muted);
                }
            }
        });

        btnMic = (MaterialDesignTextView) findViewById(R.id.fcr_btn_mic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (btnMic.getText().toString().equals(G.context.getResources().getString(R.string.md_mic))) {
                    btnMic.setText(R.string.md_mic_off);
                } else {
                    btnMic.setText(R.string.md_mic);
                }
            }
        });

        //************************************

        if (isIncomingCall) {
            playRingtone();
        } else {
            layoutCall.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);
            layoutOption.setVisibility(View.VISIBLE);
        }

        setAnimation();
        setPicture();
    }

    //***************************************************************************************

    private void setUpSwap(View view) {

        if (!down) {
            verticalSwip.setView(view);
            canTuch = true;
            down = true;
        }
    }

    private void setAnimation() {

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_enter_down_circke_button);

        layoutCaller.startAnimation(animation);
    }

    private void setPicture() {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, userID).findFirst();

        if (registeredInfo != null) {

            try {
                RealmAttachment av = registeredInfo.getLastAvatar().getFile();

                ProtoFileDownload.FileDownload.Selector se = ProtoFileDownload.FileDownload.Selector.FILE;
                String dirPath = AndroidUtils.getFilePathWithCashId(av.getCacheId(), av.getName(), G.DIR_IMAGE_USER, false);

                HelperDownloadFile.startDownload(av.getToken(), av.getCacheId(), av.getName(), av.getSize(), se, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                    @Override public void OnProgress(final String path, int progress) {

                        if (progress == 100) {

                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), userCallerPicture);
                                }
                            });
                        }
                    }

                    @Override public void OnError(String token) {

                    }
                });
            } catch (NullPointerException e) {

            }
        } else {
            new RequestUserInfo().userInfo(userID);
        }

        realm.close();
    }

    private void endVoiceAndFinish() {

        cancelRigtone();

        finish();
    }

    private void playRingtone() {
        player = MediaPlayer.create(ActivityCall.this, R.raw.iphone_5_original);
        player.start();
    }

    private void cancelRigtone() {
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }

            player.release();

            player = null;
        }
    }

    //***************************************************************************************

    class VerticalSwip {

        private int Allmoving = 0;
        private int lastY;
        private int DistanceToAccept = 200;
        boolean accept = false;

        private View view;

        public void setView(View view) {

            this.view = view;
        }

        public void dispatchTouchEvent(MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startMoving((int) event.getY());

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canTuch) {
                        moving((int) event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (canTuch) {
                        reset();
                    }

                    down = false;

                    break;
            }
        }

        private void startMoving(int y) {

            lastY = y;
            accept = false;
        }

        private void moving(int y) {
            int i = lastY - y;

            if (i > 0 || Allmoving > 0) {
                Allmoving += i;

                view.setPadding(0, 0, 0, view.getPaddingBottom() + i);

                lastY = y;
                if (Allmoving >= DistanceToAccept) {
                    accept = true;
                    reset();
                }
            }
        }

        private void reset() {
            view.setPadding(0, 0, 0, 0);
            canTuch = false;
            Allmoving = 0;

            if (accept) {

                canClick = true;
                view.performClick();
                canClick = false;

                accept = false;
            }

            view = null;
        }
    }
}
