package net.iGap.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.iGap.G;
import net.iGap.R;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

public class ActivityCustomError extends ActivityEnhanced {

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
