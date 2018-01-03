/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentNotificationBinding;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmChannelRoom;
import net.iGap.realm.RealmChatRoom;
import net.iGap.realm.RealmGroupRoom;
import net.iGap.realm.RealmNotificationSetting;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

import io.realm.Realm;

import static net.iGap.R.string.DISCARD;
import static net.iGap.R.string.array_Default;
import static net.iGap.R.string.array_Disable;

public class FragmentNotificationViewModel {

    private Realm realm;
    private RealmNotificationSetting realmNotificationSetting;
    private ProtoGlobal.Room.Type roomType;
    private FragmentNotificationBinding fragmentNotificationBinding;

    private static final int DEFAULT = 0;
    private static final int ENABLE = 1;
    private static final int DISABLE = 2;
    private int realmNotification = 0;
    private int realmVibrate = 0;
    private int realmIdSound = 0;
    private int realmLedColor;

    private String realmSound = "iGap";

    private long roomId;

    public ObservableField<String> notificationState = new ObservableField<>(G.fragmentActivity.getResources().getString(array_Default));
    public ObservableField<String> vibrate = new ObservableField<>(G.fragmentActivity.getResources().getString(array_Default));
    public ObservableField<String> sound = new ObservableField<>();

    public FragmentNotificationViewModel(FragmentNotificationBinding fragmentNotificationBinding, long roomId) {
        this.fragmentNotificationBinding = fragmentNotificationBinding;
        this.roomId = roomId;

        realm = Realm.getDefaultInstance();
        roomType = RealmRoom.detectType(roomId);
        getInfo();

        startNotificationState();
        startVibrate();
        startSound();
        startLedColor();
    }

    //===============================================================================
    //=====================================Starts====================================
    //===============================================================================

    private void startNotificationState() {
        switch (realmNotification) {
            case DEFAULT:
                notificationState.set(G.fragmentActivity.getResources().getString(R.string.array_Default));
                break;
            case ENABLE:
                notificationState.set(G.fragmentActivity.getResources().getString(R.string.array_enable));
                break;
            case DISABLE:
                notificationState.set(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                break;
        }
    }

    private void startVibrate() {
        switch (realmVibrate) {
            case 0:
                vibrate.set(G.fragmentActivity.getResources().getString(array_Default));
                break;
            case 1:
                vibrate.set(G.fragmentActivity.getResources().getString(R.string.array_Short));
                break;
            case 2:
                vibrate.set(G.fragmentActivity.getResources().getString(R.string.array_Long));
                break;
            case 3:
                vibrate.set(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                break;
            case 4:
                vibrate.set(G.fragmentActivity.getResources().getString(array_Disable));
                break;
        }
    }

    private void startSound() {
        if (realmIdSound == 0 || realmIdSound == -1) {
            sound.set(G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
        } else {
            sound.set(realmSound);
        }
    }

    private void startLedColor() {
        GradientDrawable bgShape = (GradientDrawable) fragmentNotificationBinding.ntgImgLedColorMessage.getBackground();
        bgShape.setColor(realmLedColor);
    }

    //===============================================================================
    //================================Getters/Setters================================
    //===============================================================================

    private void setNotificationState(String notificationStateString, int notificationType) {
        notificationState.set(notificationStateString);

        RealmNotificationSetting.popupNotification(roomId, roomType, notificationType);
        realmNotification = notificationType;
    }

    private void setVibrate(String vibrateString, int vibrateLevel) {
        vibrate.set(vibrateString);

        Vibrator vibrateService = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrateService != null) {
            vibrateService.vibrate(vibrateLevel);
        }
    }

    private void setSound(String soundString) {
        sound.set(soundString);
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onNotificationStateClick(View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.notifications_notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                switch (which) {
                    case DEFAULT: {
                        setNotificationState(G.context.getString(R.string.array_Default), DEFAULT);
                        break;
                    }
                    case ENABLE: {
                        setNotificationState(G.context.getString(R.string.array_enable), ENABLE);
                        break;
                    }
                    case DISABLE: {
                        setNotificationState(G.context.getString(R.string.array_Disable), DISABLE);
                        break;
                    }
                }
            }
        }).show();
    }

