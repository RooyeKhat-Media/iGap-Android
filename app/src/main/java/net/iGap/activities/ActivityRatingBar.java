package net.iGap.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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

public class ActivityRatingBar extends ActivityEnhanced {

    public static final String ID_EXTRA = "ID_EXTRA";

    RatingBar ratingBar;
    EditText edtResone;
    long id = -1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        G.isShowRatingDialog = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        G.isShowRatingDialog = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_bar);

        id = getIntent().getExtras().getLong(ID_EXTRA);

        initComponent();
    }

    private void initComponent() {
        openDialogForRating();

        LinearLayout layotRoot = (LinearLayout) findViewById(R.id.arb_layout_root);
        layotRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openDialogForRating() {

        MaterialDialog dialog = new MaterialDialog.Builder(ActivityRatingBar.this).title(R.string.Call_Quality).customView(R.layout.dialog_rating_call, true).theme(Theme.LIGHT).positiveText(R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
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
        edtResone = (EditText) view.findViewById(R.id.arb_edt_resone);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating < 3) {

                    edtResone.setVisibility(View.VISIBLE);

                    if (edtResone.getText().length() > 0) {
                        positive.setEnabled(true);
                    } else {
                        positive.setEnabled(false);
                    }
                } else {

                    edtResone.setVisibility(View.GONE);
                    positive.setEnabled(true);
                }
            }
        });

        edtResone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (edtResone.getText().length() > 0) {
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

        String resone = edtResone.getText().toString();
        int rate = (int) ratingBar.getRating();

        new RequestSignalingRate().signalingRate(id, rate, resone);
    }

    private void closeDialog() {

        ratingBar.setIsIndicator(true);
        finish();
    }
}
