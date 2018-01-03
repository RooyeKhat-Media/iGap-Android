package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentFragmentSecurityBinding;
import net.iGap.viewmodel.FragmentSecurityViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSecurity extends BaseFragment {

    public static boolean isFirstSetPassword = true;
    public static boolean isSetRecoveryEmail = false;
    public FragmentSecurityViewModel fragmentSecurityViewModel;
    public FragmentFragmentSecurityBinding fragmentSecurityBinding;
    public static OnPopBackStackFragment onPopBackStackFragment;


    public FragmentSecurity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentSecurityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fragment_security, container, false);
        return attachToSwipeBack(fragmentSecurityBinding.getRoot());
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();

        onPopBackStackFragment = new OnPopBackStackFragment() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };
    }

    private void initDataBinding() {
        fragmentSecurityViewModel = new FragmentSecurityViewModel();
        fragmentSecurityBinding.setFragmentSecurityViewModel(fragmentSecurityViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() == null) {
            return;
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    fragmentSecurityViewModel.rippleBack(v);
                    return true;
                }
                return false;
            }
        });
    }

    public interface OnPopBackStackFragment {
        void onBack();
    }
}
