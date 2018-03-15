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
import android.widget.TextView;

import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

public class TimeItem extends AbstractMessage<TimeItem, TimeItem.ViewHolder> {

    public TimeItem(Realm realmChat, IMessageItem messageClickListener) {
        super(realmChat, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutTime;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.cslt_txt_time_date) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getTimeItem());

        }

        holder.text = (TextView) holder.itemView.findViewById(R.id.cslt_txt_time_date);

        super.bindView(holder, payloads);

        setTextIfNeeded(holder.text, mMessage.messageText);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;

        public ViewHolder(View view) {
            super(view);
            /**
             *  this commented code used with xml layout
             */
            //text = (TextView) view.findViewById(R.id.cslt_txt_time_date);
        }
    }
}
