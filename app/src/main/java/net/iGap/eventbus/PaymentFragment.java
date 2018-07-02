package net.iGap.eventbus;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.PaymentDialogBinding;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoWalletPaymentInit;
import net.iGap.request.RequestWalletPaymentInit;

import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.fragment.PaymentResultDialog;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.model.PaymentResult;
import org.paygear.wallet.web.Web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.radsense.raadcore.app.AlertDialog;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.G.context;
import static org.paygear.wallet.utils.RSAUtils.getRSA;

public class PaymentFragment extends DialogFragment implements EventListener {

    private Drawable userPicture;
    private String userName;
    PaymentDialogBinding paymentDialogBinding;
    Card selectedCard = null;
    long userId = 0;
    final String[] mPrice = {""};


    public PaymentFragment() {
        // Required empty public constructor
    }


    public static PaymentFragment newInstance(Long userId, Drawable userpicture, String userName) {
        PaymentFragment fragment = new PaymentFragment();
        fragment.userName = userName;
        fragment.userPicture = userpicture;
        fragment.userId = userId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.payment_dialog, container, false);
        paymentDialogBinding = PaymentDialogBinding.bind(view);
        if (userPicture != null)
            paymentDialogBinding.imageView.setImageDrawable(userPicture);
        if (userName != null)
            paymentDialogBinding.subtitle.setText(userName);
        paymentDialogBinding.payButton.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        paymentDialogBinding.dialogHeader.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));



        paymentDialogBinding.amount.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrice[0] = s.toString().replaceAll(",", "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isSettingText) return;
                isSettingText = true;
                String s = null;
                try {
                    s = String.format(Locale.US, "%,d", Long.parseLong(mPrice[0]));
                } catch (NumberFormatException e) {
                }
                paymentDialogBinding.amount.setText(s);
                paymentDialogBinding.amount.setSelection(paymentDialogBinding.amount.length());
                isSettingText = false;
            }
        });

        paymentDialogBinding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestWalletPaymentInit().walletPaymentInit(ProtoGlobal.Language.FA_IR, Auth.getCurrentAuth().accessToken, userId, Long.parseLong(mPrice[0]), "");


            }
        });
        EventManager.getInstance().addEventListener(EventManager.ON_INIT_PAY, this);
        return view;
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        switch (id) {
            case EventManager.ON_INIT_PAY:
                final ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse = (ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder) message[0];
                if (initPayResponse != null) {
                    new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            DialogMaker.makeDialog(getActivity()).showDialog();
                            Web.getInstance().getWebService().getCredit(Auth.getCurrentAuth().getId()).enqueue(new Callback<ArrayList<Card>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                                    DialogMaker.disMissDialog();
                                    if (response.body() != null) {
                                        selectedCard = null;
                                        if (response.body().size() > 0)
                                            selectedCard = response.body().get(0);
                                        if (selectedCard != null) {
                                            if (selectedCard.cashOutBalance > Long.parseLong(mPrice[0])) {
                                                if (!selectedCard.isProtected)
                                                    showSetPinConfirm();
                                                else {

                                                    PaymentAuth paymentAuth = new PaymentAuth();
                                                    paymentAuth.publicKey = initPayResponse.getPublicKey();
                                                    paymentAuth.token = initPayResponse.getToken();
                                                    showPinConfirm(paymentAuth);
                                                }
                                            }else {
                                                Payment payment=new Payment();
                                                PaymentAuth paymentAuth=new PaymentAuth();
                                                paymentAuth.token=initPayResponse.getToken();
                                                paymentAuth.publicKey=initPayResponse.getPublicKey();
                                                payment.account=null;
                                                payment.paymentAuth=paymentAuth;
                                                payment.isCredit=false;
                                                payment.orderId=null;
                                                payment.price=Long.parseLong(mPrice[0]);
                                                Intent intent = new Intent(context, WalletActivity.class);
                                                intent.putExtra("Language", "fa");
                                                intent.putExtra("Mobile", "0" + String.valueOf(G.userId));
                                                intent.putExtra("IsP2P", true);
                                                intent.putExtra("Payment", payment);
//                                                intent.putExtra("PrimaryColor", G.appBarColor);
//                                                intent.putExtra("DarkPrimaryColor", G.progressColor);
//                                                intent.putExtra("AccentColor", G.menuBackgroundColor);

                                                intent.putExtra("PrimaryColor", "#da2128");
                                                intent.putExtra("DarkPrimaryColor", "#c50028");
                                                intent.putExtra("AccentColor","#ff0a0a");
                                                startActivityForResult(intent, 66);
                                            }

                                        }
                                    }
                                }

                                private void showPinConfirm(final PaymentAuth paymentAuth) {
                                    new AlertDialog()
                                            .setMode(AlertDialog.MODE_INPUT)
                                            .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                                            .setTitle(getString(org.paygear.wallet.R.string.paygear_card_pin))
                                            .setPositiveAction(getString(org.paygear.wallet.R.string.ok))
                                            .setNegativeAction(getString(org.paygear.wallet.R.string.cancel))
                                            .setOnActionListener(new AlertDialog.OnAlertActionListener() {
                                                @Override
                                                public boolean onAction(int i, Object o) {
                                                    if (i == 1) {
                                                        String pin = (String) o;
                                                        if (!TextUtils.isEmpty(pin.trim())) {
                                                            startPay(paymentAuth, pin);
                                                        }
                                                    }
                                                    return true;
                                                }

                                            }).show(getActivity().getSupportFragmentManager());
                                }

                                private void showSetPinConfirm() {
                                    new AlertDialog()
                                            .setTitle(getString(org.paygear.wallet.R.string.set_card_pin))
                                            .setMessage(getString(org.paygear.wallet.R.string.credit_card_set_pin_confirm))
                                            .setPositiveAction(getString(org.paygear.wallet.R.string.yes))
                                            .setNegativeAction(getString(org.paygear.wallet.R.string.no))
                                            .setOnActionListener(new AlertDialog.OnAlertActionListener() {
                                                @Override
                                                public boolean onAction(int i, Object o) {
                                                    if (i == 1) {
//                                                         getActivity().getSupportFragmentManager(.beginTransaction();
                                                    }
                                                    return true;
                                                }
                                            })
                                            .show(getActivity().getSupportFragmentManager());

                                }

//                                private void initPay(final Payment payment, final String pin) {
//                                    DialogMaker.makeDialog(getContext()).showDialog();
//                                    Web.getInstance().getWebService().initPayment(payment.getRequestBody()).enqueue(new Callback<PaymentAuth>() {
//                                        @Override
//                                        public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
//                                            DialogMaker.disMissDialog();
//                                            if (response != null)
//                                                if (response.errorBody() == null && response.body() != null) {
//                                                    payment.paymentAuth = response.body();
//                                                    if (pin == null) {
//
//                                                        Intent intent = new Intent(context, WalletActivity.class);
//                                                        intent.putExtra("Language", "fa");
//                                                        intent.putExtra("Mobile", "0" + String.valueOf(G.userId));
//                                                        intent.putExtra("IsP2P", true);
//                                                        intent.putExtra("Payment", payment);
//                                                        startActivityForResult(intent, 66);
//                                                    } else {
//                                                        String cardDataRSA = RSAUtils.getCardDataRSA(payment, selectedCard, pin, null);
//                                                        startPay(cardDataRSA, payment.paymentAuth.token);
//                                                    }
//
//                                                }
//
//                                        }
//
//                                        private void startPay(String encryptedCardData, String token) {
//
//                                            Map<String, String> finalInfoMap = new HashMap<>();
//                                            finalInfoMap.put("token", token);
//                                            finalInfoMap.put("card_info", encryptedCardData);
//                                            DialogMaker.makeDialog(getContext()).showDialog();
//                                            Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
//                                                @Override
//                                                public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
//
//                                                    DialogMaker.disMissDialog();
//                                                    if (response.errorBody() == null && response.body() != null) {
//                                                        PaymentResult paymentResult = response.body();
//
//                                                        final PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
//                                                        dialog.setListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View v) {
//
//                                                                RaadApp.cards = null;
//                                                                dialog.dismiss();
//                                                            }
//                                                        });
//                                                        dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
//                                                    }
//
//                                                }
//
//                                                @Override
//                                                public void onFailure(Call<PaymentResult> call, Throwable t) {
//                                                    DialogMaker.disMissDialog();
//                                                }
//                                            });
//                                        }
//
//                                        @Override
//                                        public void onFailure(Call<PaymentAuth> call, Throwable t) {
//                                            DialogMaker.disMissDialog();
//
//                                            Toast.makeText(context, "initPay failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//
                                @Override
                                public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                                    DialogMaker.disMissDialog();
                                    PaymentFragment.this.dismiss();
                                    Toast.makeText(context, "PayGear is unavailable", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                } else {
//                        new RequestWalletPaymentInit().walletPaymentInit();
                }
            case EventManager.ON_PAYMENT_RESULT_RECIEVED:
                int response = (int) message[0];
                switch (response) {
                    case socketMessages.PaymentResultRecievedSuccess:
                        new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                               dismiss();
                                Toast.makeText(getContext(), "پرداخت موفقیت آمیز بود اطلاعات بیشتر در قسمت تاریخچه کیف پول", Toast.LENGTH_LONG).show();
                            }
                        });

                        break;

                    case socketMessages.PaymentResultRecievedFailed:
                        new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                                Toast.makeText(getContext(), "پرداخت ناموفق بود", Toast.LENGTH_LONG).show();

                            }
                        });
                        break;

                    case socketMessages.PaymentResultNotRecieved:
                        new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                dismiss();
                                Toast.makeText(getContext(), "نتیجه پرداخت مشخص نیست به تاریخجه کیف پول مراجع یا با پشتیبانی تماس بگیرید", Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                }
        }
        // backthread

    }

    private void startPay(PaymentAuth paymentAuth, String pin) {
        String cardDataRSA = getCardDataRSA(paymentAuth, selectedCard, pin, null);
        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", paymentAuth.token);
        finalInfoMap.put("card_info", cardDataRSA);
        DialogMaker.makeDialog(getContext()).showDialog();
        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {

                DialogMaker.disMissDialog();
                if (response.errorBody() == null && response.body() != null) {
                    PaymentResult paymentResult = response.body();

                    final PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                    dialog.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            RaadApp.cards = null;
                            dialog.dismiss();
                        }
                    });
                    dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
                }

            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                DialogMaker.disMissDialog();
            }
        });

    }

    public static String getCardDataRSA(PaymentAuth paymentAuth, Card mCard, String pin2, String cvv2) {
        Map<String, Object> map = new HashMap();
        map.put("t", System.currentTimeMillis());
        map.put("c", mCard.token);
        map.put("bc", mCard.bankCode);
        map.put("type", mCard.type);
        if (!TextUtils.isEmpty(cvv2)) {
            map.put("cv", cvv2);
        }

        if (pin2 != null) {
            map.put("p2", pin2);
        }

        Gson gson = new Gson();
        String cardInfoJson = gson.toJson(map);
        String publicKey;
        if (paymentAuth != null) {
            publicKey = paymentAuth.publicKey;
        } else {
            publicKey = Auth.getCurrentAuth().getPublicKey();
        }

        return getRSA(publicKey, cardInfoJson);
    }

}

