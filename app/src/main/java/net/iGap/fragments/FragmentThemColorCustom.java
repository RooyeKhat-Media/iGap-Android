package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentThemColorCustomBinding;
import net.iGap.viewmodel.FragmentThemColorCustomViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThemColorCustom extends BaseFragment {


    private FragmentThemColorCustomViewModel fragmentThemColorCustomViewModel;
    private FragmentThemColorCustomBinding fragmentThemColorCustomBinding;


    public FragmentThemColorCustom() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentThemColorCustomBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_them_color_custom, container, false);
        return attachToSwipeBack(fragmentThemColorCustomBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        fragmentThemColorCustomBinding.stnsRippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });
    }

    private void initDataBinding() {
        fragmentThemColorCustomViewModel = new FragmentThemColorCustomViewModel(this, fragmentThemColorCustomBinding);
        fragmentThemColorCustomBinding.setFragmentThemColorCustomViewModel(fragmentThemColorCustomViewModel);

    }
}
