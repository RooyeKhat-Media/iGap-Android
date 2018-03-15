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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.HelperMimeType;
import net.iGap.module.structs.StructExplorerItem;

import java.util.ArrayList;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.ViewHolder> {

    private HelperMimeType helperMimeType;
    private ArrayList<StructExplorerItem> item;
    private ViewHolder viewholder;
    private OnItemClickListenerExplorer onItemClickListener;

    public AdapterExplorer(ArrayList<StructExplorerItem> items, OnItemClickListenerExplorer onItemClickListener) {
        item = items;
        this.onItemClickListener = onItemClickListener;
        helperMimeType = new HelperMimeType();
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        StructExplorerItem rowItem = item.get(position);
        holder.txtTitle.setText(rowItem.name);
        holder.imageView.setImageResource(rowItem.image);

        if (rowItem.image == R.mipmap.j_pic) {
            helperMimeType.LoadImageTumpnail(holder.imageView, rowItem.path);
        } else if (rowItem.image == R.mipmap.j_video) {
            helperMimeType.loadVideoThumbnail(holder.imageView, rowItem.path);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_layout_explorer, parent, false);
        viewholder = new ViewHolder(v);
        return viewholder;
    }

    public interface OnItemClickListenerExplorer {

        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.sle_sub_textView1);
            imageView = (ImageView) itemView.findViewById(R.id.sle_sub_imageView1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = ViewHolder.super.getPosition();
                    onItemClickListener.onItemClick(view, position);
                }
            });
        }
    }
}
