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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentNotificationBinding;
import net.iGap.viewmodel.FragmentNotificationViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotification extends BaseFragment {

    private long roomId;
    private FragmentNotificationBinding fragmentNotificationBinding;
    private FragmentNotificationViewModel fragmentNotificationViewModel;

    public FragmentNotification() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentNotificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return attachToSwipeBack(fragmentNotificationBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomId = getArguments().getLong("ID");
        initDataBinding();
        fragmentNotificationBinding.toolbar2.setBackgroundColor(Color.parseColor(G.appBarColor));

        fragmentNotificationBinding.ntgTxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    popBackStackFragment();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initDataBinding() {
        fragmentNotificationViewModel = new FragmentNotificationViewModel(fragmentNotificationBinding, roomId);
        fragmentNotificationBinding.setFragmentNotificationViewModel(fragmentNotificationViewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentNotificationViewModel.destroy();
    }
}
