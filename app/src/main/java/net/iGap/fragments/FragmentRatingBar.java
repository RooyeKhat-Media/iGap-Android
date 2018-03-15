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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import net.iGap.G;
import net.iGap.R;
import net.iGap.request.RequestSignalingRate;

public class FragmentRatingBar extends BaseFragment {

    public static final String ID_EXTRA = "ID_EXTRA";
    private RatingBar ratingBar;
    private EditText edtResponse;
    private long id = -1;

    public static FragmentRatingBar newInstance(long id) {
        FragmentRatingBar fragmentRatingBar = new FragmentRatingBar();
        Bundle bundle = new Bundle();
        bundle.putLong(ID_EXTRA, id);
        fragmentRatingBar.setArguments(bundle);
        return fragmentRatingBar;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_rating_bar, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        G.isShowRatingDialog = true;
        G.fragmentActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        id = getArguments().getLong(ID_EXTRA);
        initComponent(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.isShowRatingDialog = false;
    }

    private void initComponent(View view) {
        openDialogForRating();
        LinearLayout layoutRoot = (LinearLayout) view.findViewById(R.id.arb_layout_root);
        layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void openDialogForRating() {
        MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).title(R.string.Call_Quality).customView(R.layout.dialog_rating_call, true).theme(Theme.LIGHT).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                sendRateToServer();
                closeDialog();

            }
        }).negativeText(R.string.cancel).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                closeDialog();
            }
        }).build();
        View view = dialog.getView();

        dialog.getTitleView().setTypeface(G.typeface_IRANSansMobile);
        dialog.getActionButton(DialogAction.NEGATIVE).setTypeface(G.typeface_IRANSansMobile);
        dialog.getActionButton(DialogAction.POSITIVE).setTypeface(G.typeface_IRANSansMobile);


        final View positive = dialog.getActionButton(DialogAction.POSITIVE);
        positive.setEnabled(false);

        ratingBar = (RatingBar) view.findViewById(R.id.arb_ratingBar_call);
        edtResponse = (EditText) view.findViewById(R.id.arb_edt_resone);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating < 3) {
                    edtResponse.setVisibility(View.VISIBLE);
                    if (edtResponse.getText().length() > 0) {
                        positive.setEnabled(true);
                    } else {
                        positive.setEnabled(false);
                    }
                } else {
                    edtResponse.setVisibility(View.GONE);
                    positive.setEnabled(true);
                }
            }
        });

        edtResponse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtResponse.getText().length() > 0) {
                    positive.setEnabled(true);
                } else {
                    positive.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        view.findViewById(R.id.arb_layout_rate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // no action
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeDialog();
            }
        });
        dialog.show();
    }

    private void sendRateToServer() {
        String response = edtResponse.getText().toString();
        int rate = (int) ratingBar.getRating();
        new RequestSignalingRate().signalingRate(id, rate, response);
    }

    private void closeDialog() {
        ratingBar.setIsIndicator(true);
        popBackStackFragment();
    }
}
