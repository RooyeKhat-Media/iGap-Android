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

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPaymentBillBinding;
import net.iGap.fragments.FragmentPaymentBill;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperPermission;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.interfaces.OnMplResult;
import net.iGap.request.RequestMplGetBillToken;

import java.io.IOException;

import static net.iGap.activities.ActivityMain.requestCodeBarcode;


public class FragmentPaymentBillViewModel {

    private FragmentPaymentBill fragmentPaymentBill;
    private FragmentPaymentBillBinding fragmentPaymentBillBinding;
    public ObservableInt observeCompany = new ObservableInt(View.GONE);
    public ObservableField<String> observeTitleToolbar = new ObservableField<>("");
    public ObservableBoolean observePolice = new ObservableBoolean(false);
    public ObservableBoolean observeEnabledPayment = new ObservableBoolean(true);
    public ObservableBoolean observeAmount = new ObservableBoolean(false);

    public ObservableField<Drawable> observeBackGround = new ObservableField<>();

    private boolean isPolice = false;

    public FragmentPaymentBillViewModel(FragmentPaymentBill fragmentPaymentBill, FragmentPaymentBillBinding fragmentPaymentBillBinding, int resTitleId) {
        this.fragmentPaymentBill = fragmentPaymentBill;
        this.fragmentPaymentBillBinding = fragmentPaymentBillBinding;
        observeTitleToolbar.set(G.context.getString(resTitleId));

        isPolice = resTitleId == R.string.pay_bills_crime;

        if (isPolice) {
            observePolice.set(true);
            observeAmount.set(false);
        }

        Drawable myIcon = G.context.getResources().getDrawable(R.drawable.oval_green);
        myIcon.setColorFilter(Color.parseColor(G.appBarColor), PorterDuff.Mode.SRC_IN);
        observeBackGround.set(myIcon);
    }


    public void onTextChangedBillId(CharSequence s, int start, int before, int count) {
        if (isPolice) {
            return;
        }
        if (s.length() == 13) {
            observeCompany.set(View.VISIBLE);
            fragmentPaymentBillBinding.fpbImvCompany.setImageResource(getCompany(s.toString().substring(11, 12)));
        } else {
            observeCompany.set(View.GONE);
        }
    }


    public void onClickBarCode(View v) {

        try {
            HelperPermission.getCameraPermission(G.currentActivity, new OnGetPermission() {
                @Override
                public void Allow() throws IOException, IllegalStateException {
                    IntentIntegrator integrator = IntentIntegrator.forSupportFragment(fragmentPaymentBill);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.CODE_128);
                    integrator.setRequestCode(requestCodeBarcode);
                    integrator.setBeepEnabled(false);
                    integrator.setPrompt("");
                    integrator.initiateScan();
                }

                @Override
                public void deny() {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onPayBillClick(View v) {

        if (!G.userLogin) {
            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
            return;
        }

        String billId = fragmentPaymentBillBinding.fpbEdtBillId.getText().toString();

        if (isPolice) {
            if (billId.length() == 0) {
                HelperError.showSnackMessage(G.context.getResources().getString(R.string.biling_id_not_valid), false);
                return;
            }

        } else {
            if (billId.length() != 13) {
                HelperError.showSnackMessage(G.context.getResources().getString(R.string.biling_id_not_valid), false);
                return;
            }
        }


        String payId = fragmentPaymentBillBinding.fpbEdtPayId.getText().toString();

        if (isPolice) {
            if (payId.length() == 0) {
                HelperError.showSnackMessage(G.context.getResources().getString(R.string.pay_id_is_not_valid), false);
                return;
            }
        } else {
            if (payId.length() > 13 || payId.length() < 5) {
                HelperError.showSnackMessage(G.context.getResources().getString(R.string.pay_id_is_not_valid), false);
                return;
            }
        }


        G.onMplResult = new OnMplResult() {
            @Override
            public void onResult(boolean error) {

                if (error) {
                    observeEnabledPayment.set(true);
                } else {
                    fragmentPaymentBillBinding.getBackHandler().onBack();
                }

            }
        };

        RequestMplGetBillToken requestMplGetBillToken = new RequestMplGetBillToken();
        requestMplGetBillToken.mplGetBillToken(Long.parseLong(billId), Long.parseLong(payId));

        observeEnabledPayment.set(false);
    }


    public static int getCompany(String value) {

        int result = 0;
        switch (value) {
            case "1":
                result = R.drawable.bill_water_pec;
                break;
            case "2":
                result = R.drawable.bill_elc_pec;
                break;
            case "3":
                result = R.drawable.bill_gaz_pec;
                break;
            case "4":
                result = R.drawable.bill_telecom_pec;
                break;
            case "5":
                result = R.drawable.bill_mci_pec;
                break;
            case "6":
                result = R.drawable.bill_shahrdari_pec;
                break;
        }

        return result;
    }


}
