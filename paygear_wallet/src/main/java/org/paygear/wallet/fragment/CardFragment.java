package org.paygear.wallet.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.paygear.wallet.R;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentResult;
import org.paygear.wallet.utils.BankUtils;
import org.paygear.wallet.utils.RSAUtils;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;
import org.paygear.wallet.widget.BankCardView;

import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.app.AlertDialog;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardFragment extends Fragment {

    private RaadToolBar appBar;
    private BankCardView cardView;
    private SwitchCompat defaultCardSwitch;
    private TextView button;
    private ProgressBar progressBar;
    private ProgressBar defaultCardProgress;

    private EditText pinText;
    private EditText cvv2Text;

    private Card mCard;
    private Payment mPayment;

    private boolean isUpdating;

    public CardFragment() {
    }

    public static CardFragment newInstance(Card card) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable("Card", card);
        fragment.setArguments(args);
        return fragment;
    }

    public static CardFragment newInstance(Payment payment, Card card) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable("Payment", payment);
        args.putSerializable("Card", card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPayment = (Payment) getArguments().getSerializable("Payment");
            mCard = (Card) getArguments().getSerializable("Card");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        appBar = view.findViewById(R.id.app_bar);
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        appBar.showBack();
        if (mPayment != null) {
            appBar.setTitle(getString(R.string.payment));
        } else {
            appBar.setTitle(BankUtils.getBank(getContext(), mCard.bankCode).getName());
        }

        cardView = view.findViewById(R.id.card_view);
        ViewGroup rootCarView = view.findViewById(R.id.rootCardView);
        rootCarView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        TextView defaultCardTitle = view.findViewById(R.id.default_card_title);
        defaultCardSwitch = view.findViewById(R.id.default_card_switch);
        button = view.findViewById(R.id.button);
        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.button_green_selector_24dp);
        if (mDrawableSkip != null) {
            if (WalletActivity.isDarkTheme) {
                mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.backgroundTheme_2), PorterDuff.Mode.SRC_IN));
            } else {
                mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
        }
        progressBar = view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColorWhite), PorterDuff.Mode.SRC_IN);
        defaultCardProgress = view.findViewById(R.id.default_card_progress);
        defaultCardProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColorWhite), PorterDuff.Mode.SRC_IN);

        TextView paymentPriceText = view.findViewById(R.id.payment_price);

        TextView pinTitle = view.findViewById(R.id.pin_title);
        TextView cvv2Title = view.findViewById(R.id.cvv2_title);
        pinText = view.findViewById(R.id.pin);
        cvv2Text = view.findViewById(R.id.cvv2);

        if (WalletActivity.isDarkTheme) {
            cvv2Text.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            cvv2Text.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            pinText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            pinText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            defaultCardTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            pinTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cvv2Title.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            paymentPriceText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        }

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_BOLD, defaultCardTitle, button,
                pinTitle, cvv2Title, paymentPriceText);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_REGULAR, pinText, cvv2Text);

        int cardHeight = BankCardView.getDefaultCardHeight(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, cardHeight);
        int dp16 = RaadCommonUtils.getPx(16, getContext());
        params.setMargins(dp16, dp16, dp16, dp16);
        cardView.setLayoutParams(params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPayment != null) {
                    startPayProcess();
                } else {
                    showDeleteConfirm();
                }
            }
        });


        if (mPayment != null) {
            paymentPriceText.setVisibility(View.VISIBLE);
            paymentPriceText.setText(getString(R.string.pay_with_price_x)
                    .replace("*", RaadCommonUtils.formatPrice(mPayment.getPaymentPrice(), true)));
            view.findViewById(R.id.pin_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.switch_layout).setVisibility(View.GONE);

//            if (mPayment.getPaymentPrice() <= Payment.MAX_PRICE_CVV2) {
//                cvv2Title.setVisibility(View.GONE);
//                cvv2Text.setVisibility(View.GONE);
//            }

            button.setText(R.string.pay);
            button.setBackgroundResource(R.drawable.button_green_selector_24dp);

        } else {
            button.setText(R.string.delete_card);
            button.setBackgroundColor(Color.parseColor(WalletActivity.primaryColor));
            button.setTextColor(Color.WHITE);
//            ViewCompat.setBackground(button, RaadCommonUtils.getSelector(getContext(), R.color.remove_card_button_normal, R.color.remove_card_button_selected, 0, 24, 0));
            defaultCardSwitch.setChecked(mCard.isDefault);
            defaultCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!isUpdating)
                        updateCard();
                }
            });
        }

        cardView.setCard(mCard, true);
        return view;
    }

    private void updateCard() {
        if (isUpdating)
            return;
        isUpdating = true;
        defaultCardProgress.setVisibility(View.VISIBLE);
        defaultCardSwitch.setEnabled(false);

        Map<String, Object> map = new HashMap<>();
        map.put("default", defaultCardSwitch.isChecked());

        Web.getInstance().getWebService().updateCard(mCard.token, PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(CardFragment.this, call, response);
                if (success == null)
                    return;

                defaultCardProgress.setVisibility(View.GONE);
                defaultCardSwitch.setEnabled(true);

                if (response.isSuccessful()) {
                    ((NavigationBarActivity) getActivity()).broadcastMessage(
                            CardFragment.this, null, CardsFragment.class);
                    //getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    defaultCardSwitch.setChecked(!defaultCardSwitch.isChecked());
                }
                isUpdating = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(CardFragment.this, call, t)) {
                    defaultCardProgress.setVisibility(View.GONE);
                    defaultCardSwitch.setChecked(!defaultCardSwitch.isChecked());
                    defaultCardSwitch.setEnabled(true);
                    isUpdating = false;
                }
            }
        });
    }

    private void showDeleteConfirm() {

        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_card)
                .content(R.string.delete_card_confirm)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeCard();
                    }
                })
                .show();
    }

    private void removeCard() {
        setLoading(true);
        Web.getInstance().getWebService().deleteCard(mCard.token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(CardFragment.this, call, response);
                if (success == null)
                    return;

                setLoading(false);

                if (success) {
                    ((NavigationBarActivity) getActivity()).broadcastMessage(
                            CardFragment.this, null, CardsFragment.class);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(CardFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void startPayProcess() {
        String pin2 = pinText.getText().toString();
        if (pin2.length() == 0) {
            Toast.makeText(getActivity(), R.string.enter_your_second_pin, Toast.LENGTH_SHORT).show();
            return;
        }

        String cvv2 = null;
        if (cvv2Text.getVisibility() == View.VISIBLE) {
            cvv2 = cvv2Text.getText().toString();
            if (cvv2.length() == 0) {
                Toast.makeText(getActivity(), R.string.enter_cvv2, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Utils.hideKeyboard(getContext(), pinText);
        startPay(RSAUtils.getCardDataRSA(mPayment, mCard, pin2, cvv2));
    }


    private void startPay(String encryptedCardData) {
        setLoading(true);

        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", mPayment.paymentAuth.token);
        finalInfoMap.put("card_info", encryptedCardData);

        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                Boolean success = Web.checkResponse(CardFragment.this, call, response);
                if (success == null)
                    return;

                setLoading(false);
                if (success) {
                    PaymentResult paymentResult = response.body();

                    PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                    dialog.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (getActivity() instanceof NavigationBarActivity) {
                                ((NavigationBarActivity) getActivity()).broadcastMessage(
                                        CardFragment.this, null, CardsFragment.class);
                            }

                            String frag = "PaymentEntryFragment";
                            getActivity().getSupportFragmentManager().popBackStack(null,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        }
                    }, WalletActivity.primaryColor );
                    dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
                } else {
                    /*if (mCard == null && getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).broadcastMessageToPreviousFragment(CardFragment.this, null, CardsFragment.class);
                    }*/
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (Web.checkFailureResponse(CardFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        pinText.setEnabled(!loading);
        cvv2Text.setEnabled(!loading);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(mPayment != null ? R.string.pay : R.string.delete_card));
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

}
