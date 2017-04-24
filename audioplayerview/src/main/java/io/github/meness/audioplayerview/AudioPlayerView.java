package io.github.meness.audioplayerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import io.github.meness.audioplayerview.listeners.IAnotherPlayOrPause;
import io.github.meness.audioplayerview.listeners.OnAudioPlayerViewControllerClick;
import java.lang.ref.WeakReference;
import java.util.Formatter;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class AudioPlayerView extends FrameLayout implements IAnotherPlayOrPause {
    private MediaPlayer mPlayer;
    private ViewGroup mAnchor;
    private View mRoot;
    private SeekBar mProgress;
    private TextView mEndTime, mCurrentTime;
    private boolean mShowing;
    private boolean mDragging;
    private static final int SHOW_PROGRESS = 2;
    private static final int ACTION_CHANGED = 3;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private ImageButton mActionButton;
    private TextView mRecordedBy;
    private Handler mHandler = new MessageHandler(this);
    private OnAudioPlayerViewControllerClick mOnClickListener;
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            reset();
        }
    };

    public MediaPlayer getPlayer() {
        return mPlayer;
    }

    public void setRecordedBy(String s) {
        if (mRecordedBy != null) {
            mRecordedBy.setText(String.format(getResources().getString(R.string.audioplayerview_recorded_by_s), s));
        }
    }

    /**
     * set times colors
     *
     * @param currentTimeRes current time color res
     * @param endTimeRes end time color res
     */
    public void setTimesColor(@ColorRes int currentTimeRes, @ColorRes int endTimeRes) {
        if (mCurrentTime != null) {
            mCurrentTime.setTextColor(getResources().getColor(currentTimeRes));
        }
        if (mEndTime != null) {
            mEndTime.setTextColor(getResources().getColor(endTimeRes));
        }
    }

    public void hideRecordedBy() {
        if (mRecordedBy != null) {
            mRecordedBy.setVisibility(GONE);
        }
    }

    public void setTime(long total) {
        if (mCurrentTime != null) {
            mCurrentTime.setText("00:00");
        }
        if (mEndTime != null) {
            mEndTime.setText(stringForTime((int) total, true));
        }
    }

    private void init() {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    public AudioPlayerView(Context context) {
        super(context);
        init();
    }

    @SuppressWarnings("unused")
    public void setClickListener(OnAudioPlayerViewControllerClick listener) {
        mOnClickListener = listener;
    }

    public AudioPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AudioPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    public void onFinishInflate() {
        if (mRoot != null) initControllerView(mRoot);
    }

    public void setMediaPlayer(MediaPlayer player) {
        mPlayer = player;
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
            }
        });
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     *
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     *
     * @return The controller view.
     */
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.audioplayerview_audio_player_view, null);

        initControllerView(mRoot);

        return mRoot;
    }

    private void updateDrawables() {
        if (!mPlayer.isPlaying()) {
            mActionButton.setImageResource(R.drawable.audioplayerview_ic_media_play);
            mActionButton.setTag("play");
        } else {
            mActionButton.setImageResource(R.drawable.audioplayerview_ic_media_pause);
            mActionButton.setTag("pause");
        }
    }

    private void initControllerView(View v) {
        mRecordedBy = (TextView) v.findViewById(R.id.recordedBy);

        mActionButton = (ImageButton) v.findViewById(R.id.action);
        if (mActionButton != null) {
            mActionButton.setOnClickListener(mActionListener);
        }

        mProgress = (SeekBar) v.findViewById(R.id.seekBar);
        if (mProgress != null) {
            mProgress.setOnSeekBarChangeListener(mSeekListener);
            mProgress.setMax(100);
        }

        mEndTime = (TextView) v.findViewById(R.id.totalTime);
        mCurrentTime = (TextView) v.findViewById(R.id.elapsedTime);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        if (mPlayer == null) {
            return;
        }

    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * <p>
     * the controller until hide() is called.
     */
    public void show() {
        if (!mShowing && mAnchor != null) {
            updateProgress();
            if (mActionButton != null) {
                mActionButton.requestFocus();
            }
            disableUnsupportedButtons();

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

            mAnchor.addView(this, tlp);
            mShowing = true;
        }

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }

    /**
     * set new color for progress bar
     *
     * @param res color resource
     */
    public void setProgressColor(@ColorRes int res) {
        if (mProgress != null) {
            mProgress.getProgressDrawable().mutate().setColorFilter(getResources().getColor(res), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    /**
     * set progress bar thumb color
     *
     * @param res color res
     */
    @TargetApi(JELLY_BEAN)
    public void setProgressThumb(@ColorRes int res) {
        if (mProgress != null) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                mProgress.getThumb().mutate().setColorFilter(getResources().getColor(res), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    /**
     * set new color for drawables
     *
     * @param res color resource
     */
    public void setDrawablesColor(@ColorRes int res) {
        if (mActionButton != null) {
            mActionButton.setColorFilter(getResources().getColor(res), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    public void setRecordedByColor(@ColorRes int res) {
        if (mRecordedBy != null) {
            mRecordedBy.setTextColor(getResources().getColor(res));
        }
    }

    @SuppressWarnings("unused")
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("MediaController", "already removed");
        }
        mShowing = false;
    }

    private String stringForTime(int timeMs, boolean separator) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format((separator ? "/ " : "") + "%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format((separator ? "/ " : "") + "%02d:%02d", minutes, seconds).toString();
        }
    }

    private void updateProgress() throws IllegalStateException {
        if (mPlayer == null) {
            return;
        }

        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                int pos = (100 * position) / duration;
                mProgress.setProgress(pos);
            }
        }

        if (mEndTime != null) {
            mEndTime.setText(stringForTime(duration, true));
        }
        if (mCurrentTime != null) {
            mCurrentTime.setText(stringForTime(position, false));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show();
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show();
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPlayer == null) {
            return true;
        }

        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show();
                if (mActionButton != null) {
                    mActionButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                show();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                show();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show();
        return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener mActionListener = new View.OnClickListener() {
        public void onClick(View v) {
            if ("play".equalsIgnoreCase((String) mActionButton.getTag())) {
                doPlay();

                if (mOnClickListener != null) {
                    mOnClickListener.onPlayClick(AudioPlayerView.this);
                }
            } else if ("pause".equalsIgnoreCase((String) mActionButton.getTag())) {
                doPause();

                if (mOnClickListener != null) {
                    mOnClickListener.onPauseClick(AudioPlayerView.this);
                }
            }
            show();
        }
    };

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
    }

    private void doPause() {
        if (mPlayer == null) {
            return;
        }

        mHandler.sendEmptyMessage(ACTION_CHANGED);
    }

    private void doPlay() {
        if (mPlayer == null) {
            return;
        }

        mHandler.sendEmptyMessage(ACTION_CHANGED);
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            mDragging = true;
            doPause();

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mPlayer == null) {
                return;
            }

            if (fromuser) {
                int duration = mPlayer.getDuration();
                int newPosition = (duration * progress) / 100;
                mPlayer.seekTo(newPosition);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
        }
    };

    @Override
    public void setEnabled(boolean enabled) {
        if (mActionButton != null) {
            mActionButton.setEnabled(enabled);
        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    public void reset() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mHandler.sendEmptyMessage(ACTION_CHANGED);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(AudioPlayerView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(AudioPlayerView.class.getName());
    }

    @Override
    public void onAnotherPlay(MediaPlayer player) {
        if (getPlayer() == player) {
            doPlay();
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    }

    @Override
    public void onAnotherPause(MediaPlayer player) {
        if (getPlayer() == player) {
            doPause();
        }
    }

    private static class MessageHandler extends Handler {
        private final WeakReference<AudioPlayerView> mView;

        MessageHandler(AudioPlayerView view) {
            mView = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            AudioPlayerView view = mView.get();
            if (view == null || view.mPlayer == null) {
                return;
            }

            switch (msg.what) {
                case SHOW_PROGRESS:
                    view.updateProgress();

                    if (!view.mDragging && view.mShowing && view.mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessage(msg);
                    }
                    break;
                case ACTION_CHANGED:
                    view.updateDrawables();
                    break;
            }
        }
    }
}