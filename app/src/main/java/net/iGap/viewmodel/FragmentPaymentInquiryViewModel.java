package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPaymentInquiryBinding;
import net.iGap.helper.HelperError;
import net.iGap.interfaces.OnInquiry;
import net.iGap.proto.ProtoBillInquiryMci;
import net.iGap.proto.ProtoBillInquiryTelecom;
import net.iGap.request.RequestBillInquiryMci;
import net.iGap.request.RequestBillInquiryTelecom;
import net.iGap.request.RequestMplGetBillToken;

import java.util.HashMap;

import static net.iGap.fragments.FragmentPaymentBill.addCommasToNumericString;

public class FragmentPaymentInquiryViewModel {

    public enum OperatorType {
        mci, telecome;
    }

    public static HashMap<String, OperatorType> MCI = new HashMap<String, OperatorType>() {
        {
            put("0910", OperatorType.mci);
            put("0911", OperatorType.mci);
            put("0912", OperatorType.mci);
            put("0913", OperatorType.mci);
            put("0914", OperatorType.mci);
            put("0915", OperatorType.mci);
            put("0916", OperatorType.mci);
            put("0917", OperatorType.mci);
            put("0918", OperatorType.mci);
            put("0919", OperatorType.mci);
            put("0990", OperatorType.mci);
            put("0991", OperatorType.mci);
        }
    };

    public ObservableInt observeMci = new ObservableInt(View.GONE);
    public ObservableInt observeTelecom = new ObservableInt(View.GONE);
    public ObservableInt observeProgress = new ObservableInt(View.GONE);
    public ObservableInt observeMidTerm = new ObservableInt(View.VISIBLE);
    public ObservableBoolean observeInquiry = new ObservableBoolean(false);
    public ObservableBoolean observableLastTermMessage = new ObservableBoolean(false);
    public ObservableBoolean observableMidTermMessage = new ObservableBoolean(false);
    public ObservableField<String> observeTitleToolbar = new ObservableField<>("");

    public ObservableField<String> lastTermBillId = new ObservableField<>("");
    public ObservableField<String> lastTermPayId = new ObservableField<>("");
    public ObservableField<String> lastTermAmount = new ObservableField<>("");
    public ObservableField<String> lastTermMessage = new ObservableField<>("");

    public ObservableField<String> midTermBillId = new ObservableField<>("");
    public ObservableField<String> midTermPayId = new ObservableField<>("");
    public ObservableField<String> midTermAmount = new ObservableField<>("");
    public ObservableField<String> midTermMessage = new ObservableField<>("");

    public ObservableField<Drawable> observeBackGround = new ObservableField<>();

    private OperatorType operatorType;
    private FragmentPaymentInquiryBinding fragmentPaymentInquiryBinding;

    public FragmentPaymentInquiryViewModel(FragmentPaymentInquiryBinding fragmentPaymentInquiryBinding, OperatorType operatorType) {
        this.fragmentPaymentInquiryBinding = fragmentPaymentInquiryBinding;
        this.operatorType = operatorType;

        Drawable myIcon = G.context.getResources().getDrawable(R.drawable.oval_green);
        myIcon.setColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN);
        observeBackGround.set(myIcon);

