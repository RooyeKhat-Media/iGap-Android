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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.iGap.R;
import net.iGap.helper.FontCache;

public class AvlDirectCall extends LinearLayout {

    public AvlDirectCall(Context context) {
        super(context);

        init(context);
    }

    public AvlDirectCall(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public AvlDirectCall(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) public AvlDirectCall(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {

        TextView txtDirect1 = (TextView) makeHeaderTextView(context);
        TextView txtDirect2 = (TextView) makeHeaderTextView(context);
        TextView txtDirect3 = (TextView) makeHeaderTextView(context);

        addAnimation(txtDirect1, 0);
        addAnimation(txtDirect2, 250);
        addAnimation(txtDirect3, 500);

        this.addView(txtDirect1);
        this.addView(txtDirect2);
        this.addView(txtDirect3);
    }

    private View makeHeaderTextView(Context context) {

        TextView textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#312B30"));
        textView.setTextSize(context.getResources().getDimension(R.dimen.dp14));
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(R.string.md_expand_arrow);
        textView.setTypeface(FontCache.get("fonts/MaterialIcons-Regular.ttf", context));

        return textView;
    }

    private void addAnimation(View view, int start) {

        Animation fadeIn = new AlphaAnimation(.4f, 1);
        fadeIn.setStartOffset(start);
        fadeIn.setDuration(750);
        fadeIn.setRepeatCount(Animation.REVERSE);
        fadeIn.setRepeatCount(Animation.INFINITE);

        view.setAnimation(fadeIn);
    }
}
