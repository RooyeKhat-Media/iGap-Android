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
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentMediaPlayer;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperLog;
import net.iGap.interfaces.OnAudioFocusChangeListener;
import net.iGap.interfaces.OnComplete;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import static net.iGap.G.context;

public class MusicPlayer extends Service implements AudioManager.OnAudioFocusChangeListener, OnAudioFocusChangeListener {

    public static final int notificationId = 19;
    public static boolean canDoAction = true;
    public static String repeatMode = RepeatMode.noRepeat.toString();
    public static boolean isShuffelOn = false;
    public static TextView txt_music_time;
    public static TextView txt_music_time_counter;
    //    private static Bitmap orginalWallPaper = null;
    //    private static boolean isGetOrginalWallpaper=false;
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
    public static boolean isPause = false;
    public static ArrayList<RealmRoomMessage> mediaList;
    public static final int limitMediaList = 50;
    public static String strTimer = "";
    public static String messageId = "";
    public static boolean isNearDistance = false;
    public static int currentDuration = 0;
    public static boolean isVoice = false;
    public static boolean pauseSoundFromIGapCall = false;
    public static boolean inChangeStreamType = false;
    public static boolean pauseSoundFromCall = false;
    public static boolean isMusicPlyerEnable = false;
    public static boolean playNextMusic = false;
    public static String STARTFOREGROUND_ACTION = "STARTFOREGROUND_ACTION";
    public static String STOPFOREGROUND_ACTION = "STOPFOREGROUND_ACTION";
    public static boolean downloadNewItem = false;
    public static LinearLayout mainLayout;
    public static LinearLayout chatLayout;
    public static LinearLayout shearedMediaLayout;
    public static UpdateName updateName;
    private static SensorManager mSensorManager;
    private static Sensor mProximity;
    private static SensorEventListener sensorEventListener;
    private static MediaSessionCompat mSession;
    private static int latestAudioFocusState;
    private static LinearLayout layoutTripMusic;
    private static TextView btnPlayMusic;
    private static TextView btnCloseMusic;
    private static TextView txt_music_name;
    private static RemoteViews remoteViews;
    private static NotificationManager notificationManager;
    private static Notification notification;
    private static int selectedMedia = 0;
    private static Timer mTimer, mTimeSecend;
    private static long time = 0;
    private static double amoungToupdate;
    private static int stateHedset = 0;
    private static boolean pauseOnAudioFocusChange;
    private static HeadsetPluginReciver headsetPluginReciver;
    private static BluetoothCallbacks bluetoothCallbacks;
    private static RemoteControlClient remoteControlClient;
    private static ComponentName remoteComponentName;
    private static Realm mRealm;
    private static boolean isRegisterSensor = false;

    private static Realm getRealm() {
        if (mRealm == null || mRealm.isClosed()) {
            mRealm = Realm.getDefaultInstance();
        }

        return mRealm;
    }

    public static void setMusicPlayer(LinearLayout layoutTripMusic) {

        if (remoteViews == null)
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.music_layout_notification);
        if (notificationManager == null)
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (layoutTripMusic != null) {
            layoutTripMusic.setVisibility(View.GONE);
        }

        initLayoutTripMusic(layoutTripMusic);

        getAttribute();

        //getOrginallWallpaper();
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

        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicSetting", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("RepeatMode", str);
        editor.apply();

