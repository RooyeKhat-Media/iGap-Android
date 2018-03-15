/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.module.PointsOverlayView;
import net.iGap.request.RequestUserVerifyNewDevice;

import static net.iGap.R.id.apvq_qrdecoderview;

public class FragmentQrCodeNewDevice extends BaseFragment implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView myDecoderView;
    private PointsOverlayView pointsOverlayView;
    private boolean isFirst = true;

    public static FragmentQrCodeNewDevice newInstance() {
        return new FragmentQrCodeNewDevice();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_qr_code_new_device, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myDecoderView = (QRCodeReaderView) view.findViewById(apvq_qrdecoderview);
        myDecoderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        myDecoderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        myDecoderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        //myDecoderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        //myDecoderView.setFrontCamera();

        // Use this function to set back camera preview
        myDecoderView.setBackCamera();

        pointsOverlayView = (PointsOverlayView) view.findViewById(R.id.points_overlay_view);

        myDecoderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        RelativeLayout root = (RelativeLayout) view.findViewById(R.id.root_qrCoderView);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStackFragment();

                if (G.fragmentActivity != null) {
                    ((ActivityMain) G.fragmentActivity).openNavigation();
                }

                G.fragmentActivity.overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        G.fragmentActivity.overridePendingTransition(0, 0);
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if (isFirst) {
            isFirst = false;
            new RequestUserVerifyNewDevice().verifyNewDevice(text);
        }
        pointsOverlayView.setPoints(points);
        popBackStackFragment();

        if (G.fragmentActivity != null) {
            ((ActivityMain) G.fragmentActivity).openNavigation();
        }
    }
}
