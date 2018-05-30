package net.iGap.activities;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityEnterPassCodeBinding;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.ActivityEnterPassCodeViewModel;

import static net.iGap.G.context;

public class ActivityEnterPassCode extends ActivityEnhanced {

    private ActivityEnterPassCodeViewModel activityManageSpaceViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (G.isDarkTheme) {
            this.setTheme(R.style.Material_blackCustom);
        } else {
            this.setTheme(R.style.Material_lightCustom);
        }

        super.onCreate(savedInstanceState);
        ActivityEnterPassCodeBinding activityEnterPassCodeBinding = DataBindingUtil.setContentView(this, R.layout.activity_enter_pass_code);
        activityManageSpaceViewModel = new ActivityEnterPassCodeViewModel(this, activityEnterPassCodeBinding.getRoot());
        activityEnterPassCodeBinding.setActivityEnterPassCodeViewModel(activityManageSpaceViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();

        activityManageSpaceViewModel.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activityManageSpaceViewModel.onDestroy();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (ActivityMain.finishActivity != null) {
            ActivityMain.finishActivity.finishActivity();
        }
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            System.exit(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityManageSpaceViewModel.onStart();
    }

}
