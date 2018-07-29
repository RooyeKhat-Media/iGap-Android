package org.paygear.wallet.fragment;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.paygear.wallet.R;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;

import ir.radsense.raadcore.OnFragmentInteraction;
import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.utils.Typefaces;


public class CashOutFragment extends Fragment {

    RaadToolBar appBar;
    ViewPager mPager;
    WalletPagerAdapter mPagerAdapter;

    Card mCard;
    boolean isCashOut;

    public CashOutFragment() {
    }

    public static CashOutFragment newInstance(Card card, boolean isCashOut) {
        CashOutFragment fragment = new CashOutFragment();
        Bundle args = new Bundle();
        args.putSerializable("Card", card);
        args.putBoolean("IsCashOut", isCashOut);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCard = (Card) getArguments().getSerializable("Card");
            isCashOut = getArguments().getBoolean("IsCashOut");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cash_out, container, false);

        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.primaryColor));
        }
        appBar = view.findViewById(R.id.app_bar);
        appBar.setTitle(getString(isCashOut ? R.string.cash_out_paygear : R.string.charge_paygear));
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape,true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor),PorterDuff.Mode.SRC_IN));
        appBar.showBack();

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        mPager = view.findViewById(R.id.view_pager);
        mPagerAdapter = new WalletPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        tabLayout.setupWithViewPager(mPager);

        if (isCashOut) {
            mPager.setCurrentItem(1);
        }

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                TextView textView = new TextView(getContext());
                textView.setId(android.R.id.text1);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setTypeface(Typefaces.get(getContext(), Typefaces.IRAN_YEKAN_REGULAR));

                tab.setCustomView(textView);
            }
        }

        return view;
    }


    class WalletPagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments;

        public WalletPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[getCount()];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return CashOutRequestFragment.newInstance(mCard,
                            isCashOut ? CashOutRequestFragment.REQUEST_CASH_OUT_NORMAL : CashOutRequestFragment.REQUEST_CASH_IN);
                case 1:
                    return CashOutRequestFragment.newInstance(mCard, CashOutRequestFragment.REQUEST_CASH_OUT_IMMEDIATE);
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            fragments[position] = (Fragment) super.instantiateItem(container, position);
            return fragments[position];
        }

        public Fragment getFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return isCashOut ? 2 : 1;
        }

        public void notifyFragments(Fragment fragment, Bundle bundle) {
            //((OnFragmentInteraction) fragments[0]).onFragmentResult(fragment, bundle);
            if (fragments[2] != null)
                ((OnFragmentInteraction) fragments[2]).onFragmentResult(fragment, bundle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(isCashOut ? R.string.normal_cash_out : R.string.charge_paygear);
                case 1:
                    return getString(R.string.immediate_cash_out);
                default:
                    return "";
            }
        }
    }

}
