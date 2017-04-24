package io.meness.github.messageprogress;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;


public final class AndroidUtils {
    public static Drawable getDrawable(Context context, @DrawableRes int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(res, context.getTheme());
        } else {
            return ContextCompat.getDrawable(context, res);
        }
    }
}
