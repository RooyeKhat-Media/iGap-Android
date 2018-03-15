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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.interfaces.OnColorChangedListenerSelect;

/**
 * for show dialog select color and return the select color code
 */

public class ColorPiker extends Dialog {

    private OnColorChangedListenerSelect Listener;
    private int[] mHueBarColors;
    private Button btnOk;
    private int[] mMainColors;
    private Button btnCancel;
    private ImageView ivLineColor;
    private ImageView ivSquareColor;
    private Bitmap bitmapsqure;
    private int defaultColor;
    private int selectColor;

    public ColorPiker(Context context, int defultColor, OnColorChangedListenerSelect onColorChangedListenerSelect) {
        super(context);
        Listener = onColorChangedListenerSelect;
        defaultColor = defultColor;
        selectColor = defaultColor;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setContentView(R.layout.dialog_select_color);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        adjustLineColor();
        adjustSqureColor(defaultColor);
        super.onWindowFocusChanged(hasFocus);
    }

    private void init() {

        btnOk = (Button) findViewById(R.id.button_ok);
        btnCancel = (Button) findViewById(R.id.button_cancel);
        ivLineColor = (ImageView) findViewById(R.id.imageView_line_color);
        ivSquareColor = (ImageView) findViewById(R.id.imageView_square_color);

        ivLineColor.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {

                adjustSqureColor(mHueBarColors[(int) event.getY()]);

                return false;
            }
        });

        ivSquareColor.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent event) {
                selectColor = bitmapsqure.getPixel((int) event.getX(), (int) event.getY());
                btnOk.setBackgroundColor(selectColor);

                return false;
            }
        });

        btnOk.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Listener.colorChanged("ok", selectColor);
                dismiss();
                return false;
            }
        });

        btnCancel.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }

    private void adjustLineColor() {

        int linewith = ivLineColor.getWidth();
        int linehight = ivLineColor.getHeight();
        Bitmap bitmapline = Bitmap.createBitmap(linewith, linehight, Bitmap.Config.ARGB_8888);
        bitmapline.eraseColor(Color.BLACK);
        ivLineColor.setImageBitmap(bitmapline);
        Canvas canvasline = new Canvas(bitmapline);
        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(1);

        int index = 0;
        mHueBarColors = new int[linehight];
        for (int i = 0; i < linehight; i++)
            mHueBarColors[i] = Color.BLACK;

        float counter = 256 / ((linehight) / 7);

        try {
            for (float i = 0; i < 256; i += counter) // Red (#f00) to pink  (#f0f)
            {
                mHueBarColors[index] = Color.rgb(255, 0, (int) i);
                index++;
            }
            for (float i = 0; i < 256; i += counter) // Pink (#f0f) to blue  (#00f)

            {
                mHueBarColors[index] = Color.rgb(255 - (int) i, 0, 255);
                index++;
            }
            for (float i = 0; i < 256; i += counter) // Blue (#00f) to light  blue (#0ff)

            {
                mHueBarColors[index] = Color.rgb(0, (int) i, 255);
                index++;
            }
            for (float i = 0; i < 256; i += counter) // Light blue (#0ff) to  green (#0f0)

            {
                mHueBarColors[index] = Color.rgb(0, 255, 255 - (int) i);
                index++;
            }
            for (float i = 0; i < 256; i += counter) // Green (#0f0) to yellow   (#ff0)

            {
                mHueBarColors[index] = Color.rgb((int) i, 255, 0);
                index++;
            }
            for (float i = 0; i < 256; i += counter) // Yellow (#ff0) to red  (#f00)

            {
                mHueBarColors[index] = Color.rgb(255, 255 - (int) i, 0);
                index++;
            }

            if (index < linehight) {
                for (float i = 0; index < linehight; i += counter) // Yellow (#ff0) to red  (#f00)

                {
                    mHueBarColors[index] = Color.rgb(255 - (int) i, (int) i, (int) i);
                    index++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < linehight; i++) {

            mPaint.setColor(mHueBarColors[i]);

            canvasline.drawLine(0, i, linewith, i, mPaint);
        }
    }

    private void adjustSqureColor(int color) {

        int squareWith = ivSquareColor.getWidth();
        int squareHight = ivSquareColor.getHeight();
        bitmapsqure = Bitmap.createBitmap(squareWith, squareHight, Bitmap.Config.ARGB_8888);
        bitmapsqure.eraseColor(Color.GREEN);
        ivSquareColor.setImageBitmap(bitmapsqure);
        Canvas canvasSquare = new Canvas(bitmapsqure);

        Paint mPaint = new Paint();
        mPaint.setStrokeWidth(1);

        updateMainColors(color, squareWith);

        for (int i = 0; i < squareWith; i++) {
            int[] colors = new int[2];
            colors[0] = mMainColors[i];
            colors[1] = Color.BLACK;
            Shader shader = new LinearGradient(0, 0, 0, squareHight, colors, null, Shader.TileMode.REPEAT);
            mPaint.setShader(shader);
            canvasSquare.drawLine(i, 0, i, squareHight, mPaint);
        }

        mPaint.setShader(null);
    }

    private void updateMainColors(int color, int with) {
        int mainColor = color;
        int index = 0;
        int[] topColors = new int[200000];
        mMainColors = new int[with];
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < with; x++) {
                if (y == 0) {
                    mMainColors[index] = Color.rgb(255 - (255 - Color.red(mainColor)) * x / with, 255 - (255 - Color.green(mainColor)) * x / with, 255 - (255 - Color.blue(mainColor)) * x / with);
                    topColors[x] = mMainColors[index];
                } else {
                    mMainColors[index] = Color.rgb((255 - y) * Color.red(topColors[x]) / 255, (255 - y) * Color.green(topColors[x]) / 255, (255 - y) * Color.blue(topColors[x]) / 255);
                }

                if (index < with - 1) {
                    index++;
                }
            }
        }
    }
}
