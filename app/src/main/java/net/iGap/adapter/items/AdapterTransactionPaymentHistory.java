/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.adapter.items;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mikepenz.fastadapter.items.AbstractItem;
import java.util.List;
import net.iGap.R;
import net.iGap.fragments.FragmentTransactionPaymentHistory;

public class AdapterTransactionPaymentHistory extends AbstractItem<AdapterTransactionPaymentHistory, AdapterTransactionPaymentHistory.ViewHolder> {
    public FragmentTransactionPaymentHistory.StructHistoryPayment item;

    public AdapterTransactionPaymentHistory setContact(FragmentTransactionPaymentHistory.StructHistoryPayment item) {
        this.item = item;
        return this;
    }

    @Override public int getType() {
        return R.id.ftphs_txt_time1;
    }

    @Override public int getLayoutRes() {
        return R.layout.fragment_transaction_payment_history_sub_layout;
    }

    @Override public void bindView(ViewHolder h, List payloads) {
        super.bindView(h, payloads);

        h.txtTime1.setText(item.time1);
        h.txtTime2.setText(item.time2);
        h.txtPrice.setText(item.price);
        h.txtComment.setText(item.comment);

        switch (item.pyamentAction) {
            case pending:
                h.txtCondition.setText(R.string.pending);
                h.imvCircle.setImageResource(R.drawable.circle_red);
                break;
            case completed:
                h.txtCondition.setText(R.string.completed);
                h.imvCircle.setImageResource(R.drawable.circle_color_notificatin_setting);
                break;
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView txtTime1;
        protected TextView txtTime2;
        protected ImageView imvCircle;
        protected TextView txtPrice;
        protected TextView txtComment;
        protected TextView txtCondition;
        protected net.iGap.module.MaterialDesignTextView txtRightArrowIcon;

        public ViewHolder(View view) {
            super(view);

            txtTime1 = (TextView) view.findViewById(R.id.ftphs_txt_time1);
            txtTime2 = (TextView) view.findViewById(R.id.ftphs_txt_time2);
            imvCircle = (ImageView) view.findViewById(R.id.ftphs_imv_circle);
            txtPrice = (TextView) view.findViewById(R.id.ftphs_txt_price);
            txtComment = (TextView) view.findViewById(R.id.ftphs_txt_comment);
            txtCondition = (TextView) view.findViewById(R.id.ftphs_txt_condition);
            txtCondition = (TextView) view.findViewById(R.id.ftphs_txt_condition);
            txtRightArrowIcon = (net.iGap.module.MaterialDesignTextView) view.findViewById(R.id.ftphs_txt_icon_right_arrow);
        }
    }

    @Override public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }
}


