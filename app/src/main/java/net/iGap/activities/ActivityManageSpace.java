package net.iGap.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import net.iGap.R;
import net.iGap.databinding.ActivityManageSpaceBinding;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.viewmodel.ActivityManageSpaceViewModel;

public class ActivityManageSpace extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
