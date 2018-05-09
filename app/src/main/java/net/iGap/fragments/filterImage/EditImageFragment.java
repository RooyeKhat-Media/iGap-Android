/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */


package net.iGap.fragments.filterImage;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import net.iGap.R;


public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {
    private EditImageFragmentListener listener;
    private SeekBar seekBarBrightness;
    private SeekBar seekBarContrast;
    private SeekBar seekBarSaturation;

    public void setListener(EditImageFragmentListener listener) {
        this.listener = listener;
    }

    public EditImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBarBrightness = view.findViewById(R.id.seekbar_brightness);
        seekBarContrast = view.findViewById(R.id.seekbar_contrast);
        seekBarSaturation = view.findViewById(R.id.seekbar_saturation);

        seekBarBrightness.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));
        seekBarContrast.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));
        seekBarSaturation.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN));

        // keeping brightness value b/w -100 / +100
        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);

        // keeping contrast value b/w 1.0 - 3.0
        seekBarContrast.setMax(20);
        seekBarContrast.setProgress(0);

        // keeping saturation value b/w 0.0 - 3.0
        seekBarSaturation.setMax(30);
        seekBarSaturation.setProgress(10);

        seekBarBrightness.setOnSeekBarChangeListener(this);
        seekBarContrast.setOnSeekBarChangeListener(this);
        seekBarSaturation.setOnSeekBarChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_filter_image, container, false);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (listener != null) {

            if (seekBar.getId() == R.id.seekbar_brightness) {
                // brightness values are b/w -100 to +100
                listener.onBrightnessChanged(progress - 100);
            }

            if (seekBar.getId() == R.id.seekbar_contrast) {
                // converting int value to float
                // contrast values are b/w 1.0f - 3.0f
                // progress = progress > 10 ? progress : 10;
                progress += 10;
                float floatVal = .10f * progress;
                listener.onContrastChanged(floatVal);
            }

            if (seekBar.getId() == R.id.seekbar_saturation) {
                // converting int value to float
                // saturation values are b/w 0.0f - 3.0f
                float floatVal = .10f * progress;
                listener.onSaturationChanged(floatVal);
            }
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditStarted();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null)
            listener.onEditCompleted();
    }

    public void resetControls() {
        seekBarBrightness.setProgress(100);
        seekBarContrast.setProgress(0);
        seekBarSaturation.setProgress(10);
    }

    public interface EditImageFragmentListener {
        void onBrightnessChanged(int brightness);

        void onSaturationChanged(float saturation);

        void onContrastChanged(float contrast);

        void onEditStarted();

        void onEditCompleted();
    }
}
