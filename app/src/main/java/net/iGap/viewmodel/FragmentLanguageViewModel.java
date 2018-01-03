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

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;

import net.iGap.G;
import net.iGap.fragments.FragmentLanguage;
import net.iGap.helper.HelperCalander;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.onRefreshActivity;

public class FragmentLanguageViewModel {

    private SharedPreferences sharedPreferences;
    private FragmentLanguage fragmentLanguage;
    private String textLanguage;


    public FragmentLanguageViewModel(FragmentLanguage fragmentLanguage) {
        this.fragmentLanguage = fragmentLanguage;
        getInfo();
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onClickEnglish(View v) {
        if (!G.selectedLanguage.equals("en")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "English");
            editor.apply();
            setLocale("en");
            HelperCalander.isPersianUnicode = false;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = false;

            if (onRefreshActivity != null) {
                FragmentLanguage.languageChanged = true;
                G.isRestartActivity = true;
                onRefreshActivity.refresh("en");
            }

            G.selectedLanguage = "en";
        }

        if (MusicPlayer.updateName != null) {
            MusicPlayer.updateName.rename();
        }

        fragmentLanguage.removeFromBaseFragment(fragmentLanguage);
    }

    public void onClickFarsi(View v) {
        if (!G.selectedLanguage.equals("fa")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "فارسی");
            editor.apply();
            G.selectedLanguage = "fa";
            setLocale("fa");
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = true;
            HelperCalander.isLanguageArabic = false;
            G.isAppRtl = true;
            if (onRefreshActivity != null) {
                FragmentLanguage.languageChanged = true;
                G.isRestartActivity = true;
                onRefreshActivity.refresh("fa");
            }
        }

        if (MusicPlayer.updateName != null) {
            MusicPlayer.updateName.rename();
        }

        fragmentLanguage.removeFromBaseFragment(fragmentLanguage);
    }

    public void onClickArabi(View v) {

        if (!G.selectedLanguage.equals("ar")) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(SHP_SETTING.KEY_LANGUAGE, "العربی");
            editor.apply();
            G.selectedLanguage = "ar";
            setLocale("ar");
            HelperCalander.isPersianUnicode = true;
            HelperCalander.isLanguagePersian = false;
            HelperCalander.isLanguageArabic = true;
            G.isAppRtl = true;

            if (onRefreshActivity != null) {
                FragmentLanguage.languageChanged = true;
                G.isRestartActivity = true;
                onRefreshActivity.refresh("ar");
            }
        }

        if (MusicPlayer.updateName != null) {
            MusicPlayer.updateName.rename();
        }

        fragmentLanguage.removeFromBaseFragment(fragmentLanguage);
    }

    public boolean isEnglish() {
        return textLanguage.equals("English") ? true : false;
    }

    public boolean isFarsi() {
        return textLanguage.equals("فارسی") ? true : false;
    }

    public boolean isArabi() {
        return textLanguage.equals("العربی") ? true : false;
    }


    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private void getInfo() {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        G.fragmentActivity.getBaseContext().getResources().updateConfiguration(config, G.fragmentActivity.getBaseContext().getResources().getDisplayMetrics());
    }


}
