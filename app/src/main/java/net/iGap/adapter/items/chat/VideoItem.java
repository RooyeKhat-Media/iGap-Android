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

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.fastadapter.FastAdapter;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;

import static net.iGap.module.AndroidUtils.suitablePath;

public class VideoItem extends AbstractMessage<VideoItem, VideoItem.ViewHolder> {

    public VideoItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener, Activity activity) {
        super(true, type, messageClickListener);
    }

    @Override public int getType() {
        return R.id.chatSubLayoutVideo;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_video;
    }

    @Override public void onLoadThumbnailFromLocal(final ViewHolder holder, String localPath, LocalFileType fileType) {
        super.onLoadThumbnailFromLocal(holder, localPath, fileType);

        if (fileType == LocalFileType.THUMBNAIL) {

            G.imageLoader.displayImage(suitablePath(localPath), holder.image);

            holder.image.setCornerRadius(HelperRadius.computeRadius(localPath));
        } else {

            MessageProgress progress = (MessageProgress) holder.itemView.findViewById(R.id.progress);
            AppUtils.setProgresColor(progress.progressBar);

            progress.setVisibility(View.VISIBLE);
            progress.withDrawable(R.drawable.ic_play, true);
        }
    }

    @Override protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override public FastAdapter.OnClickListener<VideoItem> getOnItemClickListener() {
        return super.getOnItemClickListener();
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getAttachment() != null) {
                holder.duration.setText(
                    String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.forwardedFrom.getAttachment().getDuration() * 1000L)),
                        AndroidUtils.humanReadableByteCount(mMessage.forwardedFrom.getAttachment().getSize(), true)));
            }
        } else {
            if (mMessage.attachment != null) {
                holder.duration.setText(String.format(holder.itemView.getResources().getString(R.string.video_duration), AndroidUtils.formatDuration((int) (mMessage.attachment.duration * 1000L)),
                    AndroidUtils.humanReadableByteCount(mMessage.attachment.size, true) + " " + mMessage.attachment.compressing));
            }
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected ReserveSpaceRoundedImageView image;
        protected TextView duration;

        public ViewHolder(View view) {
            super(view);
            image = (ReserveSpaceRoundedImageView) view.findViewById(R.id.thumbnail);
            duration = (TextView) view.findViewById(R.id.duration);
        }
    }
}
