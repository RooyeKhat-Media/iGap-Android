package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.Context;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentBio;
import net.iGap.helper.HelperError;
import net.iGap.request.RequestUserProfileSetBio;

public class FragmentBioViewModel {

    public ObservableField<String> callBackEdtBio = new ObservableField<>();
    public ObservableField<String> callBackTxtCountBio = new ObservableField<>("70");
    private int mCount = 70;
    private boolean isEndLine = true;
    private String specialRequests;
    private String bio = "";

    public FragmentBioViewModel(Bundle arguments) {

        getInfo(arguments);

    }

    public void onClickRippleOk(View v) {

        new RequestUserProfileSetBio().setBio(callBackEdtBio.get());
        closeKeyboard(v);
        if (FragmentBio.onBackFragment != null) FragmentBio.onBackFragment.onBack();

    }

    public void onClickRippleBack(View v) {
        closeKeyboard(v);
        if (FragmentBio.onBackFragment != null) FragmentBio.onBackFragment.onBack();
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        callBackTxtCountBio.set("" + (mCount - s.toString().length()));
    }

    public void afterTextChanged(Editable s) {


        if (s.toString().length() > 70) {
            callBackEdtBio.set(specialRequests);
            //edtMessageGps.setSelection(lastSpecialRequestsCursorPosition);

            if (isEndLine) {
                isEndLine = false;
                error(G.fragmentActivity.getResources().getString(R.string.exceed_4_line));
            }
        } else {
            isEndLine = true;
            specialRequests = s.toString();
        }
    }

    private void getInfo(Bundle arguments) {
        if (arguments != null) bio = arguments.getString("BIO");
        callBackEdtBio.set(bio);
        callBackTxtCountBio.set("" + (mCount - bio.length()));
    }

    private void closeKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) G.fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }

    private void error(String error) {
        try {

            HelperError.showSnackMessage(error, true);

        } catch (IllegalStateException e) {
            e.getStackTrace();
        }
    }
}
