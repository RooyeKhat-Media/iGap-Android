/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;

public class Activity_charge_balance extends ActivityEnhanced {

    EditText edtPrice;
    EditText edtEmail;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_charge_balance);

        initComponent();
    }

    private void initComponent() {

        RippleView rippleBackButton = (RippleView) findViewById(R.id.acb_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        findViewById(R.id.apcb_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        TextView txtCancel = (TextView) findViewById(R.id.acb_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        TextView txtCharge = (TextView) findViewById(R.id.acb_txt_charge);
        txtCharge.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        edtPrice = (EditText) findViewById(R.id.acb_edt_price);
        final View edtBottomLinePrice = findViewById(R.id.acb_view_bottom_line_price);
        edtPrice.setPadding(0, 8, 0, 8);
        edtPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLinePrice.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLinePrice.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        edtEmail = (EditText) findViewById(R.id.acb_edt_email);
        final View edtBottomLineEmail = findViewById(R.id.acb_view_bottom_line_email);
        edtEmail.setPadding(0, 8, 0, 8);
        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineEmail.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineEmail.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Iranian Rials(IRR)");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_charge_balance.this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.acb_spinner_price_type);
        spinner.setAdapter(adapter);
    }
}
