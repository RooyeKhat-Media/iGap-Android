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
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

public class ContactItem extends AbstractMessage<ContactItem, ContactItem.ViewHolder> {

    public ContactItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        AppUtils.setImageDrawable(((ImageView) holder.itemView.findViewById(R.id.image)), R.drawable.black_contact);
    }

    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        ((TextView) holder.itemView.findViewById(R.id.name)).setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        ((TextView) holder.itemView.findViewById(R.id.name)).setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        AppUtils.setImageDrawable(((ImageView) holder.itemView.findViewById(R.id.image)), R.drawable.green_contact);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutContact;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {

        if (holder.itemView.findViewById(R.id.mainContainer) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getContactItem());
        }

        holder.name = (TextView) holder.itemView.findViewById(R.id.name);
        holder.number = (TextView) holder.itemView.findViewById(R.id.number);


        super.bindView(holder, payloads);

        if (mMessage.forwardedFrom != null) {
            if (mMessage.forwardedFrom.getRoomMessageContact() != null) {
                holder.name.setText(mMessage.forwardedFrom.getRoomMessageContact().getFirstName() + " " + mMessage.forwardedFrom.getRoomMessageContact().getLastName());
                holder.number.setText(mMessage.forwardedFrom.getRoomMessageContact().getLastPhoneNumber());
            }
        } else {
            if (mMessage.userInfo != null) {
                holder.name.setText(mMessage.userInfo.displayName);
                holder.number.setText(mMessage.userInfo.phone);
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * this commented code used with xml layout
         */
        protected TextView name;
        protected TextView number;
        //   protected ImageView image;

        public ViewHolder(View view) {
            super(view);
            //name = (TextView) view.findViewById(R.id.name);
            //number = (TextView) view.findViewById(R.id.number);
            //image = (ImageView) view.findViewById(R.id.image);
        }
    }
}
