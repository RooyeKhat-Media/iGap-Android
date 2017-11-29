package net.iGap.fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentData extends BaseFragment {

    private SharedPreferences sharedPreferences;

    private final int MILADI = 0;
    private final int SHAMSI = 1;
    private final int GHAMARY = 2;

    public FragmentData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_data, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        ViewGroup root = (ViewGroup) view.findViewById(R.id.rootFragmentLanguage);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stns_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });

        TextView txtMiladi = (TextView) view.findViewById(R.id.txtLanguageMiladi);
        TextView iconMiladi = (TextView) view.findViewById(R.id.st_icon_miladi);
        TextView txtEnShamsi = (TextView) view.findViewById(R.id.txtShamsi);
        TextView iconShamsi = (TextView) view.findViewById(R.id.st_icon_shamsi);
        TextView iconGhamari = (TextView) view.findViewById(R.id.st_icon_ghamary);


        int typeData = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 0);

        switch (typeData) {
            case MILADI:
                iconMiladi.setVisibility(View.VISIBLE);
                break;
            case SHAMSI:
                iconShamsi.setVisibility(View.VISIBLE);
                break;
            case GHAMARY:
                iconGhamari.setVisibility(View.VISIBLE);
                break;
        }

        ViewGroup vgMiladi = (ViewGroup) view.findViewById(R.id.st_layout_miladi);
        ViewGroup vgShamsi = (ViewGroup) view.findViewById(R.id.st_layout_Shamsi);
        ViewGroup vgGhamari = (ViewGroup) view.findViewById(R.id.st_layout_ghamari);

        vgShamsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onDateChanged != null) {
                    G.onDateChanged.onChange();
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_DATA, SHAMSI);
                editor.apply();
                removeFromBaseFragment(FragmentData.this);

                if (FragmentSetting.dateType != null) {
                    FragmentSetting.dateType.dataName(G.fragmentActivity.getResources().getString(R.string.shamsi));
                }
            }
        });

        vgMiladi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onDateChanged != null) {
                    G.onDateChanged.onChange();
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_DATA, MILADI);
                editor.apply();
                removeFromBaseFragment(FragmentData.this);

                if (FragmentSetting.dateType != null) {
                    FragmentSetting.dateType.dataName(G.fragmentActivity.getResources().getString(R.string.miladi));
                }
            }
        });

        vgGhamari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onDateChanged != null) {
                    G.onDateChanged.onChange();
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(SHP_SETTING.KEY_DATA, GHAMARY);
                editor.apply();
                removeFromBaseFragment(FragmentData.this);

                if (FragmentSetting.dateType != null) {
                    FragmentSetting.dateType.dataName(G.fragmentActivity.getResources().getString(R.string.ghamari));
                }
            }
        });
    }

}
