package net.iGap.fragments;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.MyService;
import net.iGap.helper.MyServiceTemporat;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotificationAndSound extends BaseFragment {
    private TextView txtVibrateMessage;
    private TextView txtPopupNotification;
    private TextView txtVibrateGroup;
    private TextView txtPopupNotificationGroup;
    private TextView txtSoundGroup;
    private TextView txtSoundMessage;
    private TextView txtRepeat_Notifications;
    private ImageView imgLedMessage, imgLedColor_group;

    private int poRbDialogSoundGroup = -1;
    private int poRbDialogSoundMessage = -1;
    private ToggleButton tgAlert, tgMessagePreview, tgAlert_group, tgMessagePreview_group, tgApp_sound, tgApp_Vibrate, tgApp_preview, tgChat_sound, tgContact_joined, tgPinned_message, tgKeep_alive_service, tgBackground_connection, tgBadge_content;

    private SharedPreferences sharedPreferences;


    public FragmentNotificationAndSound() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_notification_and_sound, container, false));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        TextView txtBack = (TextView) view.findViewById(R.id.stns_txt_back);

        view.findViewById(R.id.asn_toolbar).setBackgroundColor(Color.parseColor(G.appBarColor));

        RippleView rippleBack = (RippleView) view.findViewById(R.id.stns_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                //finish();
                // G.fragmentActivity.getSupportFragmentManager().popBackStack();

                popBackStackFragment();
            }
        });

        //=============================================================================Message

        tgAlert = (ToggleButton) view.findViewById(R.id.stns_toggle_alert);
        int alert_message = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
        if (alert_message == 1) {
            tgAlert.setChecked(true);
        } else {
            tgAlert.setChecked(false);
        }

        TextView ltAlert = (TextView) view.findViewById(R.id.stns_txt_alert);

        tgAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 0);
                    editor.apply();
                }
            }
        });

        ltAlert.setOnClickListener(new View.OnClickListener() { // alert 1
            @Override
            public void onClick(View view) {

                tgAlert.setChecked(!tgAlert.isChecked());
            }
        });

        tgMessagePreview = (ToggleButton) view.findViewById(R.id.stns_toggle_messagePreview);
        int preview_message = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
        if (preview_message == 1) {
            tgMessagePreview.setChecked(true);
        } else {
            tgMessagePreview.setChecked(false);
        }
        TextView ltMessagePreview = (TextView) view.findViewById(R.id.stns_txt_messagePreview);

        tgMessagePreview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 0);
                    editor.apply();
                }
            }
        });

        ltMessagePreview.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                tgMessagePreview.setChecked(!tgMessagePreview.isChecked());
            }
        });
        int ledColorMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
        imgLedMessage = (ImageView) view.findViewById(R.id.stns_img_ledColorMessage);
        GradientDrawable bgShape = (GradientDrawable) imgLedMessage.getBackground();
        bgShape.setColor(ledColorMessage);
        ViewGroup ltLedColorMessage = (ViewGroup) view.findViewById(R.id.stns_layout_ledColorMessage);
        ltLedColorMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView)
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.set))
                        .negativeText(G.fragmentActivity.getResources().getString(R.string.DISCARD))
                        .title(G.fragmentActivity.getResources().getString(R.string.st_led_color))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
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
                        GradientDrawable bgShape = (GradientDrawable) imgLedMessage.getBackground();
                        bgShape.setColor(picker.getColor());
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, picker.getColor());
                        editor.apply();
                    }
                });

                dialog.show();
            }
        });

        txtVibrateMessage = (TextView) view.findViewById(R.id.stns_txt_vibrate_message_text);
        int vibrateMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
        switch (vibrateMessage) {
            case 0:
                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                break;
            case 1:
                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Default));
                break;
            case 2:
                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Short));
                break;
            case 3:
                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Long));
                break;
            case 4:
                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                break;
        }

        ViewGroup ltVibrate_message = (ViewGroup) view.findViewById(R.id.stns_layout_vibrate_message);
        ltVibrate_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, which);
                        editor.apply();
                        switch (which) {
                            case 0:
                                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Default));
                                Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vDefault.vibrate(350);
                                break;
                            case 1:
                                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Short));
                                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vShort.vibrate(200);

                                break;
                            case 2:
                                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Long));
                                Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vLong.vibrate(500);
                                break;
                            case 3:
                                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                                AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);

                                switch (am2.getRingerMode()) {
                                    case AudioManager.RINGER_MODE_SILENT:
                                        Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                        break;
                                }
                                break;
                            case 4:

                                txtVibrateMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Disable));

                                break;

                        }
                    }
                }).show();
            }
        });

        txtPopupNotification = (TextView) view.findViewById(R.id.stns_txt_popupNotification_message_text);

        int mode = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);

        switch (mode) {
            case 0:
                txtPopupNotification.setText(G.fragmentActivity.getResources().getString(R.string.array_No_popup));
                break;
            case 1:
                txtPopupNotification.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_on));
                break;
            case 2:
                txtPopupNotification.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_off));
                break;
            case 3:
                txtPopupNotification.setText(G.fragmentActivity.getResources().getString(R.string.array_Always_show_popup));
                break;
        }

        ViewGroup ltPopupNotification = (ViewGroup) view.findViewById(R.id.stns_layout_popupNotification_message);
        ltPopupNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int po = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.popup_Notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        txtPopupNotification.setText(text.toString());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, which);
                        editor.apply();
                        return false;
                    }
                }).show();
            }
        });

        poRbDialogSoundMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
        txtSoundMessage = (TextView) view.findViewById(R.id.stns_txt_sound_text);
        String soundMessage = sharedPreferences.getString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
        if (poRbDialogSoundMessage == 0) {
            txtSoundMessage.setText(G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
        } else {
            txtSoundMessage.setText(soundMessage);
        }
        ViewGroup ltSoundMessage = (ViewGroup) view.findViewById(R.id.stns_layout_sound_message);

        ltSoundMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(poRbDialogSoundMessage, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

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

                        txtSoundMessage.setText(text.toString());
                        poRbDialogSoundMessage = which;

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SHP_SETTING.KEY_STNS_SOUND_MESSAGE, text.toString());
                        editor.putInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, which);
                        editor.apply();

                        return true;
                    }
                }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
            }
        });

        //=============================================================================Group

        tgAlert_group = (ToggleButton) view.findViewById(R.id.stns_toggle_alert_group);
        int alert_group = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
        if (alert_group == 1) {
            tgAlert_group.setChecked(true);
        } else {
            tgAlert_group.setChecked(false);
        }
        TextView ltAlert_group = (TextView) view.findViewById(R.id.stns_txt_alert_group);
        ltAlert_group.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgAlert_group.isChecked()) {
                    tgAlert_group.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 0);
                    editor.apply();
                } else {
                    tgAlert_group.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
                    editor.apply();
                }
            }
        });

        tgMessagePreview_group = (ToggleButton) view.findViewById(R.id.stns_toggle_messagePreview_group);
        int preview_group = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
        if (preview_group == 1) {
            tgMessagePreview_group.setChecked(true);
        } else {
            tgMessagePreview_group.setChecked(false);
        }
        TextView ltMessagePreview_group = (TextView) view.findViewById(R.id.stns_txt_messagePreview_group);
        ltMessagePreview_group.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgMessagePreview_group.isChecked()) {
                    tgMessagePreview_group.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 0);
                    editor.apply();
                } else {
                    tgMessagePreview_group.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                    editor.apply();
                }
            }
        });

        final int ledColorGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
        imgLedColor_group = (ImageView) view.findViewById(R.id.stns_img_ledColor_group);

        GradientDrawable bgShapeGroup = (GradientDrawable) imgLedColor_group.getBackground();
        bgShapeGroup.setColor(ledColorGroup);

        ViewGroup ltLedColor_group = (ViewGroup) view.findViewById(R.id.stns_layout_ledColor_group);
        ltLedColor_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SharedPreferences.Editor editor = sharedPreferences.edit();

                boolean wrapInScrollView = true;
                final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView)
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.set))
                        .negativeText(G.fragmentActivity.getResources().getString(R.string.DISCARD))
                        .title(G.fragmentActivity.getResources().getString(R.string.st_led_color))
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
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
                        GradientDrawable bgShapeGroup = (GradientDrawable) imgLedColor_group.getBackground();
                        bgShapeGroup.setColor(picker.getColor());
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, picker.getColor());
                        editor.apply();
                    }
                });

                dialog.show();
            }
        });

        txtVibrateGroup = (TextView) view.findViewById(R.id.stns_txt_vibrate_group_text);
        int vibrateGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 1);
        switch (vibrateGroup) {
            case 0:
                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Disable));
                break;
            case 1:
                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Default));
                break;
            case 2:
                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Short));
                break;
            case 3:
                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Long));
                break;
            case 4:
                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                break;
        }
        ViewGroup ltVibrateGroup = (ViewGroup) view.findViewById(R.id.stns_layout_vibrate_group);
        ltVibrateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_vibrate)).items(R.array.vibrate).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, which);
                        editor.apply();
                        switch (which) {
                            case 0:
                                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Default));
                                Vibrator vDefault = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vDefault.vibrate(350);
                                break;
                            case 1:
                                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Short));
                                Vibrator vShort = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vShort.vibrate(200);

                                break;
                            case 2:
                                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Long));
                                Vibrator vLong = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                vLong.vibrate(500);
                                break;
                            case 3:
                                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_if_silent));
                                AudioManager am2 = (AudioManager) G.fragmentActivity.getSystemService(Context.AUDIO_SERVICE);

                                switch (am2.getRingerMode()) {
                                    case AudioManager.RINGER_MODE_SILENT:
                                        Vibrator vSilent = (Vibrator) G.context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vSilent.vibrate(AudioManager.VIBRATE_SETTING_ONLY_SILENT);
                                        break;
                                }
                                break;
                            case 4:

                                txtVibrateGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Disable));

                                break;
                        }
                    }
                }).show();
            }
        });
        txtPopupNotificationGroup = (TextView) view.findViewById(R.id.stns_txt_popupNotification_group_text);
        int modeGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
        switch (modeGroup) {
            case 0:
                txtPopupNotificationGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_No_popup));
                break;
            case 1:
                txtPopupNotificationGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_on));
                break;
            case 2:
                txtPopupNotificationGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Only_when_screen_off));
                break;
            case 3:
                txtPopupNotificationGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Always_show_popup));
                break;
        }

        ViewGroup ltPopupNotificationGroup = (ViewGroup) view.findViewById(R.id.stns_layout_popupNotification_group);
        ltPopupNotificationGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int po = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_popupNotification)).items(R.array.popup_Notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(po, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        txtPopupNotificationGroup.setText(text.toString());
                        editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, which);
                        editor.apply();
                        return false;
                    }
                }).show();
            }
        });

        poRbDialogSoundGroup = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
        txtSoundGroup = (TextView) view.findViewById(R.id.stns_txt_sound_group_text);
        String soundGroup = sharedPreferences.getString(SHP_SETTING.KEY_STNS_SOUND_GROUP, G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
        if (poRbDialogSoundGroup == 0) {
            txtSoundGroup.setText(G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
        } else {
            txtSoundGroup.setText(soundGroup);
        }

        ViewGroup ltSoundGroup = (ViewGroup) view.findViewById(R.id.stns_layout_sound_group);
        ltSoundGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.Ringtone)).titleGravity(GravityEnum.START).titleColor(G.context.getResources().getColor(android.R.color.black)).items(R.array.sound_message).alwaysCallSingleChoiceCallback().itemsCallbackSingleChoice(poRbDialogSoundGroup, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
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

                        txtSoundGroup.setText(text.toString());
                        poRbDialogSoundGroup = which;

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(SHP_SETTING.KEY_STNS_SOUND_GROUP, text.toString());
                        editor.putInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, which);
                        editor.apply();
                        return true;
                    }
                }).positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).show();
            }
        });

        //============================================================================= In App Notification

        tgApp_sound = (ToggleButton) view.findViewById(R.id.stns_toggle_app_sound);
        int appSound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
        if (appSound == 1) {
            tgApp_sound.setChecked(true);
        } else {
            tgApp_sound.setChecked(false);
        }
        TextView ltApp_sound = (TextView) view.findViewById(R.id.stns_txt_app_sound);

        tgApp_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
                    editor.apply();
                }
            }
        });

        ltApp_sound.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                tgApp_sound.setChecked(!tgApp_sound.isChecked());
            }
        });

        tgApp_Vibrate = (ToggleButton) view.findViewById(R.id.stns_toggle_app_vibrate);
        int appVibrate = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
        if (appVibrate == 1) {
            tgApp_Vibrate.setChecked(true);
        } else {
            tgApp_Vibrate.setChecked(false);
        }
        TextView ltApp_Vibrate = (TextView) view.findViewById(R.id.stns_txt_app_vibrate);

        tgApp_Vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
                    editor.apply();
                }
            }
        });

        ltApp_Vibrate.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                tgApp_Vibrate.setChecked(!tgApp_Vibrate.isChecked());
            }
        });

        tgApp_preview = (ToggleButton) view.findViewById(R.id.stns_toggle_app_preview);
        int appPreview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
        if (appPreview == 1) {
            tgApp_preview.setChecked(true);
        } else {
            tgApp_preview.setChecked(false);
        }

        tgApp_preview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
                    editor.apply();
                }
            }
        });

        TextView ltApp_preview = (TextView) view.findViewById(R.id.stns_txt_app_preview);
        ltApp_preview.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {
                tgApp_preview.setChecked(!tgApp_preview.isChecked());
            }
        });

        tgChat_sound = (ToggleButton) view.findViewById(R.id.stns_toggle_chat_sound);
        int chat_cound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);
        if (chat_cound == 1) {
            tgChat_sound.setChecked(true);
        } else {
            tgChat_sound.setChecked(false);
        }
        TextView ltChat_sound = (TextView) view.findViewById(R.id.stns_txt_chat_sound);

        tgChat_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 1);
                    editor.apply();
                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);
                    editor.apply();
                }
            }
        });

        ltChat_sound.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                tgChat_sound.setChecked(!tgChat_sound.isChecked());
            }
        });

        //============================================================================= In chat sound

        tgContact_joined = (ToggleButton) view.findViewById(R.id.stns_toggle_Contact_joined);
        int contact_joined = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
        if (contact_joined == 1) {
            tgContact_joined.setChecked(true);
        } else {
            tgContact_joined.setChecked(false);
        }
        TextView ltContact_joined = (TextView) view.findViewById(R.id.stns_txt_Contact_joined);
        ltContact_joined.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgContact_joined.isChecked()) {
                    tgContact_joined.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 0);
                    editor.apply();
                } else {
                    tgContact_joined.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
                    editor.apply();
                }
            }
        });

        tgPinned_message = (ToggleButton) view.findViewById(R.id.stns_toggle_pinned_message);
        int pinnedMessage = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
        if (pinnedMessage == 1) {
            tgPinned_message.setChecked(true);
        } else {
            tgPinned_message.setChecked(false);
        }
        TextView ltPinned_message = (TextView) view.findViewById(R.id.stns_txt_pinned_message);
        ltPinned_message.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgPinned_message.isChecked()) {
                    tgPinned_message.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 0);
                    editor.apply();
                } else {
                    tgPinned_message.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
                    editor.apply();
                }
            }
        });

        //============================================================================= Other

        tgKeep_alive_service = (ToggleButton) view.findViewById(R.id.stns_toggle_keep_alive_service);
        int keep_alive_service = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
        if (keep_alive_service == 1) {
            tgKeep_alive_service.setChecked(true);
        } else {
            tgKeep_alive_service.setChecked(false);
        }
        TextView ltKeep_alive_service = (TextView) view.findViewById(R.id.stns_txt_keep_alive_service);

        tgKeep_alive_service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (isChecked) {
                    editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
                    editor.apply();

                    G.fragmentActivity.startService(new Intent(G.fragmentActivity, MyServiceTemporat.class));

                } else {
                    editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 0);
                    editor.apply();

                    Intent intentService = new Intent(G.context, MyService.class);
                    intentService.putExtra("ACTION", MyService.STOPFOREGROUND_ACTION);
                    G.fragmentActivity.startService(intentService);

                }
            }
        });

        ltKeep_alive_service.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                if (tgKeep_alive_service.isChecked()) {
                    tgKeep_alive_service.setChecked(false);
                } else {
                    tgKeep_alive_service.setChecked(true);
                }
            }
        });

        tgBackground_connection = (ToggleButton) view.findViewById(R.id.stns_toggle_background_connection);
        int background_connection = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
        if (background_connection == 1) {
            tgBackground_connection.setChecked(true);
        } else {
            tgBackground_connection.setChecked(false);
        }
        TextView ltBackground_connection = (TextView) view.findViewById(R.id.stns_txt_background_connection);
        ltBackground_connection.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgBackground_connection.isChecked()) {
                    tgBackground_connection.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 0);
                    editor.apply();
                } else {
                    tgBackground_connection.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
                    editor.apply();
                }
            }
        });
        tgBadge_content = (ToggleButton) view.findViewById(R.id.stns_badge_counter);
        int badge_content = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
        if (badge_content == 1) {
            tgBadge_content.setChecked(true);
        } else {
            tgBadge_content.setChecked(false);
        }
        TextView ltBadge_content = (TextView) view.findViewById(R.id.stns_txt_badge_countent);
        ltBadge_content.setOnClickListener(new View.OnClickListener() { // 2
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if (tgBadge_content.isChecked()) {
                    tgBadge_content.setChecked(false);
                    editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 0);
                    editor.apply();
                } else {
                    tgBadge_content.setChecked(true);
                    editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
                    editor.apply();
                }
            }
        });

        txtRepeat_Notifications = (TextView) view.findViewById(R.id.st_txt_Repeat_Notifications);
        String repeat_Notifications = sharedPreferences.getString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_Off));
        txtRepeat_Notifications.setText(repeat_Notifications);
        ViewGroup ltRepeat_Notifications = (ViewGroup) view.findViewById(R.id.st_layout_Repeat_Notifications);
        ltRepeat_Notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(G.fragmentActivity).title(G.fragmentActivity.getResources().getString(R.string.st_Repeat_Notifications)).items(R.array.repeat_notification).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        switch (which) {
                            case 0:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_Off));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_Off));
                                editor.apply();
                                break;
                            case 1:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_5_minutes));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_5_minutes));
                                editor.apply();
                                break;
                            case 2:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_10_minutes));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_10_minutes));
                                editor.apply();
                                break;
                            case 3:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_30_minutes));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_30_minutes));
                                editor.apply();
                                break;
                            case 4:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_1_hour));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_1_hour));
                                editor.apply();
                                break;
                            case 5:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_2_hour));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_2_hour));
                                editor.apply();
                                break;
                            case 6:
                                txtRepeat_Notifications.setText(G.fragmentActivity.getResources().getString(R.string.array_4_hour));
                                editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_4_hour));
                                editor.apply();
                                break;
                        }
                    }
                }).show();
            }
        });

        //============================================================================= reset

        ViewGroup ltReset_all_notification = (ViewGroup) view.findViewById(R.id.st_layout_reset_all_notification);
        ltReset_all_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MaterialDialog.Builder(G.fragmentActivity).title(R.string.st_title_reset).content(R.string.st_dialog_reset_all_notification).positiveText(R.string.st_dialog_reset_all_notification_yes).negativeText(R.string.st_dialog_reset_all_notification_no).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_ALERT_GROUP, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);
                        editor.putInt(SHP_SETTING.KEY_STNS_CONTACT_JOINED, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_PINNED_MESSAGE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_BACKGROUND_CONNECTION, 1);
                        editor.putInt(SHP_SETTING.KEY_STNS_BADGE_CONTENT, 1);
                        editor.putString(SHP_SETTING.KEY_STNS_REPEAT_NOTIFICATION, G.fragmentActivity.getResources().getString(R.string.array_1_hour));
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                        editor.putInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                        editor.apply();
                        Toast.makeText(G.fragmentActivity, G.fragmentActivity.getResources().getString(R.string.st_reset_all_notification), Toast.LENGTH_SHORT).show();

                        removeFromBaseFragment(FragmentNotificationAndSound.this);
                        new HelperFragment(new FragmentNotificationAndSound()).setReplace(false).load();

                    }
                }).show();
            }
        });
    }
}
