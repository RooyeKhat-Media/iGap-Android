package io.meness.github.messageprogress;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;


public interface IMessageProgress {
    void withDrawable(@DrawableRes int res, boolean hideProgress);

    void withDrawable(Drawable drawable, boolean hideProgress);

    void withProgress(int i);

    void withIndeterminate(boolean b);

    float getProgress();

    void withProgressFinishedDrawable(@DrawableRes int d);

    void withProgressFinishedDrawable(Drawable d);

    void withProgressFinishedHide();

    void reset();

    void withOnMessageProgress(OnMessageProgressClick listener);

    void withOnProgress(OnProgress listener);

    void performProgress();
}
