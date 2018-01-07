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
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnVoiceRecord;
import net.iGap.proto.ProtoGlobal;

public class VoiceRecord {

    private MediaRecorder mediaRecorder;
    private String outputFile;
    private boolean canStop = false;
    private boolean state = false;
    private ImageView imgPicRecord;
    private TimerTask timertask;
    private Timer timer;
    private Timer secondTimer;
    private Timer millisecondTimer;
    private int second = 0;
    private int minute = 0;
    private TextView txtTimeRecord;
    private LinearLayout layoutMicLock;
    private int firstX;
    private boolean cancel = false;
    private int distanceToCancel = 130;
    private TextView txt_slide_to_cancel;
    private String itemTag = "";
    private View layoutAttach;
    private View layoutMic;
    private OnVoiceRecord onVoiceRecordListener;
    private TextView txtMillisecond;
    private int milliSecond = 0;
    private MaterialDesignTextView btnMicLayout;
    private MaterialDesignTextView btnLock;
    private boolean continuePlay;
    private boolean isHandFree = false;
    private int firstY;

    public VoiceRecord(Context context, View layoutMic, View layoutAttach, OnVoiceRecord listener) {
        imgPicRecord = (ImageView) layoutMic.findViewById(R.id.img_pic_record);
        txtTimeRecord = (TextView) layoutMic.findViewById(R.id.txt_time_record);
        txtMillisecond = (TextView) layoutMic.findViewById(R.id.txt_time_mili_secend);
        layoutMicLock = (LinearLayout) layoutMic.findViewById(R.id.lmr_layout_mic);
        txt_slide_to_cancel = (TextView) layoutMic.findViewById(R.id.txt_slideto_cancel);
        btnMicLayout = (MaterialDesignTextView) layoutMic.findViewById(R.id.lmr_btn_mic_layout);
        btnLock = (MaterialDesignTextView) layoutMic.findViewById(R.id.lmr_txt_Lock);
        AndroidUtils.setBackgroundShapeColor(btnMicLayout, Color.parseColor(G.appBarColor));
        this.layoutAttach = layoutAttach;
        this.layoutMic = layoutMic;
        this.onVoiceRecordListener = listener;
        distanceToCancel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, context.getResources().getDisplayMetrics());

        txt_slide_to_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel = true;
                reset();
            }
        });

        btnMicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isHandFree) {
                    reset();
                }
            }
        });
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public String getItemTag() {
        return itemTag;
    }

    private void stopVoiceRecord() {
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

        if (secondTimer == null) {

            secondTimer = new Timer();
            secondTimer.schedule(new TimerTask() {

                @Override
                public void run() {

                    second++;
                    if (second >= 60) {
                        minute++;
                        second %= 60;
                    }
                    if (minute >= 60) {
                        minute %= 60;
                    }
                    if (second >= 1) {
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
                            if (second < 10) {
                                s += "0" + second;
                            } else {
                                s += second;
                            }

                            txtTimeRecord.setText(s);
                        }
                    });
                }
            }, 1000, 1000);
        }

        if (millisecondTimer == null) {
            millisecondTimer = new Timer();
            millisecondTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    milliSecond++;
                    if (milliSecond >= 99) milliSecond = 1;
                    txtMillisecond.post(new Runnable() {
                        @Override
                        public void run() {
                            if (milliSecond < 10) {
                                txtMillisecond.setText(":0" + milliSecond + "");
                            } else {
                                txtMillisecond.setText(":" + milliSecond + "");
                            }
                        }
                    });
                }
            }, 10, 10);
        }
    }

    public void dispatchTouchEvent(MotionEvent event) {

        if (isHandFree) {
            return;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startMoving((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (itemTag.equals("ivVoice")) moving((int) event.getX(), (int) event.getY());
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

    private void startMoving(int x, int y) {
        isHandFree = false;
        firstY = y;
        firstX = x;
        cancel = false;
    }

    private void moving(int x, int y) {
        int MoveY = Math.abs(firstY - y);
        int moveX = Math.abs(firstX - x);

        if (MoveY > 100) {
            lockVoice();
            return;
        }

        if (moveX > distanceToCancel) {
            cancel = true;
            reset();
        } else {
            txt_slide_to_cancel.setAlpha(((float) (distanceToCancel - moveX) / distanceToCancel));
            layoutMicLock.setPadding(0, 0, 0, MoveY);
            txt_slide_to_cancel.setPadding(0, 0, moveX, 0);
        }
    }

    private void lockVoice() {
        isHandFree = true;
        layoutMicLock.setPadding(0, 0, 0, 0);
        txt_slide_to_cancel.setPadding(0, 0, 50, 0);
        txt_slide_to_cancel.setAlpha(1);
        txt_slide_to_cancel.setText(R.string.cancel);
        txt_slide_to_cancel.setTextColor(G.context.getResources().getColor(R.color.red));
        txt_slide_to_cancel.setTypeface(Typeface.DEFAULT_BOLD);
        btnMicLayout.setText(R.string.md_send_button);
        btnMicLayout.setTextColor(G.context.getResources().getColor(R.color.white));
        btnMicLayout.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp16));
        btnLock.setText(R.string.md_igap_lock);
        btnLock.setTextColor(G.context.getResources().getColor(R.color.red));
    }

    private void reset() {

        layoutMicLock.setPadding(0, 0, 0, 0);
        txt_slide_to_cancel.setPadding(0, 0, 0, 0);
        txt_slide_to_cancel.setText(R.string.slide_to_cancel_en);
        txt_slide_to_cancel.setAlpha(1);
        txt_slide_to_cancel.setTextColor(G.context.getResources().getColor(R.color.gray));
        txt_slide_to_cancel.setTypeface(Typeface.DEFAULT);
        btnMicLayout.setText(R.string.md_voice_message_microphone_button);
        btnMicLayout.setTextColor(G.context.getResources().getColor(R.color.black_register));
        btnMicLayout.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp26));
        btnLock.setText(R.string.md_igap_lock_open_outline);
        btnLock.setTextColor(G.context.getResources().getColor(R.color.gray_4c));

        itemTag = "";
        layoutAttach.setVisibility(View.VISIBLE);
        layoutMic.setVisibility(View.GONE);

        isHandFree = false;

        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (secondTimer != null) {
            secondTimer.cancel();
            secondTimer.purge();
            secondTimer = null;
        }
        if (millisecondTimer != null) {
            millisecondTimer.cancel();
            millisecondTimer.purge();
            millisecondTimer = null;
        }

        second = 0;
        minute = 0;
        txtTimeRecord.setText("00:00");

        if (canStop) {
            stopVoiceRecord();
        }
        if (cancel) {
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
