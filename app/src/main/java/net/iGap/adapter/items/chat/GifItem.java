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

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.io.File;
import java.util.List;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AppUtils;
import net.iGap.module.ReserveSpaceGifImageView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import pl.droidsonroids.gif.GifDrawable;

import static android.content.Context.MODE_PRIVATE;

public class GifItem extends AbstractMessage<GifItem, GifItem.ViewHolder> {

    public GifItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override public void onPlayPauseGIF(ViewHolder holder, String localPath) {
        super.onPlayPauseGIF(holder, localPath);

        MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
        AppUtils.setProgresColor(progress.progressBar);

        progress.withDrawable(R.drawable.photogif, true);

        GifDrawable gifDrawable = (GifDrawable) holder.image.getDrawable();
        if (gifDrawable != null) {
            if (gifDrawable.isPlaying()) {
                gifDrawable.pause();
                holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            } else {
                gifDrawable.start();
                holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
            }
        }
    }

    @Override public int getType() {
        return R.id.chatSubLayoutGif;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_gif;
    }

    @Override public void onLoadThumbnailFromLocal(ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);
        holder.image.setImageURI(Uri.fromFile(new File(localPath)));

        if (fileType == LocalFileType.FILE) {
            SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            if (sharedPreferences.getInt(SHP_SETTING.KEY_AUTOPLAY_GIFS, SHP_SETTING.Defaults.KEY_AUTOPLAY_GIFS) == 1) {
                holder.itemView.findViewById(R.id.progress).setVisibility(View.GONE);
            } else {
                if (holder.image.getDrawable() instanceof GifDrawable) {
                    GifDrawable gifDrawable = (GifDrawable) holder.image.getDrawable();
                    // to get first frame
                    gifDrawable.stop();
                    holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.itemView.findViewById(R.id.progress).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!isSelected()) {
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                        return;
                    }
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                        messageClickListener.onFailedMessageClick(v, mMessage, holder.getAdapterPosition());
                    } else {
                        if (mMessage.forwardedFrom != null && mMessage.forwardedFrom.getAttachment().isFileExistsOnLocal()) {
                            onPlayPauseGIF(holder, mMessage.forwardedFrom.getAttachment().getLocalFilePath());
                        } else {
                            if (mMessage.attachment.isFileExistsOnLocal()) {
                                onPlayPauseGIF(holder, mMessage.attachment.getLocalFilePath());
                            }
                        }
                    }
                }
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                holder.itemView.findViewById(R.id.progress).performClick();
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });
    }

    @Override protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceGifImageView image;

        public ViewHolder(View view) {
            super(view);

            image = (ReserveSpaceGifImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
