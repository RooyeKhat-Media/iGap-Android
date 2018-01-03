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
import net.iGap.databinding.FragmentBioBinding;
import net.iGap.viewmodel.FragmentBioViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBio extends BaseFragment {

    private FragmentBioViewModel fragmentBioViewModel;
    private FragmentBioBinding fragmentBioBinding;
    public static OnBackFragment onBackFragment;

    public FragmentBio() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentBioBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bio, container, false);
        return attachToSwipeBack(fragmentBioBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        initDataBinding(getArguments());

        onBackFragment = new OnBackFragment() {
            @Override
            public void onBack() {
                G.fragmentActivity.onBackPressed();
            }
        };
    }

    private void initDataBinding(Bundle arguments) {

        fragmentBioViewModel = new FragmentBioViewModel(arguments);
        fragmentBioBinding.setFragmentBioViewModel(fragmentBioViewModel);

    }


    public interface OnBackFragment {

        void onBack();

    }
}
