package net.iGap.helper;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.SHP_SETTING;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

import io.realm.Realm;

public class HelperNotificationChannel {

    public void initSetting(long roomId) {

        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

        int notification = 0;

        int vibrator = 1;
        int sound = 0;

        if (realmRoom == null) {
            return;
        }
        boolean isMute = realmRoom.getMute();
        if (isMute) {
            return;
        }

        HelperNotificationAndBadge.updateBadgeOnly(realm, -1);

        if (realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null) {
            notification = realmRoom.getChannelRoom().getRealmNotificationSetting().getNotification();
            if (realmRoom.getChannelRoom().getRealmNotificationSetting().getVibrate() != -1) {
                vibrator = realmRoom.getChannelRoom().getRealmNotificationSetting().getVibrate();
            } else {
                vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 1);
            }
            if (realmRoom.getChannelRoom().getRealmNotificationSetting().getIdRadioButtonSound() != -1) {
                sound = realmRoom.getChannelRoom().getRealmNotificationSetting().getIdRadioButtonSound();
            } else {
                sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
            }

        } else {
            vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
            sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
        }

        if (notification != 2) {
            AudioManager am2 = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
            if (am2 != null && am2.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                vibrateAct(vibrator);
                soundAct(sound);
            } else if (am2 != null && am2.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                vibrateAct(vibrator);
            }
        }
    }

    private void vibrateAct(int which) {
        Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vSilent == null) {
            return;
        }
        switch (which) {
            case 0:
                vSilent.vibrate(300);
                break;
            case 1:
                vSilent.vibrate(200);

                break;
            case 2:
                vSilent.vibrate(700);
                break;
            case 3:
                AudioManager am2 = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);

                if (am2 == null) {
                    return;
                }
                switch (am2.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        vSilent.vibrate(0);
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        vSilent.vibrate(300);

                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        vSilent.vibrate(0);
                        break;
                }
                break;
        }

    }

    private void soundAct(int which) {

        MediaPlayer mediaPlayer = MediaPlayer.create(G.context, setSound(which));
        if (mediaPlayer != null)mediaPlayer.start();
    }

    public int setSound(int which) {
        int sound = R.raw.igap;
        switch (which) {
            case 0:

                sound = R.raw.igap;
                break;
            case 1:
                sound = R.raw.aooow;
                break;
            case 2:
                sound = R.raw.bbalert;
                break;
            case 3:
                sound = R.raw.boom;
                break;
            case 4:
                sound = R.raw.bounce;
                break;
            case 5:
                sound = R.raw.doodoo;
                break;
            case 6:
                sound = R.raw.jing;
                break;
            case 7:
                sound = R.raw.lili;
                break;
            case 8:
                sound = R.raw.msg;
                break;
            case 9:
                sound = R.raw.newa;
                break;
            case 10:
                sound = R.raw.none;
                break;
            case 11:
                sound = R.raw.onelime;
                break;
            case 12:
                sound = R.raw.tone;
                break;
            case 13:
                sound = R.raw.woow;
                break;
        }
        return sound;
    }


}
