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

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.HelperSaveFile;
import net.iGap.interfaces.OnComplete;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.libs.ripplesoundplayer.RippleVisualizerView;
import net.iGap.libs.ripplesoundplayer.renderer.LineRenderer;
import net.iGap.libs.ripplesoundplayer.util.PaintUtil;
import net.iGap.module.DialogAnimation;
import net.iGap.module.MusicPlayer;

import java.io.File;
import java.io.IOException;

public class FragmentMediaPlayerViewModel {


    public ObservableField<String> callBackMusicName = new ObservableField<>(G.context.getResources().getString(R.string.music_name));
    public ObservableField<String> callBackMusicPlace = new ObservableField<>(G.context.getResources().getString(R.string.place));
    public ObservableField<String> callBackBtnPlayMusic = new ObservableField<>();
    public ObservableField<String> callBackTxtTimer = new ObservableField<>("00:00}");
    public ObservableField<String> callBackTxtMusicInfo = new ObservableField<>("");
    public ObservableField<String> callBackTxtMusicTime = new ObservableField<>(G.context.getResources().getString(R.string.music_time));
    public ObservableField<String> callBackBtnReplayMusic = new ObservableField<>(G.context.getResources().getString(R.string.md_synchronization_arrows));
    public ObservableInt txtMusicInfoVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt imgRepeadOneVisibility = new ObservableInt(View.VISIBLE);
    public ObservableInt imgMusicPicture = new ObservableInt(View.VISIBLE);
    public ObservableInt imgMusIciconDefault = new ObservableInt(View.VISIBLE);
    public ObservableInt btnShuffelMusicColor = new ObservableInt(G.context.getResources().getColor(R.color.black));
    public ObservableInt btnReplayMusicColor = new ObservableInt(G.context.getResources().getColor(R.color.black));
    public ObservableInt seekBar1 = new ObservableInt();
    public ObservableBoolean txtMusicInfoSingleLine = new ObservableBoolean(true);
    private RippleVisualizerView rippleVisualizerView;
    private View v;

    public FragmentMediaPlayerViewModel(View v) {

        this.v = v;

        getInfo();
    }

    public void onClickRippleBack(View v) {

        if (FragmentMediaPlayer.onBackFragment != null) {
            FragmentMediaPlayer.onBackFragment.onBack();
        }

    }

    public void onClickRippleMenu(View v) {
        popUpMusicMenu();
    }

    public void onClickBtnShuffelMusic(View v) {
        MusicPlayer.shuffleClick();
    }

    public void onClickBtnReplayMusic(View v) {
        MusicPlayer.repeatClick();
    }

    public void onClickBtnPlayMusic(View v) {
        MusicPlayer.playAndPause();
    }

    public void onClickBtnForwardMusic(View v) {
        MusicPlayer.nextMusic();
    }

    public void onClickBtnPreviousMusic(View v) {
        MusicPlayer.previousMusic();
    }

    private void getInfo() {

        FragmentMediaPlayer.onComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, final String MessageTow) {

                if (messageOne.equals("play")) {
                    callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.md_play_rounded_button));

