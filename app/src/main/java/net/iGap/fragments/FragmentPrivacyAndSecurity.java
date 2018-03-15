/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentPrivacyAndSecurityBinding;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.realm.RealmPrivacy;
import net.iGap.request.RequestUserContactsGetBlockedList;
import net.iGap.request.RequestUserProfileGetSelfRemove;
import net.iGap.viewmodel.FragmentPrivacyAndSecurityViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPrivacyAndSecurity extends BaseFragment {


    private FragmentPrivacyAndSecurityViewModel fragmentPrivacyAndSecurityViewModel;
    private FragmentPrivacyAndSecurityBinding fragmentPrivacyAndSecurityBinding;


    public FragmentPrivacyAndSecurity() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentPrivacyAndSecurityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_and_security, container, false);
        return attachToSwipeBack(fragmentPrivacyAndSecurityBinding.getRoot());
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPrivacyAndSecurityViewModel.onPause();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();

        new RequestUserContactsGetBlockedList().userContactsGetBlockedList();

        RealmPrivacy.getUpdatePrivacyFromServer();

        fragmentPrivacyAndSecurityBinding.parentPrivacySecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fragmentPrivacyAndSecurityBinding.stpsRippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                popBackStackFragment();
            }
        });

        new RequestUserProfileGetSelfRemove().userProfileGetSelfRemove();

    }

    private void initDataBinding() {

        fragmentPrivacyAndSecurityViewModel = new FragmentPrivacyAndSecurityViewModel();
        fragmentPrivacyAndSecurityBinding.setFragmentPrivacyAndSecurityViewModel(fragmentPrivacyAndSecurityViewModel);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentPrivacyAndSecurityViewModel.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPrivacyAndSecurityViewModel.onResume();
    }
}
