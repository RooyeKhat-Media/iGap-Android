package io.github.meness.audioplayerview.listeners;

import android.media.MediaPlayer;


public interface IAnotherPlayOrPause {
    void onAnotherPlay(MediaPlayer player);

    void onAnotherPause(MediaPlayer player);
}