                    if (rippleVisualizerView != null) {
                        rippleVisualizerView.setEnabled(false);
                        rippleVisualizerView.pauseVisualizer();
                    }
                } else if (messageOne.equals("pause")) {
                    callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.md_round_pause_button));

                    if (rippleVisualizerView != null) {
                        rippleVisualizerView.setEnabled(true);
                        rippleVisualizerView.startVisualizer();
                    }
                } else if (messageOne.equals("update")) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateUi();
                        }
                    });
                } else if (messageOne.equals("updateTime")) {

                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBackTxtTimer.set(MessageTow);
                            seekBar1.set(MusicPlayer.musicProgress);
                        }
                    });

                } else if (messageOne.equals("RepeatMode")) {
                    setReplayButton();
                } else if (messageOne.equals("Shuffel")) {
                    setShuffleButton();
                } else if (messageOne.equals("finish")) {
                    if (FragmentMediaPlayer.onBackFragment != null) {
                        FragmentMediaPlayer.onBackFragment.onBack();
                    }
                }
            }
        };

        setMusicInfo();
        initVisualizer(v);
        setShuffleButton();
        setReplayButton();

        if (HelperCalander.isPersianUnicode) {
            callBackTxtTimer.set(HelperCalander.convertToUnicodeFarsiNumber(callBackTxtTimer.get()));
            callBackTxtMusicTime.set(HelperCalander.convertToUnicodeFarsiNumber(callBackTxtMusicTime.get()));
        }
    }


    private void popUpMusicMenu() {

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View v = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        ViewGroup root1 = (ViewGroup) v.findViewById(R.id.dialog_root_item1_notification);
        ViewGroup root2 = (ViewGroup) v.findViewById(R.id.dialog_root_item2_notification);

        final TextView txtShare = (TextView) v.findViewById(R.id.dialog_text_item1_notification);
        TextView txtSaveToGallery = (TextView) v.findViewById(R.id.dialog_text_item2_notification);

        TextView iconSaveToGallery = (TextView) v.findViewById(R.id.dialog_icon_item1_notification);
        iconSaveToGallery.setText(G.fragmentActivity.getResources().getString(R.string.md_save));

        txtShare.setText(G.fragmentActivity.getResources().getString(R.string.save_to_Music));
        txtSaveToGallery.setText(G.fragmentActivity.getResources().getString(R.string.share_item_dialog));

        root1.setVisibility(View.VISIBLE);
        root2.setVisibility(View.VISIBLE);

        TextView iconShare = (TextView) v.findViewById(R.id.dialog_icon_item2_notification);
        iconShare.setText(G.fragmentActivity.getResources().getString(R.string.md_share_button));

        root1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                saveToMusic();
            }
        });

        root2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                shareMusic();
            }
        });
    }

    private void saveToMusic() {

        HelperSaveFile.saveToMusicFolder(MusicPlayer.musicPath, MusicPlayer.musicName);
    }

    private void shareMusic() {

        String sharePath = MusicPlayer.musicPath;

        Uri uri = Uri.fromFile(new File(sharePath));
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        G.fragmentActivity.startActivity(Intent.createChooser(share, G.fragmentActivity.getResources().getString(R.string.shate_audio_file)));
    }

    private void initVisualizer(final View view) {

        if (G.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            try {
                HelperPermission.getMicroPhonePermission(G.currentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {

                        rippleVisualizerView = (RippleVisualizerView) view.findViewById(R.id.line_renderer_demo);

                        if (rippleVisualizerView != null) {

                            rippleVisualizerView.setCurrentRenderer(new LineRenderer(PaintUtil.getLinePaint(Color.parseColor("#15E4EE"))));


                            if (MusicPlayer.mp.isPlaying()) {
                                rippleVisualizerView.setEnabled(true);
                            } else {
                                rippleVisualizerView.setEnabled(false);
                            }

                            rippleVisualizerView.setAmplitudePercentage(3);
                        }
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUi() {
        callBackTxtMusicTime.set(MusicPlayer.musicTime);
        callBackMusicPlace.set(MusicPlayer.musicInfoTitle);
        callBackMusicName.set(MusicPlayer.musicName);
        callBackTxtTimer.set(MusicPlayer.strTimer);

        if (MusicPlayer.mp != null) {
            if (MusicPlayer.mp.isPlaying()) {
                callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.md_round_pause_button));
            } else {
                callBackBtnPlayMusic.set(G.fragmentActivity.getResources().getString(R.string.md_play_rounded_button));
            }

            if (MusicPlayer.mediaThumpnail != null) {

                if (FragmentMediaPlayer.onSetImage != null)
                    FragmentMediaPlayer.onSetImage.setImage();

                imgMusicPicture.set(View.VISIBLE);
                imgMusIciconDefault.set(View.GONE);
            } else {
                imgMusicPicture.set(View.GONE);
                imgMusIciconDefault.set(View.VISIBLE);
            }

            setMusicInfo();
        }

        if (HelperCalander.isPersianUnicode) {
            callBackTxtMusicTime.set(HelperCalander.convertToUnicodeFarsiNumber(callBackTxtMusicTime.get().toString()));
        }


        if (rippleVisualizerView != null) {
            rippleVisualizerView.setMediaPlayer(MusicPlayer.mp);
        }
    }

    private void setReplayButton() {
        if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.noRepeat.toString())) {
            callBackBtnReplayMusic.set(G.context.getResources().getString(R.string.md_synchronization_arrows));
            btnReplayMusicColor.set(Color.GRAY);
            imgRepeadOneVisibility.set(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.repeatAll.toString())) {
            callBackBtnReplayMusic.set(G.context.getResources().getString(R.string.md_synchronization_arrows));
            btnReplayMusicColor.set(Color.BLACK);
            imgRepeadOneVisibility.set(View.GONE);
        } else if (MusicPlayer.repeatMode.equals(MusicPlayer.RepeatMode.oneRpeat.toString())) {
            callBackBtnReplayMusic.set(G.context.getResources().getString(R.string.md_synchronization_arrows));
            btnReplayMusicColor.set(Color.BLACK);
            imgRepeadOneVisibility.set(View.VISIBLE);
        }
    }

    private void setShuffleButton() {

        if (MusicPlayer.isShuffelOn) {
            btnShuffelMusicColor.set(Color.BLACK);
        } else {
            btnShuffelMusicColor.set(Color.GRAY);
        }
    }

    private void setMusicInfo() {

        if (MusicPlayer.musicInfo.trim().length() > 0) {
            txtMusicInfoVisibility.set(View.VISIBLE);
            callBackTxtMusicInfo.set(MusicPlayer.musicInfo);
            //txt_musicInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            //txt_musicInfo.setSelected(true);
            txtMusicInfoSingleLine.set(true);
        } else {
            txtMusicInfoVisibility.set(View.GONE);
        }
    }

    public void onResume() {
        if (MusicPlayer.mp == null) {
            if (FragmentMediaPlayer.onBackFragment != null) {
                FragmentMediaPlayer.onBackFragment.onBack();
            }
        } else {
            MusicPlayer.isShowMediaPlayer = true;
            updateUi();
            MusicPlayer.onComplete = FragmentMediaPlayer.onComplete;
        }
    }

    public void onStop() {
        if (rippleVisualizerView != null) {
            rippleVisualizerView.pauseVisualizer();
        }
    }
}
