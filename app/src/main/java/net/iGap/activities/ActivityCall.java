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
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityCallBinding;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnCallLeaveView;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.ActivityCallViewModel;
import net.iGap.webrtc.WebRTC;

import java.io.IOException;

import static net.iGap.G.context;

public class ActivityCall extends ActivityEnhanced implements OnCallLeaveView {

    public static final String USER_ID_STR = "USER_ID";
    public static final String INCOMING_CALL_STR = "INCOMING_CALL_STR";
    private static final int SENSOR_SENSITIVITY = 4;

    //public static TextView txtTimeChat, txtTimerMain;
    public static boolean isGoingfromApp = false;
    public static View stripLayoutChat;
    public static View stripLayoutMain;
    public static boolean isNearDistance = false;
    public static OnFinishActivity onFinishActivity;
    boolean isIncomingCall = false;
    long userId;
    boolean canClick = false;
    boolean canTouch = false;
    boolean down = false;
    VerticalSwipe verticalSwipe;
    LinearLayout layoutCaller;
    FrameLayout layoutAnswer;
    MaterialDesignTextView btnCircleChat;
    MaterialDesignTextView btnEndCall;
    MaterialDesignTextView btnAnswer;
    MediaPlayer player;
    MediaPlayer ringtonePlayer;
    SensorEventListener sensorEventListener;
    HeadsetPluginReciver headsetPluginReciver;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private ActivityCallViewModel activityCallViewModel;
    private ActivityCallBinding activityCallBinding;

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
    protected void onDestroy() {
        super.onDestroy();

        if (activityCallViewModel != null) {
            activityCallViewModel.onDestroy();
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

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (preferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false)) {
            this.setTheme(R.style.Material_blackCustom);
        } else {
            this.setTheme(R.style.Material_lightCustom);
        }

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
            HelperPermission.getMicroPhonePermission(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {


                    userId = getIntent().getExtras().getLong(USER_ID_STR);
                    isIncomingCall = getIntent().getExtras().getBoolean(INCOMING_CALL_STR);

                    //setContentView(R.layout.activity_call);
                    activityCallBinding = DataBindingUtil.setContentView(ActivityCall.this, R.layout.activity_call);
                    activityCallViewModel = new ActivityCallViewModel(ActivityCall.this, userId, isIncomingCall, activityCallBinding);
                    activityCallBinding.setActivityCallViewModel(activityCallViewModel);


                    initComponent();
                    //initCallBack();

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

        onFinishActivity = new OnFinishActivity() {
            @Override
            public void finishActivity() {
                finish();
            }
        };
    }

    //***************************************************************************************

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (verticalSwipe != null) {
            verticalSwipe.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLeaveView(String type) {
        if (activityCallViewModel != null) {
            activityCallViewModel.onLeaveView(type);
        }
    }

    private void initComponent() {
        verticalSwipe = new VerticalSwipe();
        layoutCaller = activityCallBinding.fcrLayoutCaller;

        /**
         * *************** layoutCallEnd ***************
         */

        final FrameLayout layoutCallEnd = activityCallBinding.fcrLayoutChatCallEnd;
        btnEndCall = activityCallBinding.fcrBtnEnd;

        if (isIncomingCall) {
            layoutCallEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (canClick) {
                        layoutCallEnd.setVisibility(View.INVISIBLE);
                        activityCallViewModel.endCall();
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
                    activityCallViewModel.endCall();
                    btnEndCall.setVisibility(View.GONE);
                }
            });
        }

        /**
         * *************** layoutChat ***************
         */

        final FrameLayout layoutChat = activityCallBinding.fcrLayoutChatCall;
        btnCircleChat = activityCallBinding.fcrBtnCircleChat;

        if (isIncomingCall) {
            layoutChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canClick) {

                        activityCallViewModel.onClickBtnChat(v);
                        //btnChat.performClick();
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

        layoutAnswer = activityCallBinding.fcrLayoutAnswerCall;
        btnAnswer = activityCallBinding.fcrBtnCall;

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

        setAnimation();

    }

    /**
     * *************** common methods ***************
     */

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
                    activityCallViewModel.endCall();
                    btnEndCall.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_enter_down_circke_button);
        layoutCaller.startAnimation(animation);
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

            if (activityCallViewModel.vibrator != null) {
                activityCallViewModel.vibrator.cancel();
                activityCallViewModel.vibrator = null;
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

    //*****************************  distance sensor  **********************************************************

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

    private void registerSensor() {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (activityCallBinding != null) {
                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                        boolean newIsNear = Math.abs(event.values[0]) < Math.min(event.sensor.getMaximumRange(), 3);
                        if (newIsNear != isNearDistance) {
                            isNearDistance = newIsNear;
                            if (isNearDistance) {
                                // near
                                screenOff();
                            } else {
                                //far
                                screenOn();
                            }
                        }
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

        enableDisableViewGroup((ViewGroup) activityCallBinding.acLayoutCallRoot, true);
    }

    private void screenOff() {

        if (ActivityCallViewModel.isConnected) {

            WindowManager.LayoutParams params = this.getWindow().getAttributes();

            params.screenBrightness = 0;
            this.getWindow().setAttributes(params);

            enableDisableViewGroup((ViewGroup) activityCallBinding.acLayoutCallRoot, false);
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

    private void setUpSwap(View view) {
        if (!down) {
            verticalSwipe.setView(view);
            canTouch = true;
            down = true;

            stopRingAnimation();
        }
    }

    //***************************************************************************************

    public interface OnFinishActivity {
        void finishActivity();
    }

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
                    activityCallViewModel.playRingtone();
                }
            }
        }
    }

    class VerticalSwipe {

        boolean accept = false;
        private int AllMoving = 0;
        private int lastY;
        private int DistanceToAccept = (int) G.context.getResources().getDimension(R.dimen.dp120);
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
