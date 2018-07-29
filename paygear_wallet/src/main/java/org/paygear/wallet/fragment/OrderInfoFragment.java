package org.paygear.wallet.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.paygear.wallet.R;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Order;
import org.paygear.wallet.model.PaymentEntryListItem;
import org.paygear.wallet.web.Web;
import org.paygear.wallet.widget.OrderView;

import java.util.ArrayList;
import java.util.Calendar;

import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.widget.ProgressLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderInfoFragment extends Fragment {

    private OrderView mOrderView;
    private RecyclerView mList;
    private ProgressLayout progress;

    private String mOrderId;
    private Order mOrder;

    private PaymentEntryListAdapter adapter;

    public OrderInfoFragment() {
    }

    public static OrderInfoFragment newInstance(String orderId) {
        OrderInfoFragment fragment = new OrderInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable("OrderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderId = getArguments().getString("OrderId");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order_info, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        RaadToolBar appBar = view.findViewById(R.id.app_bar);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape,true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor),PorterDuff.Mode.SRC_IN));
        appBar.showBack();
        appBar.setTitle(getString(R.string.order_info));

        mOrderView = view.findViewById(R.id.order_view);

        mList = view.findViewById(R.id.list);
        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadOrder();
            }
        });


        if (mOrder != null)
            updateInfo();
        else
            loadOrder();
        return view;
    }

    private void loadOrder() {
        progress.setStatus(0);
        Web.getInstance().getWebService().getSingleOrder(Auth.getCurrentAuth().getId(), mOrderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                Boolean success = Web.checkResponse(OrderInfoFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mOrder = response.body();
                    updateInfo();
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                if (Web.checkFailureResponse(OrderInfoFragment.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }

    private void updateInfo() {
        progress.setStatus(1);

        mOrderView.setOrder(mOrder);

        if (adapter == null) {
            setAdapter();
        } else {
            mList.setAdapter(adapter);
        }
    }

    private void setAdapter() {
        ArrayList<PaymentEntryListItem> items = new ArrayList<>();
        if (!TextUtils.isEmpty(mOrder.cardNumber))
            items.add(new PaymentEntryListItem(getString(R.string.source_card), mOrder.cardNumber, null, true));
        if (!TextUtils.isEmpty(mOrder.targetCardNumber))
            items.add(new PaymentEntryListItem(getString(R.string.destination_card), mOrder.targetCardNumber, null, true));
        if (!TextUtils.isEmpty(mOrder.targetShebaNumber))
            items.add(new PaymentEntryListItem(getString(R.string.destination_sheba_number), mOrder.targetShebaNumber, null, true));

        if (mOrder.traceNumber > 0)
            items.add(new PaymentEntryListItem(getString(R.string.trace_no), String.valueOf(mOrder.traceNumber), null, true));
        if (mOrder.invoiceNumber > 0)
            items.add(new PaymentEntryListItem(getString(R.string.reference_code), String.valueOf(mOrder.invoiceNumber), null, true));

        if (mOrder.orderType == Order.ORDER_TYPE_CASH_OUT) {
            Calendar calendar = Calendar.getInstance();

            if (mOrder.createdMicroTime > 0) {
                calendar.setTimeInMillis(mOrder.createdMicroTime);
                items.add(new PaymentEntryListItem(getString(R.string.request_time), RaadCommonUtils.getLocaleFullDateTime(calendar), null, true));
            }
            if (mOrder.paidMicroTime > 0) {
                calendar.setTimeInMillis(mOrder.paidMicroTime);
                items.add(new PaymentEntryListItem(getString(R.string.settlement_time), RaadCommonUtils.getLocaleFullDateTime(calendar), null, true));
            }

            items.add(new PaymentEntryListItem(null, getString(R.string.requested_amount_rial), RaadCommonUtils.formatPrice(mOrder.amount, false), false));
            //items.add(new PaymentEntryListItem(null, getString(R.string.settlement_wage), RaadCommonUtils.formatPrice(mOrder.paidAmount - mOrder.amount, false), false));
            items.add(new PaymentEntryListItem(null, getString(R.string.deposits), RaadCommonUtils.formatPrice(mOrder.paidAmount, false), false));
        } else {
            if (mOrder.isPay() && mOrder.receiver.type != Account.TYPE_USER) {
                /*if (mOrder.pod != null)
                    items.add(new PaymentEntryListItem(getString(R.string.delivery_place), mOrder.pod.toString(getContext()), null, true));
                items.add(new PaymentEntryListItem(getString(R.string.coupon), getString(mOrder.hasCoupon ? R.string.has : R.string.not_has), null, true));
                if (mOrder.cart != null)
                    items.add(new PaymentEntryListItem(getString(R.string.cart), getString(R.string.see_details), null, true));
*/
                items.add(new PaymentEntryListItem(null, getString(R.string.total_price), RaadCommonUtils.formatPrice(mOrder.getTotalPrice(), false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.discount), RaadCommonUtils.formatPrice(mOrder.discountPrice, false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.service_price), RaadCommonUtils.formatPrice(mOrder.additionalFee, false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.tax), RaadCommonUtils.formatPrice(mOrder.tax, false), false));
                items.add(new PaymentEntryListItem(null, getString(R.string.delivery_price), RaadCommonUtils.formatPrice(mOrder.deliveryPrice, false), false));
            }

            items.add(new PaymentEntryListItem(null, getString(R.string.final_price_rial), RaadCommonUtils.formatPrice(mOrder.amount, false), false));

        }
        adapter = new PaymentEntryListAdapter(getContext(), items, new PaymentEntryListAdapter.OnPaymentEntryItemClickListener() {
            @Override
            public void onItemClick(PaymentEntryListItem item) {
                /*if (item.isSelectable && item.title1 != null && item.title1.equals(getString(R.string.cart))) {
                    if (getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).replaceFragment(
                                CartInfoFragment.newInstance(mOrder.cart), "CartInfoFragment", true);
                    }
                }*/
            }
        });

        mList.setAdapter(adapter);
    }

}
