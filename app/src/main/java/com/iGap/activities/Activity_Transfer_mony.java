/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;
import java.util.ArrayList;
import java.util.List;

public class Activity_Transfer_mony extends ActivityEnhanced {


    EditText edtAmount;
    EditText edtDescription;
    EditText edtTransferTo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_transfer_mony);


        initComponent();

    }

    private void initComponent() {


        RippleView rippleBackButton = (RippleView) findViewById(R.id.atm_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        findViewById(R.id.aptm_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        TextView txtCancel = (TextView) findViewById(R.id.atm_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        TextView txtCharge = (TextView) findViewById(R.id.atm_txt_charge);
        txtCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        edtTransferTo = (EditText) findViewById(R.id.atm_edt_transfer_to);
        final View edtBottomLineTransferTo = findViewById(R.id.atm_view_bottom_line_transfer_to);
        edtTransferTo.setPadding(0, 8, 0, 8);
        edtTransferTo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineTransferTo.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineTransferTo.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        edtAmount = (EditText) findViewById(R.id.atm_edt_amount);
        final View edtBottomLineAmount = findViewById(R.id.atm_view_bottom_line_amount);
        edtAmount.setPadding(0, 8, 0, 8);
        edtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineAmount.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineAmount.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        edtDescription = (EditText) findViewById(R.id.atm_edt_description);
        final View edtBottomLineDescription = findViewById(R.id.atm_view_bottom_line_description);
        edtDescription.setPadding(0, 8, 0, 8);
        edtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineDescription.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineDescription.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });


        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add(getString(R.string.iranian_rials));


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_Transfer_mony.this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.atm_spinner_price_type);
        spinner.setAdapter(adapter);


    }
}
