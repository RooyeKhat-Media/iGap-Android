/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperFragment;
import net.iGap.libs.swipeback.SwipeBackFragment;
import net.iGap.libs.swipeback.SwipeBackLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static net.iGap.G.fragmentActivity;

public class BaseFragment extends SwipeBackFragment {

    protected Fragment currentFragment;
    public boolean isNeedResume = false;


    @Override
    public void onAttach(Context context) {
        //super.onAttach(context);
        super.onAttach(CalligraphyContextWrapper.wrap(context));
        G.fragmentActivity = (FragmentActivity) context;
        currentFragment = this;
        hideKeyboard();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        G.checkLanguage();
        checkFont();
        super.onCreate(savedInstanceState);

        getSwipeBackLayout().setEdgeOrientation(SwipeBackLayout.EDGE_LEFT);

        if (G.oneFragmentIsOpen != null) {
            G.oneFragmentIsOpen.justOne();
        }

        getSwipeBackLayout().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                if (ActivityMain.disableSwipe) {
                    getSwipeBackLayout().setEnableGesture(false);
                } else {
                    getSwipeBackLayout().setEnableGesture(true);
                }
                return false;
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();

        if (G.oneFragmentIsOpen != null) {
            G.oneFragmentIsOpen.justOne();
        }
        hideKeyboard();
        try {

            for (int i = G.fragmentManager.getFragments().size() - 1; i >= 0; i--) {

                Fragment f = G.fragmentManager.getFragments().get(i);

                if (f == null || f == currentFragment) {
                    continue;
                }

                BaseFragment bf = (BaseFragment) f;

                if (bf.isNeedResume) {
                    bf.onResume();
                }

                break;
            }
        } catch (Exception e) {

            // try need for activity register
        }
    }

    /**
     * check the selected language user and set the language if change it
     */
    private void checkFont() {

        if (G.typeface_IRANSansMobile == null) {
            G.typeface_IRANSansMobile = Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile.ttf");
        }

        if (G.typeface_IRANSansMobile_Bold == null) {
            G.typeface_IRANSansMobile_Bold = Typeface.createFromAsset(G.context.getAssets(), "fonts/IRANSansMobile_Bold.ttf");
        }

        if (G.typeface_Fontico == null) {
            G.typeface_Fontico = Typeface.createFromAsset(G.context.getAssets(), "fonts/iGap-Fontico.ttf");
        }

        if (G.typeface_neuropolitical == null) {
            G.typeface_neuropolitical = Typeface.createFromAsset(G.context.getAssets(), "fonts/neuropolitical.ttf");
        }
    }



    private void hideKeyboard() {
        View view = G.fragmentActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void popBackStackFragment() {
        fragmentActivity.getSupportFragmentManager().popBackStack();

        if (G.iTowPanModDesinLayout != null) {
            G.iTowPanModDesinLayout.onLayout(ActivityMain.chatLayoutMode.none);
        }
    }

    public void removeFromBaseFragment() {
        new HelperFragment(currentFragment).remove();
    }

    public void removeFromBaseFragment(Fragment fragment) {
        new HelperFragment(fragment).remove();
    }
}
