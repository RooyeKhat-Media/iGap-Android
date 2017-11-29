package net.iGap.fragments;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.request.RequestUserProfileSetBio;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBio extends BaseFragment {

    private RippleView txtBack;
    private RippleView rippleOk;
    private EditText edtBio;
    private int mCount = 70;
    private TextView txtCount;
    private boolean isEndLine = true;
    private String specialRequests;
    private String bio;

    public FragmentBio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_bio, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));


        Bundle bundle = getArguments();

        if (bundle != null) {
            bio = bundle.getString("BIO");
        }


        txtBack = (RippleView) view.findViewById(R.id.stns_ripple_back);
        rippleOk = (RippleView) view.findViewById(R.id.verifyPassword_rippleOk);

        txtCount = (TextView) view.findViewById(R.id.txtCountBio);


        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                G.fragmentActivity.onBackPressed();
            }
        });

        rippleOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new RequestUserProfileSetBio().setBio(edtBio.getText().toString());
                    closeKeyboard(v);

                G.fragmentActivity.onBackPressed();
            }
        });


        edtBio = (EditText) view.findViewById(R.id.edtBio);
        edtBio.setText(bio);

        edtBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                txtCount.setText("" + (mCount - edtBio.getText().length()));

            }

            @Override
            public void afterTextChanged(Editable s) {

                edtBio.removeTextChangedListener(this);
                if (edtBio.getText().length() >= 70) {
                    edtBio.setText(specialRequests);
                    //edtMessageGps.setSelection(lastSpecialRequestsCursorPosition);

                    if (isEndLine) {
                        isEndLine = false;
                        error(G.fragmentActivity.getResources().getString(R.string.exceed_4_line));
                    }
                } else {
                    isEndLine = true;
                    specialRequests = edtBio.getText().toString();
                }

                edtBio.addTextChangedListener(this);

            }
        });
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
