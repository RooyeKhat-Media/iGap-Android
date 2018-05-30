package net.iGap.fragments;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentDarkThemeBinding;
import net.iGap.viewmodel.FragmentDarkThemeViewModel;
import net.iGap.viewmodel.FragmentLanguageViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDarkTheme extends BaseFragment {

    public static FragmentDarkTheme newInstance() {
        return new FragmentDarkTheme();
    }


    private FragmentDarkThemeViewModel fragmentDarkThemeViewModel;
    private FragmentDarkThemeBinding fragmentDarkThemeBinding;


    public FragmentDarkTheme() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentDarkThemeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dark_theme, container, false);
        return attachToSwipeBack(fragmentDarkThemeBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();

        initDataBinding();
        fragmentDarkThemeBinding.stnsRippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });

    }

    private void initDataBinding() {
        fragmentDarkThemeViewModel = new FragmentDarkThemeViewModel(this);
        fragmentDarkThemeBinding.setFragmentDarkThemeViewModel(fragmentDarkThemeViewModel);

    }
}
