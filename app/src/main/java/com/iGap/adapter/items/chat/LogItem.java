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
import android.widget.TextView;
import com.iGap.R;
import com.iGap.helper.HelperLogMessage;
import com.iGap.interfaces.IMessageItem;
import com.iGap.proto.ProtoGlobal;
import java.util.List;

public class LogItem extends AbstractMessage<LogItem, LogItem.ViewHolder> {

    public LogItem(IMessageItem messageClickListener) {
        super(false, ProtoGlobal.Room.Type.CHAT, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLog;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);

        //Realm realm=Realm.getDefaultInstance();
        //
        //RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID ,Long.parseLong(mMessage.messageID)).findFirst();
        //if(roomMessage!=null){
        //
        //}
        //
        //realm.close();

        holder.text.setText(HelperLogMessage.getLogMessageWithLink(mMessage.messageText));

        // setTextIfNeeded(holder.text, mMessage.messageText);
    }

    @Override
    protected void voteAction(ViewHolder holder) {
        super.voteAction(holder);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView text;
        public ViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
            text.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}
