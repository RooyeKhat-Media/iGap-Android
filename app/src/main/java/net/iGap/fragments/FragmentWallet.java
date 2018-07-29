package net.iGap.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentWalletBinding;
import net.iGap.interfaces.IBackHandler;
import net.iGap.viewmodel.FragmentWalletViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWallet extends BaseFragment {

    private FragmentWalletBinding fragmentWalletBinding;

    public static FragmentWallet newInstance() {
        return new FragmentWallet();
    }

    public FragmentWallet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentWalletBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);
        return attachToSwipeBack(fragmentWalletBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding(getArguments());
    }

    private void initDataBinding(Bundle arguments) {
        FragmentWalletViewModel fragmentWalletViewModel = new FragmentWalletViewModel(arguments);
        fragmentWalletBinding.setFragmentWalletViewModel(fragmentWalletViewModel);

        IBackHandler iBackHandler = new IBackHandler() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };

        fragmentWalletBinding.setBackHandler(iBackHandler);
    }
}