package net.iGap.viewmodel;
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
import net.iGap.databinding.FragmentNotificationAndSoundBinding;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;

public class FragmentNotificationAndSoundViewModel {

    public FragmentNotificationAndSoundBinding fragmentNotificationAndSoundBinding;
    public int ledColorMessage;
    public int ledColorGroup;
    public ObservableField<Boolean> isAlertMassage = new ObservableField<>();
    public ObservableField<Boolean> isMassagePreview = new ObservableField<>();
    public ObservableField<String> callbackVibrateMessage = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.array_Default));
    public ObservableField<String> callbackPopUpNotificationMessage = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> callbackSoundMessage = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<Boolean> isAlertGroup = new ObservableField<>();
    public ObservableField<Boolean> isMessagePreViewGroup = new ObservableField<>();
    public ObservableField<String> callbackVibrateGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.array_Default));
    public ObservableField<String> callbackPopUpNotificationGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<String> callBackSoundGroup = new ObservableField<>(G.fragmentActivity.getResources().getString(R.string.st_sound));
    public ObservableField<Boolean> isAppSound = new ObservableField<>();
    public ObservableField<Boolean> isInAppVibration = new ObservableField<>();
    public ObservableField<Boolean> isInAppPreView = new ObservableField<>();
    public ObservableField<Boolean> isSoundInChat = new ObservableField<>();
    public ObservableField<Boolean> isKeepService = new ObservableField<>();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int vibrateMessage;
    private int mode;
    private int poRbDialogSoundMessage;
    private String soundMessage;
    private int vibrateGroup;
    private int modeGroup;
    private int poRbDialogSoundMessageGroup;
    private String soundMessageGroup;
    private String soundMessageSelected = "";
    private String soundMessageGroupSelected = "";
    private int soundMessageWhich = 0;
    private int soundMessageGroupWhich = 0;



    public FragmentNotificationAndSoundViewModel(FragmentNotificationAndSoundBinding fragmentNotificationAndSoundBinding) {
        this.fragmentNotificationAndSoundBinding = fragmentNotificationAndSoundBinding;
        getInfo();
        startLedColorMessage();
        startVibrateMessage();
        startPopupNotification();
        poRbDialogSoundMessage();
        startVibrateGroup();
        startPopupNotificationGroup();
        poRbDialogSoundGroup();

    }

    //===============================================================================
    //=====================================Starts====================================
    //===============================================================================

    private void startLedColorMessage() {


    }

    public void startVibrateMessage() {

        switch (vibrateMessage) {
            case 0:
                callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Default));
                break;
            case 1:
                callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Short));
                break;
            case 2:
                callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Long));
                break;
            case 3:
                callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                break;
            case 4:
                callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                break;
        }
    }

    private void startPopupNotification() {
        switch (mode) {
            case 0:
                callbackPopUpNotificationMessage.set(G.fragmentActivity.getResources().getString(R.string.array_No_popup));
                break;
            case 1:
                callbackPopUpNotificationMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_on));
                break;
            case 2:
                callbackPopUpNotificationMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_off));
                break;
            case 3:
                callbackPopUpNotificationMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Always_show_popup));
                break;
        }
    }

    private void poRbDialogSoundMessage() {
        if (poRbDialogSoundMessage == 0) {

            callbackSoundMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));

        } else {
            callbackSoundMessage.set(soundMessage);
        }
    }


    private void startVibrateGroup() {

        switch (vibrateGroup) {
            case 0:
                callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Default));
                break;
            case 1:
                callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Short));
                break;
            case 2:
                callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Long));
                break;
            case 3:
                callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                break;
            case 4:
                callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                break;
        }
    }

    private void startPopupNotificationGroup() {
        switch (modeGroup) {
            case 0:
                callbackPopUpNotificationGroup.set(G.fragmentActivity.getResources().getString(R.string.array_No_popup));
                break;
            case 1:
                callbackPopUpNotificationGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_on));
                break;
            case 2:
                callbackPopUpNotificationGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_off));
                break;
            case 3:
                callbackPopUpNotificationGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Always_show_popup));
                break;
        }
    }

    private void poRbDialogSoundGroup() {
        if (poRbDialogSoundMessage == 0) {

            callBackSoundGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));

        } else {
            callBackSoundGroup.set(soundMessageGroup);
        }
    }

    //===============================================================================
    //================================Getters/Setters================================
    //===============================================================================

    public void setAlertMassage(Boolean isChecked) {

        isAlertMassage.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 0);
            editor.apply();
        }

    }

    private void setMessagePreview(Boolean isChecked) {


        isMassagePreview.set(isChecked);

        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 0);
            editor.apply();
        }
    }

    private void setAlertGroup(Boolean isChecked) {

        isAlertGroup.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 0);
            editor.apply();
        }

    }

    private void setMessagePreviewGroup(Boolean isChecked) {

        isMessagePreViewGroup.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 0);
            editor.apply();
        }
    }

    private void setAppSound(Boolean isChecked) {

        isAppSound.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
            editor.apply();
        }

    }

    private void setInAppVibrate(Boolean isChecked) {
        isInAppVibration.set(isChecked);

        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
            editor.apply();
        }

    }

    private void setInAppPreView(Boolean isChecked) {

        isInAppPreView.set(isChecked);

        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
            editor.apply();
        }

    }

    private void setInSoundChat(Boolean isChecked) {
        isSoundInChat.set(isChecked);

        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);
            editor.apply();
        }

    }

    private void setKeepService(Boolean isChecked) {

        isKeepService.set(isChecked);
        if (isChecked) {
            editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
            editor.apply();
        } else {
            editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 0);
            editor.apply();
        }

    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================

    public void onClickAlertMessage(View view) {

        isAlertMassage.set(!isAlertMassage.get());
    }

    public void onCheckedChangedAlertMessage(boolean isChecked) {
        setAlertMassage(isChecked);
    }


    public void onClickMessagePreView(View view) {

        isMassagePreview.set(!isMassagePreview.get());
    }

    public void onCheckedChangedMassagePreview(boolean isChecked) {
        setMessagePreview(isChecked);
    }

    public void onClickLedColorMessage(View view) {

        boolean wrapInScrollView = true;
        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView).positiveText(G.fragmentActivity.getResources().getString(R.string.set)).negativeText(G.fragmentActivity.getResources().getString(R.string.DISCARD)).title(G.fragmentActivity.getResources().getString(R.string.st_led_color)).onNegative(new MaterialDialog.SingleButtonCallback() {
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
                GradientDrawable bgShape = (GradientDrawable) fragmentNotificationAndSoundBinding.stnsImgLedColorMessage.getBackground();
                bgShape.setColor(picker.getColor());
                editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, picker.getColor());
                editor.apply();
            }
        });

        dialog.show();


    }

    public void onClickVibrationMessage(View view) {


        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, which);
                editor.apply();

                switch (which) {
                    case 0:
                        callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Default));
                        Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                        vDefault.vibrate(350);
                        break;
                    case 1:
                        callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Short));
                        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                        vShort.vibrate(200);

                        break;
                    case 2:
                        callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Long));
                        Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                        vLong.vibrate(500);
                        break;
                    case 3:
                        callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                        AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);

                        switch (am2.getRingerMode()) {
                            case AudioManager.RINGER_MODE_SILENT:
                                Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                break;
                        }
                        break;
                    case 4:
                        callbackVibrateMessage.set(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                        break;
                }
            }
        }).show();

    }

    public void onClickPopUpNotificationMessage(View view) {

        int po = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.popup_Notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                callbackPopUpNotificationMessage.set(text.toString());
                editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, which);
                editor.apply();
                return false;
            }
        }).show();
    }

    public void onClickSoundMessage(View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(poRbDialogSoundMessage, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                playSound(which);

                soundMessageSelected = text.toString();
                soundMessageWhich = which;

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                callbackSoundMessage.set(soundMessageSelected);
                poRbDialogSoundMessage = soundMessageWhich;
                editor.putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, soundMessageWhich);
                editor.putString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, soundMessageSelected);
                editor.apply();

            }
        }).show();


    }

    public void onClickAlertGroup(View view) {
        isAlertGroup.set(!isAlertGroup.get());
    }

    public void onCheckedChangedAlertGroup(boolean isChecked) {
        setAlertGroup(isChecked);
    }

    public void onClickMessagePreViewGroup(View view) {

        isMessagePreViewGroup.set(!isMessagePreViewGroup.get());

    }

    public void onCheckedChangedMessagePreViewGroup(boolean isChecked) {
        setMessagePreviewGroup(isChecked);
    }

    public void onClickLedGroup(View view) {

        boolean wrapInScrollView = true;
        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView).positiveText(G.fragmentActivity.getResources().getString(R.string.set)).negativeText(G.fragmentActivity.getResources().getString(R.string.DISCARD)).title(G.fragmentActivity.getResources().getString(R.string.st_led_color)).onNegative(new MaterialDialog.SingleButtonCallback() {
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
        picker.setOldCenterColor(ledColorGroup);
        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                GradientDrawable bgShapeGroup = (GradientDrawable) fragmentNotificationAndSoundBinding.stnsImgLedColorGroup.getBackground();
                bgShapeGroup.setColor(picker.getColor());
                editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, picker.getColor());
                editor.apply();
            }
        });

        dialog.show();

    }

    public void onClickVibrationGroup(View view) {
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
            @Override
            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, which);
                editor.apply();
                switch (which) {
                    case 0:
                        callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Default));
                        Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                        vDefault.vibrate(350);
                        break;
                    case 1:
                        callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Short));
                        Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                        vShort.vibrate(200);

                        break;
                    case 2:
                        callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Long));
                        Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                        vLong.vibrate(500);
                        break;
                    case 3:
                        callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                        AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);

                        switch (am2.getRingerMode()) {
                            case AudioManager.RINGER_MODE_SILENT:
                                Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                break;
                        }
                        break;
                    case 4:
                        callbackVibrateGroup.set(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                        break;
                }
            }
        }).show();
    }

    public void onClickPopUpNotificationGroup(View view) {

        int po = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.popup_Notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                callbackPopUpNotificationGroup.set(text.toString());
                editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, which);
                editor.apply();
                return false;
            }
        }).show();

    }

    public void onClickSoundGroup(View view) {

        new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(poRbDialogSoundMessageGroup, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                playSound(which);

                soundMessageGroupSelected = text.toString();
                soundMessageGroupWhich = which;

                return true;
            }
        }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                callBackSoundGroup.set(soundMessageGroupSelected);
                poRbDialogSoundMessageGroup = soundMessageGroupWhich;

                editor.putString(SHP_SETTING.KEY_STNS_SOUND_GROUP, soundMessageGroupSelected);
                editor.putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, soundMessageGroupWhich);
                editor.apply();

            }
        }).show();
    }

    public void onClickInAppSound(View view) {
        isAppSound.set(!isAppSound.get());
    }

    public void onCheckedChangedAppSound(boolean isChecked) {
        setAppSound(isChecked);
    }

    public void onClickInAppVibration(View view) {
        isInAppVibration.set(!isInAppVibration.get());
    }

    public void onCheckedChangedInAppVibration(boolean isChecked) {
        setInAppVibrate(isChecked);
    }

    public void onClickInAppPreView(View view) {
        isInAppPreView.set(!isInAppPreView.get());
    }

    public void onCheckedChangedInAppPreView(boolean isChecked) {

        setInAppPreView(isChecked);
    }

    public void onClickSoundInChat(View view) {

        isSoundInChat.set(!isSoundInChat.get());
    }

    public void onCheckedChangedSoundInChat(boolean isChecked) {
        setInSoundChat(isChecked);
    }

    public void onClickKeepService(View view) {

        isKeepService.set(!isKeepService.get());
    }

    public void onCheckedChangedKeepService(boolean isChecked) {
        setKeepService(isChecked);
    }

    //===============================================================================
    //====================================Methods====================================
    //===============================================================================

    private boolean getBoolean(int num) {
        if (num == 0) {
            return false;
        }
        return true;
    }

    private void getInfo() {
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        isAlertMassage.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1)));
        isMassagePreview.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1)));

        ledColorMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
        vibrateMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 0);
        mode = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
        poRbDialogSoundMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
        soundMessage = sharedPreferences.getString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));

        isAlertGroup.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1)));
        isMessagePreViewGroup.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1)));

        ledColorGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
        vibrateGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0);
        modeGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
        poRbDialogSoundMessageGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
        soundMessageGroup = sharedPreferences.getString(SHP_SETTING.KEY_STNS_SOUND_GROUP, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));

        isAppSound.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0)));
        isInAppVibration.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0)));
        isInAppPreView.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0)));

        isSoundInChat.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0)));
        isKeepService.set(getBoolean(sharedPreferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1)));

    }

    private void playSound(int which) {

        int musicId = R.raw.igap;

        switch (which) {
            case 0:
                musicId = R.raw.igap;
                break;
            case 1:
                musicId = R.raw.aooow;
                break;
            case 2:
                musicId = R.raw.bbalert;
                break;
            case 3:
                musicId = R.raw.boom;
                break;
            case 4:
                musicId = R.raw.bounce;
                break;
            case 5:
                musicId = R.raw.doodoo;
                break;
            case 6:
                musicId = R.raw.jing;
                break;
            case 7:
                musicId = R.raw.lili;
                break;
            case 8:
                musicId = R.raw.msg;
                break;
            case 9:
                musicId = R.raw.newa;
                break;
            case 10:
                musicId = R.raw.none;
                break;
            case 11:
                musicId = R.raw.onelime;
                break;
            case 12:
                musicId = R.raw.tone;
                break;
            case 13:
                musicId = R.raw.woow;
                break;
        }


        MediaPlayer mediaPlayer = MediaPlayer.create(G.fragmentActivity, musicId);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

            ;
        });

    }

}
