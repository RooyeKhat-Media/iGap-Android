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
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import net.iGap.G;

public class HeaderTextview extends TextView {

    public HeaderTextview(Context context) {
        super(context);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }

    public HeaderTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }

    public HeaderTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTextColor(Color.parseColor(G.headerTextColor));
    }
}
