/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.adapter.items.chat;

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.helper.HelperCalander;
import com.iGap.interfaces.IMessageItem;
import com.iGap.interfaces.OnComplete;
import com.iGap.module.AppUtils;
import com.iGap.module.MusicPlayer;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import io.realm.Realm;
import java.io.File;
import java.util.List;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {

    public VoiceItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_voice;
    }

    @Override
    public void onLoadThumbnailFromLocal(ViewHolder holder, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {
            holder.mFilePath = localPath;
            holder.btnPlayMusic.setEnabled(true);
        } else {
            holder.btnPlayMusic.setEnabled(false);
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        ProtoGlobal.RoomMessageType _type = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;

        AppUtils.rightFileThumbnailIcon(holder.thumbnail, _type, null);

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getUserId() : Long.parseLong(mMessage.senderID)).findFirst();

        if (registeredInfo != null) {
            holder.author.setText("Recorded By " + registeredInfo.getDisplayName());
        } else {
            holder.author.setText("");
        }

        realm.close();

        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * 1000);

        holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);
            holder.txt_Timer.setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);

            holder.mTimeMusic = MusicPlayer.musicTime;

            if (MusicPlayer.mp != null) {
                if (MusicPlayer.mp.isPlaying()) {
                    holder.btnPlayMusic.setText(R.string.md_pause_button);
                } else {
                    holder.btnPlayMusic.setText(R.string.md_play_arrow);
                }
            }

        } else {
            holder.musicSeekbar.setProgress(0);
            holder.btnPlayMusic.setText(R.string.md_play_arrow);
        }

        holder.mMessageID = mMessage.messageID;

        if (HelperCalander.isLanguagePersian) holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));

    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
        }

        holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
        holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        holder.author.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.iGapColor));
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
            holder.author.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.green));
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
            holder.author.setTextColor(holder.itemView.getResources().getColor(R.color.black90));
        }



    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView thumbnail;
        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;
        protected TextView author;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";

        public ViewHolder(View view) {
            super(view);

            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            author = (TextView) view.findViewById(R.id.cslv_txt_author);
            btnPlayMusic = (TextView) view.findViewById(R.id.csla_btn_play_music);
            txt_Timer = (TextView) view.findViewById(R.id.csla_txt_timer);
            musicSeekbar = (SeekBar) view.findViewById(R.id.csla_seekBar1);

            complete = new OnComplete() {
                @Override
                public void complete(boolean result, String messageOne, final String MessageTow) {

                    if (messageOne.equals("play")) {
                        btnPlayMusic.setText(R.string.md_play_arrow);
                    } else if (messageOne.equals("pause")) {
                        btnPlayMusic.setText(R.string.md_pause_button);
                    } else if (messageOne.equals("updateTime")) {
                        txt_Timer.post(new Runnable() {
                            @Override
                            public void run() {
                                txt_Timer.setText(MessageTow + "/" + mTimeMusic);

                                if (HelperCalander.isLanguagePersian) txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));


                                musicSeekbar.setProgress(MusicPlayer.musicProgress);
                            }
                        });
                    }
                }
            };

            btnPlayMusic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mFilePath.length() < 1) return;

                    if (mMessageID.equals(MusicPlayer.messageId)) {
                        MusicPlayer.onCompleteChat = complete;

                        if (MusicPlayer.mp != null) {
                            MusicPlayer.playAndPause();
                        } else {
                            MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);
                        }
                    } else {

                        MusicPlayer.stopSound();
                        MusicPlayer.onCompleteChat = complete;
                        MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);

                        mTimeMusic = MusicPlayer.musicTime;
                    }
                }
            });

            musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (mMessageID.equals(MusicPlayer.messageId)) {
                            MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
