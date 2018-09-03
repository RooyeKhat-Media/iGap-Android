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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperPublicMethod;
import net.iGap.interfaces.ISignalingCallBack;
import net.iGap.module.AndroidUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.CallState;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestSignalingGetLog;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestUserInfo;
import net.iGap.webrtc.WebRTC;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class ActivityCallViewModel {

    public static boolean isConnected = false;
    public static TextView txtTimeChat, txtTimerMain;
    public Vibrator vibrator;
    public ObservableField<String> cllBackBtnSpeaker = new ObservableField<>(G.context.getResources().getString(R.string.md_Mute));
    public ObservableField<String> cllBackBtnMic = new ObservableField<>(G.context.getResources().getString(R.string.md_mic));
    public ObservableField<String> callBackTxtTimer = new ObservableField<>("00:00");
    public ObservableField<String> callBackTxtStatus = new ObservableField<>("Status");
    public ObservableField<String> callBackTxtName = new ObservableField<>("Name");
    public ObservableInt txtAviVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt layoutOptionVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt txtTimerVisibility = new ObservableInt(View.GONE);
    public ObservableInt layoutChatCallVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt layoutAnswerCallVisibility = new ObservableInt(View.VISIBLE);
    private boolean isIncomingCall = false;
    private long userId;
    private boolean isSendLeave = false;
    private int musicVolum = 0;
    private boolean isMuteAllMusic = false;
    private Timer secendTimer;
    private int secend = 0;
    private int minute = 0;
    private MediaPlayer player;
    private MediaPlayer ringtonePlayer;
    private Context context;
    private ActivityCallBinding activityCallBinding;
    private boolean isFinish = false;


    public ActivityCallViewModel(Context context, long userId, boolean isIncomingCall, ActivityCallBinding activityCallBinding) {

        this.context = context;
        this.userId = userId;
        this.isIncomingCall = isIncomingCall;
        this.activityCallBinding = activityCallBinding;
        getInfo();

    }

    public void onClickBtnChat(View v) {

        if (!isConnected && isIncomingCall) {
            endCall();

        }

        HelperPublicMethod.goToChatRoom(userId, null, null);

    }

    public void onClickBtnMic(View v) {

        if (cllBackBtnMic.get().toString().equals(G.fragmentActivity.getResources().getString(R.string.md_mic))) {
            cllBackBtnMic.set(G.fragmentActivity.getResources().getString(R.string.md_mic_off));
            WebRTC.muteSound();
        } else {
            cllBackBtnMic.set(G.fragmentActivity.getResources().getString(R.string.md_mic));
            WebRTC.unMuteSound();
        }
    }

    public void onClickBtnSpeaker(View v) {
        if (cllBackBtnSpeaker != null && cllBackBtnSpeaker.get() != null && G.fragmentActivity != null) {
            if (cllBackBtnSpeaker.get().equals(G.fragmentActivity.getResources().getString(R.string.md_Mute))) {
                cllBackBtnSpeaker.set(G.fragmentActivity.getResources().getString(R.string.md_unMuted));
                setSpeakerphoneOn(true);
            } else {
                cllBackBtnSpeaker.set(G.fragmentActivity.getResources().getString(R.string.md_Mute));
                setSpeakerphoneOn(false);
            }
        }
    }


    private void getInfo() {
        initComponent();
        initCallBack();
        muteMusic();
    }


    private void initComponent() {
        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.pauseSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
            }
        }


        if (isIncomingCall) {
            playRingtone();
            callBackTxtStatus.set(G.context.getResources().getString(R.string.incoming_call));
            layoutOptionVisibility.set(View.GONE);
        } else {

            playSound(R.raw.igap_signaling);
            callBackTxtStatus.set(G.context.getResources().getString(R.string.signaling));
            layoutAnswerCallVisibility.set(View.GONE);
            layoutChatCallVisibility.set(View.GONE);

        }

        //setAnimation();
        setPicture();
    }

    private void initCallBack() {
        G.iSignalingCallBack = new ISignalingCallBack() {
            @Override
            public void onStatusChanged(final CallState callState) {
                //G.handler(new Runnable() {
                //    @Override
                //    public void run() {
                callBackTxtStatus.set(getTextString(callState));
                switch (callState) {
                    case RINGING:
                        playSound(R.raw.igap_ringing);
                        txtAviVisibility.set(View.VISIBLE);
                        break;
                    case INCAMING_CALL:
                        txtAviVisibility.set(View.VISIBLE);
                        break;
                    case CONNECTING:
                        txtAviVisibility.set(View.VISIBLE);
                        break;
                    case CONNECTED:
                        txtAviVisibility.set(View.GONE);

                        layoutOptionVisibility.set(View.VISIBLE);
                        if (!isConnected) {
                            isConnected = true;

                            playSound(R.raw.igap_connect);

                            G.handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    cancelRingtone();
                                    startTimer();
                                }
                            }, 350);
                        }

                        break;
                    case DISCONNECTED:
                        txtAviVisibility.set(View.GONE);
                        playSound(R.raw.igap_discounect);
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopTimer();
                                endVoiceAndFinish();
                            }
                        }, 1000);
                        if (!isSendLeave) {
                            new RequestSignalingLeave().signalingLeave();
                        }
                        isConnected = false;
                        break;
                    case BUSY:
                        playSound(R.raw.igap_busy);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case REJECT:
                        playSound(R.raw.igap_discounect);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case FAILD:
                        playSound(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
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
                        txtAviVisibility.set(View.GONE);
                        break;
                    case UNAVAILABLE:
                        playSound(R.raw.igap_noresponse);
                        txtAviVisibility.set(View.GONE);
                        break;
                    case TOO_LONG:
                        playSound(R.raw.igap_discounect);
                        txtAviVisibility.set(View.GONE);
                        break;
                }
                //    }
                //});
            }
        };
    }

    private String getTextString(CallState callState) {

        String result = "";

        switch (callState) {

            case SIGNALING:
                result = G.context.getResources().getString(R.string.signaling);
                break;
            case INCAMING_CALL:
                result = G.context.getResources().getString(R.string.incoming_call);
                break;
            case RINGING:
                result = G.context.getResources().getString(R.string.ringing);
                break;
            case CONNECTING:
                result = G.context.getResources().getString(R.string.connecting_call);
                break;
            case CONNECTED:
                result = G.context.getResources().getString(R.string.connected);
                break;
            case DISCONNECTED:
                result = G.context.getResources().getString(R.string.disconnected);
                break;
            case FAILD:
                result = G.context.getResources().getString(R.string.faild);
                break;
            case REJECT:
                result = G.context.getResources().getString(R.string.reject);
                break;
            case BUSY:
                result = G.context.getResources().getString(R.string.busy);
                break;
            case NOT_ANSWERED:
                result = G.context.getResources().getString(R.string.not_answered);
                break;
            case UNAVAILABLE:
                result = G.context.getResources().getString(R.string.unavalable);
                break;
            case TOO_LONG:
                result = G.context.getResources().getString(R.string.too_long);
                break;
        }

        return result;
    }

    /**
     * *************** common methods ***************
     */

    /**
     * Sets the speaker phone mode.
     */
    private void setSpeakerphoneOn(boolean on) {

        AudioManager audioManager = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);

        boolean wasOn = false;
        if (audioManager != null) {
            wasOn = audioManager.isSpeakerphoneOn();
        }
        if (wasOn == on) {
            return;
        }
        if (audioManager != null) {
            audioManager.setSpeakerphoneOn(on);
        }
    }

    public void endCall() {

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

        if (ActivityCall.onFinishActivity != null) {
            ActivityCall.onFinishActivity.finishActivity();
        }

        if (G.iCallFinishChat != null) {
            G.iCallFinishChat.onFinish();
        }

        if (G.iCallFinishMain != null) {
            G.iCallFinishMain.onFinish();
        }

        if (MusicPlayer.pauseSoundFromIGapCall) {
            MusicPlayer.pauseSoundFromIGapCall = false;
            MusicPlayer.playSound();
        }

        txtTimeChat = txtTimerMain = null;

    }

    private void startTimer() {


        txtTimerVisibility.set(View.VISIBLE);
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

                activityCallBinding.fcrTxtTimer.post(new Runnable() {

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

                        callBackTxtTimer.set(s);

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

        txtTimerVisibility.set(View.GONE);

        if (secendTimer != null) {
            secendTimer.cancel();
            secendTimer = null;
        }
    }

    //private void setAnimation() {
    //    Animation animation = AnimationUtils.loadAnimation(G.context.getApplicationContext(), R.anim.translate_enter_down_circke_button);
    //    layoutCaller.startAnimation(animation);
    //}

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
            callBackTxtName.set(registeredInfo.getDisplayName());
            RealmAttachment av = registeredInfo.getLastAvatar().getFile();
            ProtoFileDownload.FileDownload.Selector se = ProtoFileDownload.FileDownload.Selector.FILE;
            String dirPath = AndroidUtils.getFilePathWithCashId(av.getCacheId(), av.getName(), G.DIR_IMAGE_USER, false);

            HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE,System.currentTimeMillis() + "", av.getToken(), av.getUrl(), av.getCacheId(), av.getName(), av.getSize(), se, dirPath, 4, new HelperDownloadFile.UpdateListener() {
                @Override
                public void OnProgress(final String path, int progress) {
                    if (progress == 100) {
                        if (activityCallBinding.fcrImvBackground != null) {
                            activityCallBinding.fcrImvBackground.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), activityCallBinding.fcrImvBackground);
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
            AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
            if (am == null) {
                return;
            }
            int result = am.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {

                }
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                musicVolum = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                am.setStreamMute(AudioManager.STREAM_MUSIC, true);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                isMuteAllMusic = true;
            }
        }
    }

    private void unMuteMusic() {

        if (isMuteAllMusic) {
            AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolum, 0);
                am.setStreamMute(AudioManager.STREAM_MUSIC, false);
                isMuteAllMusic = false;
            }
        }
    }

    public void playRingtone() {
        boolean canPlay = false;
        AudioManager am = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                canPlay = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                canPlay = false;

                vibrator = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
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
                ringtonePlayer.setDataSource(G.context, alert);

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

        setSpeakerphoneOn(false);

        if (player == null) {
            try {
                player = new MediaPlayer();
                player.setDataSource(context, Uri.parse("android.resource://" + G.context.getPackageName() + "/" + resSound));

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
                player.setDataSource(context, Uri.parse("android.resource://" + G.context.getPackageName() + "/" + resSound));

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
            e.printStackTrace();
        }

        try {

            if (vibrator != null) {
                vibrator.cancel();
                vibrator = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }

        stopRingAnimation();
    }

    private void startRingAnimation() {

        final int start = 1600;
        final int duration = 700;

        final Animation animation1 = new TranslateAnimation(0, 0, 0, -G.context.getResources().getDimension(R.dimen.dp32));
        animation1.setStartOffset(start);
        animation1.setDuration(duration);
        //animation1.setRepeatMode(Animation.RESTART);
        //animation1.setRepeatCount(Animation.INFINITE);
        animation1.setInterpolator(new BounceInterpolator());

        final Animation animation2 = new TranslateAnimation(0, 0, -G.context.getResources().getDimension(R.dimen.dp32), 0);
        animation2.setDuration(duration);
        animation2.setInterpolator(new BounceInterpolator());

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                activityCallBinding.fcrBtnCall.startAnimation(animation2);
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

                activityCallBinding.fcrBtnCall.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activityCallBinding.fcrBtnCall.startAnimation(animation1);
                    }
                }, start);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        activityCallBinding.fcrBtnCall.startAnimation(animation1);

    }

    private void stopRingAnimation() {

        try {

            if (activityCallBinding.fcrBtnCall != null) {
                activityCallBinding.fcrBtnCall.clearAnimation();
            }

        } catch (Exception e) {

            Log.e("debug", "activityCall     stopRingAnimation      " + e.toString());
        }


    }

    //*****************************  distance sensor  **********************************************************

    public void onDestroy() {

        G.isInCall = false;
        G.iSignalingCallBack = null;
        G.onCallLeaveView = null;

        setSpeakerphoneOn(false);

        cancelRingtone();
        unMuteMusic();
        new RequestSignalingGetLog().signalingGetLog(0, 1);
        if (!isSendLeave) {
            new WebRTC().leaveCall();
        }
    }

    public void onLeaveView(String type) {
        isConnected = false;

        if (type.equals("error")) {

            //G.handler(new Runnable() {
            //    @Override
            //    public void run() {
            cancelRingtone();
            txtAviVisibility.set(View.GONE);
            callBackTxtStatus.set("");
            //    }
            //});

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

}
