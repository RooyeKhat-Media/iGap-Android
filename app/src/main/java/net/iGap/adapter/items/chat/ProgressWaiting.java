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
import android.widget.ProgressBar;
import java.util.List;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

public class ProgressWaiting extends AbstractMessage<net.iGap.adapter.items.chat.ProgressWaiting, net.iGap.adapter.items.chat.ProgressWaiting.ViewHolder> {

    public ProgressWaiting(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override public int getType() {
        return R.id.cslp_progress_bar_waiting;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layout_progress;
    }

    @Override public void bindView(net.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
    }

    @Override protected void voteAction(net.iGap.adapter.items.chat.ProgressWaiting.ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public ViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.cslp_progress_bar_waiting);
        }
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
