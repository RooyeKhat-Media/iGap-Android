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
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;

public class Activity_payViaQRCode extends ActivityEnhanced implements QRCodeReaderView.OnQRCodeReadListener {

    EditText edtCode;

    TextView txtQRCode;

    private QRCodeReaderView mydecoderview;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_pay_via_qr_code);

        initComponent();

        txtQRCode = (TextView) findViewById(R.id.apvq_txt_qr_result);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.apvq_qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        mydecoderview.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        mydecoderview.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        mydecoderview.setTorchEnabled(true);

        // Use this function to set front camera preview
        mydecoderview.setFrontCamera();

        // Use this function to set back camera preview
        mydecoderview.setBackCamera();
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override public void onQRCodeRead(String text, PointF[] points) {

        txtQRCode.setText(text);
    }

    @Override protected void onResume() {
        super.onResume();
        mydecoderview.startCamera();
    }

    @Override protected void onPause() {
        super.onPause();
        mydecoderview.stopCamera();
    }

    private void initComponent() {

        RippleView rippleBackButton = (RippleView) findViewById(R.id.apvq_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        findViewById(R.id.appvqc_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleCreate = (RippleView) findViewById(R.id.apvq_ripple_plus);
        rippleCreate.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

            }
        });

        edtCode = (EditText) findViewById(R.id.apvq_edt_code);
        final View edtBottomLineCode = findViewById(R.id.apvq_view_bottom_line_code);
        edtCode.setPadding(0, 8, 0, 8);
        edtCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineCode.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineCode.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });
    }
}
