package net.iGap.libs.ripplesoundplayer.util;

import android.graphics.Paint;
import android.support.annotation.ColorInt;

import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;

public class PaintUtil {
    public static Paint getLinePaint(@ColorInt int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        return paint;
    }

    public static Paint getBarGraphPaint(@ColorInt int color) {
        Paint paint = new Paint();
        paint.setStrokeWidth(ViewMaker.i_Dp(R.dimen.dp6));
        paint.setAntiAlias(true);
        paint.setColor(color);
        return paint;
    }
}
