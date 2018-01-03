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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import net.iGap.R;
import net.iGap.databinding.FragmentCreateChannelBinding;
import net.iGap.module.AppUtils;
import net.iGap.viewmodel.FragmentCreateChannelViewModel;

public class FragmentCreateChannel extends BaseFragment {

    public static OnRemoveFragment onRemoveFragment;

    private FragmentCreateChannelViewModel fragmentCreateChannelViewModel;
    private FragmentCreateChannelBinding fragmentCreateChannelBinding;

    public FragmentCreateChannel() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false);
        return attachToSwipeBack(fragmentCreateChannelBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        ProgressBar prgWaiting = fragmentCreateChannelBinding.fchPrgWaitingAddContact;
        AppUtils.setProgresColler(prgWaiting);

        onRemoveFragment = new OnRemoveFragment() {
            @Override
            public void remove() {
                popBackStackFragment();
            }
        };
    }

    private void initDataBinding() {

        fragmentCreateChannelViewModel = new FragmentCreateChannelViewModel(getArguments(), fragmentCreateChannelBinding);
        fragmentCreateChannelBinding.setFragmentCreateChannelViewModel(fragmentCreateChannelViewModel);
    }

    @Override
    public void onDetach() {

        fragmentCreateChannelViewModel.onDetach();
        super.onDetach();
    }

    public interface OnRemoveFragment {
        void remove();
    }

}
