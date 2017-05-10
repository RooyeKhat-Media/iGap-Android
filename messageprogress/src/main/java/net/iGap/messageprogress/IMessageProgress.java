package net.iGap.messageprogress;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

/* * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved. */

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
