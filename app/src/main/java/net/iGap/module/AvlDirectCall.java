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
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AvlDirectCall(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context);
    }

    private void init(Context context) {

        int time = 250;

        final TextView txtDirect2 = (TextView) makeHeaderTextView(context);
        final TextView txtDirect3 = (TextView) makeHeaderTextView(context);
        final TextView txtDirect4 = (TextView) makeHeaderTextView(context);

        final Animation fadeIn2 = new AlphaAnimation(0, 1);
        fadeIn2.setDuration(time);

        final Animation fadeIn3 = new AlphaAnimation(0, 1);
        fadeIn3.setDuration(time);

        final Animation fadeIn4 = new AlphaAnimation(0, 1);
        fadeIn4.setDuration(time);

        txtDirect4.setAnimation(fadeIn4);

        fadeIn4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                txtDirect3.startAnimation(fadeIn3);
                txtDirect4.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeIn3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtDirect2.startAnimation(fadeIn2);
                txtDirect3.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fadeIn2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                txtDirect4.setVisibility(INVISIBLE);
                txtDirect3.setVisibility(INVISIBLE);

                txtDirect4.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtDirect4.startAnimation(fadeIn4);
                    }
                }, 750);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        this.addView(txtDirect2);
        this.addView(txtDirect3);
        this.addView(txtDirect4);
    }

    private View makeHeaderTextView(Context context) {

        TextView textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#ffffff"));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.dp16));
        LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(lp);
        textView.setPadding(0, -(ViewMaker.i_Dp(R.dimen.dp6)), 0, -(ViewMaker.i_Dp(R.dimen.dp6)));
        textView.setText(R.string.md_expand_arrow);
        textView.setVisibility(INVISIBLE);
        textView.setTypeface(G.typeface_Fontico);


        return textView;
    }

    private void getAnimation(View view, int start) {

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setStartOffset(start);
        // fadeIn.setDuration(600);
        // fadeIn.setRepeatCount(Animation.REVERSE);
        fadeIn.setRepeatCount(Animation.INFINITE);

        view.setAnimation(fadeIn);
    }
}
