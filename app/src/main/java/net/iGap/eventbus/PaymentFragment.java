package net.iGap.eventbus;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.PaymentDialogBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.interfaces.OnUserProfileSetNickNameResponse;
import net.iGap.module.EmojiEditTextE;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoWalletPaymentInit;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestUserProfileSetNickname;
import net.iGap.request.RequestUserVerifyNewDevice;
import net.iGap.request.RequestWalletPaymentInit;
import net.iGap.webservice.APIService;
import net.iGap.webservice.ApiUtils;
import net.iGap.webservice.Post;

import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.fragment.CardFragment;
import org.paygear.wallet.fragment.PaymentResultDialog;
import org.paygear.wallet.fragment.SetCardPinFragment;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.model.PaymentResult;
import org.paygear.wallet.web.Web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.ErrorManager;

import ir.radsense.raadcore.app.AlertDialog;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.PostRequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static net.iGap.G.context;
import static net.iGap.G.fragmentActivity;
import static net.iGap.G.smsNumbers;
import static org.paygear.wallet.utils.RSAUtils.getRSA;

public class PaymentFragment extends BaseFragment implements EventListener {

    private Drawable userPicture;
    private String userName;
    PaymentDialogBinding paymentDialogBinding;
    Card selectedCard = null;
    long userId = 0;
    final String[] mPrice = {""};
    public static final int requestCodePaymentCharge = 1;
    public static final int requestCodePaymentBill = 2;
    public static final int requestCodeQrCode = 200;
    public static final int requestCodeBarcode = 201;
    MaterialDialog progressDialog;
    private APIService mAPIService;

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
        if (G.selectedCard != null) {
            paymentDialogBinding.amountCard.setText(getResources().getString(R.string.wallet_Your_credit) + " " + String.valueOf(G.cardamount) + " " + getResources().getString(R.string.wallet_Reial));
        } else {
            paymentDialogBinding.amountCard.setVisibility(View.GONE);
        }
        paymentDialogBinding.payButton.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        paymentDialogBinding.dialogHeader.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));

        paymentDialogBinding.outside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                G.fragmentActivity.onBackPressed();
            }
        });
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
                if (mPrice[0] != null && !mPrice[0].isEmpty()) {
                    paymentDialogBinding.payButton.setEnabled(false);
                    showProgress();
                    new RequestWalletPaymentInit().walletPaymentInit(ProtoGlobal.Language.FA_IR, Auth.getCurrentAuth().accessToken, userId, Long.parseLong(mPrice[0]), paymentDialogBinding.edtDescription.getText().toString());

                }

            }
        });
        EventManager.getInstance().addEventListener(EventManager.ON_INIT_PAY, this);
        return view;
    }

    @Override
    public void receivedMessage(int id, Object... message) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                paymentDialogBinding.payButton.setEnabled(true);
            }
        });

        switch (id) {
            case EventManager.ON_INIT_PAY:
                if (message == null) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) progressDialog.dismiss();
                            HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                        }
                    });
                    return;
                }
                final ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse = (ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder) message[0];
                if (initPayResponse != null) {
                    new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Web.getInstance().getWebService().getCredit(Auth.getCurrentAuth().getId()).enqueue(new Callback<ArrayList<Card>>() {
                                @Override
                                public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                                    if (progressDialog != null) progressDialog.dismiss();

                                    if (!HelperFragment.isFragmentVisible("PaymentFragment"))
                                        return;
                                    if (response.body() != null) {
                                        selectedCard = null;
                                        if (response.body().size() > 0)
                                            selectedCard = response.body().get(0);
                                        if (selectedCard != null) {
                                            if (selectedCard.cashOutBalance >= Long.parseLong(mPrice[0])) {
                                                if (!selectedCard.isProtected) {
                                                    if (progressDialog != null)
                                                        progressDialog.dismiss();

                                                    setNewPassword();

                                                } else {

                                                    if (progressDialog != null)
                                                        progressDialog.dismiss();
                                                    PaymentAuth paymentAuth = new PaymentAuth();
                                                    paymentAuth.publicKey = initPayResponse.getPublicKey();
                                                    paymentAuth.token = initPayResponse.getToken();
//                                                    showPinConfirm(paymentAuth);
                                                    setConfirmPassword(paymentAuth);


                                                }
                                            } else {
                                                if (progressDialog != null)
                                                    progressDialog.dismiss();

                                                Payment payment = new Payment();
                                                PaymentAuth paymentAuth = new PaymentAuth();
                                                paymentAuth.token = initPayResponse.getToken();
                                                paymentAuth.publicKey = initPayResponse.getPublicKey();
                                                payment.account = null;
                                                payment.paymentAuth = paymentAuth;
                                                payment.isCredit = false;
                                                payment.orderId = null;
                                                payment.price = Long.parseLong(mPrice[0]);
                                                Intent intent = new Intent(context, WalletActivity.class);
                                                intent.putExtra("Language", "fa");
                                                intent.putExtra("Mobile", "0" + String.valueOf(G.userId));
                                                intent.putExtra("IsP2P", true);
                                                intent.putExtra("Payment", payment);
                                                intent.putExtra("PrimaryColor", G.appBarColor);
                                                intent.putExtra("DarkPrimaryColor", G.appBarColor);
                                                intent.putExtra("AccentColor", G.appBarColor);
                                                intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
                                                intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                                                intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
                                                intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme_2);
                                                intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                                                intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                                                startActivityForResult(intent, 66);
                                                G.currentActivity.onBackPressed();
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                                    if (paymentDialogBinding.payButton != null)
                                        paymentDialogBinding.payButton.setEnabled(true);
                                    if (progressDialog != null) progressDialog.dismiss();

                                    HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                                }
                            });

                        }
                    });
                } else {
                    if (progressDialog != null) progressDialog.dismiss();
                    HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                }
            case EventManager.ON_PAYMENT_RESULT_RECIEVED:

                if (progressDialog != null) progressDialog.dismiss();
                int response = (int) message[0];
                switch (response) {
                    case socketMessages.PaymentResultRecievedSuccess:
                        new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                fragmentActivity.onBackPressed();

                                HelperError.showSnackMessage(getResources().getString(R.string.result_4), false);
                            }
                        });

                        break;

                    case socketMessages.PaymentResultRecievedFailed:
                        new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                fragmentActivity.onBackPressed();
                                HelperError.showSnackMessage(getResources().getString(R.string.not_success_2), false);
                            }
                        });
                        break;

                    case socketMessages.PaymentResultNotRecieved:
                        new android.os.Handler(getContext().getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                fragmentActivity.onBackPressed();
                                HelperError.showSnackMessage(getResources().getString(R.string.result_3), false);

                            }
                        });
                        break;
                }

            case EventManager.ON_INIT_PAY_ERROR:
                if (progressDialog != null) progressDialog.dismiss();
                break;
        }
        // backthread

    }

    private void startPay(final PaymentAuth paymentAuth, String pin) {

        String cardDataRSA = getCardDataRSA(paymentAuth, selectedCard, pin, null);
        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", paymentAuth.token);
        finalInfoMap.put("card_info", cardDataRSA);
//        DialogMaker.makeDialog(getContext()).showDialog();
        showProgress();
        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, final Response<PaymentResult> response) {

                Boolean success = Web.checkResponse(PaymentFragment.this, call, response);
                if (success == null)
                    return;

                if (progressDialog != null) progressDialog.dismiss();
//                DialogMaker.disMissDialog();
                if (response.errorBody() == null && response.body() != null) {
                    PaymentResult paymentResult = response.body();
                    final PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                    dialog.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RaadApp.cards = null;
                            dialog.dismiss();
                            fragmentActivity.onBackPressed();
                            sendPost(response.body().callbackUrl, paymentAuth.token);
                            G.cardamount -= response.body().amount;
                        }
                    } , G.appBarColor );
                    dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
