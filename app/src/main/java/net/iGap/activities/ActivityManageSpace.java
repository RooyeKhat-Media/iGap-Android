package net.iGap.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import net.iGap.R;
import net.iGap.databinding.ActivityManageSpaceBinding;
import net.iGap.fragments.FragmentDataUsage;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDataUsage;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.proto.ProtoGlobal;
import net.iGap.viewmodel.ActivityManageSpaceViewModel;

public class ActivityManageSpace extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManageSpaceBinding activityManageSpaceBinding = DataBindingUtil.setContentView(this, R.layout.activity_manage_space);
        ActivityManageSpaceViewModel activityManageSpaceViewModel = new ActivityManageSpaceViewModel(this);
        activityManageSpaceBinding.setActivityManageSpaceViewModel(activityManageSpaceViewModel);


        activityManageSpaceBinding.vgMobileDataUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("TYPE", true);
                FragmentDataUsage fragmentDataUsage = new FragmentDataUsage();
                fragmentDataUsage.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
                fragmentTransaction.add(activityManageSpaceBinding.dataUsageContainer.getId(), fragmentDataUsage, fragmentDataUsage.getClass().getName());

                fragmentTransaction.addToBackStack(fragmentDataUsage.getClass().getName());
                fragmentTransaction.commit();
            }
        });
        activityManageSpaceBinding.vgWifiDataUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putBoolean("TYPE", false);
                FragmentDataUsage fragmentDataUsage = new FragmentDataUsage();
                fragmentDataUsage.setArguments(bundle);
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left);
                fragmentTransaction.add(activityManageSpaceBinding.dataUsageContainer.getId(), fragmentDataUsage);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        activityManageSpaceBinding.stnsRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                finish();
            }
        });

    }

}
