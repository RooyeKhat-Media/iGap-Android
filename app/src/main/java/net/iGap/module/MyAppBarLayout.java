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
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

public class MyAppBarLayout extends AppBarLayout implements AppBarLayout.OnOffsetChangedListener {
    private OnMoveListener mListener;

    public MyAppBarLayout(Context context, OnMoveListener listener) {
        super(context);

        mListener = listener;

        init();
    }

    public MyAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        addOnOffsetChangedListener(this);
    }

    public void addOnMoveListener(OnMoveListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int verticalOffsetAbs = Math.abs(verticalOffset);
        int appBarLayoutHeight = appBarLayout.getHeight();

        if (verticalOffset == 0) {
            if (mListener != null) {
                mListener.onAppBarLayoutMove(appBarLayout, verticalOffset, false);
            }
            return;
        } else if (verticalOffset == appBarLayoutHeight) {
            if (mListener != null) {
                mListener.onAppBarLayoutMove(appBarLayout, verticalOffset, true);
            }
            return;
        }

        if (mListener != null) {
            mListener.onAppBarLayoutMove(appBarLayout, verticalOffset, verticalOffsetAbs > appBarLayoutHeight - AndroidUtils.getStatusBarHeight(getContext()));
        }
    }

    public interface OnMoveListener {
        void onAppBarLayoutMove(AppBarLayout appBarLayout, int verticalOffset, boolean moveUp);
    }
}
