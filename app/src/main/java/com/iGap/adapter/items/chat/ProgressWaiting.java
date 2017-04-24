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
import android.widget.ProgressBar;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import java.util.List;

public class ProgressWaiting extends AbstractMessage<com.iGap.adapter.items.chat.ProgressWaiting, com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> {

    public ProgressWaiting(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cslp_progress_bar_waiting;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_progress;
    }

    @Override
    public void bindView(com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
    }

    @Override
    protected void voteAction(com.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.cslp_progress_bar_waiting);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
