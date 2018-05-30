package net.iGap.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class CustomButtonListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String str = intent.getExtras().getString("mode");

        if (str == null || str.length() == 0 || MusicPlayer.mp == null) {
            str = "close";
        }

        switch (str) {
            case "previous":
                MusicPlayer.previousMusic();
                break;
            case "play":
                MusicPlayer.playAndPause();
                break;
            case "forward":
                MusicPlayer.nextMusic();
                break;
            case "close":
                MusicPlayer.closeLayoutMediaPlayer();
                break;
        }
    }
}
