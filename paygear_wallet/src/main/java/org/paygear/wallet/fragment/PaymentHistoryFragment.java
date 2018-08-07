package org.paygear.wallet.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Order;
import org.paygear.wallet.web.Web;
import org.paygear.wallet.widget.OrderView;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.PaginateList;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.widget.ProgressLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentHistoryFragment extends Fragment {

    RecyclerView mList;
    ProgressLayout mProgress;
    ListItemAdapter adapter;

    PaginateList<Order> orderList;
    MaterialDialog progressDialog;
    private int mOrderType;
    private boolean mShowAppBar = true;

    public PaymentHistoryFragment() {
    }

    public static PaymentHistoryFragment newInstance(int orderType, boolean showAppBar) {
        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        Bundle args = new Bundle();
        args.putInt("OrderType", orderType);
        args.putBoolean("ShowAppBar", showAppBar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderType = getArguments().getInt("OrderType");
            mShowAppBar = getArguments().getBoolean("ShowAppBar");
        }


        RaadApp.paygearHistoryCloseWallet = new PaygearHistoryCloseWallet() {
            @Override
            public void closeWallet() {
                if (progressDialog != null) progressDialog.dismiss();
                getActivity().getFragmentManager().popBackStack();
            }

            @Override
            public void error() {
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();

            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);

        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        RaadToolBar appBar = view.findViewById(R.id.app_bar);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        appBar.setTitle(getString(R.string.payment_history));
        appBar.showBack();
        if (!mShowAppBar)
            appBar.hide();

        mList = view.findViewById(R.id.list);
        mList.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        DividerItemDecoration divider = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.list_divider));
        mList.addItemDecoration(divider);

        mProgress = view.findViewById(R.id.progress);
        mProgress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load(false);
            }
        });

        if (adapter != null) {
            setAdapter();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    load(false);
                }
            }, 250);
        }

        return view;
    }

    private void load(final boolean loadMore) {
        if (!loadMore)
            mProgress.setStatus(0);

        String lastId = loadMore && orderList != null && orderList.hasItems() ? orderList.items.get(orderList.items.size() - 1).id : null;
        Web.getInstance().getWebService().getOrders(Auth.getCurrentAuth().getId(), 0, mOrderType != 0 ? String.valueOf(mOrderType) : null, true, lastId, 40).enqueue(new Callback<PaginateList<Order>>() {
            @Override
            public void onResponse(Call<PaginateList<Order>> call, Response<PaginateList<Order>> response) {
                Boolean success = Web.checkResponse(PaymentHistoryFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    PaginateList<Order> result = response.body();
                    if (!loadMore) {
                        orderList = result;
                        setAdapter();
                    } else {
                        orderList.items.addAll(result.items);
                        orderList.hasNextPage = result.hasNextPage;
                        adapter.notifyDataSetChanged();

                        if (adapter != null)
                            adapter.finishLoading(true);
                    }
                } else {
                    if (orderList == null || !orderList.hasItems())
                        mProgress.setStatus(-1, getString(R.string.server_error));

                    if (adapter != null)
                        adapter.finishLoading(false);
                }
            }

            @Override
            public void onFailure(Call<PaginateList<Order>> call, Throwable t) {
                if (Web.checkFailureResponse(PaymentHistoryFragment.this, call, t)) {
                    if (orderList == null || !orderList.hasItems())
                        mProgress.setStatus(-1, getString(R.string.network_error));
                    if (adapter != null)
                        adapter.finishLoading(false);
                }
            }
        });
    }

    private void setAdapter() {
        if (adapter == null)
            adapter = new ListItemAdapter();
        mList.setAdapter(adapter);
        if (orderList == null || !orderList.hasItems())
            mProgress.setStatus(2, getString(R.string.no_item));
        else
            mProgress.setStatus(1);
    }

    class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {

        private int lastLoadingItemStatus;
        private String lastLoadingItemText;
        private int lastLoadingItem = -1;
        private boolean isLoadingMore;

        public void finishLoading(boolean success) {
            if (isLoadingMore) {
                lastLoadingItemStatus = success ? 1 : -1;
                lastLoadingItemText = "";
                notifyItemChanged(lastLoadingItem);
                isLoadingMore = false;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LinearLayout root = new LinearLayout(getContext());
            root.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
            root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            root.setOrientation(LinearLayout.VERTICAL);

            OrderView orderView = new OrderView(getContext());
            orderView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            orderView.setBackgroundResource(R.drawable.simple_selector);
            orderView.setTag("OrderView");
            root.addView(orderView);

            ProgressLayout progress = new ProgressLayout(getContext());
            progress.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, RaadCommonUtils.getPx(100, getContext())));
            progress.setStatus(1);
            progress.setTag("Progress");
            progress.setOnRetryButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    load(true);
                }
            });
            root.addView(progress);

            return new ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Order order = orderList.items.get(position);
            holder.orderView.setOrder(order);

            if (position > 0 && !isLoadingMore && (orderList != null && orderList.hasNextPage) && lastLoadingItem != position && (position == getItemCount() - 1)) {
                isLoadingMore = true;
                lastLoadingItem = position;
                holder.progress.setStatus(0);
                load(true);
            } else {
                if (position == lastLoadingItem)
                    holder.progress.setStatus(lastLoadingItemStatus, "");
                else
                    holder.progress.setStatus(1);
            }
        }

        @Override
        public int getItemCount() {
            return orderList.items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            OrderView orderView;
            ProgressLayout progress;

            public ViewHolder(View view) {
                super(view);
                orderView = view.findViewWithTag("OrderView");
                progress = view.findViewWithTag("Progress");

                orderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() instanceof NavigationBarActivity) {
                            Order order = orderList.items.get(getAdapterPosition());
                            if (order.isPay()){
                                RaadApp.paygearHistoryOpenChat.paygearId(order.receiver.id);
                            }else {

                                RaadApp.paygearHistoryOpenChat.paygearId(order.sender.id);
                            }

                            showProgress();
                        }
                    }
                });
            }
        }


    }

    private void showProgress() {
        progressDialog = new MaterialDialog.Builder(getContext())
                .content(R.string.please_wait)
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .build();

        progressDialog.show();
    }

    public interface PaygearHistoryOpenChat {

        void paygearId(String id);

    }

    public interface PaygearHistoryCloseWallet {
        void closeWallet();

        void error();
    }


}

