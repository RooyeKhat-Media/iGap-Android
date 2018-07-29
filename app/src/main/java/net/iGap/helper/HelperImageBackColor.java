/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import net.iGap.G;

public class HelperImageBackColor {

    /**
     * this methode get a name and return a random color by this name
     */
    public static String getColor(String name) {

        int sum = 0;
        int color = 0;
        String str = "#dd3333";

        if (name == null) return str;

        for (int i = 0; i < name.length(); i++) {

            sum += name.codePointAt(i);
        }

        if (sum > 0) {
            color = sum % 12;
        }

        if (color == 0) {
            str = "#dd3333";
        } else if (color == 1) {
            str = "#df2592";
        } else if (color == 2) {
            str = "#df2551";
        } else if (color == 3) {
            str = "#9448e1";
        } else if (color == 4) {
            str = "#7200ff";
        } else if (color == 5) {
            str = "#5c9dff";
        } else if (color == 6) {
            str = "#0ca3b9";
        } else if (color == 7) {
            str = "#0cb99f";
        } else if (color == 8) {
            str = "#4fb559";
        } else if (color == 9) {
            str = "#b9d242";
        } else if (color == 10) {
            str = "#ff8a00";
        } else if (color == 11) str = "#f8b500";

        return str;
    }

    /**
     * get letter and color and size finally draw bitmap with this info
     *
     * @param with  size of bitmap
     * @param text  letter for drawing
     * @param color color of bitmap
     * @return bitmap that painted alphabet
     */

    public static Bitmap drawAlphabetOnPicture(int with, String text, String color) {

        String alphabetName = text;
        String mColor = "#f4f4f4";

        if (color == null || color.equals("")) {
            color = "#7f7f7f7f";
        }
        if (text == null || text.equals("")) {
            alphabetName = " ";
        }

        Bitmap bitmap = Bitmap.createBitmap(with, with, Bitmap.Config.ARGB_8888);
        /**
         * set gradient for circle image
         * set gradient color on LinearGradient
         */

        Paint p = new Paint();
        p.setDither(true);
        p.setAntiAlias(true);
        p.setColor(Color.parseColor(color));

        try {
            Canvas c = new Canvas(bitmap);
            c.drawCircle(with / 2, with / 2, with / 2, p);

            int fontSize = with / 3;
            Canvas cs = new Canvas(bitmap);

            Paint textPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            textPaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.SOLID));
            textPaint.setColor(Color.parseColor(mColor));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setTextSize(fontSize);
            textPaint.setTypeface(G.typeface_IRANSansMobile_Bold);
            textPaint.setStyle(Paint.Style.FILL);
            cs.drawText(alphabetName, with / 2, with / 2 + fontSize / 4, textPaint);
        } catch (NullPointerException e) {
            HelperLog.setErrorLog("HelperImageBackColor  drawAlphabetOnPicture  " + e);
        }

        return bitmap;
    }

    /**
     * this method get a text and return first character of text and first character of last word of
     * text
     */
    public static String getFirstAlphabetName(String name) {
        name = name.trim();
        String[] splitted = name.split("\\s+");

        int size = splitted.length;
        String charFirst, lowName, upName;

        if (!splitted[0].equals("") && splitted[0] != null && !splitted[0].isEmpty()) {
            if (size == 1) {
                charFirst = splitted[0].trim();
                lowName = charFirst.substring(0, 1);
            } else {
                charFirst = splitted[0].trim();
                String charLast = splitted[size - 1].trim();
                lowName = charFirst.substring(0, 1) + charLast.substring(0, 1);
            }
            upName = lowName.toUpperCase();
        } else {
            upName = " ";
        }

        return upName;
    }
}
