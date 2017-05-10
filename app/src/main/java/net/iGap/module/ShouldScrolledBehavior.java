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

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import net.iGap.activities.ActivityMain;

/**
 * scroll when adapter's items are not completely visible. in other words,
 * toolbar is always visible till adapter's items are enough to be scrolled.
 */
public class ShouldScrolledBehavior extends AppBarLayout.ScrollingViewBehavior {
    private LinearLayoutManager mLayoutManager;
    private ActivityMain.RoomAdapter mAdapter;

    public ShouldScrolledBehavior(LinearLayoutManager layoutManager, ActivityMain.RoomAdapter adapter) {
        super();
        this.mLayoutManager = layoutManager;
        this.mAdapter = adapter;
    }

    @Override public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        return shouldScrolled();
    }

    public boolean shouldScrolled() {
        // adapter has more items that not shown yet
        if (mLayoutManager.findLastCompletelyVisibleItemPosition() != mAdapter.getItemCount() - 1) {
            return true;
        }
        // last completely visible item is the last item in adapter but it may be occurred in 2 ways:
        // 1) all items are shown
        // 2) scrolled to the last item (implemented following)
        else if (mLayoutManager.findLastCompletelyVisibleItemPosition() == mAdapter.getItemCount() - 1 && mLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
            return true;
        }
        return false;
    }
}