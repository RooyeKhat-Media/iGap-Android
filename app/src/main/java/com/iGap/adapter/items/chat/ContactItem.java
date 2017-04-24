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
import android.widget.ImageView;
import android.widget.TextView;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import java.util.List;

public class ContactItem extends AbstractMessage<ContactItem, ContactItem.ViewHolder> {

    public ContactItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    protected void updateLayoutForSend(ViewHolder holder) {
        super.updateLayoutForSend(holder);
        //  holder.name.setTextColor(Color.WHITE);
        //  holder.number.setTextColor(Color.WHITE);
        holder.image.setImageResource(R.drawable.black_contact);
    }


    @Override
    protected void updateLayoutForReceive(ViewHolder holder) {
        super.updateLayoutForReceive(holder);
        holder.name.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        holder.number.setTextColor(holder.itemView.getResources().getColor(R.color.colorOldBlack));
        holder.image.setImageResource(R.drawable.green_contact);
    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutContact;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_contact;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
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

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView number;
        protected ImageView image;
        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            number = (TextView) view.findViewById(R.id.number);
            image = (ImageView) view.findViewById(R.id.image);
        }

    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
