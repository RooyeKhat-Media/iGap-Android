package org.paygear.wallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import org.paygear.wallet.fragment.CardsFragment;
import org.paygear.wallet.fragment.CashOutFragment;
import org.paygear.wallet.fragment.CashOutRequestFragment;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;

import ir.radsense.raadcore.OnWebResponseListener;
import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletActivity extends NavigationBarActivity {

    public static String token;
    public static Intent intent;
    public static String primaryColor = "#f69228";
    public static String darkPrimaryColor = "#37a9a1";
    public static String accentColor = "#00B0Bf";
    public static String progressColorWhite = "#ffffff";
    public static String progressColor = "#00B0Bf";
    public static String lineBorder = "#00B0Bf";
    public static String backgroundTheme = "#00B0Bf";
    public static String backgroundTheme_2 = "#00B0Bf";
    public static String textTitleTheme = "#00B0Bf";
    public static String textSubTheme = "#00B0Bf";
    public static String PROGRESSBAR = "PROGRESSBAR";
    public static String LINE_BORDER = "LINE_BORDER";
    public static String BACKGROUND = "BACKGROUND";
    public static String BACKGROUND_2 = "BACKGROUND_2";
    public static String TEXT_TITLE = "TEXT_TITLE";
    public static String TEXT_SUB_TITLE = "TEXT_SUB_TITLE";
    public static String API_KEY = "API_KEY";
    public static String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static String IS_DARK_THEME = "IS_DARK_THEME";
    public static String LANGUAGE = "LANGUAGE";
    public static boolean isDarkTheme = false;
    public static String selectedLanguage = "en";
    public static boolean isResetPassword = false;
    public static String SH_SETTING = "SH_SETTING";
    public static String RESET_PASSWORD = "RESET_PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout navBarView = findViewById(R.id.nav_bar);
        navBarView.setVisibility(View.GONE);


   //     Utils.setLocale(this, "fa");

        WebBase.apiKey = Web.API_KEY;
        WebBase.isDebug = true;
        WebBase.onResponseListener = new OnWebResponseListener() {
            @Override
            public boolean onResponse(Context context, Response response) {
                /*if (context instanceof NavigationBarActivity) {
                    if (response.code() == 401 || response.code() == 403) {
                        Utils.signOutAndGoLogin((NavigationBarActivity) context);
                        return false;
                    }
                }*/
                return true;
            }
        };

        Raad.init(getApplicationContext());
        intent = getIntent();
        String phone = intent.getStringExtra("Mobile");
        String language = intent.getStringExtra("Language");
        boolean isP2P = intent.getBooleanExtra("IsP2P", false);
        Payment payment = (Payment) intent.getSerializableExtra("Payment");
        primaryColor = intent.getStringExtra("PrimaryColor");
        darkPrimaryColor = intent.getStringExtra("DarkPrimaryColor");
        selectedLanguage = intent.getStringExtra(LANGUAGE);
        isDarkTheme = intent.getBooleanExtra(IS_DARK_THEME, false);
        progressColor = intent.getStringExtra(PROGRESSBAR);
        lineBorder = intent.getStringExtra(LINE_BORDER);
        backgroundTheme = intent.getStringExtra(BACKGROUND);
        backgroundTheme_2 = intent.getStringExtra(BACKGROUND_2);
        textTitleTheme = intent.getStringExtra(TEXT_TITLE);
        textSubTheme = intent.getStringExtra(TEXT_SUB_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(WalletActivity.darkPrimaryColor));
        }
        if (selectedLanguage != null) {
            Utils.setLocale(this, selectedLanguage);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (isP2P && payment != null) {
            ft.add(R.id.content_container, CardsFragment.newInstance(payment));

        } else {
            ft.add(R.id.content_container, new CardsFragment());
        }
        ft.commit();
    }

    @Override
    protected boolean isNavBarShowingOnSwitchFragment(String fragmentName) {
        return false;
    }
}
