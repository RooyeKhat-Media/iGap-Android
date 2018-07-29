package net.iGap.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;

public class FragmentFinancialServices extends BaseFragment {

    public static final String OPEN_IN_FRAGMENT_MAIN = "OPEN_IN_FRAGMENT_MAIN";
    boolean openInMain = false;

    public static FragmentFinancialServices newInstance(boolean openInFragmentMain) {
        FragmentFinancialServices fragmentFinancialServices = new FragmentFinancialServices();
        Bundle bundle = new Bundle();
        bundle.putBoolean(OPEN_IN_FRAGMENT_MAIN, openInFragmentMain);
        fragmentFinancialServices.setArguments(bundle);
        return fragmentFinancialServices;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_financial_services, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        openInMain = getArguments().getBoolean(OPEN_IN_FRAGMENT_MAIN);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color


        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.fragmentActivity.onBackPressed();
            }
        });


        TextView txtTop = view.findViewById(R.id.top);
        txtTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://taps.io/get-top";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        TextView txtPagear = view.findViewById(R.id.paygear);
        txtPagear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://taps.io/get-paygear";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        if (openInMain) {
            view.findViewById(R.id.fc_layot_title).setVisibility(View.GONE);

        }

    }

}
