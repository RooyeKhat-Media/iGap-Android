package org.paygear.wallet.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.CashoutUserConfirm;
import org.paygear.wallet.model.CreditLimit;
import org.paygear.wallet.model.Order;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import ir.radsense.raadcore.widget.ProgressLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CashOutRequestFragment extends Fragment {

    public static final int REQUEST_CASH_IN = 0;
    public static final int REQUEST_CASH_OUT_NORMAL = 1;
    public static final int REQUEST_CASH_OUT_IMMEDIATE = 2;
    public static final int REQUEST_P2P_PAYMENT = 3;


    ProgressLayout progress;
    ProgressBar progressBar;
    TextView button;
    TextView limitTextView1;
    TextView limitTextView2;

    TextView priceTitle;
    EditText priceText;

    TextView numberTitle;
    AutoCompleteTextView numberText;

    TextView hintText;


    Card mCard;
    int mRequestType;

    String mPrice = "";
    String mNumber = "";

    ArrayList<Card> mCards;
    Card mSelectedCard;

    public CashOutRequestFragment() {
    }

    public static CashOutRequestFragment newInstance(Card card, int requestType) {
        CashOutRequestFragment fragment = new CashOutRequestFragment();
        Bundle args = new Bundle();
        args.putSerializable("Card", card);
        args.putInt("RequestType", requestType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCard = (Card) getArguments().getSerializable("Card");
            mRequestType = getArguments().getInt("RequestType");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_out_request, container, false);

        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }

        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColorWhite), PorterDuff.Mode.SRC_IN);
        button = view.findViewById(R.id.button);
        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.button_green_selector_24dp);
        if (mDrawableSkip != null) {
            mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mRequestType) {
                    case REQUEST_CASH_IN:
                        if (TextUtils.isEmpty(priceText.getText())) {
                            Toast.makeText(getContext(), R.string.enter_cash_in_price, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Payment payment = new Payment();
                        Account account = new Account();
                        account.id = Auth.getCurrentAuth().getId();
                        payment.account = account;
                        payment.price = Long.parseLong(mPrice);
                        payment.orderType = Order.ORDER_TYPE_CHARGE_CREDIT;
                        payment.isCredit = false;

                        initPay(payment);
                        break;
                    case REQUEST_CASH_OUT_NORMAL:
                        if (!mCard.isProtected) {
                            showSetPinConfirm();
                        } else {
                            if (TextUtils.isEmpty(priceText.getText()) || (mRequestType != REQUEST_CASH_IN && TextUtils.isEmpty(numberText.getText()))) {
                                Toast.makeText(getContext(), R.string.enter_info_completely, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            showPinConfirm();
                        }
                        break;
                    case REQUEST_CASH_OUT_IMMEDIATE:
                        startUserConfirm();
                        break;
                }
                Utils.hideKeyboard(getContext(), priceText);
            }
        });

        limitTextView1 = view.findViewById(R.id.limit1);
        limitTextView2 = view.findViewById(R.id.limit2);

        priceTitle = view.findViewById(R.id.price_title);
        ViewGroup rootPrice = view.findViewById(R.id.rootPrice);
        rootPrice.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        priceText = view.findViewById(R.id.price);

        numberTitle = view.findViewById(R.id.number_title);
        numberText = view.findViewById(R.id.number);

        hintText = view.findViewById(R.id.hint);


        if (WalletActivity.isDarkTheme) {

            priceTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            priceText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            priceText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            numberText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            numberTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            numberText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            hintText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        }


        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_BOLD, button, limitTextView2, priceTitle, numberTitle);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_REGULAR, limitTextView1, priceText, numberText, hintText);

        limitTextView1.setText(getString(R.string.paygear_account_balance) + ":   " +
                RaadCommonUtils.formatPrice(RaadApp.paygearCard.balance, true));

        limitTextView2.setText(getString(R.string.cashable_balance) + ":   " +
                RaadCommonUtils.formatPrice(RaadApp.paygearCard.cashOutBalance, true));


        priceText.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrice = s.toString().replaceAll(",", "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isSettingText) return;
                isSettingText = true;
                String s = null;
                try {
                    s = String.format(Locale.US, "%,d", Long.parseLong(mPrice));
                } catch (NumberFormatException e) {
                }
                priceText.setText(s);
                priceText.setSelection(priceText.length());
                isSettingText = false;
            }
        });


        numberText.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNumber = s.toString()
                        .replaceAll(" ", "")
                        .replaceAll("-", "");
            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (mRequestType) {
                    case REQUEST_CASH_OUT_NORMAL:
                        if (!s.toString().startsWith("IR")) {
                            numberText.setText("IR" + (!s.toString().equals("I") ? s : ""));
                            Selection.setSelection(numberText.getText(), numberText.getText().length());
                        }
                        break;
                    case REQUEST_CASH_OUT_IMMEDIATE:
                        if (mSelectedCard != null) {
                            numberText.setTextColor(Color.parseColor("#212121"));
                            mSelectedCard = null;
                        }
                        if (isSettingText) return;
                        isSettingText = true;
                        numberText.setText(Utils.formatCardNumber(mNumber));
                        numberText.setSelection(numberText.length());
                        isSettingText = false;
                        break;
                }
            }
        });

        progress.setStatus(1);
        setViews();
        if (mRequestType == REQUEST_CASH_OUT_IMMEDIATE)
            loadCashOutCards();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.hideKeyboard(getContext(), priceText);
    }

    private void setViews() {
        switch (mRequestType) {
            case REQUEST_CASH_IN:
                priceTitle.setText(R.string.enter_cash_in_price);
                numberTitle.setText(R.string.enter_your_card_number);
                numberTitle.setVisibility(View.GONE);
                numberText.setVisibility(View.GONE);
                numberText.setHint(R.string.card_16_digits);
                numberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                hintText.setText(R.string.cash_in_hint);
                break;
            case REQUEST_CASH_OUT_NORMAL:
                priceTitle.setText(R.string.enter_cash_out_price);
                numberTitle.setText(R.string.enter_your_sheba_number);
                hintText.setText(R.string.cash_out_normal_hint);
                numberText.setHint(R.string.sheba_20_digits);
                numberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(26)});
                break;
            case REQUEST_CASH_OUT_IMMEDIATE:
                priceTitle.setText(R.string.enter_cash_out_price);
                numberTitle.setText(R.string.enter_your_card_number);
                hintText.setText(R.string.cash_out_immediate_hint);
                numberText.setHint(R.string.card_16_digits);
                numberText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                break;
        }
    }

    private void loadLimits() {
        progress.setStatus(0);
        Web.getInstance().getWebService().getCashOutLimits(Auth.getCurrentAuth().getId(), mCard.token).enqueue(new Callback<CreditLimit>() {
            @Override
            public void onResponse(Call<CreditLimit> call, Response<CreditLimit> response) {
                Boolean success = Web.checkResponse(CashOutRequestFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    String priceStr = RaadCommonUtils.formatPrice(mCard.cashOutBalance, true);
                    String text = getString(R.string.cashable_price) + "  " + priceStr;
                    limitTextView1.setText(text);
                    priceStr = RaadCommonUtils.formatPrice(response.body().todayLimit, true);
                    text = getString(R.string.cashout_today_limit) + "  " + priceStr;
                    limitTextView2.setText(text);
                    //setAdapter();
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<CreditLimit> call, Throwable t) {
                if (Web.checkFailureResponse(CashOutRequestFragment.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });


    }

    private void loadCashOutCards() {
        Web.getInstance().getWebService().getCards(null, true).enqueue(new Callback<ArrayList<Card>>() {
            @Override
            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                Boolean success = Web.checkResponse(CashOutRequestFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mCards = response.body();

                    String[] array = new String[mCards.size()];
                    for (int i = 0; i < array.length; i++) {
                        array[i] = mCards.get(i).cardNumber;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_list_item_1, array);
                    numberText.setAdapter(adapter);
                    numberText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            for (Card card : mCards) {
                                if (mNumber.equals(card.cardNumber)) {
                                    mSelectedCard = card;
                                    numberText.setTextColor(Color.GRAY);
                                    break;
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                if (Web.checkFailureResponse(CashOutRequestFragment.this, call, t)) {

                }
            }
        });
    }

    private void startUserConfirm() {
        if (TextUtils.isEmpty(priceText.getText()) || (mRequestType != REQUEST_CASH_IN && TextUtils.isEmpty(numberText.getText()))) {
            Toast.makeText(getContext(), R.string.enter_info_completely, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!mCard.isProtected) {
            showSetPinConfirm();
            return;
        }

        setLoading(true);

        Web.getInstance().getWebService().getCashOutUserConfirm(Auth.getCurrentAuth().getId(), mPrice, mSelectedCard != null ? null : mNumber, mSelectedCard != null ? mSelectedCard.token : null).enqueue(new Callback<CashoutUserConfirm>() {
            @Override
            public void onResponse(Call<CashoutUserConfirm> call, Response<CashoutUserConfirm> response) {
                Boolean success = Web.checkResponse(CashOutRequestFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    showUserConfirmSummary(response.body());
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<CashoutUserConfirm> call, Throwable t) {
                if (Web.checkFailureResponse(CashOutRequestFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void showUserConfirmSummary(CashoutUserConfirm info) {
        long amount = 0;
        try {
            amount = Long.parseLong(mPrice);
        } catch (NumberFormatException e) {
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.requested_amount));
        sb.append(" : ");
        sb.append(RaadCommonUtils.formatPrice(amount, true));
        sb.append("\n");

        long deposit = 0;
        if (amount + info.transferFee <= mCard.cashOutBalance)
            deposit = amount;
        else if (amount - info.transferFee <= mCard.cashOutBalance)
            deposit = amount - info.transferFee;

        sb.append(getString(R.string.wage));
        sb.append(" : ");
        sb.append(RaadCommonUtils.formatPrice(info.transferFee, true));
        sb.append("\n");

        sb.append(getString(R.string.deposits));
        sb.append(" : ");
        sb.append(RaadCommonUtils.formatPrice(deposit, true));
        sb.append("\n");

        sb.append(getString(R.string.destination_card));
        sb.append(" : ");
        sb.append(numberText.getText().toString());
        sb.append("\n");

        sb.append(getString(R.string.destination_bank));
        sb.append(" : ");
        sb.append(info.owner.bankName);
        sb.append("\n");

        sb.append(getString(R.string.card_owner));
        sb.append(" : ");
        sb.append(info.owner.firstName);
        sb.append(" ");
        sb.append(info.owner.lastName);


        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.cashout_request))
                .content(sb.toString())
                .positiveText(getString(R.string.ok))
                .negativeText(getString(R.string.cancel))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showPinConfirm();
                    }
                })
                .show();

    }

    private void showPinConfirm() {


        setConfirmPassword();

    }

    private void showSetPinConfirm() {

        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.set_card_pin))
                .content(getString(R.string.credit_card_set_pin_confirm))
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        ((NavigationBarActivity) getActivity()).pushFullFragment(
                                new SetCardPinFragment(), "SetCardPinFragment");

                    }
                })
                .show();
    }

    private void startRequest(String pin) {
        long amount = 0;
        try {
            amount = Long.parseLong(mPrice);
        } catch (NumberFormatException e) {
        }
        if (amount == 0) {
            Toast.makeText(getContext(), R.string.enter_info_completely, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Map<String, Object> map = new HashMap<>();
        map.put("amount", amount);
        if (mCard.isProtected)
            map.put("pin", pin);


        if (mRequestType == REQUEST_CASH_OUT_NORMAL) {
            map.put("shaba_number", mNumber);
            map.put("is_instant", false);
        } else if (mRequestType == REQUEST_CASH_OUT_IMMEDIATE) {
            if (mSelectedCard != null) {
                map.put("card_token", mSelectedCard.token);
            } else {
                map.put("card_number", mNumber);
            }
            map.put("is_instant", true);
        }

        Web.getInstance().getWebService().requestCashOut(Auth.getCurrentAuth().getId(), mCard.token, PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(CashOutRequestFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    Toast.makeText(getContext(), R.string.cash_out_request_done, Toast.LENGTH_SHORT).show();
                    if (getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).broadcastMessage(
                                CashOutRequestFragment.this, null, CardsFragment.class);
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(CashOutRequestFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void initPay(final Payment payment) {
        setLoading(true);
        Web.getInstance().getWebService().initPayment(payment.getRequestBody()).enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(CashOutRequestFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    payment.paymentAuth = response.body();
                    if (getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).replaceFullFragment(
                                CardsFragment.newInstance(payment), "CardsFragment", true);
                    }
                } else {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(CashOutRequestFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });

    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(R.string.ok));
    }


    class CardsAdapter extends BaseAdapter implements Filterable {
        private ArrayList<Card> originalData = null;
        private ArrayList<Card> filteredData = null;
        private ItemFilter mFilter = new ItemFilter();

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int i) {
            return mCards.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView;
            if (view == null) {
                int dp8 = RaadCommonUtils.getPx(8, getContext());
                textView = new TextView(getContext());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                //textView.setGravity(Gravity.CENTER);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setTypeface(Typefaces.get(getContext(), Typefaces.IRAN_YEKAN_REGULAR));
                textView.setPadding(dp8, 0, dp8, 0);
            } else {
                textView = (TextView) view;
            }

            textView.setText(mCards.get(i).cardNumber);
            return view;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                int count = mCards.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);

                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = mCards.get(i).cardNumber;
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //filteredData = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }

        }
    }

    public void setConfirmPassword() {
        final LinearLayout layoutNickname = new LinearLayout(getActivity());
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        final View viewFirstName = new View(getActivity());
        viewFirstName.setBackgroundColor(getActivity().getResources().getColor(R.color.line_edit_text));
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextInputLayout inputNewPassWord = new TextInputLayout(getActivity());
        final EditText newPassWord = new EditText(getActivity());
        newPassWord.setHint(getActivity().getResources().getString(R.string.please_enter_your_password));
        newPassWord.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//        newPassWord.setTypeface(G.typeface_IRANSansMobile);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_LIGHT, newPassWord);
        newPassWord.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.dp14));
        newPassWord.setTextColor(getActivity().getResources().getColor(R.color.text_edit_text));
        newPassWord.setHintTextColor(getActivity().getResources().getColor(R.color.hint_edit_text));
        newPassWord.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassWord.setPadding(0, 8, 0, 8);
        newPassWord.setMaxLines(1);
        inputNewPassWord.addView(newPassWord);
        inputNewPassWord.addView(viewFirstName, viewParams);
        final View viewLastName = new View(getActivity());
        viewLastName.setBackgroundColor(getActivity().getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newPassWord.setBackground(getActivity().getResources().getDrawable(android.R.color.transparent));
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 15);
        LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lastNameLayoutParams.setMargins(0, 15, 0, 10);

        layoutNickname.addView(inputNewPassWord, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .title(getActivity().getResources().getString(R.string.your_password))
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(getActivity().getResources().getString(R.string.ok)).customView(layoutNickname, true)
                        .widgetColor(Color.parseColor(WalletActivity.primaryColor)).negativeText(getActivity().getResources().getString(R.string.cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        newPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewFirstName.setBackgroundColor(Color.parseColor(WalletActivity.primaryColor));
                } else {
                    viewFirstName.setBackgroundColor(getActivity().getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pin = newPassWord.getText().toString();
                if (!TextUtils.isEmpty(pin.trim())) {
                    startRequest(pin);
                }
            }
        });

        dialog.show();
    }


}
