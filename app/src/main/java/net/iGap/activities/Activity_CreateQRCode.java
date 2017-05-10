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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.libs.rippleeffect.RippleView;

public class Activity_CreateQRCode extends ActivityEnhanced {

    EditText edtPrice;
    EditText edtDescription;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_crate_qr_code);

        initComponent();
    }

    private void initComponent() {

        RippleView rippleBackButton = (RippleView) findViewById(R.id.acqc_ripple_back_Button);
        rippleBackButton.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                finish();
            }
        });

        findViewById(R.id.apcac_ll_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleCreate = (RippleView) findViewById(R.id.acqc_ripple_plus);
        rippleCreate.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {

            }
        });

        TextView txtCancel = (TextView) findViewById(R.id.acqc_txt_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

            }
        });

        TextView txtCreate = (TextView) findViewById(R.id.acqc_txt_create);
        txtCreate.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {

                try {
                    HelperPermision.getStoragePermision(Activity_CreateQRCode.this, new OnGetPermission() {
                        @Override public void Allow() {
                            CreateBarcode();
                        }

                        @Override public void deny() {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        edtPrice = (EditText) findViewById(R.id.acqc_edt_price);
        final View edtBottomLinePrice = findViewById(R.id.acqc_view_bottom_line_price);
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

        edtDescription = (EditText) findViewById(R.id.acqc_edt_description);
        final View edtBottomLineDescription = findViewById(R.id.acqc_view_bottom_line_description);
        edtDescription.setPadding(0, 8, 0, 8);
        edtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean b) {

                if (b) {
                    edtBottomLineDescription.setBackgroundColor(getResources().getColor(R.color.toolbar_background));
                } else {
                    edtBottomLineDescription.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                }
            }
        });

        List<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add(getString(R.string.iranian_rials));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Activity_CreateQRCode.this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.acqc_spinner_price_type);
        spinner.setAdapter(adapter);
    }

    private void CreateBarcode() {
        String _tmp = edtPrice.getText().toString();

        if (_tmp.trim().length() > 0) {

            _tmp += "  " + edtDescription.getText().toString();

            ImageView iv = (ImageView) findViewById(R.id.acqc_iv_barcode);

            try {
                iv.setImageBitmap(encodeAsBitmap(_tmp, 120));
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    // this is method call from on create and return bitmap image of QRCode.
    Bitmap encodeAsBitmap(String str, int WIDTH) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black_register) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }
}
