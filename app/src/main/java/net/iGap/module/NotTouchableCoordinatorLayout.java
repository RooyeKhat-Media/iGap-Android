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
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * touching on CoordinatorLayout has disabled to only be scrolled while RecyclerView is scrolling
 */
public class NotTouchableCoordinatorLayout extends CoordinatorLayout {
    public NotTouchableCoordinatorLayout(Context context) {
        super(context);
    }

    public NotTouchableCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotTouchableCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}