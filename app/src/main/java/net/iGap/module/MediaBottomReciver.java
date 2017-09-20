package net.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MediaBottomReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (!MusicPlayer.isMusicPlyerEnable) {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            if (intent.getExtras() == null) {
                return;
            }
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent == null) {
                return;
            }
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN) return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    MusicPlayer.playAndPause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    if (MusicPlayer.isPause) {
                        MusicPlayer.playAndPause();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    if (!MusicPlayer.isPause) {
                        MusicPlayer.playAndPause();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:

                    MusicPlayer.stopSound();

                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    MusicPlayer.nextMusic();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    MusicPlayer.previousMusic();
                    break;
            }
        }
    }
}
