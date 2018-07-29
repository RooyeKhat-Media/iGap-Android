package org.paygear.wallet.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.paygear.wallet.R;
import org.paygear.wallet.model.PaymentEntryListItem;

import java.util.ArrayList;

import ir.radsense.raadcore.utils.Typefaces;

/**
 * Created by Software1 on 9/14/2017.
 */

class PaymentEntryListAdapter extends RecyclerView.Adapter<PaymentEntryListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<PaymentEntryListItem> items;
    private OnPaymentEntryItemClickListener listener;

    public interface OnPaymentEntryItemClickListener {
        void onItemClick(PaymentEntryListItem item);
    }


    public PaymentEntryListAdapter(Context context, ArrayList<PaymentEntryListItem> items, OnPaymentEntryItemClickListener listener) {
        mContext = context;
        this.items = items;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_payment_entry_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        PaymentEntryListItem item = items.get(position);
        holder.title1.setText(item.title1);
        holder.title2.setText(item.title2);
        holder.value.setText(item.value);

        holder.title2.setTextColor(ContextCompat.getColor(mContext, item.isSelectable ? R.color.primary_text : R.color.secondary_text));
        holder.divider.setVisibility(item.isSelectable ? View.VISIBLE : View.GONE);
        holder.view.setBackgroundResource(item.isSelectable ? R.drawable.simple_selector : 0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView title1;
        TextView title2;
        TextView value;
        View divider;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title1 = view.findViewById(R.id.title1);
            title2 = view.findViewById(R.id.title2);
            value = view.findViewById(R.id.value);
            divider = view.findViewById(R.id.divider);
            Typefaces.setTypeface(mContext, Typefaces.IRAN_YEKAN_REGULAR, title1, title2, value);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    PaymentEntryListItem item = items.get(position);
                    if (listener != null) {
                        listener.onItemClick(item);
                    }
                }
            });


        }
    }

}
