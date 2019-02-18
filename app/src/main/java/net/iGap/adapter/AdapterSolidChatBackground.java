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
import java.util.Arrays;

public class AdapterSolidChatBackground extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private ArrayList<String> mList;
    private FragmentChatBackground.OnImageClick onImageClick;
    private Fragment fragment;
    private Enum imgSwitcher;
ArrayList<String> palletList=new ArrayList<>(Arrays.asList("#2962ff","#00b8d4",
        "#b71c1c","#e53935","#e57373",
        "#880e4f","#d81b60","#f06292",
        "#4a148c","#8e24aa","#ba68c8",
        "#311b92","#5e35b1","#9575cd",
        "#1a237e","#3949ab","#7986cb",
        "#0d47a1","#1e88e5","#64b5f6",
        "#01579b","#039be5","#4fc3f7",
        "#006064","#00acc1","#4dd0e1",
        "#004d40","#00897b","#4db6ac",
        "#1b5e20","#43a047","#81c784",
        "#33691e","#7cb342","#aed581",
        "#827717","#c0ca33","#dce775",
        "#f57f17","#fdd835","#fff176",
        "#ff6f00","#ffb300","#ffd54f",
        "#e65100","#fb8c00","#fb8c00",
        "#bf360c","#f4511e","#ff8a65",
        "#3e2723","#6d4c41","#a1887f",
        "#212121","#757575","#e0e0e0",
        "#263238","#546e7a","#90a4ae"));
    public AdapterSolidChatBackground(Fragment fragment, ArrayList<String> List, FragmentChatBackground.OnImageClick onImageClick) {
        this.fragment = fragment;
        this.mList = List;
        this.onImageClick = onImageClick;
        this.imgSwitcher=imgSwitcher;
        mList.addAll(palletList);
        ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_image, parent, false);
            return new ViewHolderItem(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final ViewHolderItem holder2 = (ViewHolderItem) holder;
            holder2.img.setImageDrawable(null);

            if (mList.size() < (position + 1)) {
                return;
            }
           String wallpaper = mList.get(position);


               holder2.img.setBackgroundColor(Color.parseColor(wallpaper));


            String bigImagePath;

                bigImagePath = wallpaper;

            if (bigImagePath!=null) {
                holder2.messageProgress.setVisibility(View.GONE);
                try{
                    if (wallpaper != null)
                        holder2.mPath = wallpaper;
                    else
                        holder2.mPath = bigImagePath;
                }catch (Exception e){}

            } else {
                holder2.mPath = "";
                holder2.messageProgress.setVisibility(View.VISIBLE);
            }

    }

    @Override
    public int getItemViewType(int position) {
            return mList.size();
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
