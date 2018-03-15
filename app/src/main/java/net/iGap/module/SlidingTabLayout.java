/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * make main activity sliding tabs contain all and chat and group and channel
 */

public class SlidingTabLayout extends HorizontalScrollView {

    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 18;
    private final SlidingTabStrip mTabStrip;
    private List<tabParameter> mTabs = new ArrayList<tabParameter>();

    private int mTitleOffset;
    private int mTabViewLayoutId;
    private int mTabViewTextViewId;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    public SlidingTabLayout(Context context) {
        this(context, null);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);

        mTabStrip = new SlidingTabStrip(context);

        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    public void setDividerColors(int... colors) {
        mTabStrip.setDividerColors(colors);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    public void setViewPager(ViewPager viewPager) {

        fillTabs();

        setCustomTabColorizer(new TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });

        mTabStrip.removeAllViews();
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    protected TextView createDefaultTabView(Context context, int i) {

        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);

        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP * 2);
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (TAB_VIEW_TEXT_SIZE_SP * 1.5));
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) (TAB_VIEW_TEXT_SIZE_SP * .75));
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        }

        //    textView.setTypeface(custom_font);
        textView.setTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {// allcaps is for api 14 and upper
            textView.setAllCaps(false);
        }

        if (i == 0) {
            textView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8.5f));
        } else if (i == 1) {
            textView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8.5f));
        } else if (i == 2) {
            textView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8.5f));
        } else {
            textView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 8.5f));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style

        }

        float dp = getResources().getDisplayMetrics().density;
        textView.setPadding(2, (int) (20 * dp), 2, (int) (20 * dp));

        return textView;
    }

    @SuppressWarnings("deprecation")
    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();

        for (int i = 0; i < adapter.getCount(); i++) {
            View tabView = null;
            TextView tabTitleView = null;

            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip, false);
                tabTitleView = (TextView) tabView.findViewById(mTabViewTextViewId);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext(), i);
            }

            if (tabTitleView == null && TextView.class.isInstance(tabView)) {
                tabTitleView = (TextView) tabView;
            }

            tabTitleView.setText(mTabs.get(i).getTitle());
            tabView.setOnClickListener(tabClickListener);
            tabView.setBackgroundDrawable(null);
            mTabStrip.addView(tabView);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);

        for (int i = 0; i < mTabStrip.getChildCount(); i++) {
            //	mTabStrip.getChildAt(i).setBackgroundColor(Color.parseColor("#2bbfbd"));

            //
            //			if (i != 0) {
            //				mTabStrip.getChildAt(i).findViewById(R.id.btn_create).setVisibility(View.VISIBLE);
            //			} else {
            //				mTabStrip.getChildAt(i).findViewById(R.id.btn_create).setVisibility(View.GONE);
            //			}

            ((TextView) mTabStrip.getChildAt(i)).setTextColor(Color.parseColor("#ffffff"));
        }

        if (selectedChild != null) {

            //	selectedChild.setBackgroundColor(Color.parseColor("#ffffff"));
            ((TextView) selectedChild).setTextColor(Color.parseColor("#ffffff"));
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }

            scrollTo(targetScrollX, 0);
        }
    }

    private void fillTabs() {

        mTabs.add(new tabParameter(
                //   getContext().getString(R.string.fa_map_signs_all) + " ",//+getContext().getString(R.string.all_en), // Title
                "weekly", Color.WHITE, // Indicator color
                Color.parseColor("#2bbfbd")// Divider color
        ));

        mTabs.add(new tabParameter(
                //   getContext().getString(R.string.fa_user) + " ",//+getContext().getString(R.string.chats_en), // Title
                "mounthly", Color.WHITE, // Indicator color
                Color.parseColor("#2bbfbd") // Divider color
        ));

        mTabs.add(new tabParameter(
                //    getContext().getString(R.string.fa_group) + " ",//+getContext().getString(R.string.group_en), // Title
                "yearly", Color.WHITE, // Indicator color
                Color.parseColor("#2bbfbd") // Divider color
        ));
    }

    public interface TabColorizer {

        int getIndicatorColor(int position);

        int getDividerColor(int position);
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {

        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null) ? (int) (positionOffset * selectedTitle.getWidth()) : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {

            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }
    }

    private class TabClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                //	mTabStrip.getChildAt(i).setBackgroundColor(Color.parseColor("#2bbfbd"));

                ((TextView) mTabStrip.getChildAt(i)).setTextColor(Color.parseColor("#ffffff"));

                if (v == mTabStrip.getChildAt(i)) {
                    mViewPager.setCurrentItem(i);
                    //	mTabStrip.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
                    ((TextView) mTabStrip.getChildAt(i)).setTextColor(Color.parseColor("#ffffff"));
                }
            }
        }
    }

    private class tabParameter {

        private final String mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        public tabParameter(String title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }
    }
}
