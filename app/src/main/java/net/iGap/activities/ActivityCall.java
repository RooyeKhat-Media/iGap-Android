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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperPermision;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.CallState;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestSignalingGetLog;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestUserInfo;
import net.iGap.webrtc.WebRTC;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class ActivityCall extends ActivityEnhanced implements OnCallLeaveView {

    public static final String USER_ID_STR = "USER_ID";
    public static final String INCOMING_CALL_STR = "INCOMING_CALL_STR";

    public static boolean isGoingfromApp = false;

    public static TextView txtTimeChat, txtTimerMain;

    boolean isIncomingCall = false;
    long userId;

    boolean canClick = false;
    boolean canTouch = false;
    boolean down = false;
    boolean isSendLeave = false;
    public static boolean isConnected = false;

    Vibrator vibrator;
    int musicVolum = 0;

    boolean isMuteAllMusic = false;

    private Timer secendTimer;
    private int secend = 0;
    private int minute = 0;

    VerticalSwipe verticalSwipe;
    TextView txtName;
    TextView txtStatus;
    TextView avLoadingIndicatorView;
    ImageView userCallerPicture;
    LinearLayout layoutCaller;
    LinearLayout layoutOption;
    FrameLayout layoutAnswer;
    MaterialDesignTextView btnCircleChat;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnAnswer;
    MaterialDesignTextView btnMic;
    MaterialDesignTextView btnChat;
    MaterialDesignTextView btnSpeaker;
    TextView txtTimer;
    MediaPlayer player;
    MediaPlayer ringtonePlayer;

    private SensorManager mSensorManager;
    private Sensor mProximity;
    SensorEventListener sensorEventListener;
    private static final int SENSOR_SENSITIVITY = 4;

    HeadsetPluginReciver headsetPluginReciver;

    public static View stripLayoutChat;
    public static View stripLayoutMain;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        G.isInCall = false;
        G.iSignalingCallBack = null;
        G.onCallLeaveView = null;

        cancelRingtone();

        unMuteMusic();

        new RequestSignalingGetLog().signalingGetLog(0, 1);

        if (!isSendLeave) {
            new WebRTC().leaveCall();
        }
    }

    @Override
    public void onBackPressed() throws IllegalStateException {
        //super.onBackPressed();
        //
        //if (!isSendLeave) {
        //    new WebRTC().leaveCall();
        //}

        startActivity(new Intent(ActivityCall.this, ActivityMain.class));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD | LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);

        if (isGoingfromApp) {
            isGoingfromApp = false;
        } else {

            G.isInCall = false;

            Intent intent = new Intent(this, ActivityMain.class);
            startActivity(intent);
            finish();
            return;
        }

        G.isInCall = true;


        try {
            HelperPermision.getMicroPhonePermission(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {

                    setContentView(R.layout.activity_call);

                    userId = getIntent().getExtras().getLong(USER_ID_STR);
                    isIncomingCall = getIntent().getExtras().getBoolean(INCOMING_CALL_STR);

                    initComponent();
                    initCallBack();

                    G.onCallLeaveView = ActivityCall.this;

                    if (!isIncomingCall) {
                        new WebRTC().createOffer(userId);
                    }
                }

                @Override
                public void deny() {
                    G.isInCall = false;
                    finish();
                    new WebRTC().leaveCall();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerSensor();

        headsetPluginReciver = new HeadsetPluginReciver();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (verticalSwipe != null) {
            verticalSwipe.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    //***************************************************************************************

    @Override
    public void onLeaveView(String type) {

        isConnected = false;

        if (type.equals("error")) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelRingtone();
                    avLoadingIndicatorView.setVisibility(View.GONE);
                    txtStatus.setText("");
                }
            });

            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    endVoiceAndFinish();
                }
            }, 2000);
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    endVoiceAndFinish();
                }
            }, 1000);
        }
    }

    private void initCallBack() {
        G.iSignalingCallBack = new ISignalingCallBack() {
            @Override
            public void onStatusChanged(final CallState callState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtStatus.setText(getTextString(callState));

                        switch (callState) {

                            case RINGING:
                                playSound(R.raw.igap_ringing);
                                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                                break;
                            case INCAMING_CALL:
                                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                                break;
                            case CONNECTING:
                                avLoadingIndicatorView.setVisibility(View.VISIBLE);
                                break;
                            case CONNECTED:
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                layoutOption.setVisibility(View.VISIBLE);

                                if (!isConnected) {
                                    isConnected = true;

                                    playSound(R.raw.igap_connect);

                                    G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            cancelRingtone();
                                            startTimer();
                                        }
                                    }, 800);
                                }

                                break;
                            case DISCONNECTED:
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                playSound(R.raw.igap_discounect);
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopTimer();
                                        endVoiceAndFinish();
                                    }
                                }, 1000);
                                new RequestSignalingLeave().signalingLeave();
                                isConnected = false;
                                break;
                            case BUSY:
                                playSound(R.raw.igap_busy);
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                break;
                            case REJECT:
                                playSound(R.raw.igap_discounect);
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                break;
                            case FAILD:
                                playSound(R.raw.igap_noresponse);
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                new RequestSignalingLeave().signalingLeave();

                                isConnected = false;
                                G.handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        stopTimer();
                                        endVoiceAndFinish();
                                    }
                                }, 500);

                                break;
                            case NOT_ANSWERED:
                                playSound(R.raw.igap_noresponse);
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                break;
                            case UNAVAILABLE:
                                playSound(R.raw.igap_noresponse);
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                break;
                            case TOO_LONG:
                                playSound(R.raw.igap_discounect);
                                avLoadingIndicatorView.setVisibility(View.GONE);
                                break;
                        }
                    }
                });
            }
        };
    }

    private String getTextString(CallState callState) {

        String result = "";

        switch (callState) {

            case SIGNALING:
                result = getResources().getString(R.string.signaling);
                break;
            case INCAMING_CALL:
                result = getResources().getString(R.string.incoming_call);
                break;
            case RINGING:
                result = getResources().getString(R.string.ringing);
                break;
            case CONNECTING:
                result = getResources().getString(R.string.connecting_call);
                break;
            case CONNECTED:
                result = getResources().getString(R.string.connected);
                break;
            case DISCONNECTED:
                result = getResources().getString(R.string.disconnected);
                break;
            case FAILD:
                result = getResources().getString(R.string.faild);
                break;
            case REJECT:
                result = getResources().getString(R.string.reject);
                break;
            case BUSY:
                result = getResources().getString(R.string.busy);
                break;
            case NOT_ANSWERED:
                result = getResources().getString(R.string.not_answered);
                break;
            case UNAVAILABLE:
                result = getResources().getString(R.string.unavalable);
                break;
            case TOO_LONG:
                result = getResources().getString(R.string.too_long);
                break;
        }

        return result;
    }

    //***************************************************************************************

    private void initComponent() {

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.pauseSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
            }
        }

        verticalSwipe = new VerticalSwipe();
        txtName = (TextView) findViewById(R.id.fcr_txt_name);
        txtStatus = (TextView) findViewById(R.id.fcr_txt_status);
        avLoadingIndicatorView = (TextView) findViewById(R.id.fcr_txt_avi);
        userCallerPicture = (ImageView) findViewById(R.id.fcr_imv_background);
        layoutCaller = (LinearLayout) findViewById(R.id.fcr_layout_caller);
        layoutOption = (LinearLayout) findViewById(R.id.fcr_layout_option);

        txtTimer = (TextView) findViewById(R.id.fcr_txt_timer);

        if (isIncomingCall) {
            txtStatus.setText(R.string.incoming_call);
        } else {
            txtStatus.setText(R.string.signaling);
        }

        /**
         * *************** layoutCallEnd ***************
         */

        final FrameLayout layoutCallEnd = (FrameLayout) findViewById(R.id.fcr_layout_chat_call_end);
        btnEndCall = (MaterialDesignTextView) findViewById(R.id.fcr_btn_end);

        if (isIncomingCall) {
            layoutCallEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (canClick) {
                        layoutCallEnd.setVisibility(View.INVISIBLE);
                        endCall();
                    }
                }
            });

            btnEndCall.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    setUpSwap(layoutCallEnd);
                    return false;
                }
            });
        } else {

            btnEndCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endCall();
                    btnEndCall.setVisibility(View.GONE);
                }
            });
        }

        /**
         * *************** layoutChat ***************
         */

        final FrameLayout layoutChat = (FrameLayout) findViewById(R.id.fcr_layout_chat_call);
        btnCircleChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_circle_chat);

        if (isIncomingCall) {
            layoutChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canClick) {
                        btnChat.performClick();
                        layoutChat.setVisibility(View.INVISIBLE);
                    }
                }
            });

            btnCircleChat.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    setUpSwap(layoutChat);
                    return false;
                }
            });
        }

        /**
         * *************** layoutAnswer ***************
         */

        layoutAnswer = (FrameLayout) findViewById(R.id.fcr_layout_answer_call);
        btnAnswer = (MaterialDesignTextView) findViewById(R.id.fcr_btn_call);

        if (isIncomingCall) {
            layoutAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answer(layoutAnswer, layoutChat);
                }
            });

            btnAnswer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    setUpSwap(layoutAnswer);
                    return false;
                }
            });
        }

        /**
         * *********************************************
         */

        btnChat = (MaterialDesignTextView) findViewById(R.id.fcr_btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!isConnected && isIncomingCall) {
                    endCall();

                }

                HelperPublicMethod.goToChatRoom(userId, null, null);

            }
        });

        btnSpeaker = (MaterialDesignTextView) findViewById(R.id.fcr_btn_speaker);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnSpeaker != null && btnSpeaker.getText() != null && G.fragmentActivity != null) {
                    if (btnSpeaker.getText().toString().equals(G.fragmentActivity.getResources().getString(R.string.md_Mute))) {
                        btnSpeaker.setText(R.string.md_unMuted);
                        setSpeakerphoneOn(true);
                    } else {
                        btnSpeaker.setText(R.string.md_Mute);
                        setSpeakerphoneOn(false);
                    }
                }
            }
        });

        btnMic = (MaterialDesignTextView) findViewById(R.id.fcr_btn_mic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnMic.getText().toString().equals(G.fragmentActivity.getResources().getString(R.string.md_mic))) {
                    btnMic.setText(R.string.md_mic_off);
                    WebRTC.muteSound();
                } else {
                    btnMic.setText(R.string.md_mic);
                    WebRTC.unMuteSound();
                }
            }
        });

        if (isIncomingCall) {
            playRingtone();
            layoutOption.setVisibility(View.GONE);
        } else {

            playSound(R.raw.igap_signaling);

            layoutAnswer.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);

        }

        muteMusic();

        setAnimation();

        setPicture();

        //  setSpeakerphoneOn(false);
    }

    /**
     * *************** common methods ***************
     */

    /**
     * Sets the speaker phone mode.
     */
    private void setSpeakerphoneOn(boolean on) {

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        boolean wasOn = audioManager.isSpeakerphoneOn();
        if (wasOn == on) {
            return;
        }
        audioManager.setSpeakerphoneOn(on);
    }

    private void endCall() {

        G.isInCall = false;

        new WebRTC().leaveCall();
        isSendLeave = true;


        isConnected = false;

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                endVoiceAndFinish();
            }
        }, 1000);
    }

    private void endVoiceAndFinish() {

        G.isInCall = false;

        cancelRingtone();

        finish();

        if (G.iCallFinishChat != null) {
            G.iCallFinishChat.onFinish();
        }

        if (G.iCallFinishMain != null) {
            G.iCallFinishMain.onFinish();
        }

        if (MusicPlayer.pauseSoundFromIGapCall) {
            MusicPlayer.pauseSoundFromIGapCall = false;

            MusicPlayer.playSound();

            if (MusicPlayer.isVoice && MusicPlayer.isSpeakerON) {
                setSpeakerphoneOn(true);
            }
        }

        txtTimeChat = txtTimerMain = null;

    }

    private void answer(FrameLayout layoutAnswer, FrameLayout layoutChat) {
        if (canClick) {
            layoutAnswer.setVisibility(View.GONE);
            layoutChat.setVisibility(View.GONE);

            new WebRTC().createAnswer();
            cancelRingtone();

            btnEndCall.setOnTouchListener(null);

            btnEndCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    endCall();
                    btnEndCall.setVisibility(View.GONE);
                }
            });
        }
    }

    private void startTimer() {


        txtTimer.setVisibility(View.VISIBLE);
        secend = 0;
        minute = 0;

        secendTimer = new Timer();
        secendTimer.schedule(new TimerTask() {

            @Override
            public void run() {

                secend++;
                if (secend >= 60) {
                    minute++;
                    secend %= 60;
                }
                if (minute >= 60) {
                    minute %= 60;
                }

                txtTimer.post(new Runnable() {

                    @Override
                    public void run() {
                        String s = "";
                        if (minute < 10) {
                            s += "0" + minute;
                        } else {
                            s += minute;
                        }
                        s += ":";
                        if (secend < 10) {
                            s += "0" + secend;
                        } else {
                            s += secend;
                        }

                        if (HelperCalander.isPersianUnicode) {
                            s = HelperCalander.convertToUnicodeFarsiNumber(s);
                        }

                        txtTimer.setText(s);

                        if (txtTimeChat != null) {
                            txtTimeChat.setText(s);
                        }

                        if (txtTimerMain != null) {
                            txtTimerMain.setText(s);
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    private void stopTimer() {

        txtTimeChat = txtTimerMain = null;

        txtTimer.setVisibility(View.GONE);

        if (secendTimer != null) {
            secendTimer.cancel();
            secendTimer = null;
        }
    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_enter_down_circke_button);
        layoutCaller.startAnimation(animation);
    }

    private void setPicture() {
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);

        if (registeredInfo != null) {
            loadOrDownloadPicture(registeredInfo);
        } else {
            new RequestUserInfo().userInfo(userId);
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Realm realm = Realm.getDefaultInstance();
                    RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);

                    if (registeredInfo != null) {
                        loadOrDownloadPicture(registeredInfo);
                    }
                    realm.close();
                }
            }, 3000);
        }

        realm.close();
    }

    private void loadOrDownloadPicture(RealmRegisteredInfo registeredInfo) {

        try {

            txtName.setText(registeredInfo.getDisplayName());

            RealmAttachment av = registeredInfo.getLastAvatar().getFile();

            ProtoFileDownload.FileDownload.Selector se = ProtoFileDownload.FileDownload.Selector.FILE;
            String dirPath = AndroidUtils.getFilePathWithCashId(av.getCacheId(), av.getName(), G.DIR_IMAGE_USER, false);

            HelperDownloadFile.startDownload(System.currentTimeMillis() + "", av.getToken(), av.getCacheId(), av.getName(), av.getSize(), se, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, int progress) {

                    if (progress == 100) {

                        if (userCallerPicture != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), userCallerPicture);
                                }
                            });
                        }


                    }
                }

                @Override
                public void OnError(String token) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void muteMusic() {

        if (!isMuteAllMusic) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            musicVolum = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            am.setStreamMute(AudioManager.STREAM_MUSIC, true);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            isMuteAllMusic = true;
        }
    }

    private void unMuteMusic() {

        if (isMuteAllMusic) {
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolum, 0);
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
            isMuteAllMusic = false;
        }
    }

    private void playRingtone() {
        boolean canPlay = false;
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                canPlay = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                canPlay = false;

                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 100, 1000};
                vibrator.vibrate(pattern, 0);

                break;
            case AudioManager.RINGER_MODE_NORMAL:
                canPlay = true;
                break;
        }

        if (am.isWiredHeadsetOn()) {
            canPlay = true;
        }

        if (canPlay) {

            try {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                ringtonePlayer = new MediaPlayer();
                ringtonePlayer.setDataSource(ActivityCall.this, alert);

                if (am.isWiredHeadsetOn()) {
                    ringtonePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                } else {
                    ringtonePlayer.setAudioStreamType(AudioManager.STREAM_RING);
                }

                ringtonePlayer.setLooping(true);
                ringtonePlayer.prepare();
                ringtonePlayer.start();
            } catch (Exception e) {
            }
        }

        startRingAnimation();
    }

    private void playSound(final int resSound) {

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);

        if (player == null) {
            try {
                player = new MediaPlayer();
                player.setDataSource(ActivityCall.this, Uri.parse("android.resource://" + getPackageName() + "/" + resSound));

                //if (audioManager.isWiredHeadsetOn()) {
                //    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //} else {
                //   player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //}
                player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                player.setLooping(true);
                player.prepare();
                player.start();
            } catch (Exception e) {
            }
        } else {

            try {
                player.reset();
                player.setDataSource(ActivityCall.this, Uri.parse("android.resource://" + getPackageName() + "/" + resSound));

                //if (audioManager.isWiredHeadsetOn()) {
                //    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //} else {
                //    player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                //}
                player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

                player.prepare();
                player.setLooping(true);
                player.start();
            } catch (Exception e) {
            }
        }
    }

    private void cancelRingtone() {

        try {
            if (ringtonePlayer != null) {
                ringtonePlayer.stop();
                ringtonePlayer.release();
                ringtonePlayer = null;
            }
        } catch (Exception e) {

        }

        try {

            if (vibrator != null) {
                vibrator.cancel();
                vibrator = null;
            }
        } catch (Exception e) {

        }

        try {
            if (player != null) {
                if (player.isPlaying()) {
                    player.stop();
                }

                player.release();
                player = null;
            }
        } catch (Exception e) {

        }

        stopRingAnimation();
    }

    private void startRingAnimation() {

        final int start = 1600;
        final int duration = 700;

        final Animation animation1 = new TranslateAnimation(0, 0, 0, -getResources().getDimension(R.dimen.dp32));
        animation1.setStartOffset(start);
        animation1.setDuration(duration);
        //animation1.setRepeatMode(Animation.RESTART);
        //animation1.setRepeatCount(Animation.INFINITE);
        animation1.setInterpolator(new BounceInterpolator());

        final Animation animation2 = new TranslateAnimation(0, 0, -getResources().getDimension(R.dimen.dp32), 0);
        animation2.setDuration(duration);
        animation2.setInterpolator(new BounceInterpolator());

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnAnswer.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                btnAnswer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnAnswer.startAnimation(animation1);
                    }
                }, start);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //AnimationSet animationSet = new AnimationSet(true);
        //animationSet.addAnimation(animation1);
        //animationSet.addAnimation(animation2);

        btnAnswer.startAnimation(animation1);

        //AnimationSet animationSet = new AnimationSet(true);
        //
        //TranslateAnimation a = new TranslateAnimation(Animation.ABSOLUTE,200, Animation.ABSOLUTE,200, Animation.ABSOLUTE,200, Animation.ABSOLUTE,200);
        //a.setDuration(1000);
        //
        //RotateAnimation r = new RotateAnimation(0f, -90f,200,200);
        //r.setStartOffset(1000);
        //r.setDuration(1000);
        //
        //animationSet.addAnimation(a);
        //animationSet.addAnimation(r);
        //
        //btnAnswer.setAnimation(animationSet);

        //Animation animation3 = new TranslateAnimation(0, 0, 0, -getResources().getDimension(R.dimen.dp32));
        //animation3.setStartOffset(start + 150);
        //animation3.setDuration(duration - 150);
        //animation3.setRepeatMode(Animation.RESTART);
        //animation3.setInterpolator(new BounceInterpolator());
        //animation3.setRepeatCount(Animation.INFINITE);
        //btnCircleChat.setAnimation(animation3);
        //
        //Animation animation2 = new TranslateAnimation(0, 0, 0, -getResources().getDimension(R.dimen.dp32));
        //animation2.setStartOffset(start + 300);
        //animation2.setDuration(duration - 300);
        //animation2.setInterpolator(new DecelerateInterpolator());
        //animation2.setRepeatMode(Animation.RESTART);
        //animation2.setRepeatCount(Animation.INFINITE);
        //btnEndCall.setAnimation(animation2);
    }

    private void stopRingAnimation() {

        try {

            if (btnAnswer != null) {
                btnAnswer.clearAnimation();
            }

            // btnEndCall.clearAnimation();
            // btnCircleChat.clearAnimation();

        } catch (Exception e) {

            Log.e("debug", "activityCall     stopRingAnimation      " + e.toString());
        }


    }

    //*****************************  distance sensor  **********************************************************

    private void registerSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                        // near
                        screenOff();
                    } else {
                        //far
                        screenOn();
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    private void screenOn() {

        WindowManager.LayoutParams params = this.getWindow().getAttributes();

        params.screenBrightness = 1;
        this.getWindow().setAttributes(params);

        enableDisableViewGroup((ViewGroup) findViewById(R.id.ac_layout_call_root), true);
    }

    private void screenOff() {

        if (isConnected) {

            WindowManager.LayoutParams params = this.getWindow().getAttributes();

            params.screenBrightness = 0;
            this.getWindow().setAttributes(params);

            enableDisableViewGroup((ViewGroup) findViewById(R.id.ac_layout_call_root), false);
        }
    }

    /**
     * Enables/Disables all child views in a view group.
     *
     * @param viewGroup the view group
     * @param enabled   <code>true</code> to enable, <code>false</code> to disable
     *                  the views.
     */
    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        if (viewGroup != null) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = viewGroup.getChildAt(i);
                view.setEnabled(enabled);
                if (view instanceof ViewGroup) {
                    enableDisableViewGroup((ViewGroup) view, enabled);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);

        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetPluginReciver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);

        unregisterReceiver(headsetPluginReciver);
    }

    //***************************************************************************************

    class HeadsetPluginReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                // if you need ti dermine plugin state

               /* int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        Log.d("dddddd", "Headset is unplugged");
                        break;
                    case 1:
                        Log.d("dddddd", "Headset is plugged");
                        break;
                    default:
                        Log.d("dddddd", "I have no idea what the headset state is");
                }

              */

                if (ringtonePlayer != null && ringtonePlayer.isPlaying()) {
                    cancelRingtone();
                    playRingtone();
                }
            }
        }
    }

    //***************************************************************************************

    private void setUpSwap(View view) {
        if (!down) {
            verticalSwipe.setView(view);
            canTouch = true;
            down = true;

            stopRingAnimation();
        }
    }

    class VerticalSwipe {

        private int AllMoving = 0;
        private int lastY;
        private int DistanceToAccept = (int) G.context.getResources().getDimension(R.dimen.dp120);
        boolean accept = false;
        private View view;

        public void setView(View view) {
            this.view = view;
        }

        void dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startMoving((int) event.getY());

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (canTouch) {
                        moving((int) event.getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (canTouch) {
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

            if (i > 0 || AllMoving > 0) {
                AllMoving += i;

                view.setPadding(0, 0, 0, view.getPaddingBottom() + i);

                lastY = y;
                if (AllMoving >= DistanceToAccept) {
                    accept = true;
                    reset();
                }
            }
        }

        private void reset() {
            view.setPadding(0, 0, 0, 0);
            canTouch = false;
            AllMoving = 0;

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
