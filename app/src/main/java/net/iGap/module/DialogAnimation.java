package net.iGap.module;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.app.Dialog;
import android.view.Gravity;
import android.view.WindowManager;
import net.iGap.R;

/**
 * set animation for open Dialog from top or bottom
 */

public class DialogAnimation {

    public static void animationUp(Dialog dialog) {

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.TOP | Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationUp;
    }

    public static void animationDown(Dialog dialog) {

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationDown;
    }
}
