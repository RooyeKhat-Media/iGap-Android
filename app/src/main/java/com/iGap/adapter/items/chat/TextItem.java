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
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import com.iGap.G;
import com.iGap.R;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import io.github.meness.emoji.EmojiTextView;
import java.util.List;

public class TextItem extends AbstractMessage<TextItem, TextItem.ViewHolder> {

    public TextItem(ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutMessage;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_message;
    }

    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        String text;
        if (mMessage.forwardedFrom != null) {
            text = mMessage.forwardedFrom.getMessage();
        } else {
            text = mMessage.messageText;
        }
        setTextIfNeeded(holder.messageText, text);

        if (mMessage.hasLinkInMessage) {

            holder.llTime.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {

                }
            });
        } else {
            holder.messageText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override public boolean onLongClick(View v) {
                    holder.itemView.performLongClick();
                    return false;
                }
            });

            holder.messageText.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (!isSelected()) {
                        if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            return;
                        }
                        if (mMessage.status.equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            messageClickListener.onFailedMessageClick(v, mMessage, holder.getAdapterPosition());
                        } else {
                            messageClickListener.onContainerClick(v, mMessage, holder.getAdapterPosition());
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected EmojiTextView messageText;
        protected LinearLayout llTime;


        public ViewHolder(View view) {
            super(view);

            llTime = (LinearLayout) view.findViewById(R.id.csl_ll_time);
            messageText = (EmojiTextView) view.findViewById(R.id.messageText);
            messageText.setTextSize(G.userTextSize);
            messageText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
