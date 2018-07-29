package org.paygear.wallet.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;

import org.paygear.wallet.WalletActivity;

import java.util.Locale;
import java.util.Random;

import ir.radsense.raadcore.Raad;

/**
 * Created by Software1 on 9/19/2017.
 */

public class Utils {

    public static boolean isValidJson(String string) {
        try {
            Gson gson = new Gson();
            gson.fromJson(string, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*public static void changeLocale(Context context, String lan) {
        SettingHelper.putString(context, SettingHelper.APP_LANGUAGE, lan);
        setLocale(context, lan);
    }

    public static void setLocale(Context context) {
        String lan = SettingHelper.getString(context, SettingHelper.APP_LANGUAGE, "fa");
        setLocale(context, lan);
    }*/

    public static void setLocale(Context context, String lan) {

        Raad.language = lan;
        if (lan.toLowerCase().equals("fa") || lan.toLowerCase().equals("ar"))
            Raad.isFA = true;
        else
            Raad.isFA = false;


        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(new Locale(lan.toLowerCase())); // API 17+ only.}
        } else {
            conf.locale = new Locale(lan.toLowerCase());
        }
        res.updateConfiguration(conf, dm);

    }

    public static void setInstart(Context context, String lan) {
        Raad.language = lan;
        Raad.isFA = lan.toLowerCase().equals("fa");
    }

    public static Context updateResources(Context baseContext) {
        String selectedLanguage = WalletActivity.selectedLanguage;
        if (selectedLanguage == null) {
            selectedLanguage = "en";
        }

        Locale locale = new Locale(selectedLanguage);
        Locale.setDefault(locale);

        Resources res = baseContext.getResources();
        Configuration configuration = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            baseContext = baseContext.createConfigurationContext(configuration);
        } else {
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

//        G.context = baseContext;

        return baseContext;
    }

    public static void setShadow(View view, Drawable sd) {
        RoundRectShape rss = new RoundRectShape(new float[]{12f, 12f, 12f,
                12f, 12f, 12f, 12f, 12f}, null, null);
        ShapeDrawable sds = new ShapeDrawable(rss);
        sds.setShaderFactory(new ShapeDrawable.ShaderFactory() {

            @Override
            public Shader resize(int width, int height) {
                LinearGradient lg = new LinearGradient(0, 0, 0, height,
                        new int[]{Color.parseColor("#e5e5e5"),
                                Color.parseColor("#e5e5e5"),
                                Color.parseColor("#e5e5e5"),
                                Color.parseColor("#e5e5e5")}, new float[]{0,
                        0.50f, 0.50f, 1}, Shader.TileMode.REPEAT);
                return lg;
            }
        });

        LayerDrawable ld = new LayerDrawable(new Drawable[]{sds, sd});
        ld.setLayerInset(0, 5, 5, 0, 0); // inset the shadow so it doesn't start right at the left/top
        ld.setLayerInset(1, 0, 0, 5, 5); // inset the top drawable so we can leave a bit of space for the shadow to use

        view.setBackgroundDrawable(ld);
    }

    public static int getAColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }

    public static String formatCardNumber(String number) {
        int c = number.length();
        int sec = c / 4;
        int rem = c % 4;
        //if (sec > 0 && rem == 0)
        //sec -= 1;
        String formatted = "";
        for (int i = 0; i < sec; i++) {
            formatted += number.subSequence((i * 4), (i * 4) + 4);
            if (i < 3)
                formatted += " - ";
        }

        if (rem > 0) {
            formatted += number.subSequence((sec * 4), (sec * 4) + rem);
        }

        if (formatted.endsWith(" - "))
            formatted = formatted.substring(0, formatted.length() - 3);
        return formatted;
    }

    public static void showCustomTab(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
            Bundle extras = new Bundle();
            extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null);

            String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
            //intent.putExtra(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, webPageColor);

            intent.putExtras(extras);
            intent.setPackage("com.android.chrome");

            try {
                context.startActivity(intent);
                return;
            } catch (ActivityNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            }
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }


}
