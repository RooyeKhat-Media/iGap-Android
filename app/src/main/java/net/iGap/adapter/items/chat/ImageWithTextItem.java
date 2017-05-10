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

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.emoji.EmojiTextView;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import static net.iGap.module.AndroidUtils.suitablePath;

public class ImageWithTextItem extends AbstractMessage<ImageWithTextItem, ImageWithTextItem.ViewHolder> {

    public ImageWithTextItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override public int getType() {
        return R.id.chatSubLayoutImageWithText;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_image_with_text;
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            setTextIfNeeded(holder.messageText, mMessage.forwardedFrom.getMessage());
        } else {
            setTextIfNeeded(holder.messageText, mMessage.messageText);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!isSelected()) {
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                        return;
                    }
                    if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                        messageClickListener.onFailedMessageClick(v, mMessage, holder.getAdapterPosition());
                    } else {
                        messageClickListener.onOpenClick(v, mMessage, holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });

        if (!mMessage.hasLinkInMessage) {
            holder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    holder.itemView.performLongClick();
                    return false;
                }
            });

            holder.messageText.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
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

    @Override public void onLoadThumbnailFromLocal(final ViewHolder holder, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        G.imageLoader.displayImage(suitablePath(localPath), holder.image);
        holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
    }

    @Override protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;
        protected EmojiTextView messageText;

        public ViewHolder(View view) {
            super(view);

            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            messageText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
