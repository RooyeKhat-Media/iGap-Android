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

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperRadius;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.AndroidUtils;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import io.github.meness.emoji.EmojiTextView;
import io.meness.github.messageprogress.MessageProgress;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

public class VideoWithTextItem extends AbstractMessage<VideoWithTextItem, VideoWithTextItem.ViewHolder> {

    public VideoWithTextItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutVideoWithText;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_video_with_text;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.forwardedFrom.getAttachment().getDuration() * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
            }

            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            if (mMessage.attachment != null) {
                holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)), AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + mMessage.attachment.compressing));
            }

            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }

        if (!mMessage.hasLinkInMessage) {
            holder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.itemView.performLongClick();
                    return false;
                }
            });

            holder.messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isSelected()) {
                        if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            return;
                        }
                        if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            messageClickListener.onFailedMessageClick(v, mMessage, holder.getAdapterPosition());
                        } else {
                            messageClickListener.onContainerClick(v, mMessage, holder.getAdapterPosition());
                        }
                    }
                }
            });
        }


    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (fileType == LocalFileType.THUMBNAIL) {

            G.imageLoader.displayImage(suitablePath(localPath), holder.image);

            holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
        } else {
            holder.itemView.findViewById(R.id.progress).setVisibility(View.VISIBLE);
            ((MessageProgress) holder.itemView.findViewById(R.id.progress)).withDrawable(R.drawable.ic_play, true);
        }

    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;
        protected TextView duration;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
            duration = (TextView) view.findViewById(R.id.duration);
            messageText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
