package net.iGap.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentPaymentInquiryBinding;
import net.iGap.interfaces.IBackHandler;
import net.iGap.viewmodel.FragmentPaymentInquiryViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaymentInquiry extends BaseFragment {

    private FragmentPaymentInquiryBinding fragmentPaymentInquiryBinding;


    public static FragmentPaymentInquiry newInstance(FragmentPaymentInquiryViewModel.OperatorType type, String phone) {

        Bundle args = new Bundle();
        args.putSerializable("type", type);

        if (phone != null && phone.length() > 0) {
            args.putString("phone", phone);
        }

        FragmentPaymentInquiry fragmentPaymentInquiry = new FragmentPaymentInquiry();
        fragmentPaymentInquiry.setArguments(args);

        return fragmentPaymentInquiry;
    }

    public FragmentPaymentInquiry() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPaymentInquiryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_inquiry, container, false);
        return attachToSwipeBack(fragmentPaymentInquiryBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentPaymentInquiryViewModel.OperatorType type = (FragmentPaymentInquiryViewModel.OperatorType) getArguments().getSerializable("type");

        FragmentPaymentInquiryViewModel fragmentPaymentInquiryViewModel = new FragmentPaymentInquiryViewModel(fragmentPaymentInquiryBinding, type);
        fragmentPaymentInquiryBinding.setFragmentPaymentInquiryViewModel(fragmentPaymentInquiryViewModel);

        IBackHandler iBackHandler = new IBackHandler() {
            @Override
            public void onBack() {
                popBackStackFragment();
            }
        };
        fragmentPaymentInquiryBinding.setBackHandler(iBackHandler);

        String phone = getArguments().getString("phone");
        if (phone != null && phone.length() > 0) {
            fragmentPaymentInquiryBinding.fpiEdtMci.setText(phone);
            fragmentPaymentInquiryViewModel.onInquiryClick(null);
        }
    }


}