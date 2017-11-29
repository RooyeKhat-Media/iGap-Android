/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnVoiceRecord;
import net.iGap.proto.ProtoGlobal;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceRecord {

    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean canStop = false;
    private boolean state = false;
    private ImageView imgPicRecord;
    private TimerTask timertask;
    private Timer timer;
    private Timer secendTimer;
    private Timer miliSecendTimer;
    private int secend = 0;
    private int minute = 0;
    private TextView txtTimeRecord;
    private int leftPading;
    private int Allmoving = 0;
    private LinearLayout layout3;
    private int lastX;
    private boolean cansel = false;
    private int DistanceToCancel = 130;
    private TextView txt_slide_to_cancel;
    private String itemTag = "";
    private View layoutAttach;
    private View layoutMic;
    private OnVoiceRecord onVoiceRecordListener;
    private TextView txtMilisecend;
    private int milisecend = 0;
    private MaterialDesignTextView btnMicLayout;
    private boolean continuePlay;

    private Context context;

    public VoiceRecord(Context context, View layoutMic, View layoutAttach, OnVoiceRecord listener) {

        this.context = context;

        imgPicRecord = (ImageView) layoutMic.findViewById(R.id.img_pic_record);
        txtTimeRecord = (TextView) layoutMic.findViewById(R.id.txt_time_record);
        txtMilisecend = (TextView) layoutMic.findViewById(R.id.txt_time_mili_secend);
        layout3 = (LinearLayout) layoutMic.findViewById(R.id.layout3);
        txt_slide_to_cancel = (TextView) layoutMic.findViewById(R.id.txt_slideto_cancel);
        btnMicLayout = (MaterialDesignTextView) layoutMic.findViewById(R.id.lmr_btn_mic_layout);
        AndroidUtils.setBackgroundShapeColor(btnMicLayout, Color.parseColor(G.appBarColor));

        this.layoutAttach = layoutAttach;
        this.layoutMic = layoutMic;
        this.onVoiceRecordListener = listener;

        DistanceToCancel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, context.getResources().getDisplayMetrics());
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public String getItemTag() {
        return itemTag;
    }

    public void stopVoiceRecord() {
        if (null != mediaRecorder) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                if (continuePlay) {
                    continuePlay = false;
                    MusicPlayer.playSound();
                }

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void startRecording() {

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                MusicPlayer.pauseSound();
                MusicPlayer.pauseSoundFromIGapCall = true;
                continuePlay = true;
            }
        }


        if (G.onHelperSetAction != null) {
            G.onHelperSetAction.onAction(ProtoGlobal.ClientAction.RECORDING_VOICE);
        }

        outputFile = G.DIR_AUDIOS + "/" + "record_" + HelperString.getRandomFileName(3) + ".mp3";

        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(128000);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setOutputFile(outputFile);

            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void startVoiceRecord() {

        canStop = false;
        startRecording();
        timertask = new TimerTask() {

            @Override
            public void run() {
                if (state) {

                    imgPicRecord.post(new Runnable() {

                        @Override
                        public void run() {
                            imgPicRecord.setImageResource(R.mipmap.circle_white);
                            state = false;
                        }
                    });
                } else {
                    imgPicRecord.post(new Runnable() {

                        @Override
                        public void run() {
                            imgPicRecord.setImageResource(R.mipmap.circle_red);
                            state = true;
                        }
                    });
                }
            }
        };

        if (timer == null) {
            timer = new Timer();
            timer.schedule(timertask, 100, 300);
        }

        if (secendTimer == null) {

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
                    if (secend >= 1) {
                        canStop = true;
                    }

                    txtTimeRecord.post(new Runnable() {

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

                            txtTimeRecord.setText(s);
                        }
                    });
                }
            }, 1000, 1000);
        }

        if (miliSecendTimer == null) {
            miliSecendTimer = new Timer();
            miliSecendTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    milisecend++;
                    if (milisecend >= 99) milisecend = 1;
                    txtMilisecend.post(new Runnable() {
                        @Override
                        public void run() {
                            if (milisecend < 10) {
                                txtMilisecend.setText(":0" + milisecend + "");
                            } else {
                                txtMilisecend.setText(":" + milisecend + "");
                            }
                        }
                    });
                }
            }, 10, 10);
        }
    }

    public void dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startMoving((int) event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                if (itemTag.equals("ivVoice")) moving((int) event.getX());
                break;
            case MotionEvent.ACTION_UP:
                if (itemTag.equals("ivVoice")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reset();
                        }
                    }, 100);
                }
                break;
        }
    }

    private void startMoving(int x) {

        if (layout3 != null) leftPading = layout3.getPaddingRight();

        lastX = x;
        cansel = false;
    }

    private void moving(int x) {
        int i = lastX - x;

        if (i > 0 || Allmoving > 0) {
            Allmoving += i;
            txt_slide_to_cancel.setAlpha(((float) (DistanceToCancel - Allmoving) / DistanceToCancel));
            layout3.setPadding(0, 0, layout3.getPaddingRight() + i, 0);
            lastX = x;

            if (Allmoving >= DistanceToCancel) {
                cansel = true;
                reset();
            }
        }
    }

    private void reset() {
        layout3.setPadding(0, 0, leftPading, 0);
        txt_slide_to_cancel.setAlpha(1);
        Allmoving = 0;
        itemTag = "";
        layoutAttach.setVisibility(View.VISIBLE);
        layoutMic.setVisibility(View.GONE);

        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (secendTimer != null) {
            secendTimer.cancel();
            secendTimer.purge();
            secendTimer = null;
        }
        if (miliSecendTimer != null) {
            miliSecendTimer.cancel();
            miliSecendTimer.purge();
            miliSecendTimer = null;
        }

        secend = 0;
        minute = 0;
        txtTimeRecord.setText("00:00");

        if (canStop) {
            stopVoiceRecord();
        }
        if (cansel) {
            if (onVoiceRecordListener != null) {
                onVoiceRecordListener.onVoiceRecordCancel();
            }
        } else {
            if (canStop) {
                try {
                    if (onVoiceRecordListener != null) {
                        onVoiceRecordListener.onVoiceRecordDone(outputFile);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (onVoiceRecordListener != null) {
                    onVoiceRecordListener.onVoiceRecordCancel();
                }
            }
        }
    }
}
