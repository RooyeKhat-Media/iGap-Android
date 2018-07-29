/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items.chat;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.IMessageItem;
import net.iGap.interfaces.OnComplete;
import net.iGap.module.AppUtils;
import net.iGap.module.MusicPlayer;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmRegisteredInfo;

import java.io.File;
import java.util.List;

import io.realm.Realm;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static net.iGap.fragments.FragmentChat.getRealmChat;

public class VoiceItem extends AbstractMessage<VoiceItem, VoiceItem.ViewHolder> {

    private long roomId;


    public VoiceItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }


    @Override
    public int getType() {
        return R.id.chatSubLayoutVoice;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String tag, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, tag, localPath, fileType);

        if (!TextUtils.isEmpty(localPath) && new File(localPath).exists()) {
            holder.mFilePath = localPath;
            holder.musicSeekbar.setEnabled(true);
            holder.btnPlayMusic.setEnabled(true);

            if (!mMessage.isSenderMe() && Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColorDarker), PorterDuff.Mode.SRC_IN);
            }
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.toolbar_background));
        } else {
            holder.musicSeekbar.setEnabled(false);
            holder.btnPlayMusic.setEnabled(false);
            holder.btnPlayMusic.setTextColor(holder.itemView.getResources().getColor(R.color.gray_6c));
        }
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.mainContainer) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getVoiceItem());
        }

        holder.thumbnail = (ImageView) holder.itemView.findViewById(R.id.thumbnail);
        holder.author = (TextView) holder.itemView.findViewById(R.id.cslv_txt_author);
        holder.btnPlayMusic = (TextView) holder.itemView.findViewById(R.id.csla_btn_play_music);
        holder.txt_Timer = (TextView) holder.itemView.findViewById(R.id.csla_txt_timer);
        holder.musicSeekbar = (SeekBar) holder.itemView.findViewById(R.id.csla_seekBar1);
        holder.musicSeekbar.setTag(mMessage.messageID);
        //tic = (ImageView) view.findViewById(R.id.cslr_txt_tic);

        holder.complete = new OnComplete() {
            @Override
            public void complete(final boolean result, String messageOne, final String MessageTow) {

                if (holder.musicSeekbar.getTag().equals(mMessage.messageID) && mMessage.messageID.equals(MusicPlayer.messageId)) {
                    if (messageOne.equals("play")) {
                        holder.btnPlayMusic.setText(R.string.md_play_arrow);
                    } else if (messageOne.equals("pause")) {
                        holder.btnPlayMusic.setText(R.string.md_pause_button);
                    } else if (messageOne.equals("updateTime")) {

                        if (result) {

                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    if (mMessage.messageID.equals(MusicPlayer.messageId)) {
                                        holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                                        if (HelperCalander.isPersianUnicode) {
                                            holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                        }
                                        holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);
                                    }
                                }
                            });
                        } else {
                            holder.btnPlayMusic.post(new Runnable() {
                                @Override
                                public void run() {

                                    holder.txt_Timer.setText(MessageTow + "/" + holder.mTimeMusic);
                                    if (HelperCalander.isPersianUnicode) {
                                        holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
                                    }
                                    holder.musicSeekbar.setProgress(0);
                                }
                            });
                        }
                    }
                }
            }
        };

        holder.itemView.findViewById(R.id.mainContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.mFilePath.length() < 1) return;

                G.chatUpdateStatusUtil.sendUpdateStatus(holder.mType, holder.mRoomId, Long.parseLong(holder.mMessageID), ProtoGlobal.RoomMessageStatus.LISTENED);

                RealmClientCondition.addOfflineListen(holder.mRoomId, Long.parseLong(holder.mMessageID));

                if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                    MusicPlayer.onCompleteChat = holder.complete;

                    if (MusicPlayer.mp != null) {
                        MusicPlayer.playAndPause();
                    } else {
                        MusicPlayer.startPlayer("", holder.mFilePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, true, holder.mMessageID);
                        messageClickListener.onPlayMusic(holder.mMessageID);
                    }
                } else {

                    MusicPlayer.stopSound();
                    MusicPlayer.onCompleteChat = holder.complete;
                    MusicPlayer.startPlayer("", holder.mFilePath, FragmentChat.titleStatic, FragmentChat.mRoomIdStatic, true, holder.mMessageID);
                    messageClickListener.onPlayMusic(holder.mMessageID);

                    holder.mTimeMusic = MusicPlayer.musicTime;
                }
            }
        });

        holder.musicSeekbar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (holder.mMessageID.equals(MusicPlayer.messageId)) {
                        MusicPlayer.setMusicProgress(holder.musicSeekbar.getProgress());
                    }
                }
                return false;
            }
        });

        super.bindView(holder, payloads);

        ProtoGlobal.RoomMessageType _type = mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getMessageType() : mMessage.messageType;
        holder.mType = type;
        AppUtils.rightFileThumbnailIcon(holder.thumbnail, _type, null);

        holder.mRoomId = mMessage.roomId;

        RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(getRealmChat(), mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getUserId() : Long.parseLong(mMessage.senderID));

        if (registeredInfo != null) {
            holder.author.setText(G.context.getString(R.string.recorded_by) + " " + registeredInfo.getDisplayName());
        } else {
            holder.author.setText("");
        }

        final long _st = (int) ((mMessage.forwardedFrom != null ? mMessage.forwardedFrom.getAttachment().getDuration() : mMessage.attachment.duration) * 1000);

        holder.txt_Timer.setText("00/" + MusicPlayer.milliSecondsToTimer(_st));

        if (holder.musicSeekbar.getTag().equals(mMessage.messageID) && mMessage.messageID.equals(MusicPlayer.messageId)) {
            MusicPlayer.onCompleteChat = holder.complete;

            holder.musicSeekbar.setProgress(MusicPlayer.musicProgress);

            if (MusicPlayer.musicProgress > 0) {
                holder.txt_Timer.setText(MusicPlayer.strTimer + "/" + MusicPlayer.musicTime);
            }


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

        if (HelperCalander.isPersianUnicode)
            holder.txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(holder.txt_Timer.getText().toString()));
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);

        if (mMessage.isSenderMe() && ProtoGlobal.RoomMessageStatus.valueOf(mMessage.status) == ProtoGlobal.RoomMessageStatus.LISTENED) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.iGapColor), PorterDuff.Mode.SRC_IN);
            }
            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.iGapColor), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }
            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        holder.txt_Timer.setTextColor(Color.parseColor(G.textTitleTheme));
        holder.author.setTextColor(Color.parseColor(G.textTitleTheme));
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);

        if (type == ProtoGlobal.Room.Type.CHANNEL) {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.text_line1_igap_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txt_Timer.setTextColor(Color.parseColor(G.textTitleTheme));
            holder.author.setTextColor(Color.parseColor(G.textTitleTheme));
        } else {
            if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
                holder.musicSeekbar.getThumb().mutate().setColorFilter(G.context.getResources().getColor(R.color.gray_6c), PorterDuff.Mode.SRC_IN);
            }

            holder.musicSeekbar.getProgressDrawable().setColorFilter(holder.itemView.getResources().getColor(R.color.gray10), android.graphics.PorterDuff.Mode.SRC_IN);
            holder.txt_Timer.setTextColor(holder.itemView.getResources().getColor(R.color.grayNewDarker));
            holder.author.setTextColor(Color.parseColor(G.textTitleTheme));
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ImageView thumbnail;
        //protected ImageView tic;
        protected TextView btnPlayMusic;
        protected SeekBar musicSeekbar;
        protected OnComplete complete;
        protected TextView txt_Timer;
        protected TextView author;
        protected String mFilePath = "";
        protected String mMessageID = "";
        protected String mTimeMusic = "";
        protected long mRoomId;
        protected ProtoGlobal.Room.Type mType;

        public ViewHolder(View view) {
            super(view);
            /**
             *  this commented code used with xml layout
             */
            //thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //author = (TextView) view.findViewById(R.id.cslv_txt_author);
            //btnPlayMusic = (TextView) view.findViewById(R.id.csla_btn_play_music);
            //txt_Timer = (TextView) view.findViewById(R.id.csla_txt_timer);
            //musicSeekbar = (SeekBar) view.findViewById(R.id.csla_seekBar1);
            //
            //complete = new OnComplete() {
            //    @Override public void complete(boolean result, String messageOne, final String MessageTow) {
            //
            //        if (messageOne.equals("play")) {
            //            btnPlayMusic.setText(R.string.md_play_arrow);
            //        } else if (messageOne.equals("pause")) {
            //            btnPlayMusic.setText(R.string.md_pause_button);
            //        } else if (messageOne.equals("updateTime")) {
            //            txt_Timer.post(new Runnable() {
            //                @Override public void run() {
            //                    txt_Timer.setText(MessageTow + "/" + mTimeMusic);
            //
            //                    if (HelperCalander.isPersianUnicode) txt_Timer.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_Timer.getText().toString()));
            //
            //                    musicSeekbar.setProgress(MusicPlayer.musicProgress);
            //                }
            //            });
            //        }
            //    }
            //};
            //
            //btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            //    @Override public void onClick(View v) {
            //
            //        if (mFilePath.length() < 1) return;
            //
            //        if (mMessageID.equals(MusicPlayer.messageId)) {
            //            MusicPlayer.onCompleteChat = complete;
            //
            //            if (MusicPlayer.mp != null) {
            //                MusicPlayer.playAndPause();
            //            } else {
            //                MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);
            //            }
            //        } else {
            //
            //            MusicPlayer.stopSound();
            //            MusicPlayer.onCompleteChat = complete;
            //            MusicPlayer.startPlayer(mFilePath, ActivityChat.titleStatic, ActivityChat.mRoomIdStatic, true, mMessageID);
            //
            //            mTimeMusic = MusicPlayer.musicTime;
            //        }
            //    }
            //});
            //
            //musicSeekbar.setOnTouchListener(new View.OnTouchListener() {
            //
            //    @Override public boolean onTouch(View v, MotionEvent event) {
            //
            //        if (event.getAction() == MotionEvent.ACTION_UP) {
            //            if (mMessageID.equals(MusicPlayer.messageId)) {
            //                MusicPlayer.setMusicProgress(musicSeekbar.getProgress());
            //            }
            //        }
            //        return false;
            //    }
            //});
        }
    }
}
