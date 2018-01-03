package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentPassCodeBinding;
import net.iGap.module.AppUtils;
import net.iGap.viewmodel.FragmentPassCodeViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPassCode extends BaseFragment {

    private FragmentPassCodeViewModel fragmentPassCodeViewModel;
    private FragmentPassCodeBinding fragmentPassCodeBinding;

    public FragmentPassCode() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentPassCodeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_pass_code, container, false);
        return attachToSwipeBack(fragmentPassCodeBinding.getRoot());
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        fragmentPassCodeBinding.stnsTxtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //G.fragmentActivity.getSupportFragmentManager().popBackStack();

                popBackStackFragment();

                AppUtils.closeKeyboard(v);

            }
        });
    }

    private void initDataBinding() {
        fragmentPassCodeViewModel = new FragmentPassCodeViewModel();
        fragmentPassCodeBinding.setFragmentPassCodeViewModel(fragmentPassCodeViewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentPassCodeViewModel.onDestroy();
    }


}
