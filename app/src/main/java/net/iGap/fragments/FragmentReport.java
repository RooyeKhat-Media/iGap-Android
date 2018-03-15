package net.iGap.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.proto.ProtoClientRoomReport;
import net.iGap.proto.ProtoUserReport;
import net.iGap.request.RequestClientRoomReport;
import net.iGap.request.RequestUserReport;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentReport extends BaseFragment {

    private RippleView txtBack;
    private RippleView rippleOk;
    private EditText edtReport;
    private long roomId;
    private long messageId = 0;
    private boolean isUserReport;

    public FragmentReport() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_report, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        Bundle extras = getArguments();
        if (extras != null) {
            roomId = extras.getLong("ROOM_ID");
            messageId = extras.getLong("MESSAGE_ID");
            isUserReport = extras.getBoolean("USER_ID");
        }

        txtBack = (RippleView) view.findViewById(R.id.stns_ripple_back);
        rippleOk = (RippleView) view.findViewById(R.id.verifyPassword_rippleOk);

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.fragmentActivity.onBackPressed();
            }
        });

        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtReport.getText().length() > 0) {
                    if (isUserReport) {
                        new RequestUserReport().userReport(roomId, ProtoUserReport.UserReport.Reason.OTHER, edtReport.getText().toString());
                    } else {
                        new RequestClientRoomReport().roomReport(roomId, messageId, ProtoClientRoomReport.ClientRoomReport.Reason.OTHER, edtReport.getText().toString());
                    }
                    closeKeyboard(v);
                    G.fragmentActivity.onBackPressed();
                } else {
                    Toast.makeText(G.fragmentActivity, "error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtReport = (EditText) view.findViewById(R.id.edtReport);

    }

    private void closeKeyboard(View v) {
        if (isAdded()) {
            try {
                InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }

    private void error(String error) {
        if (isAdded()) {
            try {

                HelperError.showSnackMessage(error, true);

            } catch (IllegalStateException e) {
                e.getStackTrace();
            }
        }
    }
}
