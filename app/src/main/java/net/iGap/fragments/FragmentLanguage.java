package net.iGap.fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Locale;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.MusicPlayer;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.G.onRefreshActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLanguage extends BaseFragment {

    private SharedPreferences sharedPreferences;

    public FragmentLanguage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_language, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        ViewGroup root = (ViewGroup) view.findViewById(R.id.rootFragmentLanguage);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stns_ripple_back);
        rippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });

        TextView txtFa = (TextView) view.findViewById(R.id.txtLanguageFarsi);
        TextView iconFa = (TextView) view.findViewById(R.id.st_icon_fatsi);
        TextView txtEn = (TextView) view.findViewById(R.id.txtLanguageEn);
        TextView iconEn = (TextView) view.findViewById(R.id.st_icon_english);
        TextView iconAr = (TextView) view.findViewById(R.id.st_icon_ar);


        String textLanguage = sharedPreferences.getString(SHP_SETTING.KEY_LANGUAGE, Locale.getDefault().getDisplayLanguage());

        if (textLanguage.equals("English")) {
            iconEn.setVisibility(View.VISIBLE);
        } else if (textLanguage.equals("فارسی")) {
            iconFa.setVisibility(View.VISIBLE);
        } else if (textLanguage.equals("العربی")) {
            iconAr.setVisibility(View.VISIBLE);
        } else if (textLanguage.equals("Deutsch")) {

        }


        ViewGroup vgFa = (ViewGroup) view.findViewById(R.id.st_layout_fa);
        ViewGroup vgEn = (ViewGroup) view.findViewById(R.id.st_layout_english);
        ViewGroup vgAr = (ViewGroup) view.findViewById(R.id.st_layout_arabi);

        vgEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        G.isRestartActivity = true;
                        onRefreshActivity.refresh("en");
                    }

                    G.selectedLanguage = "en";
                }

                if (MusicPlayer.updateName != null) {
                    MusicPlayer.updateName.rename();
                }

                removeFromBaseFragment(FragmentLanguage.this);

            }
        });

        vgFa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        G.isRestartActivity = true;
                        onRefreshActivity.refresh("fa");
                    }
                }

                if (MusicPlayer.updateName != null) {
                    MusicPlayer.updateName.rename();
                }

                removeFromBaseFragment(FragmentLanguage.this);
            }
        });

        vgAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        G.isRestartActivity = true;
                        onRefreshActivity.refresh("ar");
                    }
                }

                if (MusicPlayer.updateName != null) {
                    MusicPlayer.updateName.rename();
                }

                removeFromBaseFragment(FragmentLanguage.this);
            }
        });

    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        G.fragmentActivity.getBaseContext().getResources().updateConfiguration(config, G.fragmentActivity.getBaseContext().getResources().getDisplayMetrics());
    }
}
