/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.afollestad.materialdialogs.MaterialDialog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChatBackground;
import net.iGap.fragments.FragmentFullChatBackground;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;

public class AdapterChatBackground extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CHOOSE = 0;
    private final int ALL = 1;

    int selected_position = 1;

    private ArrayList<ActivityChatBackground.StructWallpaper> mList;

    public AdapterChatBackground(ArrayList<ActivityChatBackground.StructWallpaper> List) {
        this.mList = List;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == CHOOSE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_choose, parent, false);
            return new ViewHolderImage(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_image, parent, false);
            return new ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == ALL) {

            final ViewHolderItem holder2 = (ViewHolderItem) holder;

            if (mList.size() < (position + 1)) {
                return;
            }
            ActivityChatBackground.StructWallpaper wallpaper = mList.get(position);

            if (wallpaper.getWallpaperType() == ActivityChatBackground.WallpaperType.proto) {
                ProtoGlobal.File pf = wallpaper.getProtoWallpaper().getFile();

                final String path = G.DIR_CHAT_BACKGROUND + "/" + "thumb_" + pf.getCacheId() + "_" + pf.getName();

                if (!new File(path).exists()) {
                    HelperDownloadFile.startDownload(pf.getToken(), pf.getCacheId(), pf.getName(), pf.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, path, 4, new HelperDownloadFile.UpdateListener() {
                        @Override
                        public void OnProgress(String mPath, int progress) {
                            if (progress == 100) {
                                G.currentActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        G.imageLoader.displayImage(AndroidUtils.suitablePath(path), holder2.img);
                                    }
                                });
                            }
                        }

                        @Override
                        public void OnError(String token) {
                        }
                    });
                } else {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(path), holder2.img);
                }
            } else {
                G.imageLoader.displayImage(AndroidUtils.suitablePath(wallpaper.getPath()), holder2.img);
            }

            String bigImagePath;
            if (wallpaper.getWallpaperType() == ActivityChatBackground.WallpaperType.proto) {
                ProtoGlobal.File pf = wallpaper.getProtoWallpaper().getFile();
                bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
            } else {
                bigImagePath = wallpaper.getPath();
            }

            if (new File(bigImagePath).exists()) {
                holder2.messageProgress.setVisibility(View.GONE);
                // G.imageLoader.displayImage(AndroidUtils.suitablePath(bigImagePath), holder2.img);
                holder2.mPath = bigImagePath;
            } else {
                holder2.mPath = "";
                holder2.messageProgress.setVisibility(View.VISIBLE);
                if (HelperDownloadFile.isDownLoading(wallpaper.getProtoWallpaper().getFile().getCacheId())) {
                    startDownload(position, holder2.messageProgress, holder2.contentLoading);
                }

                holder2.messageProgress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadFile(position, holder2.messageProgress, holder2.contentLoading);
                    }
                });
            }

            if (selected_position == position) {
                holder2.itemView.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
                holder2.itemView.setPadding((int) G.context.getResources().getDimension(R.dimen.dp4), (int) G.context.getResources().getDimension(R.dimen.dp4), (int) G.context.getResources().getDimension(R.dimen.dp4), (int) G.context.getResources().getDimension(R.dimen.dp4));
            } else {
                holder2.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return CHOOSE;
        } else {
            return ALL;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolderImage(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imgBackgroundImage);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(G.currentActivity).title(G.context.getString(R.string.choose_picture)).negativeText(G.context.getString(R.string.cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            AttachFile attachFile = new AttachFile(G.currentActivity);

                            if (text.toString().equals(G.context.getString(R.string.from_camera))) {
                                try {
                                    attachFile.requestTakePicture();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    attachFile.requestOpenGalleryForImageSingleSelect();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            dialog.dismiss();
                        }
                    }).show();
                }
            });
        }
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {

        private ImageView img;
        public MessageProgress messageProgress;
        public ContentLoadingProgressBar contentLoading;
        public String mPath = "";

        ViewHolderItem(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgBackground);

            messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
            messageProgress.withDrawable(R.drawable.ic_download, true);

            contentLoading = (ContentLoadingProgressBar) itemView.findViewById(R.id.ch_progress_loadingContent);
            contentLoading.getIndeterminateDrawable().setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.MULTIPLY);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mPath.length() > 0) {

                        // Updating old as well as new positions
                        notifyItemChanged(selected_position);
                        selected_position = getLayoutPosition();
                        notifyItemChanged(selected_position);

                        FragmentFullChatBackground fragmentActivity = new FragmentFullChatBackground();
                        Bundle bundle = new Bundle();
                        bundle.putString("IMAGE", mPath);
                        fragmentActivity.setArguments(bundle);
                        ((FragmentActivity) G.currentActivity).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.stcb_root, fragmentActivity, null).commit();

                        ActivityChatBackground.savePath = mPath;
                        // Do your another stuff for your onClick
                    }
                }
            });
        }
    }

    //*******************************************************************************

    private void startDownload(final int position, final MessageProgress messageProgress, final ContentLoadingProgressBar contentLoading) {

        contentLoading.setVisibility(View.VISIBLE);
        messageProgress.withDrawable(R.drawable.ic_cancel, true);

        ProtoGlobal.File pf = mList.get(position).getProtoWallpaper().getFile();

        String path = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();

        HelperDownloadFile.startDownload(pf.getToken(), pf.getCacheId(), pf.getName(), pf.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, path, 2, new HelperDownloadFile.UpdateListener() {
            @Override
            public void OnProgress(String mPath, final int progress) {

                if (messageProgress != null) {

                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progress < 100) {
                                messageProgress.withProgress(progress);
                            } else {
                                messageProgress.withProgress(0);
                                messageProgress.setVisibility(View.GONE);
                                contentLoading.setVisibility(View.GONE);
                                notifyItemChanged(position);
                            }
                        }
                    });
                }
            }

            @Override
            public void OnError(String token) {

                G.currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(0);
                        messageProgress.withDrawable(R.drawable.ic_download, true);
                        contentLoading.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void stopDownload(int position) {

        HelperDownloadFile.stopDownLoad(mList.get(position).getProtoWallpaper().getFile().getCacheId());
    }

    private void downloadFile(int position, MessageProgress messageProgress, final ContentLoadingProgressBar contentLoading) {

        if (mList.get(position) == null || mList.get(position).getProtoWallpaper() == null) {
            return;
        }

        if (HelperDownloadFile.isDownLoading(mList.get(position).getProtoWallpaper().getFile().getCacheId())) {
            stopDownload(position);
        } else {
            startDownload(position, messageProgress, contentLoading);
        }
    }
}
