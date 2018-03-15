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
import android.graphics.Bitmap;
import android.util.AttributeSet;

import net.iGap.proto.ProtoGlobal;

import pl.droidsonroids.gif.GifImageView;

/**
 * An image view which retains the aspect ratio of the image (makes width match
 * height)
 */
public class ReserveSpaceGifImageView extends GifImageView {
    private int reservedWidth = 0;
    private int reservedHeight = 0;

    public ReserveSpaceGifImageView(Context context) {
        super(context);
    }

    public ReserveSpaceGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int[] reserveSpace(float width, float height, ProtoGlobal.Room.Type roomType) {
        final int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(getContext(), width, height, roomType);
        Bitmap bitmap = Bitmap.createBitmap(dimens[0], dimens[1], Bitmap.Config.ARGB_4444);
        setImageBitmap(bitmap);

        this.reservedWidth = dimens[0];
        this.reservedHeight = dimens[1];
        return dimens;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(reservedWidth, reservedHeight);
    }
}