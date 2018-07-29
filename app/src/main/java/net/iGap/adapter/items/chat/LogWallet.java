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
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperRadius;
import net.iGap.interfaces.IMessageItem;
import net.iGap.module.ReserveSpaceRoundedImageView;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;

import java.util.Calendar;
import java.util.List;

import io.realm.Realm;

import static net.iGap.module.AndroidUtils.suitablePath;

public class LogWallet extends AbstractMessage<LogWallet, LogWallet.ViewHolder> {
    private Realm mRealm;

    public LogWallet(Realm realmChat, ProtoGlobal.Room.Type type, IMessageItem messageClickListener) {
        super(realmChat, true, type, messageClickListener);
    }

    @Override
    public int getType() {
        return R.id.chatSubLayoutLogWallet;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.chat_sub_layout_log_wallet;
    }


    @Override
    public void bindView(final ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        RealmRegisteredInfo mRealmRegisteredInfoFrom = RealmRegisteredInfo.getRegistrationInfo(getRealm(), mMessage.structWallet.fromUserId);
        RealmRegisteredInfo mRealmRegisteredInfoTo = RealmRegisteredInfo.getRegistrationInfo(getRealm(), mMessage.structWallet.toUserId);

        String persianCalander = HelperCalander.getPersianCalander(mMessage.structWallet.payTime * DateUtils.SECOND_IN_MILLIS);

        if (HelperCalander.isPersianUnicode) {

            holder.fromUserId.setText("" + mRealmRegisteredInfoFrom.getDisplayName());
            holder.toUserId.setText("" + mRealmRegisteredInfoTo.getDisplayName());
            holder.amount.setText("" + mMessage.structWallet.amount);
            holder.traceNumber.setText("" + mMessage.structWallet.traceNumber);
            holder.invoiceNumber.setText("" + mMessage.structWallet.invoiceNumber);
            holder.payTime.setText("" + persianCalander);

            holder.fromUserId_2.setText(R.string.wallet_from);
            holder.toUserId_2.setText(R.string.wallet_to);
            holder.amount_2.setText(R.string.wallet_amount);
            holder.traceNumber_2.setText(R.string.wallet_trace);
            holder.invoiceNumber_2.setText(R.string.wallet_invoice);
            holder.payTime_2.setText(R.string.wallet_data);
        } else {
            holder.fromUserId.setText(R.string.wallet_from);
            holder.toUserId.setText(R.string.wallet_to);
            holder.amount.setText(R.string.wallet_amount);
            holder.traceNumber.setText(R.string.wallet_trace);
            holder.invoiceNumber.setText(R.string.wallet_invoice);
            holder.payTime.setText(R.string.wallet_data);

            holder.fromUserId_2.setText("" + mRealmRegisteredInfoFrom.getDisplayName());
            holder.toUserId_2.setText("" + mRealmRegisteredInfoTo.getDisplayName());
            holder.amount_2.setText("" + mMessage.structWallet.amount);
            holder.traceNumber_2.setText("" + mMessage.structWallet.traceNumber);
            holder.invoiceNumber_2.setText("" + mMessage.structWallet.invoiceNumber);
            holder.payTime_2.setText("" + persianCalander);
        }

        if (mMessage.structWallet.description.isEmpty() || mMessage.structWallet.description.equals("")) {
            holder.rootDescription.setVisibility(View.GONE);
        } else {
            holder.description.setText(mMessage.structWallet.description);
        }


        getRealm().close();
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fromUserId;
        private TextView toUserId;
        private TextView amount;
        private TextView traceNumber;
        private TextView invoiceNumber;
        private TextView payTime;
        private TextView description;
        private ViewGroup rootDescription;


        private TextView fromUserId_2;
        private TextView toUserId_2;
        private TextView amount_2;
        private TextView traceNumber_2;
        private TextView invoiceNumber_2;
        private TextView payTime_2;


        protected ReserveSpaceRoundedImageView image;

        public ViewHolder(View view) {
            super(view);

            fromUserId = view.findViewById(R.id.fromUserId);
            toUserId = view.findViewById(R.id.toUserId);
            amount = view.findViewById(R.id.amount);
            traceNumber = view.findViewById(R.id.traceNumber);
            invoiceNumber = view.findViewById(R.id.invoiceNumber);
            payTime = view.findViewById(R.id.payTime);
            description = view.findViewById(R.id.description);
            rootDescription = view.findViewById(R.id.rootDescription);


            fromUserId_2 = view.findViewById(R.id.fromUserId_2);
            toUserId_2 = view.findViewById(R.id.toUserId_2);
            amount_2 = view.findViewById(R.id.amount_2);
            traceNumber_2 = view.findViewById(R.id.traceNumber_2);
            invoiceNumber_2 = view.findViewById(R.id.invoiceNumber_2);
            payTime_2 = view.findViewById(R.id.payTime_2);

        }
    }

    private Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }
        return mRealm;
    }
}
