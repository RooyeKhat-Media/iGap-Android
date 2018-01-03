package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentLanguageBinding;
import net.iGap.viewmodel.FragmentLanguageViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLanguage extends BaseFragment {


    private FragmentLanguageViewModel fragmentLanguageViewModel;
    private FragmentLanguageBinding fragmentLanguageBinding;
    public static boolean languageChanged = false;


    public FragmentLanguage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLanguageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_language, container, false);
        return attachToSwipeBack(fragmentLanguageBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        fragmentLanguageBinding.stnsRippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });
    }

    private void initDataBinding() {
        fragmentLanguageViewModel = new FragmentLanguageViewModel(this);
        fragmentLanguageBinding.setFragmentLanguageViewModel(fragmentLanguageViewModel);

    }
}
