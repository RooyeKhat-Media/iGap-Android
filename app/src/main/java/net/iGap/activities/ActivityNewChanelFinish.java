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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MaterialDesignTextView;

public class ActivityNewChanelFinish extends ActivityEnhanced {

    private MaterialDesignTextView txtBack;
    private RadioButton radioButtonPublic, radioButtonPrivate;
    private EditText edtLink;
    private TextView txtFinish, txtCancel;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chanel_finish);

        txtBack = (MaterialDesignTextView) findViewById(R.id.nclf_txt_back);
        RippleView rippleBack = (RippleView) findViewById(R.id.nclf_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        radioButtonPublic = (RadioButton) findViewById(R.id.nclf_radioButton_Public);
        radioButtonPublic.setChecked(true);
        radioButtonPrivate = (RadioButton) findViewById(R.id.nclf_radioButton_private);

        findViewById(R.id.nclf_backgroundToolbar).setBackgroundColor(Color.parseColor(G.appBarColor));
        findViewById(R.id.ancf_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        edtLink = (EditText) findViewById(R.id.nclf_edt_link);

        txtFinish = (TextView) findViewById(R.id.nclf_txt_nextStep);
        txtCancel = (TextView) findViewById(R.id.nclf_txt_cancel);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });

        txtFinish.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                finish();
            }
        });
    }
}
