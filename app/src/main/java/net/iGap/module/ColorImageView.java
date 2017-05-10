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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;
import net.iGap.G;

public class ColorImageView extends ImageView {
    public ColorImageView(Context context) {
        super(context);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public ColorImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }

    public ColorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }

    public ColorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setColorFilter(Color.parseColor(G.attachmentColor));
    }
}