//                DialogMaker.disMissDialog();

                if (progressDialog != null) progressDialog.dismiss();
                HelperError.showSnackMessage(getResources().getString(R.string.wallet_error_server), false);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 66:
                if (resultCode == RESULT_OK) {
                    PaymentResult paymentResult = (PaymentResult) data.getSerializableExtra("result");
                    if (paymentResult != null) {
                        HelperError.showSnackMessage(getResources().getString(R.string.trace_number) + String.valueOf(paymentResult.traceNumber) + getResources().getString(R.string.amount_2) + String.valueOf(paymentResult.amount), false);
                        EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultRecievedSuccess);
                    } else {
                        HelperError.showSnackMessage(getResources().getString(R.string.not_success), false);
                        EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultRecievedFailed);

                    }
                } else {

                    HelperError.showSnackMessage(getResources().getString(R.string.payment_canceled), false);
                    EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultNotRecieved);
                }
                break;
        }

    }

    public void setNewPassword() {
        final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        final View viewNewPassword = new View(G.fragmentActivity);
        viewNewPassword.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextInputLayout inputNewPassWord = new TextInputLayout(G.fragmentActivity);
        final EditText newPassWord = new EditText(G.fragmentActivity);
        newPassWord.setHint(G.fragmentActivity.getResources().getString(R.string.please_enter_your_password));
        newPassWord.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        newPassWord.setTypeface(G.typeface_IRANSansMobile);
        newPassWord.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        newPassWord.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        newPassWord.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        newPassWord.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        newPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassWord.setPadding(0, 8, 0, 8);
        newPassWord.setMaxLines(1);
        inputNewPassWord.addView(newPassWord);
        inputNewPassWord.addView(viewNewPassword, viewParams);
        final View viewConfirmPassWord = new View(G.fragmentActivity);
        viewConfirmPassWord.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newPassWord.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }

        TextInputLayout inputConfirmPassWord = new TextInputLayout(G.fragmentActivity);
        final EditText confirmPassWord = new EditText(G.fragmentActivity);
        confirmPassWord.setHint(G.fragmentActivity.getResources().getString(R.string.please_re_enter_your_password));
        confirmPassWord.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        confirmPassWord.setTypeface(G.typeface_IRANSansMobile);
        confirmPassWord.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        confirmPassWord.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirmPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPassWord.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        confirmPassWord.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        confirmPassWord.setPadding(0, 8, 0, 8);
        confirmPassWord.setMaxLines(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            confirmPassWord.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        inputConfirmPassWord.addView(confirmPassWord);
        inputConfirmPassWord.addView(viewConfirmPassWord, viewParams);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 15);
        LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lastNameLayoutParams.setMargins(0, 15, 0, 10);

        layoutNickname.addView(inputNewPassWord, layoutParams);
        layoutNickname.addView(inputConfirmPassWord, lastNameLayoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title(G.fragmentActivity.getResources().getString(R.string.please_set_password))
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).customView(layoutNickname, true)
                        .widgetColor(Color.parseColor(G.appBarColor)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        newPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewNewPassword.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewNewPassword.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        confirmPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewConfirmPassWord.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewConfirmPassWord.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSavePin(newPassWord.getText().toString(), confirmPassWord.getText().toString());
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    /**
     * set new password for wallet
     *
     * @param newPassword
     * @param confirmPassword
     */

    private void startSavePin(String newPassword, String confirmPassword) {
        String[] data = new String[]{newPassword,
                confirmPassword,};
        if ((RaadApp.paygearCard.isProtected && TextUtils.isEmpty(data[0])) || TextUtils.isEmpty(data[1])) {
            HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.please_enter_your_password), true);
            return;
        }

        if (!data[0].equals(data[1])) {
            HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match), true);
            return;
        }

        Map<String, String> map = new HashMap<>();
        if (RaadApp.paygearCard.isProtected)
            map.put("old_password", data[0]);
        map.put("new_password", data[1]);

        Web.getInstance().getWebService().setCreditCardPin(RaadApp.paygearCard.token, PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(PaymentFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.card_pin_saved), false);
                    RaadApp.paygearCard.isProtected = true;
                    fragmentActivity.onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(PaymentFragment.this, call, t)) {
                }
            }
        });
    }


    /**
     * confirm password
     */

    public void setConfirmPassword(final PaymentAuth paymentAuth) {
        final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        final View viewFirstName = new View(G.fragmentActivity);
        viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextInputLayout inputNewPassWord = new TextInputLayout(G.fragmentActivity);
        final EditText newPassWord = new EditText(G.fragmentActivity);
        newPassWord.setHint(G.fragmentActivity.getResources().getString(R.string.please_enter_your_password));
        newPassWord.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        newPassWord.setTypeface(G.typeface_IRANSansMobile);
        newPassWord.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        newPassWord.setTextColor(G.context.getResources().getColor(R.color.text_edit_text));
        newPassWord.setHintTextColor(G.context.getResources().getColor(R.color.hint_edit_text));
        newPassWord.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassWord.setPadding(0, 8, 0, 8);
        newPassWord.setMaxLines(1);
        inputNewPassWord.addView(newPassWord);
        inputNewPassWord.addView(viewFirstName, viewParams);
        final View viewLastName = new View(G.fragmentActivity);
        viewLastName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newPassWord.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 15);
        LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lastNameLayoutParams.setMargins(0, 15, 0, 10);

        layoutNickname.addView(inputNewPassWord, layoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title(G.fragmentActivity.getResources().getString(R.string.your_password))
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).customView(layoutNickname, true)
                        .widgetColor(Color.parseColor(G.appBarColor)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        newPassWord.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    viewFirstName.setBackgroundColor(Color.parseColor(G.appBarColor));
                } else {
                    viewFirstName.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(newPassWord.getText().toString().trim())) {
                    startPay(paymentAuth, newPassWord.getText().toString());
                    dialog.dismiss();
                } else {
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.please_enter_your_password), true);
                }
            }
        });

        dialog.show();
    }

    private void showProgress() {
        progressDialog = new MaterialDialog.Builder(fragmentActivity)
                .content(R.string.please_wait)
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .build();

        progressDialog.show();
    }

    public void sendPost(String url, String token) {
        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", token);
        mAPIService = ApiUtils.getAPIService();
        mAPIService.sendToken(url, getRequestBody(finalInfoMap)).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
            }
        });

    }

    public static RequestBody getRequestBody(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    @Override
    public void onPause() {
        super.onPause();

    }
}