    public void onNotificationVibrateClick(View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, final int vibrateLevel, CharSequence text) {
                switch (vibrateLevel) {
                    case 0:
                        setVibrate(text.toString(), 350);
                        break;
                    case 1:
                        setVibrate(text.toString(), 200);
                        break;
                    case 2:
                        setVibrate(text.toString(), 500);
                        break;
                    case 3:
                        AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);
                        switch (am2.getRingerMode()) {
                            case AudioManager.RINGER_MODE_SILENT:
                                setVibrate(text.toString(), AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                        }
                        break;
                    case 4:
                        setVibrate(text.toString(), 0);
                        break;
                }

                RealmNotificationSetting.vibrate(roomId, roomType, vibrateLevel);
            }
        }).show();
    }

    public void onNotificationSoundClick(View view) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(realmIdSound, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, final int which, final CharSequence text) {

                switch (which) {
                    case 0:
                        MediaPlayer.create(G.fragmentActivity, R.raw.igap).start();
                        break;
                    case 1:
                        MediaPlayer.create(G.fragmentActivity, R.raw.aooow).start();
                        break;
                    case 2:
                        MediaPlayer.create(G.fragmentActivity, R.raw.bbalert).start();
                        break;
                    case 3:
                        MediaPlayer.create(G.fragmentActivity, R.raw.boom).start();
                        break;
                    case 4:
                        MediaPlayer.create(G.fragmentActivity, R.raw.bounce).start();
                        break;
                    case 5:
                        MediaPlayer.create(G.fragmentActivity, R.raw.doodoo).start();
                        break;

                    case 6:
                        MediaPlayer.create(G.fragmentActivity, R.raw.jing).start();
                        break;
                    case 7:
                        MediaPlayer.create(G.fragmentActivity, R.raw.lili).start();
                        break;
                    case 8:
                        MediaPlayer.create(G.fragmentActivity, R.raw.msg).start();
                        break;
                    case 9:
                        MediaPlayer.create(G.fragmentActivity, R.raw.newa).start();
                        break;
                    case 10:
                        MediaPlayer.create(G.fragmentActivity, R.raw.none).start();
                        break;
                    case 11:
                        MediaPlayer.create(G.fragmentActivity, R.raw.onelime).start();
                        break;
                    case 12:
                        MediaPlayer.create(G.fragmentActivity, R.raw.tone).start();
                        break;
                    case 13:
                        MediaPlayer.create(G.fragmentActivity, R.raw.woow).start();
                        break;
                }

                setSound(text.toString());
                realmIdSound = which;

                RealmNotificationSetting.sound(roomId, text.toString(), which, roomType);

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
    }

    public void onLedColorClick(View view) {
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView).positiveText(G.fragmentActivity.getResources().getString(R.string.set)).negativeText(G.fragmentActivity.getResources().getString(DISCARD)).title(G.fragmentActivity.getResources().getString(R.string.st_led_color)).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        }).build();

        View view1 = dialog.getCustomView();
        assert view1 != null;
        final ColorPicker picker = (ColorPicker) view1.findViewById(R.id.picker);
        SVBar svBar = (SVBar) view1.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) view1.findViewById(R.id.opacitybar);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                GradientDrawable bgShape = (GradientDrawable) fragmentNotificationBinding.ntgImgLedColorMessage.getBackground();
                bgShape.setColor(picker.getColor());

                RealmNotificationSetting.ledColor(roomId, roomType, picker.getColor());
            }
        });

        dialog.show();
    }

    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private void getInfo() {
        switch (roomType) {
            case GROUP: {

                Realm realm = Realm.getDefaultInstance();

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                    RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                    if (realmGroupRoom != null) {
                        if (realmGroupRoom.getRealmNotificationSetting() == null) {
                            setRealm(realm, realmGroupRoom, null, null);
                        } else {
                            realmNotificationSetting = realmGroupRoom.getRealmNotificationSetting();
                        }
                        getRealm();
                    }
                }

                realm.close();
            }

            break;
            case CHANNEL: {
                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        if (realmChannelRoom.getRealmNotificationSetting() == null) {
                            setRealm(realm, null, realmChannelRoom, null);
                        } else {
                            realmNotificationSetting = realmChannelRoom.getRealmNotificationSetting();
                        }
                        getRealm();
                    }
                }

                realm.close();
                break;
            }
            case CHAT: {

                Realm realm = Realm.getDefaultInstance();
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null && realmRoom.getChatRoom() != null) {
                    RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                    if (realmChatRoom != null) {
                        if (realmChatRoom.getRealmNotificationSetting() == null) {
                            setRealm(realm, null, null, realmChatRoom);
                        } else {
                            realmNotificationSetting = realmChatRoom.getRealmNotificationSetting();
                        }
                        getRealm();
                    }
                }

                realm.close();

                break;
            }
        }
    }

    private void setRealm(Realm realm, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom, final RealmChatRoom realmChatRoom) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmNotificationSetting = RealmNotificationSetting.put(realm, realmChatRoom, realmGroupRoom, realmChannelRoom);
            }
        });
    }

    private void getRealm() {
        realmNotification = realmNotificationSetting.getNotification();
        realmVibrate = realmNotificationSetting.getVibrate();
        realmSound = realmNotificationSetting.getSound();
        realmIdSound = realmNotificationSetting.getIdRadioButtonSound();
        if (realmNotificationSetting.getLedColor() != -1) {
            realmLedColor = realmNotificationSetting.getLedColor();
        } else {
            realmLedColor = -8257792;
        }
    }

    public void destroy() {
        realm.close();
    }
}
