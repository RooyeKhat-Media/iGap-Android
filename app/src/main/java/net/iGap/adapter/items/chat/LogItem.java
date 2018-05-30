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
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.HelperLogMessage;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

public class LogItem extends AbstractMessage<LogItem, LogItem.ViewHolder> {

    public LogItem(Realm realmChat, IMessageItem messageClickListener) {
        super(realmChat, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLog;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.csll_txt_log_text) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getLogItem());
        }

        holder.text = (TextView) holder.itemView.findViewById(R.id.csll_txt_log_text);
        holder.text.setMovementMethod(LinkMovementMethod.getInstance());

        super.bindView(holder, payloads);
        holder.text.setText(HelperLogMessage.deserializeLog(mMessage.logs, true));
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
            //text = (TextView) view.findViewById(R.id.csll_txt_log_text);
            //text.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
