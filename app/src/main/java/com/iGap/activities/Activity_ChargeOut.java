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
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;


public class Activity_ChargeOut extends ActivityEnhanced {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_charge_out);


        initComponent();

    }

    private void initComponent() {


        RippleView rippleBackButton = (RippleView) findViewById(R.id.aco_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        findViewById(R.id.apco_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        TextView txtCancel = (TextView) findViewById(R.id.aco_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        TextView txtCharge = (TextView) findViewById(R.id.aco_txt_charge);
        txtCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}
