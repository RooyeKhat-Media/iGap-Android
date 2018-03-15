package net.iGap.libs.ripplesoundplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.iGap.libs.ripplesoundplayer.renderer.Renderer;
import net.iGap.module.MusicPlayer;

public class RippleVisualizerView extends View {
    private static final String TAG = "RippleStatusBarView";
    private byte[] data;
    private Visualizer audioVisualizer;
    private Renderer currentRenderer;

    public RippleVisualizerView(Context context) {
        super(context);
    }

    public RippleVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RippleVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCurrentRenderer(Renderer currentRenderer) {
        this.currentRenderer = currentRenderer;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (data == null) {
            return;
        }

        currentRenderer.render(canvas, data, getWidth(), getHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getPaddingLeft() + getPaddingRight();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 0);

        int minh = getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(minh, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    public void setRippleColor(@ColorInt int color) {
        currentRenderer.changeColor(color);
        invalidate();
    }

    public void setMediaPlayer(MediaPlayer player) {

        if (audioVisualizer != null) {
            audioVisualizer.setEnabled(false);
        }

        audioVisualizer = new Visualizer(player.getAudioSessionId());
        audioVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        audioVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                if (!currentRenderer.isFFTDataRequired()) {
                    updateVisualizer(bytes);
                }
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                if (currentRenderer.isFFTDataRequired()) {
                    updateVisualizer(bytes);
                }
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, true);

        if (MusicPlayer.mp != null && MusicPlayer.mp.isPlaying()) {
            audioVisualizer.setEnabled(true);
        }
    }

    private void updateVisualizer(byte[] bytes) {
        this.data = bytes;
        invalidate();
    }

    public void pauseVisualizer() {
        if (audioVisualizer != null) {
            audioVisualizer.setEnabled(false);
        }
    }

    public void startVisualizer() {
        if (audioVisualizer != null) {
            audioVisualizer.setEnabled(true);
        }
    }

    public void setAmplitudePercentage(double ampValue) {
        currentRenderer.setAmpValue(ampValue);
        invalidate();
    }
}
