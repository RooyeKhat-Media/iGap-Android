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

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

public class UnreadMessage extends AbstractMessage<UnreadMessage, UnreadMessage.ViewHolder> {

    public UnreadMessage(Realm realmChat, IMessageItem messageClickListener) {
        super(realmChat, false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.cslum_txt_unread_message;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.cslum_txt_unread_message) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getUnreadMessageItem());
        }

        holder.txtUnreadMessage = (TextView) holder.itemView.findViewById(R.id.cslum_txt_unread_message);
        holder.txtUnreadMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.txtUnreadMessage.setBackgroundColor(Color.parseColor(G.appBarColor));

        holder.txtUnreadMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        super.bindView(holder, payloads);

        setTextIfNeeded(holder.txtUnreadMessage, mMessage.messageText);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtUnreadMessage;

        public ViewHolder(View view) {
            super(view);
            /**
             *  this commented code used with xml layout
             */
            //txtUnreadMessage = (TextView) view.findViewById(R.id.cslum_txt_unread_message);
            //txtUnreadMessage.setOnClickListener(new View.OnClickListener() {
            //    @Override public void onClick(View v) {
            //
            //    }
            //});
            //
            //txtUnreadMessage.setOnLongClickListener(new View.OnLongClickListener() {
            //    @Override public boolean onLongClick(View v) {
            //        return false;
            //    }
            //});
        }
    }
}
