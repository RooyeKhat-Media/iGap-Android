package net.iGap.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

import static net.iGap.G.context;

public class ActivityCustomError extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (G.isDarkTheme) {
            this.setTheme(R.style.Material_blackCustom);
        } else {
            this.setTheme(R.style.Material_lightCustom);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_error);

        //TextView errorDetailsText = (TextView) findViewById(R.id.error_details);
        //errorDetailsText.setText(CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));

        Button restartButton = (Button) findViewById(R.id.restart_button);
        restartButton.setBackgroundColor(Color.parseColor(G.appBarColor));

        final Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final CaocConfig config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomActivityOnCrash.restartApplicationWithIntent(ActivityCustomError.this, i, config);
            }
        });
    }
}
