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

import com.vanniktech.emoji.EmojiUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.EmojiTextViewE;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;

public class TextItem extends AbstractMessage<TextItem, TextItem.ViewHolder> {

    public TextItem(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
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

        if (holder.itemView.findViewById(R.id.mainContainer) == null) {
            ((ViewGroup) holder.itemView).addView(ViewMaker.getTextItem());
        }


        super.bindView(holder, payloads);

        String text;
        if (mMessage.forwardedFrom != null) {
            text = mMessage.forwardedFrom.getMessage();
        } else {
            text = mMessage.messageText;
        }

        if (mMessage.hasEmojiInText) {

            EmojiTextViewE textViewE = (EmojiTextViewE) holder.itemView.findViewById(R.id.messageSenderTextMessage);

            if (text.length() <= 2) {

                if (EmojiUtils.emojisCount(text) == 1) {
                    textViewE.setEmojiSize((int) G.context.getResources().getDimension(R.dimen.dp28));
                }
            }

            setTextIfNeeded(textViewE, text);


        } else {
            setTextIfNeeded((TextView) holder.itemView.findViewById(R.id.messageSenderTextMessage), text);
        }

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.itemView.performLongClick();
                return false;
            }
        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.isLinkClicked) {
                    G.isLinkClicked = false;
                    return;
                }
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

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        //  protected LinearLayout llTime;

        public ViewHolder(View view) {
            super(view);
            // llTime = (LinearLayout) view.findViewById(R.id.csl_ll_time);
        }
    }
}
