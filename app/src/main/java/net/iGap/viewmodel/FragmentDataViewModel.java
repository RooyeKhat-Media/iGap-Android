package net.iGap.viewmodel;

import android.content.SharedPreferences;
import android.view.View;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentData;
import net.iGap.fragments.FragmentSetting;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by amir on 09/12/2017.
 */

public class FragmentDataViewModel {

    private SharedPreferences sharedPreferences;
    private int typeData;

    private final int MILADI = 0;
    private final int SHAMSI = 1;
    private final int GHAMARY = 2;


    public FragmentDataViewModel() {
        getInfo();
    }


    public void miladi(View view) {

        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_SETTING.KEY_DATA, MILADI);
        editor.apply();

        if (FragmentData.onFragmentRemoveData != null) FragmentData.onFragmentRemoveData.removeFragment();

        if (FragmentSetting.dateType != null) {
            FragmentSetting.dateType.dataName(G.fragmentActivity.getResources().getString(R.string.miladi));
        }

    }

    public void shamsi(View view) {

        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_SETTING.KEY_DATA, SHAMSI);
        editor.apply();

        if (FragmentData.onFragmentRemoveData != null) FragmentData.onFragmentRemoveData.removeFragment();

        if (FragmentSetting.dateType != null) {
            FragmentSetting.dateType.dataName(G.fragmentActivity.getResources().getString(R.string.shamsi));
        }
    }

    public void ghamari(View view) {
        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SHP_SETTING.KEY_DATA, GHAMARY);
        editor.apply();

        if (FragmentData.onFragmentRemoveData != null) FragmentData.onFragmentRemoveData.removeFragment();

        if (FragmentSetting.dateType != null) {
            FragmentSetting.dateType.dataName(G.fragmentActivity.getResources().getString(R.string.ghamari));
        }
    }


    public boolean iconMiladi() {
        return typeData == MILADI ? true : false;
    }

    public boolean iconShamsi() {
        return typeData == SHAMSI ? true : false;
    }

    public boolean iconGhamari() {
        return typeData == GHAMARY ? true : false;
    }


    private void getInfo() {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        typeData = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 0);

    }


}