        if (onComplete != null) {
            onComplete.complete(true, "RepeatMode", "");
        }
    }

    public static void shuffleClick() {

        isShuffelOn = !isShuffelOn;
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicSetting", context.MODE_PRIVATE);
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
            @Override
            public void onClick(View v) {
                if (!isVoice) {
                    Intent intent = new Intent(context, ActivityMain.class);
                    intent.putExtra(ActivityMain.openMediaPlyer, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    context.startActivity(intent);
                }
            }
        });

        txt_music_time = (TextView) layout.findViewById(R.id.mls_txt_music_time);

        txt_music_time_counter = (TextView) layout.findViewById(R.id.mls_txt_music_time_counter);
        txt_music_name = (TextView) layout.findViewById(R.id.mls_txt_music_name);

        btnPlayMusic = (TextView) layout.findViewById(R.id.mls_btn_play_music);
        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAndPause();
            }
        });

        btnCloseMusic = (TextView) layout.findViewById(R.id.mls_btn_close);
        btnCloseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLayoutMediaPlayer();
            }
        });

        if (MusicPlayer.mp != null) {
            layout.setVisibility(View.VISIBLE);
            txt_music_name.setText(MusicPlayer.musicName);

            txt_music_time.setText(musicTime);

            if (MusicPlayer.mp.isPlaying()) {
                btnPlayMusic.setText(context.getString(R.string.md_pause_button));
            } else {
                btnPlayMusic.setText(context.getString(R.string.md_play_arrow));
            }
        }

        if (HelperCalander.isPersianUnicode) {
            txt_music_time.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_music_time.getText().toString()));
        }

        if (MusicPlayer.mp != null) {
            time = MusicPlayer.mp.getCurrentPosition() - 1;
            if (time >= 0) {
                updatePlayerTime();
            }
        }
    }

    //
    //    private static  void getOrginallWallpaper(){
    //
    //        if(isGetOrginalWallpaper){
    //            return;
    //        }
    //
    //        isGetOrginalWallpaper=true;
    //
    //
    //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    //
    //            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(G.context);
    //
    //            if (myWallpaperManager.isSetWallpaperAllowed()) {
    //
    //                ParcelFileDescriptor pfd = myWallpaperManager.getWallpaperFile(WallpaperManager.FLAG_LOCK);
    //                if (pfd != null) {
    //                    orginalWallPaper = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
    //                    try {
    //                        pfd.close();
    //                    } catch (IOException e) {
    //                        e.printStackTrace();
    //                    }
    //                }
    //            }
    //
    //        }
    //    }

    public static void playAndPause() {

        if (mp != null) {
            if (mp.isPlaying()) {
                pauseSound();
            } else {
                playSound();
            }
        } else {
            playSound();
        }
    }

    public static void pauseSound() {


        if (!isVoice) {
            try {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
                notificationManager.notify(notificationId, notification);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {
            stopTimer();

            if (btnPlayMusic != null) {
                btnPlayMusic.setText(context.getString(R.string.md_play_arrow));
            }

            if (!isShowMediaPlayer) {
                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "play", "");
            }
        } catch (Exception e) {
            HelperLog.setErrorLog("music player   pauseSound   aaa    " + e.toString());
        }

        try {
            if (mp != null && mp.isPlaying()) {
                mp.pause();
                isPause = true;
            }
        } catch (Exception e) {
            HelperLog.setErrorLog("music player   pauseSound   bbb    " + e.toString());
        }
        updateFastAdapter(MusicPlayer.messageId);
    }

    private static void updateFastAdapter(String messageId) {

        if (FragmentMediaPlayer.fastItemAdapter != null)
            FragmentMediaPlayer.fastItemAdapter.notifyAdapterItemChanged(FragmentMediaPlayer.fastItemAdapter.getPosition(Long.parseLong(messageId)));

    }

    public static void playSound() {

        if (mp == null) {
            return;
        }

        if (mp.isPlaying()) {
            return;
        }

        if (G.onAudioFocusChangeListener != null) {
            G.onAudioFocusChangeListener.onAudioFocusChangeListener(AudioManager.AUDIOFOCUS_GAIN);
        }

        if (!isVoice) {
            try {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
                notificationManager.notify(notificationId, notification);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {

            if (btnPlayMusic != null) {
                btnPlayMusic.setText(context.getString(R.string.md_pause_button));
            }

            if (!isShowMediaPlayer) {

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "pause", "");
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "pause", "");
            }
        } catch (Exception e) {
            HelperLog.setErrorLog("music player   playSound   aaa    " + e.toString());
        }

        try {
            if (mp != null && isPause) {
                mp.start();
                isPause = false;
                updateProgress();
            } else {
                startPlayer(musicName, musicPath, roomName, roomId, false, MusicPlayer.messageId);
            }
        } catch (Exception e) {
            HelperLog.setErrorLog("music player   playSound  bbb " + e.toString());
        }
        updateFastAdapter(MusicPlayer.messageId);
    }

    public static void stopSound() {

        String zeroTime = "0:00";

        if (HelperCalander.isPersianUnicode) {
            zeroTime = HelperCalander.convertToUnicodeFarsiNumber(zeroTime);
        }

        if (txt_music_time_counter != null) {
            txt_music_time_counter.setText(zeroTime);
        }

        if (!isVoice) {
            try {
                remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
                notificationManager.notify(notificationId, notification);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        try {

            if (btnPlayMusic != null) {
                btnPlayMusic.setText(context.getString(R.string.md_play_arrow));
            }

            musicProgress = 0;

            if (!isShowMediaPlayer) {

                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                    onCompleteChat.complete(false, "updateTime", zeroTime);
                } else {
                    if (FragmentChat.onMusicListener != null) {
                        FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, "");
                    }
                }
            } else if (onComplete != null) {
                onComplete.complete(true, "play", "");
                onComplete.complete(true, "updateTime", zeroTime);
            }
            stopTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mp != null) {
            mp.stop();
            updateFastAdapter(MusicPlayer.messageId);
        }
    }

    public static void nextMusic() {

        //if (FragmentMediaPlayer.adapterListMusicPlayer != null) FragmentMediaPlayer.adapterListMusicPlayer.notifyDataSetChanged();

//        if (!canDoAction) {
//            return;
//        }
//        canDoAction = false;

        try {

            selectedMedia = FragmentMediaPlayer.fastItemAdapter.getPosition(Long.parseLong(MusicPlayer.messageId));

            selectedMedia--;

            String beforeMessageId = MusicPlayer.messageId;

            if (selectedMedia >= 0) {

                RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));

                while (!roomMessage.getAttachment().isFileExistsOnLocal()) {
                    selectedMedia--;
                    roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                    if (selectedMedia <= 0) {
                        stopSound();
                        return;
                    }
                }

                startPlayer(roomMessage.getAttachment().getName(), roomMessage.getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");
            } else {
                int index = mediaList.size() - 1;
                if (index >= 0) {
                    selectedMedia = index;

                    RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));

                    while (!roomMessage.getAttachment().isFileExistsOnLocal()) {
                        selectedMedia--;
                        roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                        if (selectedMedia <= 0) {
                            stopSound();
                            return;
                        }
                    }
                    startPlayer(roomMessage.getAttachment().getName(), roomMessage.getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");
                }
            }

            if (FragmentChat.onMusicListener != null) {
                FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, beforeMessageId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //**************************************************************************

    private static void nextRandomMusic() {
        try {
            String beforeMessageId = MusicPlayer.messageId;
            Random r = new Random();
            selectedMedia = r.nextInt(mediaList.size() - 1);
            RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));


            int maxTry = 0;
            while (!roomMessage.getAttachment().isFileExistsOnLocal()) {
                selectedMedia = r.nextInt(mediaList.size() - 1);
                roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                maxTry++;
                if (maxTry > 3) {
                    nextMusic();
                }
            }


            startPlayer(roomMessage.getAttachment().getName(), roomMessage.getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");
            if (FragmentChat.onMusicListener != null) {
                FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, beforeMessageId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void previousMusic() {


        try {
            if (MusicPlayer.mp != null) {

                if (MusicPlayer.mp.getCurrentPosition() > 10000) {

                    musicProgress = 0;

                    MusicPlayer.mp.seekTo(0);
                    time = MusicPlayer.mp.getCurrentPosition();
                    updatePlayerTime();

                    return;
                }
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
//        if (!canDoAction) {
//            return;
//        }
//        canDoAction = false;

        try {
            String beforeMessageId = MusicPlayer.messageId;

            if (!isVoice) {
                selectedMedia = FragmentMediaPlayer.fastItemAdapter.getPosition(Long.parseLong(MusicPlayer.messageId));
            }

            selectedMedia++;
            if (selectedMedia < mediaList.size()) {

                RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));

                while (!roomMessage.getAttachment().isFileExistsOnLocal()) {
                    selectedMedia++;
                    roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                    if (selectedMedia > mediaList.size()) {
                        stopSound();
                        return;
                    }
                }
                startPlayer(roomMessage.getAttachment().getName(), roomMessage.getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");
            } else {
                if (isVoice) { // avoid from return to first voice
                    if (btnPlayMusic != null) {
                        btnPlayMusic.setText(context.getString(R.string.md_play_arrow));
                    }
                    return;
                }

                selectedMedia = 0;
                RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));

                while (!roomMessage.getAttachment().isFileExistsOnLocal()) {
                    selectedMedia++;
                    roomMessage = RealmRoomMessage.getFinalMessage(mediaList.get(selectedMedia));
                    if (selectedMedia > mediaList.size()) {
                        stopSound();
                        return;
                    }
                }

                startPlayer(roomMessage.getAttachment().getName(), roomMessage.getAttachment().getLocalFilePath(), roomName, roomId, false, mediaList.get(selectedMedia).getMessageId() + "");
            }
            if (FragmentChat.onMusicListener != null) {
                FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, beforeMessageId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void closeLayoutMediaPlayer() {

        try {

            isMusicPlyerEnable = false;

            if (layoutTripMusic != null) {
                layoutTripMusic.setVisibility(View.GONE);
            }

            if (onComplete != null) {
                onComplete.complete(true, "finish", "");
            }

            if (onCompleteChat != null) {
                onCompleteChat.complete(true, "pause", "");
            }

            stopSound();

            if (mp != null) {
                mp.release();
                mp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // clearWallpaperLockScrean();

        setMedaiInfoOnLockScreen(true);

        try {

            Intent intent = new Intent(context, MusicPlayer.class);
            intent.putExtra("ACTION", STOPFOREGROUND_ACTION);
            context.startService(intent);
        } catch (RuntimeException e) {

            if (notificationManager != null) {
                notificationManager.cancel(notificationId);
            }
        }

        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
            mRealm = null;
        }
    }

    private static String getMusicName(long messageId, String name) {
        try {
            if (isVoice) {
                String voiceName = "";
                RealmRoomMessage realmRoomMessage = RealmRoomMessage.getFinalMessage(getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst());
                if (realmRoomMessage != null) {
                    if (realmRoomMessage.getUserId() != 0) {
                        if (realmRoomMessage.getUserId() == G.userId) {
                            voiceName = G.context.getResources().getString(R.string.you);
                        } else {
                            voiceName = RealmRegisteredInfo.getNameWithId(realmRoomMessage.getUserId());
                        }

                    } else if (realmRoomMessage.getAuthorRoomId() != 0) {
                        voiceName = RealmRoom.detectTitle(realmRoomMessage.getAuthorRoomId());
                    }
                    return G.fragmentActivity.getResources().getString(R.string.recorded_by) + " " + voiceName;
                }
            }

            if (name == null) {
                name = "";
            }

            if (name.length() > 0) {
                return musicName = name;
            } else if (musicPath != null && musicPath.length() > 0) {
                return musicPath.substring(musicPath.lastIndexOf("/") + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void startPlayer(final String name, String musicPath, String roomName, long roomId, final boolean updateList, final String messageID) {

        if (!inChangeStreamType) {
//            G.handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                    canDoAction = true;
//                }
//            }, 1000);

            isVoice = false;
            isPause = false;


            if (messageID != null && messageID.length() > 0) {

                try {
                    RealmRoomMessage realmRoomMessage = RealmRoomMessage.getFinalMessage(getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(messageID)).findFirst());

                    if (realmRoomMessage != null) {
                        String type = realmRoomMessage.getMessageType().toString();

                        if (type.equals("VOICE")) {
                            isVoice = true;
                        }
                    }
                } catch (Exception e) {
                    HelperLog.setErrorLog(" music plyer   startPlayer   setISVoice    " + messageID + "    " + e.toString());
                }
            }

            if (isVoice) {
                closeLayoutMediaPlayer();
            }

            updateFastAdapter(MusicPlayer.messageId);
            MusicPlayer.messageId = messageID;
            MusicPlayer.musicPath = musicPath;
            MusicPlayer.roomName = roomName;
            mediaThumpnail = null;
            MusicPlayer.roomId = roomId;

        }

        if (MusicPlayer.downloadNextMusic(messageId)) {
            if (FragmentMediaPlayer.fastItemAdapter != null) {
                FragmentMediaPlayer.fastItemAdapter.notifyAdapterDataSetChanged();
            }
        }

        try {
            if (mp != null) {
                mp.setOnCompletionListener(null);
                mp.stop();
                mp.reset();
                mp.release();
            }

            if (layoutTripMusic != null) {
                layoutTripMusic.setVisibility(View.VISIBLE);
            }
            musicName = getMusicName(Long.parseLong(messageID), name);
            mp = new MediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mp.setDataSource(musicPath);

            if (isNearDistance) {
                mp.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mp.prepare();
            mp.start();

            if (currentDuration > 0) {
                mp.seekTo(currentDuration);
                currentDuration = 0;
            }

            updateFastAdapter(MusicPlayer.messageId);
            musicTime = milliSecondsToTimer((long) mp.getDuration());
            txt_music_time.setText(musicTime);
            btnPlayMusic.setText(context.getString(R.string.md_pause_button));
            txt_music_name.setText(musicName);
            updateName = new UpdateName() {
                @Override
                public void rename() {
                    musicName = getMusicName(Long.parseLong(messageID), name);
                }
            };

            updateProgress();

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    OnCompleteMusic();
                }
            });

            if (onComplete != null) {
                onComplete.complete(true, "update", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!inChangeStreamType) {
            updateNotification();
        }

        if (!isShowMediaPlayer) {

            if (onCompleteChat != null) {
                onCompleteChat.complete(true, "pause", "");
            }
        } else if (onComplete != null) {
            onComplete.complete(true, "pause", "");
        }

        if (updateList || downloadNewItem) {
            fillMediaList(true);
            downloadNewItem = false;
        }


        if (HelperCalander.isPersianUnicode) {
            txt_music_time.setText(HelperCalander.convertToUnicodeFarsiNumber(txt_music_time.getText().toString()));
        }

        isMusicPlyerEnable = true;


        inChangeStreamType = false;
    }

    private static void OnCompleteMusic() {
        try {
            if (isVoice) {

                fillMediaList(true);
                nextMusic();
                if (FragmentChat.onMusicListener != null) {
                    FragmentChat.onMusicListener.complete(false, MusicPlayer.messageId, "");
                } else {
                    downloadNextMusic(MusicPlayer.messageId);
                }

                String zeroTime = "0:00";
                if (onCompleteChat != null) {
                    onCompleteChat.complete(true, "play", "");
                    onCompleteChat.complete(false, "updateTime", zeroTime);
                } else {
                    if (FragmentChat.onMusicListener != null) {
                        FragmentChat.onMusicListener.complete(true, MusicPlayer.messageId, "");
                    }
                }
            } else {
                if (repeatMode.equals(RepeatMode.noRepeat.toString())) {
                    stopSound();
                } else if (repeatMode.equals(RepeatMode.repeatAll.toString())) {

                    if (playNextMusic) {

                        fillMediaList(true);

                        nextMusic();
                        if (FragmentChat.onMusicListener != null) {
                            FragmentChat.onMusicListener.complete(false, MusicPlayer.messageId, "");
                        } else {
                            downloadNextMusic(MusicPlayer.messageId);
                        }
                    } else {

                        if (isShuffelOn) {
                            nextRandomMusic();
                        } else {

                            nextMusic();
                        }
                    }
                } else if (repeatMode.equals(RepeatMode.oneRpeat.toString())) {
                    stopSound();
                    playAndPause();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private static void updateNotification() {
        if (!isVoice) {
            getMusicInfo();

            Intent intentFragmentMusic = new Intent(context, ActivityMain.class);
            intentFragmentMusic.putExtra(ActivityMain.openMediaPlyer, true);

            PendingIntent pi = PendingIntent.getActivity(context, 555, intentFragmentMusic, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setTextViewText(R.id.mln_txt_music_name, MusicPlayer.musicName);
            remoteViews.setTextViewText(R.id.mln_txt_music_outher, MusicPlayer.musicInfoTitle);

            //if (mp != null) {
            //    if (mp.isPlaying()) {
            remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.pause_button);
            //    } else {
            //        remoteViews.setImageViewResource(R.id.mln_btn_play_music, R.mipmap.play_button);
            //    }
            //}

            Intent intentPrevious = new Intent(context, customButtonListener.class);
            intentPrevious.putExtra("mode", "previous");
            PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 1, intentPrevious, 0);
            remoteViews.setOnClickPendingIntent(R.id.mln_btn_Previous_music, pendingIntentPrevious);

            Intent intentPlayPause = new Intent(context, customButtonListener.class);
            intentPlayPause.putExtra("mode", "play");
            PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(context, 2, intentPlayPause, 0);
            remoteViews.setOnClickPendingIntent(R.id.mln_btn_play_music, pendingIntentPlayPause);

            Intent intentforward = new Intent(context, customButtonListener.class);
            intentforward.putExtra("mode", "forward");
            PendingIntent pendingIntentforward = PendingIntent.getBroadcast(context, 3, intentforward, 0);
            remoteViews.setOnClickPendingIntent(R.id.mln_btn_forward_music, pendingIntentforward);

            Intent intentClose = new Intent(context, customButtonListener.class);
            intentClose.putExtra("mode", "close");
            PendingIntent pendingIntentClose = PendingIntent.getBroadcast(context, 4, intentClose, 0);
            remoteViews.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);

            notification = new NotificationCompat.Builder(context.getApplicationContext()).setTicker("music").setSmallIcon(R.mipmap.j_mp3).setContentTitle(musicName)
                    //  .setContentText(place)
                    .setContent(remoteViews).setContentIntent(pi).setDeleteIntent(pendingIntentClose).setAutoCancel(false).setOngoing(true).build();
        }

        Intent intent = new Intent(context, MusicPlayer.class);
        intent.putExtra("ACTION", STARTFOREGROUND_ACTION);
        context.startService(intent);
    }

    public static ArrayList<RealmRoomMessage> fillMediaList(boolean setSelectedItem) {

        boolean isOnListMusic = false;
        mediaList = new ArrayList<>();

        List<RealmRoomMessage> roomMessages = getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo(RealmRoomMessageFields.CREATE_TIME, 0).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

        if (!roomMessages.isEmpty()) {
            for (RealmRoomMessage realmRoomMessage : roomMessages) { //TODO Saeed Mozaffari; write better code for detect voice and audio instead get all roomMessages

                RealmRoomMessage roomMessage = RealmRoomMessage.getFinalMessage(realmRoomMessage);

                if (isVoice) {
                    if (roomMessage.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.VOICE.toString())) {
                        try {
                            if (roomMessage.getAttachment().getLocalFilePath() != null) {
                                if (new File(roomMessage.getAttachment().getLocalFilePath()).exists()) {
                                    mediaList.add(roomMessage);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    if ((roomMessage.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || roomMessage.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString()))) {

                        if (mediaList.size() <= limitMediaList || !isOnListMusic) {
                            try {
                                if (roomMessage.getMessageId() == Long.parseLong(messageId)) {
                                    isOnListMusic = true;
                                }
                                mediaList.add(roomMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if (setSelectedItem)

        {
            for (int i = mediaList.size() - 1; i >= 0; i--) {
                try {
                    RealmRoomMessage _rm = RealmRoomMessage.getFinalMessage(mediaList.get(i));
                    if (_rm.getAttachment().getLocalFilePath() != null && _rm.getAttachment().getLocalFilePath().equals(musicPath)) {
                        selectedMedia = i;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        return mediaList;
    }

    private static void updateProgress() {

        stopTimer();

        double duration = MusicPlayer.mp.getDuration();
        amoungToupdate = duration / 100;
        time = MusicPlayer.mp.getCurrentPosition();
        musicProgress = ((int) (time / amoungToupdate));

        mTimeSecend = new Timer();

        mTimeSecend.schedule(new TimerTask() {
            @Override
            public void run() {

                updatePlayerTime();
                time += 1000;
            }
        }, 0, 1000);

        if (amoungToupdate >= 1) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {

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

        if (HelperCalander.isPersianUnicode) {
            strTimer = HelperCalander.convertToUnicodeFarsiNumber(strTimer);
        }

        if (txt_music_time_counter != null) {
            G.handler.post(new Runnable() {
                @Override
                public void run() {
                    txt_music_time_counter.setText(strTimer);
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
        musicInfoTitle = context.getString(R.string.unknown_artist);

        MediaMetadataRetriever mediaMetadataRetriever = (MediaMetadataRetriever) new MediaMetadataRetriever();

        Uri uri = null;

        if (MusicPlayer.musicPath != null) {
            uri = (Uri) Uri.fromFile(new File(MusicPlayer.musicPath));
        }

        if (uri != null) {

            try {

                mediaMetadataRetriever.setDataSource(context, uri);

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

                byte[] data = mediaMetadataRetriever.getEmbeddedPicture();
                if (data != null) {
                    mediaThumpnail = BitmapFactory.decodeByteArray(data, 0, data.length);
                    //  setWallpaperLockScreen(mediaThumpnail);
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setMedaiInfoOnLockScreen(false);
                        }
                    }, 100);

                    int size = (int) context.getResources().getDimension(R.dimen.dp48);
                    remoteViews.setImageViewBitmap(R.id.mln_img_picture_music, Bitmap.createScaledBitmap(mediaThumpnail, size, size, false));
                } else {
                    remoteViews.setImageViewResource(R.id.mln_img_picture_music, R.mipmap.music_icon_green);
                    // clearWallpaperLockScrean();
                    setMedaiInfoOnLockScreen(true);
                }
            } catch (Exception e) {

                Log.e("debug", " music plyer   getMusicInfo    " + uri + "       " + e.toString());
            }
        }
    }

    private static void getAttribute() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MusicSetting", context.MODE_PRIVATE);
        repeatMode = sharedPreferences.getString("RepeatMode", RepeatMode.noRepeat.toString());
        isShuffelOn = sharedPreferences.getBoolean("Shuffel", false);
    }

    public static boolean downloadNextMusic(String messageId) {

        boolean result = false;

        RealmResults<RealmRoomMessage> roomMessages = getRealm().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).equalTo(RealmRoomMessageFields.DELETED, false).greaterThan(RealmRoomMessageFields.MESSAGE_ID, Long.parseLong(messageId)).findAllSorted(RealmRoomMessageFields.CREATE_TIME);

        if (!roomMessages.isEmpty()) {
            for (RealmRoomMessage rm : roomMessages) {
                if (isVoice) {
                    if (rm.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.VOICE.toString())) {
                        result = startDownload(rm);
                        break;
                    }
                } else {
                    if (rm.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO.toString()) || rm.getMessageType().toString().equals(ProtoGlobal.RoomMessageType.AUDIO_TEXT.toString())) {
                        result = startDownload(rm);
                        break;
                    }
                }
            }
        }

        return result;
    }

    private static boolean startDownload(RealmRoomMessage rm) {
        boolean result = false;
        try {
            if (rm.getAttachment().getLocalFilePath() == null || !new File(rm.getAttachment().getLocalFilePath()).exists()) {
                ProtoGlobal.RoomMessageType _messageType = rm.getForwardMessage() != null ? rm.getForwardMessage().getMessageType() : rm.getMessageType();
                String _cacheId = rm.getForwardMessage() != null ? rm.getForwardMessage().getAttachment().getCacheId() : rm.getAttachment().getCacheId();
                String _name = rm.getForwardMessage() != null ? rm.getForwardMessage().getAttachment().getName() : rm.getAttachment().getName();
                String _token = rm.getForwardMessage() != null ? rm.getForwardMessage().getAttachment().getToken() : rm.getAttachment().getToken();
                Long _size = rm.getForwardMessage() != null ? rm.getForwardMessage().getAttachment().getSize() : rm.getAttachment().getSize();

                if (_cacheId == null) {
                    return false;
                }

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.FILE;

                final String _path = AndroidUtils.getFilePathWithCashId(_cacheId, _name, _messageType);

                if (_token != null && _token.length() > 0 && _size > 0) {

                    if (!new File(_path).exists()) {

                        result = true;

                        HelperDownloadFile.startDownload(rm.getMessageId() + "", _token, _cacheId, _name, _size, selector, _path, 0, new HelperDownloadFile.UpdateListener() {
                            @Override
                            public void OnProgress(String path, int progress) {
                                if (progress == 100) {
                                    downloadNewItem = true;
                                }
                            }

                            @Override
                            public void OnError(String token) {

                            }
                        });

                        MusicPlayer.playNextMusic = true;
                    } else {
                        MusicPlayer.playNextMusic = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void initSensor() {

        if (!isRegisterSensor) {

            registerMediaBottom();

            headsetPluginReciver = new HeadsetPluginReciver();
            //bluetoothCallbacks = new BluetoothCallbacks();

            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                        AudioManager am = (AudioManager) G.context.getSystemService(AUDIO_SERVICE);
                        if (am.isWiredHeadsetOn() || am.isSpeakerphoneOn()) {
                            return;
                        }

                        boolean newIsNear = Math.abs(event.values[0]) < Math.min(event.sensor.getMaximumRange(), 3);
                        if (newIsNear != isNearDistance) {
                            isNearDistance = newIsNear;
                            if (isVoice) {
                                changeStreamType();
                            }
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            context.registerReceiver(headsetPluginReciver, filter);

            //IntentFilter filterBluetooth = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            //context.registerReceiver(bluetoothCallbacks, filterBluetooth);

            registerDistanceSensor();

            isRegisterSensor = true;
        }
    }

    private static void changeStreamType() {

        try {
            if (mp != null && mp.isPlaying()) {
                inChangeStreamType = true;
                currentDuration = mp.getCurrentPosition();
                startPlayer(musicName, musicPath, roomName, roomId, false, MusicPlayer.messageId);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void removeSensor() {

        if (isRegisterSensor) {

            isRegisterSensor = false;

            AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

            try {
                if (remoteComponentName != null && audioManager != null) {
                    audioManager.unregisterMediaButtonEventReceiver(remoteComponentName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (remoteControlClient != null && audioManager != null) {
                    audioManager.unregisterRemoteControlClient(remoteControlClient);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                context.unregisterReceiver(headsetPluginReciver);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //try {
            //    context.unregisterReceiver(bluetoothCallbacks);
            //} catch (Exception e) {
            //    Log.e("ddddd", "music plyer  removeSensor    unregisterReceiver " + e.toString());
            //}

            unRegisterDistanceSensor();

            remoteComponentName = null;
            remoteControlClient = null;

            // clearWallpaperLockScrean();

        }
    }

    private static void registerDistanceSensor() {

        try {

            mSensorManager.registerListener(sensorEventListener, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            Log.e("dddd", "music player registerDistanceSensor   " + e.toString());
        }
    }

    private static void unRegisterDistanceSensor() {

        try {
            mSensorManager.unregisterListener(sensorEventListener);
        } catch (Exception e) {
            Log.e("dddd", "music player unRegisterDistanceSensor   " + e.toString());
        }
    }

    private static void registerMediaBottom() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                try {

                    mSession = new MediaSessionCompat(context, context.getPackageName());
                    Intent intent = new Intent(context, MediaBottomReciver.class);
                    PendingIntent pintent = PendingIntent.getBroadcast(context, 50, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mSession.setMediaButtonReceiver(pintent);
                    mSession.setActive(true);

                    PlaybackStateCompat state = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

                            .setState(PlaybackStateCompat.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime()).build();
                    mSession.setPlaybackState(state);
                } catch (Exception e) {

                    HelperLog.setErrorLog(" music player   registerMediaBottom     " + e.toString());
                }
            } else {

                try {
                    remoteComponentName = new ComponentName(context, MediaBottomReciver.class.getName());
                    AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                    audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                } catch (Exception e) {
                    HelperLog.setErrorLog(" music plyer   registerMediaBottom    " + e.toString());
                }
            }

            if (remoteComponentName != null) {
                remoteComponentName = new ComponentName(context, MediaBottomReciver.class.getName());
            }

            try {

                if (remoteControlClient == null) {

                    Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    mediaButtonIntent.setComponent(remoteComponentName);
                    PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(context, 55, mediaButtonIntent, 0);
                    remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                    AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                    audioManager.registerRemoteControlClient(remoteControlClient);

                    remoteControlClient.setTransportControlFlags(RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP | RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
                }
            } catch (Exception e) {
                HelperLog.setErrorLog(" music plyer   setMediaControl    " + e.toString());
            }
        }
    }

    private static void setMedaiInfoOnLockScreen(boolean clear) {

        try {

            if (remoteControlClient != null) {

                RemoteControlClient.MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);

                if (clear) {

                    metadataEditor.clear();
                } else {
                    metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, musicName + "");
                    metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, musicInfoTitle + "");
                    try {
                        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mediaThumpnail);
                        // seMediaSesionMetaData();

                    } catch (Throwable e) {
                    }
                }

                metadataEditor.apply();
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(" music plyer   setMedoiInfoOnLockScreen    " + e.toString());
        }
    }

    //***************************************************************************** sensor *********************************

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        G.onAudioFocusChangeListener = this;

        if (intent == null || intent.getExtras() == null) {
            stopForeground(true);
            stopSelf();
        } else {

            String action = intent.getExtras().getString("ACTION");
            if (action != null) {

                if (action.equals(STARTFOREGROUND_ACTION)) {

                    if (notification != null) {
                        startForeground(notificationId, notification);
                        if (latestAudioFocusState != AudioManager.AUDIOFOCUS_GAIN) { // if do double "AUDIOFOCUS_GAIN", "AUDIOFOCUS_LOSS" will be called
                            latestAudioFocusState = AudioManager.AUDIOFOCUS_GAIN;
                            registerAudioFocus(AudioManager.AUDIOFOCUS_GAIN);
                        }
                    }
                    initSensor();
                } else if (action.equals(STOPFOREGROUND_ACTION)) {

                    removeSensor();

                    stopForeground(true);
                    stopSelf();
                }
            }
        }

        return START_STICKY;
    }

    private void registerAudioFocus(int audioState) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, audioState);
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        latestAudioFocusState = focusChange;
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            pauseOnAudioFocusChange = true;
            pauseSound();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            if (pauseOnAudioFocusChange) {
                pauseOnAudioFocusChange = false;
                //playSound(); // commented this line because after receive incoming call and end call, this listener will be called and sound will be played!!!
            }
        }
    }

    //*************************************************************************************** getPhoneState

    @Override
    public void onAudioFocusChangeListener(int audioState) {
        if (latestAudioFocusState != audioState) {
            latestAudioFocusState = audioState;
            registerAudioFocus(audioState);
        }
    }


    public enum RepeatMode {
        noRepeat, oneRpeat, repeatAll;
    }

    public interface UpdateName {
        void rename();
    }

    public static class customButtonListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String str = intent.getExtras().getString("mode");

            if (str.equals("previous")) {
//                canDoAction= true;
                previousMusic();
//                MusicPlayer.canDoAction = false;
            } else if (str.equals("play")) {
                playAndPause();
            } else if (str.equals("forward")) {
//                canDoAction= true;
                nextMusic();
//                MusicPlayer.canDoAction = false;
            } else if (str.equals("close")) {
                closeLayoutMediaPlayer();
            }
        }
    }

//    private static void seMediaSesionMetaData() {
//        if (mSession != null) {
//
//            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
//            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "aaaaaaa");
//            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "bbbbbbb");
//            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, "ccccccccc");
//            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 1234);
//            builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, mediaThumpnail);
//            mSession.setMetadata(builder.build());
//
//
//        }
//    }


    /*private static void setWallpaperLockScreen(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(G.context);

            try {

                if (myWallpaperManager.isSetWallpaperAllowed()) {
                    myWallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
                }

            } catch (Exception e) {

            }
        }
    }

    private static void clearWallpaperLockScrean() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                WallpaperManager myWallpaperManager = WallpaperManager.getInstance(G.context);
                if (myWallpaperManager.isSetWallpaperAllowed()) {

                    if (orginalWallPaper != null) {
                        myWallpaperManager.setBitmap(orginalWallPaper, null, true, WallpaperManager.FLAG_LOCK);
                    } else {
                        myWallpaperManager.clear(WallpaperManager.FLAG_LOCK);
                    }
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    static class HeadsetPluginReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                int state = intent.getIntExtra("state", -1);

                if (state != stateHedset) {

                    stateHedset = state;

                    switch (state) {
                        case 0:
                            if (mp != null && mp.isPlaying()) {
                                pauseSound();
                            }
                            break;
                        case 1:
                            break;
                    }
                }
            }
        }
    }

    static class BluetoothCallbacks extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (mp != null) {
                            if (mp.isPlaying()) {
                                pauseSound();
                            }
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                }
            }
        }
    }
}


