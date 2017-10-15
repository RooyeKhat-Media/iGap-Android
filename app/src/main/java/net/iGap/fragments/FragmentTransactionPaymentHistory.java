/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import java.util.ArrayList;
import java.util.List;
import net.iGap.R;
import net.iGap.adapter.items.AdapterTransactionPaymentHistory;

public class FragmentTransactionPaymentHistory extends BaseFragment {

    public static String tag_payment_type = "tag_payment_type";

    private FastAdapter fastAdapter;
    private ItemAdapter itemAdapter;
    private ArrayList<StructHistoryPayment> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private PaymentTaype paymentTaype = PaymentTaype.weekly;

    public enum PaymentTaype {
        weekly, monthly, yearly;
    }

    public enum PyamentAction {
        pending, completed;
    }

    public class StructHistoryPayment {

        public String time1 = "";
        public String time2 = "";
        public PyamentAction pyamentAction = PyamentAction.pending;
        public String price = "";
        public String comment = "";
    }

    public static FragmentTransactionPaymentHistory newInstance(PaymentTaype paymentTaype) {
        Bundle bundle = new Bundle();
        bundle.putString(tag_payment_type, paymentTaype.toString());
        FragmentTransactionPaymentHistory fragment = new FragmentTransactionPaymentHistory();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_transaction_payment_history, container, false);
        return attachToSwipeBack(fragmentView);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        String mode = bundle.getString(tag_payment_type);

        TextView txtMode = (TextView) view.findViewById(R.id.ftph_txt_mode);

        txtMode.setText(mode);

        recyclerView = (RecyclerView) view.findViewById(R.id.ftph_recycleview_transaction_history);

        initRecycleView();

        fillList();

        fillAdapter();
    }

    private void fillList() {

        StructHistoryPayment item = new StructHistoryPayment();

        item.comment = "resturant mahele";
        item.pyamentAction = PyamentAction.completed;
        item.price = "200,000 Rials";
        item.time1 = "22";
        item.time2 = "jun";

        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
        list.add(item);
    }

    private void fillAdapter() {

        switch (paymentTaype) {

            case weekly:
                fillAdapterWeekly();
                break;
            case monthly:
                fillAdapterMonthly();
                break;
            case yearly:
                fillAdapterYearly();
                break;
        }
    }

    private void fillAdapterYearly() {
    }

    private void fillAdapterMonthly() {
    }

    private void fillAdapterWeekly() {

        List<IItem> items = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            items.add(new AdapterTransactionPaymentHistory().setContact(list.get(i)).withIdentifier(100 + i));
        }

        itemAdapter.add(items);
    }

    private void initRecycleView() {

        fastAdapter = new FastAdapter();
        itemAdapter = new ItemAdapter();

        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<IItem>() {
            @Override public boolean onClick(View v, IAdapter adapter, IItem currentItem, int position) {

                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter.wrap(fastAdapter));
    }
}
