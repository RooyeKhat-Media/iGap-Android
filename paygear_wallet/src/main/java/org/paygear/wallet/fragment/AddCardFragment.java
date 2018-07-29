package org.paygear.wallet.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.paygear.wallet.R;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.utils.BankUtils;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;

import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddCardFragment extends Fragment {

    private RaadToolBar appBar;
    private ImageView imageView;
    private EditText cardNumberText;
    private EditText monthText;
    private EditText yearText;
    private SwitchCompat defaultCardSwitch;
    private TextView button;
    private ProgressBar progressBar;

    private int bankLogoRes = R.drawable.bank_logo_default;

    private String mNumber;
    private int mMonth;
    private int mYear;

    public AddCardFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_card, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        ViewGroup rootCardView = view.findViewById(R.id.rootCardView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
            rootCardView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        }



        appBar = view.findViewById(R.id.app_bar);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape,true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor),PorterDuff.Mode.SRC_IN));
        appBar.setTitle(getString(R.string.add_new_card));
        appBar.showBack();

        cardNumberText = view.findViewById(R.id.card_number);

        monthText = view.findViewById(R.id.month);
        yearText = view.findViewById(R.id.year);
        imageView = view.findViewById(R.id.image);
        TextView defaultCardTitle = view.findViewById(R.id.card_number_title);
        TextView cardNumberTitle = view.findViewById(R.id.default_card_title);
        final TextView monthTitle = view.findViewById(R.id.month_title);
        TextView yearTitle = view.findViewById(R.id.year_title);
        defaultCardSwitch = view.findViewById(R.id.default_card_switch);
        button = view.findViewById(R.id.button);
        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.button_green_selector_24dp);
        if (mDrawableSkip != null) {
            mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
        }
        if (WalletActivity.isDarkTheme) {
            cardNumberText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            cardNumberText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            monthText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            monthText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            yearText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            yearText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            defaultCardTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cardNumberTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            yearTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            monthTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        }

        progressBar = view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColorWhite), PorterDuff.Mode.SRC_IN);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_BOLD, cardNumberTitle, monthTitle, yearTitle, defaultCardTitle, button);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_REGULAR, cardNumberText, monthText, yearText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddCard();
            }
        });

        imageView.setImageResource(bankLogoRes);

        cardNumberText.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNumber = s.toString()
                        .replaceAll(" ", "")
                        .replaceAll("-", "");

                BankUtils bank = BankUtils.getBank(getContext(), mNumber);
                if (bankLogoRes != bank.getLogoRes()) {
                    bankLogoRes = bank.getLogoRes();
                    imageView.setImageResource(bank.getLogoRes());
                }
                if (mNumber.length() == 16)
                    monthText.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSettingText) return;
                isSettingText = true;
                cardNumberText.setText(Utils.formatCardNumber(mNumber));
                cardNumberText.setSelection(cardNumberText.length());
                isSettingText = false;
            }
        });

        monthText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 2)
                    yearText.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        yearText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startAddCard();
                }
                return false;
            }
        });


        cardNumberText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(25) });
        monthText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });
        yearText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(2) });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.hideKeyboard(getContext(), yearText);
    }

    private void startAddCard() {
        if (!validateFields())
            return;

        setLoading(true);
        Map<String, Object> map = new HashMap<>();
        map.put("card_number", mNumber);
        map.put("exp_m", mMonth);
        map.put("exp_y", mYear);
        map.put("default", defaultCardSwitch.isChecked());

        Web.getInstance().getWebService().addCard(PostRequest.getRequestBody(map)).enqueue(new Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                Boolean success = Web.checkResponse(AddCardFragment.this, call, response);
                if (success == null)
                    return;

                setLoading(false);

                if (success) {
                    ((NavigationBarActivity) getActivity()).broadcastMessage(
                            AddCardFragment.this, null, CardsFragment.class);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                if (Web.checkFailureResponse(AddCardFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private boolean validateFields() {
        mMonth = -1;
        mYear = -1;

        if (TextUtils.isEmpty(mNumber) || mNumber.length() < 16) {
            Toast.makeText(getActivity(), R.string.enter_card_number, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (monthText.length() < 2) {
            Toast.makeText(getActivity(), R.string.enter_expire_date_month, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                mMonth = Integer.parseInt(monthText.getText().toString());
            } catch (NumberFormatException e) { mMonth = -1; }
            if (mMonth < 1 || mMonth > 12) {
                Toast.makeText(getActivity(), R.string.enter_expire_date_month_correctly, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (yearText.length() < 2) {
            Toast.makeText(getActivity(), R.string.enter_expire_date_year, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            try {
                mYear = Integer.parseInt(yearText.getText().toString());
            } catch (NumberFormatException e) {
                mYear = -1;
            }

            if (mYear == -1) {
                Toast.makeText(getActivity(), R.string.enter_expire_date_year, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void setLoading(boolean loading) {
        cardNumberText.setEnabled(!loading);
        monthText.setEnabled(!loading);
        yearText.setEnabled(!loading);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(R.string.save));
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (loading) {
            Utils.hideKeyboard(getContext(), cardNumberText);
        }
    }

}
