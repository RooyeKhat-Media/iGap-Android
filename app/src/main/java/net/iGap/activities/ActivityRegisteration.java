package net.iGap.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentIntroduce;
import net.iGap.fragments.FragmentRegistrationNickname;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPermision;
import net.iGap.interfaces.OnGetPermission;
import net.iGap.module.StartupActions;

import java.io.IOException;

public class ActivityRegisteration extends ActivityEnhanced {

    public static final String showProfile = "showProfile";

    FrameLayout layoutRoot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        isOnGetPermission = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        try {
            HelperPermision.getStoragePermision(this, new OnGetPermission() {
                @Override
                public void Allow() throws IOException {
                    startApp();
                }

                @Override
                public void deny() {
                    //finish();
                    startApp();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startApp() {
        StartupActions.makeFolder();

        boolean showPro = false;
        try {
            if (getParent() != null && getIntent().getExtras() != null) {
                showPro = getIntent().getExtras().getBoolean(showProfile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            G.isLandscape = true;
        } else {
            G.isLandscape = false;
        }

        layoutRoot = (FrameLayout) findViewById(R.id.ar_layout_root);

        if (showPro) {
            loadFragmentProfile();
        } else {
            loadFragmentIntroduce();
        }
    }

    private void loadFragmentProfile() {
        FragmentRegistrationNickname fragment = new FragmentRegistrationNickname();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.ar_layout_root, fragment)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                .commit();
    }

    private void setFraymeSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int size = Math.min(width, height) - 50;
        ViewGroup.LayoutParams lp = layoutRoot.getLayoutParams();
        lp.width = size;
        lp.height = size;
    }

    private void loadFragmentIntroduce() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!ActivityRegisteration.this.isFinishing()) {

                    try {
                        FragmentIntroduce fragment = new FragmentIntroduce();
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.ar_layout_root, fragment)
                                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                                .commit();
                    } catch (Exception e) {
                        HelperLog.setErrorLog("activity registeration     loadFragmentIntroduce   " + e.toString());
                    }
                }
            }
        }, 1000);
    }
}