        switch (operatorType) {
            case mci:
                observeMci.set(View.VISIBLE);
                observeTelecom.set(View.GONE);
                observeTitleToolbar.set(G.context.getString(R.string.bills_inquiry_mci));
                fragmentPaymentInquiryBinding.fpiEdtMci.requestFocus();

                break;
            case telecome:
                observeMci.set(View.GONE);
                observeTelecom.set(View.VISIBLE);
                observeTitleToolbar.set(G.context.getString(R.string.bills_inquiry_telecom));
                fragmentPaymentInquiryBinding.fpiEdtTelecomArea.requestFocus();
                break;
        }
    }

    public void onItemSelectBillType(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                observeMci.set(View.VISIBLE);
                observeTelecom.set(View.GONE);
                operatorType = OperatorType.mci;
                fragmentPaymentInquiryBinding.fpiEdtMci.setText("");
                break;
            case 1:
                fragmentPaymentInquiryBinding.fpiEdtTelecom.setText("");
                fragmentPaymentInquiryBinding.fpiEdtTelecomArea.setText("");
                observeMci.set(View.GONE);
                observeTelecom.set(View.VISIBLE);
                operatorType = OperatorType.telecome;
                break;
        }


    }

    public void onInquiryClick(View view) {

        if (view != null) {
            closeKeyboard(view);
        }


        if (observeProgress.get() == View.VISIBLE) {
            HelperError.showSnackMessage(G.context.getString(R.string.just_wait_en), false);
            return;
        }

        if (!G.userLogin) {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            return;
        }


        switch (operatorType) {
            case mci:
                String phoneMci = fragmentPaymentInquiryBinding.fpiEdtMci.getText().toString();

                if (phoneMci.length() < 11) {
                    HelperError.showSnackMessage(G.context.getResources().getString(R.string.phone_number_is_not_valid), false);
                    return;
                }

                OperatorType opt = MCI.get(phoneMci.substring(0, 4));
                if (opt == null) {
                    HelperError.showSnackMessage(G.context.getResources().getString(R.string.mci_opreator_check), false);
                    return;
                }

                if (G.userLogin) {

                    G.onInquiry = new OnInquiry() {
                        @Override
                        public void OnInquiryResult(Object result) {
                            parsMci(result);
                            G.onInquiry = null;
                            observeProgress.set(View.GONE);
                        }

                        @Override
                        public void OnInquiryError() {
                            observeProgress.set(View.GONE);
                            HelperError.showSnackMessage(G.context.getString(R.string.not_answered), false);
                        }
                    };


                    new RequestBillInquiryMci().billInquiryMci(Long.parseLong(phoneMci));

                    observeProgress.set(View.VISIBLE);

                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                }

                break;
            case telecome:

                String phoneTelecom = fragmentPaymentInquiryBinding.fpiEdtTelecom.getText().toString();
                String phoneTelecomArea = fragmentPaymentInquiryBinding.fpiEdtTelecomArea.getText().toString();

                if (phoneTelecom.length() < 8 || phoneTelecomArea.length() < 3) {
                    HelperError.showSnackMessage(G.context.getResources().getString(R.string.phone_number_is_not_valid), false);
                    return;
                }

                if (G.userLogin) {

                    G.onInquiry = new OnInquiry() {
                        @Override
                        public void OnInquiryResult(Object result) {
                            parseTelecom(result);
                            G.onInquiry = null;
                            observeProgress.set(View.GONE);
                        }

                        @Override
                        public void OnInquiryError() {
                            observeProgress.set(View.GONE);
                            HelperError.showSnackMessage(G.context.getString(R.string.not_answered), false);
                        }
                    };

                    new RequestBillInquiryTelecom().billInquiryTelecom(Integer.parseInt(phoneTelecomArea), Integer.parseInt(phoneTelecom));
                    observeProgress.set(View.VISIBLE);
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                }

                break;
        }
    }

    private void parsMci(Object value) {

        ProtoBillInquiryMci.BillInquiryMciResponse.Builder message = (ProtoBillInquiryMci.BillInquiryMciResponse.Builder) value;

        if (message.getStatus() == 0) {

            observeInquiry.set(true);

            ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo lastTerm = message.getLastTerm();
            ProtoBillInquiryMci.BillInquiryMciResponse.BillInfo midTerm = message.getMidTerm();

            lastTermBillId.set(lastTerm.getBillId() + "");
            lastTermPayId.set(lastTerm.getPayId() + "");
            lastTermAmount.set(addCommasToNumericString(lastTerm.getAmount() + ""));
            lastTermMessage.set(lastTerm.getMessage() + "");

            midTermBillId.set(midTerm.getBillId() + "");
            midTermPayId.set(midTerm.getPayId() + "");
            midTermAmount.set(addCommasToNumericString(midTerm.getAmount() + ""));
            midTermMessage.set(midTerm.getMessage() + "");


            if (lastTerm.getStatus() == 0 && lastTerm.getAmount() > 0) {
                observableLastTermMessage.set(false);
            } else {
                observableLastTermMessage.set(true);
            }

            if (midTerm.getStatus() == 0 && midTerm.getAmount() > 0) {
                observableMidTermMessage.set(false);
            } else {
                observableMidTermMessage.set(true);

                if (midTerm.getMessage().length() == 0) {
                    observeMidTerm.set(View.GONE);
                }
            }


        } else {
            HelperError.showSnackMessage(message.getMessage(), false);
        }


    }

    private void parseTelecom(Object value) {

        ProtoBillInquiryTelecom.BillInquiryTelecomResponse.Builder message = (ProtoBillInquiryTelecom.BillInquiryTelecomResponse.Builder) value;

        if (message.getStatus() == 0) {

            observeInquiry.set(true);

            ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo lastTerm = message.getLastTerm();
            ProtoBillInquiryTelecom.BillInquiryTelecomResponse.BillInfo midTerm = message.getMidTerm();

            lastTermBillId.set(lastTerm.getBillId() + "");
            lastTermPayId.set(lastTerm.getPayId() + "");
            lastTermAmount.set(addCommasToNumericString(lastTerm.getAmount() + ""));

            midTermBillId.set(midTerm.getBillId() + "");
            midTermPayId.set(midTerm.getPayId() + "");
            midTermAmount.set(addCommasToNumericString(midTerm.getAmount() + ""));


            if (lastTerm.getAmount() > 0) {
                observableLastTermMessage.set(false);
            } else {
                observableLastTermMessage.set(true);
            }

            if (midTerm.getAmount() > 0) {
                observableMidTermMessage.set(false);
            } else {
                observableMidTermMessage.set(true);
                observeMidTerm.set(View.GONE);
            }

        } else {
            HelperError.showSnackMessage(message.getMessage(), false);
        }

    }

    private void closeKeyboard(final View v) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    public void onLastTermPayment(View v) {

        if (!G.userLogin) {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            return;
        }

        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        requestMplGetBillToken.mplGetBillToken(Long.parseLong(lastTermBillId.get()), Long.parseLong(lastTermPayId.get()));
        fragmentPaymentInquiryBinding.getBackHandler().onBack();
    }

    public void onMidTermPayment(View v) {

        if (!G.userLogin) {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            return;
        }
        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        requestMplGetBillToken.mplGetBillToken(Long.parseLong(midTermBillId.get()), Long.parseLong(midTermPayId.get()));
        fragmentPaymentInquiryBinding.getBackHandler().onBack();
    }
}