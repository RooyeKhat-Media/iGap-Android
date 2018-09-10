package net.iGap.fragments;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentPassCodeBinding;
import net.iGap.module.AppUtils;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentPassCodeViewModel;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPassCode extends BaseFragment {

    private FragmentPassCodeViewModel fragmentPassCodeViewModel;
    private FragmentPassCodeBinding fragmentPassCodeBinding;
    private boolean isPattern;



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

        boolean isLinePattern;
        if (isPattern){
            SharedPreferences sharedPreferences = G.currentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            isLinePattern = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        }else {
            isLinePattern = true;
        }

        fragmentPassCodeBinding.patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);       // Set the current viee more
        fragmentPassCodeBinding.patternLockView.setInStealthMode(!isLinePattern);                                     // Set the pattern in stealth mode (pattern drawing is hidden)
        fragmentPassCodeBinding.patternLockView.setTactileFeedbackEnabled(true);                            // Enables vibration feedback when the pattern is drawn
        fragmentPassCodeBinding.patternLockView.setInputEnabled(true);                                     // Disables any input from the pattern lock view completely

        fragmentPassCodeBinding.patternLockView.setDotCount(4);
        fragmentPassCodeBinding.patternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(G.fragmentActivity, R.dimen.dp22));
        fragmentPassCodeBinding.patternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(G.fragmentActivity, R.dimen.dp32));
        fragmentPassCodeBinding.patternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(G.fragmentActivity, R.dimen.pattern_lock_path_width));
        fragmentPassCodeBinding.patternLockView.setAspectRatioEnabled(true);
        fragmentPassCodeBinding.patternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        if (G.isDarkTheme) {
            fragmentPassCodeBinding.patternLockView.setNormalStateColor(ResourceUtils.getColor(G.fragmentActivity, R.color.white));
        }else {
            fragmentPassCodeBinding.patternLockView.setNormalStateColor(Color.parseColor(G.appBarColor));
        }
        fragmentPassCodeBinding.patternLockView.setCorrectStateColor(ResourceUtils.getColor(G.fragmentActivity, R.color.green));
        fragmentPassCodeBinding.patternLockView.setWrongStateColor(ResourceUtils.getColor(G.fragmentActivity, R.color.red));
        fragmentPassCodeBinding.patternLockView.setDotAnimationDuration(150);
        fragmentPassCodeBinding.patternLockView.setPathEndAnimationDuration(100);

    }

    private void initDataBinding() {
        fragmentPassCodeViewModel = new FragmentPassCodeViewModel(fragmentPassCodeBinding);
        isPattern = fragmentPassCodeViewModel.isPattern;
        fragmentPassCodeBinding.setFragmentPassCodeViewModel(fragmentPassCodeViewModel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentPassCodeViewModel.onDestroy();
    }


}
