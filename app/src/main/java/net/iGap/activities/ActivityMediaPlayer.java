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

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import java.io.File;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperSaveFile;
import net.iGap.interfaces.OnComplete;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.MusicPlayer;

public class ActivityMediaPlayer extends ActivityEnhanced {

    TextView btnReplay;
    TextView btnShuffle;
    OnComplete onComplete;
    private TextView txt_MusicName;
    private TextView txt_MusicPlace;
    private TextView txt_MusicTime;
    private TextView txt_Timer;
    private TextView txt_musicInfo;
    private SeekBar musicSeekbar;
    private ImageView img_MusicImage;
    private ImageView img_RepeatOne;
    private ImageView img_MusicImage_default_icon;
    private TextView btnPlay;


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        MusicPlayer.isShowMediaPlayer = true;

        if (MusicPlayer.mp == null) {
            finish();
            NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notifyManager.cancel(MusicPlayer.notificationId);
            return;
        }

        onComplete = new OnComplete() {
            @Override public void complete(boolean result, String messageOne, final String MessageTow) {

                if (messageOne.equals("play")) {
                    btnPlay.setText(R.string.md_play_rounded_button);
                } else if (messageOne.equals("pause")) {
                    btnPlay.setText(R.string.md_round_pause_button);
                } else if (messageOne.equals("update")) {
                    updateUi();
                } else if (messageOne.equals("updateTime")) {
                    txt_Timer.post(new Runnable() {
                        @Override public void run() {
                            txt_Timer.setText(MessageTow);
                            musicSeekbar.setProgress(MusicPlayer.musicProgress);

                            if (HelperCalander.isLanguagePersian) txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));
                        }
                    });
                } else if (messageOne.equals("RepeatMode")) {
                    setReplayButton();
                } else if (messageOne.equals("Shuffel")) {
                    setShuffleButton();
                }
            }
        };

        MusicPlayer.onComplete = onComplete;

        initComponent();

        setMusicInfo();
    }

    private void setShuffleButton() {

        if (MusicPlayer.isShuffelOn) {
            btnShuffle.setTextColor(Color.BLACK);
        } else {
            btnShuffle.setTextColor(Color.GRAY);
        }
    }

    private void setReplayButton() {
        if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.noRepeat.toString())) {
            btnReplay.setText(R.string.md_synchronization_arrows);
            btnReplay.setTextColor(Color.GRAY);
            img_RepeatOne.setVisibility(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.repeatAll.toString())) {
            btnReplay.setText(R.string.md_synchronization_arrows);
            btnReplay.setTextColor(Color.BLACK);
            img_RepeatOne.setVisibility(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.oneRpeat.toString())) {
            btnReplay.setText(R.string.md_synchronization_arrows);
            btnReplay.setTextColor(Color.BLACK);
            img_RepeatOne.setVisibility(View.VISIBLE);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        MusicPlayer.isShowMediaPlayer = false;
        MusicPlayer.onComplete = null;
        MusicPlayer.updateNotification();
    }

    @Override protected void onResume() {
        super.onResume();
        MusicPlayer.isShowMediaPlayer = true;
        MusicPlayer.onComplete = onComplete;
        updateUi();
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(MusicPlayer.notificationId);
    }

    //*****************************************************************************************

    private void initComponent() {

        txt_MusicName = (TextView) findViewById(R.id.ml_txt_music_name);
        txt_MusicPlace = (TextView) findViewById(R.id.ml_txt_music_place);
        txt_MusicTime = (TextView) findViewById(R.id.ml_txt_music_time);
        txt_Timer = (TextView) findViewById(R.id.ml_txt_timer);

        txt_musicInfo = (TextView) findViewById(R.id.ml_txt_music_info);
        img_MusicImage = (ImageView) findViewById(R.id.ml_img_music_picture);
        img_MusicImage_default_icon = (ImageView) findViewById(R.id.ml_img_music_icon_default);
        img_RepeatOne = (ImageView) findViewById(R.id.ml_img_repead_one);
        if (MusicPlayer.mediaThumpnail != null) {
            img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
            img_MusicImage.setVisibility(View.VISIBLE);
            img_MusicImage_default_icon.setVisibility(View.GONE);
        } else {
            img_MusicImage.setVisibility(View.GONE);
            img_MusicImage_default_icon.setVisibility(View.VISIBLE);
        }

        musicSeekbar = (SeekBar) findViewById(R.id.ml_seekBar1);
        musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
                }
                return false;
            }
        });

        MaterialDesignTextView btnBack = (MaterialDesignTextView) findViewById(R.id.ml_btn_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.ml_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        MaterialDesignTextView btnMusicMenu = (MaterialDesignTextView) findViewById(R.id.ml_btn_music_menu);
        RippleView rippleMusicMenu = (RippleView) findViewById(R.id.amp_ripple_menu);
        rippleMusicMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                popUpMusicMenu();
            }
        });

        TextView btnPrevious = (TextView) findViewById(R.id.ml_btn_Previous_music);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                MusicPlayer.previousMusic();
            }
        });

        btnShuffle = (TextView) findViewById(R.id.ml_btn_shuffel_music);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                MusicPlayer.shuffelClick();
            }
        });

        setShuffleButton();

        btnReplay = (TextView) findViewById(R.id.ml_btn_replay_music);
        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                MusicPlayer.repeatClick();
            }
        });
        setReplayButton();

        btnPlay = (TextView) findViewById(R.id.ml_btn_play_music);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                MusicPlayer.playAndPause();
            }
        });

        TextView btnNextMusic = (TextView) findViewById(R.id.ml_btn_forward_music);
        btnNextMusic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                MusicPlayer.nextMusic();
            }
        });

        if (HelperCalander.isLanguagePersian) {
            txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));
            txt_MusicTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_MusicTime.getText().toString()));
        }
    }

    private void popUpMusicMenu() {

        MaterialDialog dialog = new MaterialDialog.Builder(this).items(R.array.pop_up_media_player).contentColor(Color.BLACK).itemsCallback(new MaterialDialog.ListCallback() {
            @Override public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                switch (which) {
                    case 0:
                        saveToMusic();
                        break;
                    case 1:
                        shareMusuic();
                        break;
                }
            }
        }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp260);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;

        dialog.getWindow().setAttributes(layoutParams);
    }

    private void saveToMusic() {

        HelperSaveFile.saveToMusicFolder(MusicPlayer.musicPath, MusicPlayer.musicName);
    }

    private void shareMusuic() {

        String sharePath = MusicPlayer.musicPath;

        Uri uri = Uri.fromFile(new File(sharePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(share, getString(R.string.shate_audio_file)));
    }

    private void updateUi() {
        txt_MusicTime.setText(MusicPlayer.musicTime);
        txt_MusicPlace.setText(MusicPlayer.musicInfoTitle);
        txt_MusicName.setText(MusicPlayer.musicName);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                btnPlay.setText(getString(R.string.md_round_pause_button));
            } else {
                btnPlay.setText(getString(R.string.md_play_rounded_button));
            }

            if (MusicPlayer.mediaThumpnail != null) {
                img_MusicImage.setImageBitmap(MusicPlayer.mediaThumpnail);
                img_MusicImage.setVisibility(View.VISIBLE);
                img_MusicImage_default_icon.setVisibility(View.GONE);
            } else {
                img_MusicImage.setVisibility(View.GONE);
                img_MusicImage_default_icon.setVisibility(View.VISIBLE);
            }

            setMusicInfo();
        }

        if (HelperCalander.isLanguagePersian) {
            txt_MusicTime.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_MusicTime.getText().toString()));
        }
    }

    private void setMusicInfo() {

        if (MusicPlayer.musicInfo.trim().length() > 0) {
            txt_musicInfo.setVisibility(View.VISIBLE);
            txt_musicInfo.setText(MusicPlayer.musicInfo);

            txt_musicInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            txt_musicInfo.setSelected(true);
            txt_musicInfo.setSingleLine(true);
        } else {
            txt_musicInfo.setVisibility(View.GONE);
        }
    }
}
