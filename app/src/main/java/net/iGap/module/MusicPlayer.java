/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmResults;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChat;
import net.iGap.activities.ActivityMediaPlayer;
import net.iGap.helper.HelperCalander;
import net.iGap.interfaces.OnComplete;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class MusicPlayer {

    public static final int notificationId = 19;
    public static String repeatMode = RepeatMode.noRepeat.toString();
    public static boolean isShuffelOn = false;
    public static TextView txt_music_time;
    public static TextView txt_music_time_counter;
    public static String musicTime = "";
    public static String roomName;
    public static String musicPath;
    public static String musicName;
    public static String musicInfo = "";
    public static String musicInfoTitle = "";
    public static long roomId = 0;
    public static Bitmap mediaThumpnail = null;
    public static MediaPlayer mp;
    public static OnComplete onComplete = null;
    public static OnComplete onCompleteChat = null;
    public static boolean isShowMediaPlayer = false;
    public static int musicProgress = 0;
    private static LinearLayout layoutTripMusic;
    private static TextView btnPlayMusic;
    private static TextView btnCloseMusic;
    private static TextView txt_music_name;
    private static RemoteViews remoteViews;
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static boolean isPause = false;
    private static ArrayList<RealmRoomMessage> mediaList;
    private static int selectedMedia = 0;
    private static Timer mTimer, mTimeSecend;
    private static long time = 0;
    private static double amoungToupdate;
    public static String strTimer = "";
    public static String messageId = "";
    public static ArrayList<String> playedList = new ArrayList<>();

    public MusicPlayer(LinearLayout layoutTripMusic) {

        remoteViews = new RemoteViews(G.context.getPackageName(), R.layout.music_layout_notification);
        notificationManager = (NotificationManager) G.context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (this.layoutTripMusic != null) this.layoutTripMusic.setVisibility(View.GONE);

        initLayoutTripMusic(layoutTripMusic);

        getAtribuits();
    }

    public static void repeatClick() {

        String str = "";
        if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
            str = RepeatMode.repeatAll.toString();
        } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {
            str = RepeatMode.oneRpeat.toString();
        } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
            str = RepeatMode.noRepeat.toString();
        }

        repeatMode = str;

        SharedPreferences sharedPreferences = sharedPreferences = G.context.getSharedPreferences("MusicSetting", G.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RepeatMode", str);
        editor.apply();

        if (onComplete != null) {
            onComplete.complete(true, "RepeatMode", "");
        }
    }

    public static void shuffelClick() {

        isShuffelOn = !isShuffelOn;
        SharedPreferences sharedPreferences = sharedPreferences = G.context.getSharedPreferences("MusicSetting", G.context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Shuffel", isShuffelOn);
        editor.apply();
        if (onComplete != null) {
            onComplete.complete(true, "Shuffel", "");
        }
    }

    public static void initLayoutTripMusic(LinearLayout layout) {

        MusicPlayer.layoutTripMusic = layout;

        layout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(G.context, ActivityMediaPlayer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                G.context.startActivity(intent);
            }
        });

        txt_music_time = (TextView) layout.findViewById(R.id.mls_txt_music_time);
        txt_music_time_counter = (TextView) layout.findViewById(R.id.mls_txt_music_time_counter);
        txt_music_name = (TextView) layout.findViewById(R.id.mls_txt_music_name);

        btnPlayMusic = (TextView) layout.findViewById(R.id.mls_btn_play_music);
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                playAndPause();
            }
        });

        btnCloseMusic = (TextView) layout.findViewById(R.id.mls_btn_close);
        btnCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                closeLayoutMediaPlayer();
            }
        });

        if (MusicPlayer.mp != null) {
            layout.setVisibility(View.VISIBLE);
            txt_music_name.setText(MusicPlayer.musicName);
            txt_music_time.setText(MusicPlayer.milliSecondsToTimer((long) MusicPlayer.mp.getDuration()));

            if (MusicPlayer.mp.isPlaying()) {
                btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));
            } else {
                btnPlayMusic.setText(G.context.getString(R.string.md_play_arrow));
            }
        }

        if (HelperCalander.isLanguagePersian) {
            txt_music_time.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_music_time.getText().toString()));
        }
    }

    public static void playAndPause() {

        if (mp != null) {
            if (mp.isPlaying()) {
                pauseSound();
            } else {
                playSound();
            }
        } else {
            closeLayoutMediaPlayer();
        }
    }

    private static void pauseSound() {
        try {
            stopTimer();
            btnPlayMusic.setText(G.context.getString(R.string.md_play_arrow));

            if (!isShowMediaPlayer) {
                if (G.handler != null) {
                    G.handler.post(new Runnable() {
                        @Override public void run() {
                            try {
                                notificationManager.notify(notificationId, notification);
                            } catch (RuntimeException e) {
                            }
                        }
                    });
                }

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "play", "");
            }

            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
        } catch (Exception e) {
            e.printStackTrace();
        }

        isPause = true;
        mp.pause();
    }

    //**************************************************************************

    private static void playSound() {
        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
            if (!isShowMediaPlayer) {
                if (G.handler != null) {
                    G.handler.post(new Runnable() {
                        @Override public void run() {
                            try {
                                notificationManager.notify(notificationId, notification);
                            } catch (RuntimeException e) {
                            }
                        }
                    });
                }

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "pause", "");
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "pause", "");
            }
        } catch (Exception e) {
        }

        if (isPause) {
            if (mp != null) {
                mp.start();
                isPause = false;
                updateProgress();
            }
        } else {
            startPlayer(musicPath, roomName, roomId, false, MusicPlayer.messageId);
        }
    }

    public static void stopSound() {

        String zeroTime = "00";

        if (HelperCalander.isLanguagePersian) {
            zeroTime = HelperCalander.convertToUnicodeFarsiNumber(zeroTime);
        }

        try {
            btnPlayMusic.setText(G.context.getString(R.string.md_play_arrow));
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
            musicProgress = 0;

            if (!isShowMediaPlayer) {
                if (G.handler != null) {
                    G.handler.post(new Runnable() {
                        @Override public void run() {
                            try {
                                notificationManager.notify(notificationId, notification);
                            } catch (RuntimeException e) {
                            }
                        }
                    });
                }

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                    onCompleteChat.complete(true, "updateTime", zeroTime);
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "play", "");
                onComplete.complete(true, "updateTime", zeroTime);
            }
            stopTimer();
        } catch (Exception e) {
        }

        if (mp != null) {
            mp.stop();
        }
    }

    public static void nextMusic() {

        try {
            String beforMessageID = MusicPlayer.messageId;

            selectedMedia++;
            if (selectedMedia < mediaList.size()) {
                startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");
                if (onComplete != null) onComplete.complete(true, "update", "");
            } else {
                selectedMedia = 0;
                startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");

                if (onComplete != null) onComplete.complete(true, "update", "");
            }
            if (ActivityChat.onMusicListener != null) ActivityChat.onMusicListener.complete(true, MusicPlayer.messageId, beforMessageID);
        } catch (Exception e) {

            Log.e("dddd", "music player        nextMusic   " + e.toString());
        }
    }

    private static void nextRandomMusic() {

        try {

            String beforMessageID = MusicPlayer.messageId;
            Random r = new Random();
            selectedMedia = r.nextInt(mediaList.size() - 1);
            startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");

            if (onComplete != null) onComplete.complete(true, "update", "");

            if (ActivityChat.onMusicListener != null) ActivityChat.onMusicListener.complete(true, MusicPlayer.messageId, beforMessageID);
        } catch (Exception e) {

            Log.e("dddd", "music player        nextRandomMusic   " + e.toString());
        }
    }

    public static void previousMusic() {

        try {
            selectedMedia--;

            String beforMessageID = MusicPlayer.messageId;

            if (selectedMedia >= 0) {
                startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");

                if (onComplete != null) onComplete.complete(true, "update", "");
            } else {
                int index = mediaList.size() - 1;
                if (index >= 0) {
                    selectedMedia = index;
                    startPlayer(mediaList.get(selectedMedia).getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");

                    if (onComplete != null) onComplete.complete(true, "update", "");
                }
            }

            if (ActivityChat.onMusicListener != null) ActivityChat.onMusicListener.complete(true, MusicPlayer.messageId, beforMessageID);
        } catch (Exception e) {

            Log.e("dddd", "music player        previousMusic   " + e.toString());
        }
    }

    private static void closeLayoutMediaPlayer() {
        if (layoutTripMusic != null) layoutTripMusic.setVisibility(View.GONE);
        stopSound();
        if (mp != null) {
            mp.release();
            mp = null;
        }

        if (G.handler != null) {
            G.handler.post(new Runnable() {
                @Override public void run() {
                    try {
                        notificationManager.cancel(notificationId);
                    } catch (RuntimeException e) {
                    }
                }
            });
        }
    }

    public static void startPlayer(String musicPath, String roomName, long roomId, boolean updateList, String messageID) {

        MusicPlayer.messageId = messageID;
        MusicPlayer.musicPath = musicPath;
        MusicPlayer.roomName = roomName;
        mediaThumpnail = null;
        MusicPlayer.roomId = roomId;

        if (playedList.indexOf(messageID) == -1) {
            playedList.add(messageID);
        }

        if (layoutTripMusic.getVisibility() == View.GONE) {
            layoutTripMusic.setVisibility(View.VISIBLE);
        }

        if (mp != null) {
            mp.stop();
            mp.reset();

            try {
                mp.setDataSource(musicPath);
                mp.prepare();
                mp.start();

                btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));

                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
                if (!isShowMediaPlayer) {
                    if (G.handler != null) {
                        G.handler.post(new Runnable() {
                            @Override public void run() {
                                try {
                                    notificationManager.notify(notificationId, notification);
                                } catch (RuntimeException e) {
                                }
                            }
                        });
                    }

                    if (onCompleteChat != null) {
                        onCompleteChat.complete(true, "pause", "");
                    }
                } else if (onComplete != null) {
                    onComplete.complete(true, "pause", "");
                }

                musicTime = milliSecondsToTimer((long) mp.getDuration());
                txt_music_time.setText(musicTime);

                musicName = musicPath.substring(musicPath.lastIndexOf("/") + 1);
                txt_music_name.setText(musicName);

                updateNotification();
            } catch (Exception e) {
            }
        } else {

            mp = new MediaPlayer();
            try {
                mp.setDataSource(musicPath);
                mp.prepare();
                mp.start();

                musicTime = milliSecondsToTimer((long) mp.getDuration());
                txt_music_time.setText(musicTime);

                btnPlayMusic.setText(G.context.getString(R.string.md_pause_button));
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
                if (!isShowMediaPlayer) {
                    if (G.handler != null) {
                        G.handler.post(new Runnable() {
                            @Override public void run() {
                                try {
                                    notificationManager.notify(notificationId, notification);
                                } catch (RuntimeException e) {
                                }
                            }
                        });
                    }

                    if (onCompleteChat != null) {
                        onCompleteChat.complete(true, "pause", "");
                    }
                } else if (onComplete != null) {
                    onComplete.complete(true, "pause", "");
                }

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override public void onCompletion(MediaPlayer mp) {

                        if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
                            stopSound();
                        } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {

                            if (isShuffelOn) {
                                nextRandomMusic();
                            } else {

                                nextMusic();
                            }
                        } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
                            stopSound();
                            playAndPause();
                        }
                    }
                });

                musicName = musicPath.substring(musicPath.lastIndexOf("/") + 1);
                txt_music_name.setText(musicName);
                updateNotification();
            } catch (Exception e) {
            }
        }

        updateProgress();

        if (updateList) fillMediaList();

        if (HelperCalander.isLanguagePersian) {
            txt_music_time.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_music_time.getText().toString()));
        }
    }

    public static String milliSecondsToTimer(long milliseconds) {

        if (milliseconds == -1) return " ";

        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public static void updateNotification() {

        getMusicInfo();

        PendingIntent pi = PendingIntent.getActivity(G.context, 15, new Intent(G.context, ActivityMediaPlayer.class), PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setTextViewText(R.id.mln_txt_music_name, MusicPlayer.musicName);
        remoteViews.setTextViewText(R.id.mln_txt_music_outher, MusicPlayer.musicInfoTitle);

        if (mp != null) {
            if (mp.isPlaying()) {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
            } else {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
            }
        }

        Intent intentPrevious = new Intent(G.context, customButtonListener.class);
        intentPrevious.putExtra("mode", "previous");
        PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(G.context, 1, intentPrevious, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_Previous_music, pendingIntentPrevious);

        Intent intentPlayPause = new Intent(G.context, customButtonListener.class);
        intentPlayPause.putExtra("mode", "play");
        PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(G.context, 2, intentPlayPause, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_play_music, pendingIntentPlayPause);

        Intent intentforward = new Intent(G.context, customButtonListener.class);
        intentforward.putExtra("mode", "forward");
        PendingIntent pendingIntentforward = PendingIntent.getBroadcast(G.context, 3, intentforward, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_forward_music, pendingIntentforward);

        Intent intentClose = new Intent(G.context, customButtonListener.class);
        intentClose.putExtra("mode", "close");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(G.context, 4, intentClose, 0);
        remoteViews.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);

        notification = new NotificationCompat.Builder(G.context.getApplicationContext()).setTicker("music").setSmallIcon(R.mipmap.j_audio).setContentTitle(musicName)
            //  .setContentText(place)
            .setContent(remoteViews).setContentIntent(pi).setAutoCancel(false).build();
        if (G.handler != null) {
            G.handler.post(new Runnable() {
                @Override public void run() {
                    try {
                        if (!isShowMediaPlayer) {
                            notificationManager.notify(notificationId, notification);
                        }
                    } catch (RuntimeException e) {
                        Log.e("ddddd", "music player   update notification");
                    }
                }
            });
        }
    }

    public static void fillMediaList() {

        mediaList = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmRoomMessage> roomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
            equalTo(RealmRoomMessageFields.DELETED, false).findAll();

        if (!roomMessages.isEmpty()) {
            for (RealmRoomMessage realmRoomMessage : roomMessages) {

                if (realmRoomMessage.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.VOICE.toString()) || realmRoomMessage.getMessageType()
                    .toString()
                    .equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || realmRoomMessage.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString())) {
                    try {
                        if (new File(realmRoomMessage.getAttachment().getLocalFilePath()).exists()) mediaList.add(realmRoomMessage);
                    } catch (Exception e) {
                        Log.e("dddd", "music player   fillMediaList " + e.toString());
                    }
                }
            }
        }

        for (int i = mediaList.size() - 1; i >= 0; i--) {

            RealmRoomMessage rm = mediaList.get(i);

            if (rm.getAttachment() != null) {
                String tmpPath = rm.getAttachment().getLocalFilePath();
                if (tmpPath != null) {
                    if (tmpPath.equals(musicPath)) {
                        selectedMedia = i;
                        break;
                    }
                }
            }
        }

        realm.close();
    }

    private static void updateProgress() {

        stopTimer();

        double duration = MusicPlayer.mp.getDuration();
        amoungToupdate = duration / 100;
        time = MusicPlayer.mp.getCurrentPosition();
        musicProgress = ((int) (time / amoungToupdate));

        mTimeSecend = new Timer();

        mTimeSecend.schedule(new TimerTask() {
            @Override public void run() {

                updatePlayerTime();
                time += 1000;
            }
        }, 0, 1000);

        if (amoungToupdate >= 1) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override public void run() {

                    if (musicProgress < 100) {
                        musicProgress++;
                    } else {
                        stopTimer();
                    }
                }
            }, 0, (int) amoungToupdate);
        }
    }

    private static void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimeSecend != null) {
            mTimeSecend.cancel();
            mTimeSecend = null;
        }
    }

    private static void updatePlayerTime() {

        strTimer = MusicPlayer.milliSecondsToTimer(time);

        if (HelperCalander.isLanguagePersian) {
            strTimer = HelperCalander.convertToUnicodeFarsiNumber(strTimer);
        }

        if (txt_music_time_counter != null) {

            txt_music_time_counter.post(new Runnable() {
                @Override public void run() {
                    txt_music_time_counter.setText(strTimer + "/");
                }
            });
        }

        if (isShowMediaPlayer) {
            if (onComplete != null) {
                onComplete.complete(true, "updateTime", strTimer);
            }
        } else {
            if (onCompleteChat != null) {
                onCompleteChat.complete(true, "updateTime", strTimer);
            }
        }
    }

    public static void setMusicProgress(int percent) {
        try {
            musicProgress = percent;
            if (MusicPlayer.mp != null) {
                MusicPlayer.mp.seekTo((int) (musicProgress * amoungToupdate));
                time = MusicPlayer.mp.getCurrentPosition();
                updatePlayerTime();
            }
        } catch (IllegalStateException e) {
        }
    }

    private static void getMusicInfo() {

        musicInfo = "";
        musicInfoTitle = "";

        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();

        Uri uri = null;

        if (MusicPlayer.musicPath != null) uri = (Uri) Uri.fromFile(new File(MusicPlayer.musicPath));

        if (uri != null) {

            mediaMetadataRetriever.setDataSource(G.context, uri);

            String title = (String) mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            if (title != null) {
                musicInfo += title + "       ";
                musicInfoTitle = title;
            }

            String albumName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if (albumName != null) {
                musicInfo += albumName + "       ";
                musicInfoTitle = albumName;
            }

            String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            if (artist != null) {
                musicInfo += artist + "       ";
                musicInfoTitle = artist;
            }

            if (musicInfoTitle.trim().length() == 0) {
                musicInfoTitle = G.context.getString(R.string.unknown_artist);
            }

            try {
                mediaMetadataRetriever.setDataSource(G.context, uri);
                byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                if (data != null) {
                    mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                    int size = (int) G.context.getResources().getDimension(R.dimen.dp48);
                    remoteViews.setImageViewBitmap(R.id.mln_img_picture_music, Bitmap.createScaledBitmap(mediaThumpnail, size, size, false));
                } else {
                    remoteViews.setImageViewResource(R.id.mln_img_picture_music, R.mipmap.music_icon_green);
                }
            } catch (Exception e) {
            }
        }
    }

    private void getAtribuits() {
        SharedPreferences sharedPreferences = sharedPreferences = G.context.getSharedPreferences("MusicSetting", G.context.MODE_PRIVATE);
        repeatMode = sharedPreferences.getString("RepeatMode", RepeatMode.noRepeat.toString());
        isShuffelOn = sharedPreferences.getBoolean("Shuffel", false);
    }

    public enum RepeatMode {
        noRepeat, oneRpeat, repeatAll;
    }

    public static class customButtonListener extends BroadcastReceiver {

        @Override public void onReceive(Context context, Intent intent) {

            String str = intent.getExtras().getString("mode");

            if (str.equals("previous")) {
                previousMusic();
            } else if (str.equals("play")) {
                playAndPause();
            } else if (str.equals("forward")) {
                nextMusic();
            } else if (str.equals("close")) {
                closeLayoutMediaPlayer();
            }
        }
    }
}
