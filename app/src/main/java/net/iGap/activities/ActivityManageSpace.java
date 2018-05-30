package net.iGap.activities;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.ActivityManageSpaceBinding;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.ActivityManageSpaceViewModel;

import static net.iGap.G.context;

public class ActivityManageSpace extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (G.isDarkTheme) {
            this.setTheme(R.style.Material_blackCustom);
        } else {
            this.setTheme(R.style.Material_lightCustom);
        }

        super.onCreate(savedInstanceState);
        ActivityManageSpaceBinding activityManageSpaceBinding = DataBindingUtil.setContentView(this, R.layout.activity_manage_space);
        ActivityManageSpaceViewModel activityManageSpaceViewModel = new ActivityManageSpaceViewModel(this);
        activityManageSpaceBinding.setActivityManageSpaceViewModel(activityManageSpaceViewModel);
        activityManageSpaceBinding.stnsRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

    }
}
