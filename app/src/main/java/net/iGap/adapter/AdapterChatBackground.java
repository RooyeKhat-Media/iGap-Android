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

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChatBackground;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.messageprogress.MessageProgress;
import net.iGap.messageprogress.OnProgress;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.AttachFile;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmWallpaperProto;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AdapterChatBackground extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CHOOSE = 0;
    private final int ALL = 1;

    private ArrayList<FragmentChatBackground.StructWallpaper> mList;
    private FragmentChatBackground.OnImageClick onImageClick;
    private Fragment fragment;

    public AdapterChatBackground(Fragment fragment, ArrayList<FragmentChatBackground.StructWallpaper> List, FragmentChatBackground.OnImageClick onImageClick) {
        this.fragment = fragment;
        this.mList = List;
        this.onImageClick = onImageClick;
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
            holder2.img.setImageDrawable(null);

            if (mList.size() < (position + 1)) {
                return;
            }
            FragmentChatBackground.StructWallpaper wallpaper = mList.get(position);

            if (wallpaper.getWallpaperType() == FragmentChatBackground.WallpaperType.proto) {
                RealmAttachment pf = wallpaper.getProtoWallpaper().getFile();

                final String path = G.DIR_CHAT_BACKGROUND + "/" + "thumb_" + pf.getCacheId() + "_" + pf.getName();

                if (!new File(path).exists()) {
                    HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE,System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSmallThumbnail().getSize(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, path, 4, new HelperDownloadFile.UpdateListener() {
                        @Override
                        public void OnProgress(String mPath, int progress) {
                            if (progress == 100) {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (holder2.img != null) {
                                            G.imageLoader.displayImage(AndroidUtils.suitablePath(path), holder2.img);
                                        }
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
            if (wallpaper.getWallpaperType() == FragmentChatBackground.WallpaperType.proto) {
                RealmAttachment pf = wallpaper.getProtoWallpaper().getFile();
                bigImagePath = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();
            } else {
                bigImagePath = wallpaper.getPath();
            }

            if (new File(bigImagePath).exists()) {
                holder2.messageProgress.setVisibility(View.GONE);
                holder2.mPath = bigImagePath;
            } else {
                holder2.mPath = "";
                holder2.messageProgress.setVisibility(View.VISIBLE);
                startDownload(position, holder2.messageProgress);
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

    private void startDownload(final int position, final MessageProgress messageProgress) {
        messageProgress.withDrawable(R.drawable.ic_cancel, true);

        RealmAttachment pf = mList.get(position).getProtoWallpaper().getFile();

        String path = G.DIR_CHAT_BACKGROUND + "/" + pf.getCacheId() + "_" + pf.getName();


        messageProgress.withOnProgress(new OnProgress() {
            @Override
            public void onProgressFinished() {

                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(0);
                        messageProgress.setVisibility(View.GONE);
                        notifyItemChanged(position);
                    }
                });


            }
        });

        HelperDownloadFile.getInstance().startDownload(ProtoGlobal.RoomMessageType.IMAGE,System.currentTimeMillis() + "", pf.getToken(), pf.getUrl(), pf.getCacheId(), pf.getName(), pf.getSize(), ProtoFileDownload.FileDownload.Selector.FILE, path, 2, new HelperDownloadFile.UpdateListener() {
            @Override
            public void OnProgress(String mPath, final int progress) {
                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(progress);
                    }
                });
            }

            @Override
            public void OnError(String token) {
                messageProgress.post(new Runnable() {
                    @Override
                    public void run() {
                        messageProgress.withProgress(0);
                        messageProgress.withDrawable(R.drawable.ic_download, true);
                    }
                });
            }
        });
    }

    private class ViewHolderImage extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolderImage(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imgBackgroundImage);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(G.fragmentActivity).title(G.context.getString(R.string.choose_picture)).negativeText(G.context.getString(R.string.cancel)).items(R.array.profile).itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                            AttachFile attachFile = new AttachFile(G.fragmentActivity);

                            if (text.toString().equals(G.context.getString(R.string.from_camera))) {
                                try {
                                    attachFile.requestTakePicture(fragment);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    attachFile.requestOpenGalleryForImageSingleSelect(fragment);
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

        public MessageProgress messageProgress;
        public String mPath = "";
        private ImageView img;

        ViewHolderItem(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgBackground);

            messageProgress = (MessageProgress) itemView.findViewById(R.id.progress);
            AppUtils.setProgresColor(messageProgress.progressBar);

            messageProgress.withDrawable(R.drawable.ic_download, true);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPath.length() > 0) {
                        if (onImageClick != null) {
                            onImageClick.onClick(mPath);
                        }
                    }
                }
            });
        }
    }
}
