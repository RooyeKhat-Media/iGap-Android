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
import android.view.View;
import com.iGap.G;
import com.iGap.R;
import com.iGap.helper.HelperRadius;
import com.iGap.interfaces.IMessageItem;
import com.iGap.module.ReserveSpaceRoundedImageView;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import java.util.List;

import static com.iGap.module.AndroidUtils.suitablePath;

public class ImageItem extends AbstractMessage<ImageItem, ImageItem.ViewHolder> {

    public ImageItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutImage;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_image;
    }

    @Override
    public void onLoadThumbnailFromLocal(final ViewHolder holder, final String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        G.imageLoader.displayImage(suitablePath(localPath), holder.image);
        holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
    }


    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            @Override
            public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected ReserveSpaceRoundedImageView image;
        public ViewHolder(View view) {
            super(view);
            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
