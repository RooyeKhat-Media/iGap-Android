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
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import net.iGap.G;
import net.iGap.helper.Emojione;

public class MaterialDesignTextView extends AppCompatTextView {

    public MaterialDesignTextView(Context context) {
        super(context);
        init(context);
    }

    public MaterialDesignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialDesignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        //        setTypeface(FontCache.get("fonts/Flaticon.ttf", context));
        setTypeface(G.typeface_Fontico);
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        try {
            super.setText(Emojione.shortnameToUnicode(text.toString(), false), type);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
