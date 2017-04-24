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
import com.iGap.G;
import com.iGap.R;
import com.iGap.libs.rippleeffect.RippleView;

public class Activity_transactionHistory extends ActivityEnhanced {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_transaction_history);

        initComponent();
    }

    private void initComponent() {

        findViewById(R.id.apth_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBackButton = (RippleView) findViewById(R.id.ath_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

    }
}
