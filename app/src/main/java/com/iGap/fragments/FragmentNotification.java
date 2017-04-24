/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmChatRoom;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmNotificationSetting;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import io.realm.Realm;

import static com.iGap.R.id.ntg_txt_back;
import static com.iGap.R.string.DISCARD;
import static com.iGap.R.string.array_Default;
import static com.iGap.R.string.array_Disable;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotification extends Fragment {

    private static final int DEFAULT = 0;
    private static final int ENABLE = 1;
    private static final int DISABLE = 2;
    private ImageView imgLED;
    private ViewGroup root, ltLedColor, ltVibrate, ltSound, ltPopupNotification, ltSmartNotification;
    private String page, soundName;
    private int ledColor, poRbDialogSound;
    private TextView txtVibrate, txtSound, txtPopupNotification, txtSmartNotification;
    private NumberPicker numberPickerMinutes, numberPickerTimes;
    private MaterialDesignTextView txtBack;
    private long roomId;
    private int realmNotification = 0;
    private int realmVibrate;
    private String realmSound = "iGap";
    private int realmIdSound = 0;
    private String realmSmartNotification;
    private int realmMinutes;
    private int realmTimes;
    private int realmLedColor;
    private RealmNotificationSetting realmNotificationSetting;

    public FragmentNotification() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.toolbar2).setBackgroundColor(Color.parseColor(G.appBarColor));
        view.findViewById(R.id.fn_view_line).setBackgroundColor(Color.parseColor(G.appBarColor));

        page = getArguments().getString("PAGE");
        roomId = getArguments().getLong("ID");
        callObject(view);


        //=================================================Realm

        switch (page) {
            case "GROUP": {

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
            case "CHANNEL": {
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
            case "CONTACT": {

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
        //=================================================Realm

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getActivity().getSupportFragmentManager().popBackStack();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

        int popupNotification = realmNotification;
        switch (popupNotification) {
            case DEFAULT:
                txtPopupNotification.setText(getResources().getString(R.string.array_Default));
                break;
            case ENABLE:
                txtPopupNotification.setText(getResources().getString(R.string.array_enable));
                break;
            case DISABLE:
                txtPopupNotification.setText(getResources().getString(R.string.array_Disable));
                break;
        }

        ltPopupNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.st_popupNotification)).items(R.array.notifications_notification).negativeText(getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0: {
                                txtPopupNotification.setText(getResources().getString(R.string.array_Default));
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                                        if (realmRoom != null) {
                                            switch (page) {
                                                case "GROUP": {
                                                    if (realmRoom.getGroupRoom() != null) {
                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setNotification(0);
                                                    }
                                                    break;
                                                }
                                                case "CHANNEL": {
                                                    if (realmRoom.getChannelRoom() != null) {
                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setNotification(0);
                                                    }
                                                    break;
                                                }
                                                case "CONTACT": {
                                                    if (realmRoom.getChatRoom() != null) {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setNotification(0);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                                realm.close();
                                break;
                            }
                            case 1: {
                                txtPopupNotification.setText(getResources().getString(R.string.array_enable));
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                        if (realmRoom != null) {
                                            switch (page) {
                                                case "GROUP": {
                                                    if (realmRoom.getGroupRoom() != null) {

                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setNotification(1);
                                                    }
                                                    break;
                                                }
                                                case "CHANNEL": {
                                                    if (realmRoom.getChannelRoom() != null) {

                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setNotification(1);
                                                    }
                                                    break;
                                                }
                                                case "CONTACT": {
                                                    if (realmRoom.getChatRoom() != null) {
                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setNotification(1);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                                realm.close();
                                break;
                            }
                            case 2: {
                                txtPopupNotification.setText(getResources().getString(R.string.array_Disable));
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                        if (realmRoom != null) {
                                            switch (page) {
                                                case "GROUP": {
                                                    if (realmRoom.getGroupRoom() != null) {

                                                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                        realmGroupRoom.getRealmNotificationSetting().setNotification(2);
                                                    }
                                                    break;
                                                }
                                                case "CHANNEL": {
                                                    if (realmRoom.getChannelRoom() != null) {

                                                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                        realmChannelRoom.getRealmNotificationSetting().setNotification(2);
                                                    }
                                                    break;
                                                }
                                                case "CONTACT": {
                                                    if (realmRoom.getChatRoom() != null) {

                                                        RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                        realmChatRoom.getRealmNotificationSetting().setNotification(2);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                });
                                realm.close();
                                break;
                            }
                        }
                    }
                }).show();
            }
        });

        //========================================================sound
        if (realmIdSound == 0 || realmIdSound == -1) {
            txtSound.setText(getResources().getString(R.string.array_Default_Notification_tone));
        } else {
            txtSound.setText(realmSound);
        }
        ltSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).titleColor(getResources().getColor(android.R.color.black)).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(realmIdSound, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, final int which, final CharSequence text) {

                        switch (which) {
                            case 0:
                                MediaPlayer.create(getActivity(), R.raw.igap).start();
                                break;
                            case 1:
                                MediaPlayer.create(getActivity(), R.raw.aooow).start();
                                break;
                            case 2:
                                MediaPlayer.create(getActivity(), R.raw.bbalert).start();
                                break;
                            case 3:
                                MediaPlayer.create(getActivity(), R.raw.boom).start();
                                break;
                            case 4:
                                MediaPlayer.create(getActivity(), R.raw.bounce).start();
                                break;
                            case 5:
                                MediaPlayer.create(getActivity(), R.raw.doodoo).start();
                                break;

                            case 6:
                                MediaPlayer.create(getActivity(), R.raw.jing).start();
                                break;
                            case 7:
                                MediaPlayer.create(getActivity(), R.raw.lili).start();
                                break;
                            case 8:
                                MediaPlayer.create(getActivity(), R.raw.msg).start();
                                break;
                            case 9:
                                MediaPlayer.create(getActivity(), R.raw.newa).start();
                                break;
                            case 10:
                                MediaPlayer.create(getActivity(), R.raw.none).start();
                                break;
                            case 11:
                                MediaPlayer.create(getActivity(), R.raw.onelime).start();
                                break;
                            case 12:
                                MediaPlayer.create(getActivity(), R.raw.tone).start();
                                break;
                            case 13:
                                MediaPlayer.create(getActivity(), R.raw.woow).start();
                                break;
                        }

                        txtSound.setText(text.toString());
                        realmIdSound = which;
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                if (realmRoom != null) {
                                    switch (page) {
                                        case "GROUP": {
                                            if (realmRoom.getGroupRoom() != null) {
                                                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                realmGroupRoom.getRealmNotificationSetting().setSound(text.toString());
                                                realmGroupRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                                            }
                                            break;
                                        }
                                        case "CHANNEL": {
                                            if (realmRoom.getChannelRoom() != null) {
                                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                realmChannelRoom.getRealmNotificationSetting().setSound(text.toString());
                                                realmChannelRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                                            }
                                            break;
                                        }
                                        case "CONTACT": {
                                            if (realmRoom.getChatRoom() != null) {
                                                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                realmChatRoom.getRealmNotificationSetting().setSound(text.toString());
                                                realmChatRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        realm.close();
                        return true;
                    }
                }).positiveText(getResources().getString(R.string.B_ok)).negativeText(getResources().getString(R.string.B_cancel)).show();
            }
        });

        //========================================================= vibrate

        switch (realmVibrate) {
            case 0:
                txtVibrate.setText(getResources().getString(array_Disable));
                break;
            case 1:
                txtVibrate.setText(getResources().getString(array_Default));
                break;
            case 2:
                txtVibrate.setText(getResources().getString(R.string.array_Short));
                break;
            case 3:
                txtVibrate.setText(getResources().getString(R.string.array_Long));
                break;
            case 4:
                txtVibrate.setText(getResources().getString(R.string.array_Only_if_silent));
                break;
        }

        ltVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(getActivity()).title(getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {

                        txtVibrate.setText(text.toString());

                        switch (which) {
                            case 0:
                                Vibrator vDisable = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vDisable.vibrate(0);
                                break;
                            case 1:

                                Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vDefault.vibrate(350);

                                break;
                            case 2:
                                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vShort.vibrate(200);
                                break;
                            case 3:
                                Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vLong.vibrate(500);
                                break;
                            case 4:
                                AudioManager am2 = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

                                switch (am2.getRingerMode()) {
                                    case AudioManager.RINGER_MODE_SILENT:
                                        Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                }
                                break;
                        }

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                                if (realmRoom != null) {
                                    switch (page) {
                                        case "GROUP": {
                                            if (realmRoom.getGroupRoom() != null) {

                                                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                realmGroupRoom.getRealmNotificationSetting().setVibrate(which);
                                            }
                                            break;
                                        }
                                        case "CHANNEL": {
                                            if (realmRoom.getChannelRoom() != null) {

                                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                realmChannelRoom.getRealmNotificationSetting().setVibrate(which);
                                            }
                                            break;
                                        }
                                        case "CONTACT": {
                                            if (realmRoom.getChatRoom() != null) {
                                                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                realmChatRoom.getRealmNotificationSetting().setVibrate(which);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        realm.close();
                    }
                }).show();
            }
        });

        //==========================================================number pick

        txtSmartNotification.setText("Sound at must " + realmTimes + " times within " + realmMinutes + " minutes");
        ltSmartNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title("Smart Notifications").customView(R.layout.dialog_number_picker, wrapInScrollView).positiveText(R.string.B_ok).negativeText(R.string.B_cancel).build();

                View view1 = dialog.getCustomView();

                assert view1 != null;
                numberPickerMinutes = (NumberPicker) view1.findViewById(R.id.dialog_np_minutes);
                numberPickerTimes = (NumberPicker) view1.findViewById(R.id.dialog_np_times);
                numberPickerMinutes.setMinValue(0);
                numberPickerMinutes.setMaxValue(10);
                numberPickerTimes.setMinValue(0);
                numberPickerTimes.setMaxValue(10);
                numberPickerMinutes.setWrapSelectorWheel(true);

                numberPickerMinutes.setValue(realmMinutes);
                numberPickerTimes.setValue(realmTimes);

                numberPickerTimes.setWrapSelectorWheel(true);

                View btnPositive = dialog.getActionButton(DialogAction.POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        txtSmartNotification.setText("Sound at must " + numberPickerTimes.getValue() + " times within " + numberPickerMinutes.getValue() + " minutes");
                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                if (realmRoom != null) {
                                    switch (page) {
                                        case "GROUP": {
                                            if (realmRoom.getGroupRoom() != null) {

                                                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                realmGroupRoom.getRealmNotificationSetting().setMinutes(numberPickerMinutes.getValue());
                                                realmGroupRoom.getRealmNotificationSetting().setTimes(numberPickerTimes.getValue());
                                            }
                                            break;
                                        }
                                        case "CHANNEL": {
                                            if (realmRoom.getChannelRoom() != null) {

                                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                realmChannelRoom.getRealmNotificationSetting().setMinutes(numberPickerMinutes.getValue());
                                                realmChannelRoom.getRealmNotificationSetting().setTimes(numberPickerTimes.getValue());
                                            }

                                            break;
                                        }
                                        case "CONTACT": {
                                            if (realmRoom.getChatRoom() != null) {

                                                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                realmChatRoom.getRealmNotificationSetting().setMinutes(numberPickerMinutes.getValue());
                                                realmChatRoom.getRealmNotificationSetting().setTimes(numberPickerTimes.getValue());
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        realm.close();

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        //=======================================================================led color

        GradientDrawable bgShape = (GradientDrawable) imgLED.getBackground();
        bgShape.setColor(realmLedColor);
        ltLedColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).customView(R.layout.stns_popup_colorpicer, wrapInScrollView).positiveText(getResources().getString(R.string.set)).negativeText(getResources().getString(DISCARD)).title(getResources().getString(R.string.st_led_color)).onNegative(new MaterialDialog.SingleButtonCallback() {
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
                        GradientDrawable bgShape = (GradientDrawable) imgLED.getBackground();
                        bgShape.setColor(picker.getColor());

                        Realm realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                if (realmRoom != null) {
                                    switch (page) {
                                        case "GROUP": {
                                            if (realmRoom.getGroupRoom() != null) {

                                                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                                                realmGroupRoom.getRealmNotificationSetting().setLedColor(picker.getColor());
                                            }
                                            break;
                                        }
                                        case "CHANNEL": {
                                            if (realmRoom.getChannelRoom() != null) {

                                                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                                                realmChannelRoom.getRealmNotificationSetting().setLedColor(picker.getColor());
                                            }
                                            break;
                                        }
                                        case "CONTACT": {
                                            if (realmRoom.getChatRoom() != null) {
                                                RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                                                realmChatRoom.getRealmNotificationSetting().setLedColor(picker.getColor());
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        });
                        realm.close();
                    }
                });

                dialog.show();
            }
        });
    }

    private void setRealm(Realm realm, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom, final RealmChatRoom realmChatRoom) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmNotificationSetting = realm.createObject(RealmNotificationSetting.class);

                realmNotificationSetting.setNotification(0);
                realmNotificationSetting.setVibrate(-1);
                realmNotificationSetting.setSound(getResources().getString(R.string.array_Default_Notification_tone));
                realmNotificationSetting.setIdRadioButtonSound(-1);
                realmNotificationSetting.setSmartNotification(getResources().getString(R.string.array_Default));
                realmNotificationSetting.setTimes(-1);
                realmNotificationSetting.setMinutes(-1);
                realmNotificationSetting.setLedColor(-1);

                if (realmGroupRoom != null) {
                    realmGroupRoom.setRealmNotificationSetting(realmNotificationSetting);
                }

                if (realmChannelRoom != null) {
                    realmChannelRoom.setRealmNotificationSetting(realmNotificationSetting);
                }

                if (realmChatRoom != null) {
                    realmChatRoom.setRealmNotificationSetting(realmNotificationSetting);
                }
            }
        });
    }

    private void getRealm() {

        realmNotification = realmNotificationSetting.getNotification();
        realmVibrate = realmNotificationSetting.getVibrate();
        realmSound = realmNotificationSetting.getSound();
        realmIdSound = realmNotificationSetting.getIdRadioButtonSound();
        realmSmartNotification = realmNotificationSetting.getSmartNotification();
        realmTimes = realmNotificationSetting.getTimes();
        realmMinutes = realmNotificationSetting.getMinutes();
        if (realmNotificationSetting.getLedColor() != -1) {
            realmLedColor = realmNotificationSetting.getLedColor();
        } else {
            realmLedColor = -8257792;
        }
    }

    private void callObject(View view) {

        txtBack = (MaterialDesignTextView) view.findViewById(ntg_txt_back);

        txtPopupNotification = (TextView) view.findViewById(R.id.ntg_txt_desc_notifications);
        ltPopupNotification = (ViewGroup) view.findViewById(R.id.ntg_layout_notifications);
        root = (ViewGroup) view.findViewById(R.id.ntg_fragment_root);

        imgLED = (ImageView) view.findViewById(R.id.ntg_img_ledColorMessage);
        ltLedColor = (ViewGroup) view.findViewById(R.id.ntg_layout_ledColorMessage);

        txtVibrate = (TextView) view.findViewById(R.id.ntg_txt_desc_vibrate);
        ltVibrate = (ViewGroup) view.findViewById(R.id.ntg_layout_vibrate);

        txtSmartNotification = (TextView) view.findViewById(R.id.ntg_txt_desc_smartNotifications);
        ltSmartNotification = (ViewGroup) view.findViewById(R.id.ntg_layout_smartNotifications);

        txtSound = (TextView) view.findViewById(R.id.ntg_txt_desc_sound);
        ltSound = (ViewGroup) view.findViewById(R.id.ntg_layout_sound);
    }
}
