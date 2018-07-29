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
import net.iGap.databinding.FragmentThemColorBinding;
import net.iGap.viewmodel.FragmentThemColorViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThemColor extends BaseFragment {


    private FragmentThemColorViewModel fragmentThemColorViewModel;
    private FragmentThemColorBinding fragmentThemColorBinding;


    public FragmentThemColor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentThemColorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_them_color, container, false);
        return attachToSwipeBack(fragmentThemColorBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        fragmentThemColorBinding.stnsRippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });
    }

    private void initDataBinding() {
        fragmentThemColorViewModel = new FragmentThemColorViewModel(this, fragmentThemColorBinding);
        fragmentThemColorBinding.setFragmentThemColorViewModel(fragmentThemColorViewModel);

    }
}
