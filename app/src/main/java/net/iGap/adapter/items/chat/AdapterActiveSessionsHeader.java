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
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;
import net.iGap.module.structs.StructSessions;

import java.util.List;

public class AdapterActiveSessionsHeader extends AbstractItem<AdapterActiveSessionsHeader, AdapterActiveSessionsHeader.ViewHolder> {

    public StructSessions item;

    private List<StructSessions> itemList;

    public AdapterActiveSessionsHeader(List<StructSessions> item) {
        itemList = item;
    }

    public List<StructSessions> getItem() {
        return itemList;
    }

    public void setItem(List<StructSessions> item) {
        this.itemList = item;
    }

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.adph_rootLayout;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_active_sessions_header;
    }

    //The logic to bind your data to the view

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup root;

        public ViewHolder(View view) {
            super(view);

            root = (ViewGroup) view.findViewById(R.id.adph_rootLayout);
        }
    }
}