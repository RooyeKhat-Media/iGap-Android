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
import android.widget.TextView;
import java.util.List;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.proto.ProtoGlobal;

public class UnreadMessage extends AbstractMessage<UnreadMessage, UnreadMessage.ViewHolder> {

    public UnreadMessage(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override public int getType() {
        return R.id.cslum_txt_unread_message;
    }

    @Override public int getLayoutRes() {
        return R.layout.chat_sub_layot_unread_message;
    }

    @Override public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        setTextIfNeeded(holder.txtUnreadMessage, mMessage.messageText);
    }

    @Override protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtUnreadMessage;

        public ViewHolder(View view) {
            super(view);

            txtUnreadMessage = (TextView) view.findViewById(R.id.cslum_txt_unread_message);
            txtUnreadMessage.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                }
            });

            txtUnreadMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    return false;
                }
            });
        }
    }
}
