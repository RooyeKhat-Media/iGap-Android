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

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.databinding.FragmentThemColorCustomBinding;
import net.iGap.fragments.FragmentThemColorCustom;
import net.iGap.module.SHP_SETTING;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.viewmodel.FragmentThemColorViewModel.resetApp;


public class FragmentThemColorCustomViewModel {

    private SharedPreferences sharedPreferences;
    private FragmentThemColorCustom fragmentThemColorCustom;
    private FragmentThemColorCustomBinding fragmentThemColorCustomBinding;


    public FragmentThemColorCustomViewModel(FragmentThemColorCustom fragmentThemColorCustom, FragmentThemColorCustomBinding fragmentThemColorCustomBinding) {
        this.fragmentThemColorCustom = fragmentThemColorCustom;
        this.fragmentThemColorCustomBinding = fragmentThemColorCustomBinding;
        getInfo();
    }

    //===============================================================================
    //================================Event Listeners================================
    //===============================================================================


    public void onClickSetTheme(View view) {
        SharedPreferences.Editor editor;
        if (sharedPreferences == null) {
            SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            editor = sharedPreferences.edit();
        } else {
            editor = sharedPreferences.edit();
        }
        editor.putInt(SHP_SETTING.KEY_THEME_COLOR, Theme.CUSTOM);
        editor.putBoolean(SHP_SETTING.KEY_THEME_DARK, false);
        editor.apply();

        Theme.setThemeColor();
        resetApp();

    }

    public void onClickTitleBarColor(View view) {
        showSelectAppColorDialog(R.string.app_theme);
    }

    public void onClickNotificationColor(View view) {
        showSelectAppColorDialog(R.string.app_notif_color);
    }

    public void onClickToggleBottonColor(View view) {
        showSelectAppColorDialog(R.string.toggle_botton_color);
    }

    public void onClickSendAndAttachColor(View view) {
        showSelectAppColorDialog(R.string.send_and_attach_botton_color);
    }

    public void onClickDefaultHeaderFontColor(View view) {
        showSelectAppColorDialog(R.string.default_header_font_color);
    }

    public void onClickDefaultProgressColor(View view) {
        showSelectAppColorDialog(R.string.default_progress_color);
    }


    private void showSelectAppColorDialog(final int title) {

        boolean wrapInScrollView = true;

        String titleMessage = G.fragmentActivity.getResources().getString(title);

        final MaterialDialog dialog = new MaterialDialog.Builder(G.fragmentActivity).customView(R.layout.stns_popup_colorpicer, wrapInScrollView).positiveText(G.fragmentActivity.getResources().getString(R.string.set)).negativeText(G.fragmentActivity.getResources().getString(R.string.DISCARD)).title(titleMessage).onNegative(new MaterialDialog.SingleButtonCallback() {
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

                try {

                    String _strColor = "#" + Integer.toHexString(picker.getColor());
                    int _color = Color.parseColor(_strColor); // if can not parae selected color do not set selected color

                    SharedPreferences.Editor editor;
                    if (sharedPreferences == null) {
                        SharedPreferences sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                    } else {
                        editor = sharedPreferences.edit();
                    }

                    switch (title) {

                        case R.string.app_theme:

                            G.appBarColor = "#" + Integer.toHexString(picker.getColor());
                            editor.putString(SHP_SETTING.KEY_APP_BAR_COLOR, G.appBarColor);
                            editor.apply();
                            appBarColorClick(0);


                            break;
                        case R.string.app_notif_color:

                            G.notificationColor = "#" + Integer.toHexString(picker.getColor());
                            editor.putString(SHP_SETTING.KEY_NOTIFICATION_COLOR, G.notificationColor);
                            editor.apply();
                            notificationColorClick(0, true);

                            break;
                        case R.string.toggle_botton_color:

                            G.toggleButtonColor = "#" + Integer.toHexString(picker.getColor());
                            editor.putString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, G.toggleButtonColor);
                            editor.apply();
                            toggleBottomClick(0);

                            break;
                        case R.string.send_and_attach_botton_color:

                            G.attachmentColor = "#" + Integer.toHexString(picker.getColor());
                            editor.putString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, G.attachmentColor);
                            editor.apply();
                            sendAndAttachColorClick(0);
                            break;
                        case R.string.default_header_font_color:

                            G.headerTextColor = "#" + Integer.toHexString(picker.getColor());
                            editor.putString(SHP_SETTING.KEY_FONT_HEADER_COLOR, G.headerTextColor);
                            editor.apply();
                            headerColorClick(0, true);

                            break;
                        case R.string.default_progress_color:

                            G.progressColor = "#" + Integer.toHexString(picker.getColor());
                            editor.putString(SHP_SETTING.KEY_PROGRES_COLOR, G.progressColor);
                            editor.apply();
                            progressColorClick(0, true);
                            break;


                    }
                } catch (IllegalArgumentException e) {

                    new MaterialDialog.Builder(G.fragmentActivity).title(R.string.selected_color_can_not_set_on_yout_device).cancelable(true).show();
                }
            }
        });

        dialog.show();
    }


    private void getInfo() {
        sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);

        String appBarColor = sharedPreferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, Theme.default_appBarColor);
        String notificationColor = sharedPreferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, Theme.default_notificationColor);
        String toggleButtonColor = sharedPreferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, Theme.default_toggleButtonColor);
        String attachmentColor = sharedPreferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, Theme.default_attachmentColor);
        String headerTextColor = sharedPreferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, Theme.default_headerTextColor);
        String progressColor = sharedPreferences.getString(SHP_SETTING.KEY_PROGRES_COLOR, Theme.default_progressColor);

        //***********************
        GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgTitleBarColor.getBackground();
        bgShape.setColor(Color.parseColor(appBarColor));

        //***********************

        GradientDrawable bgShapeNotification = (GradientDrawable) fragmentThemColorCustomBinding.asnImgNotificationColor.getBackground();
        bgShapeNotification.setColor(Color.parseColor(notificationColor));

        //***********************

        GradientDrawable bgShapeToggleBottomColor = (GradientDrawable) fragmentThemColorCustomBinding.asnImgToggleBottonColor.getBackground();
        bgShapeToggleBottomColor.setColor(Color.parseColor(toggleButtonColor));

         /*
          page for show all image user
         */
        GradientDrawable bgShapeSendAndAttachColor = (GradientDrawable) fragmentThemColorCustomBinding.asnImgSendAndAttachColor.getBackground();
        bgShapeSendAndAttachColor.setColor(Color.parseColor(attachmentColor));


        //***********************

        GradientDrawable bgShapeHeaderTextColor = (GradientDrawable) fragmentThemColorCustomBinding.asnImgDefaultHeaderFontColor.getBackground();
        bgShapeHeaderTextColor.setColor(Color.parseColor(headerTextColor));

        //***********************

        GradientDrawable bgShapeProgressColor = (GradientDrawable) fragmentThemColorCustomBinding.asnImgDefaultProgressColor.getBackground();
        bgShapeProgressColor.setColor(Color.parseColor(progressColor));


    }

    public void appBarColorClick(int color) {

        if (fragmentThemColorCustomBinding != null) {
            GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgTitleBarColor.getBackground();
            bgShape.setColor(Color.parseColor(G.appBarColor));
        }
    }

    public void notificationColorClick(int color, boolean updateUi) {

        if (fragmentThemColorCustomBinding != null) {
            GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgNotificationColor.getBackground();
            bgShape.setColor(Color.parseColor(G.notificationColor));
        }

    }

    public void progressColorClick(int color, boolean updateUi) {


        if (fragmentThemColorCustomBinding != null) {
            GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgDefaultProgressColor.getBackground();
            bgShape.setColor(Color.parseColor(G.progressColor));
        }
    }


    public void toggleBottomClick(int color) {

        if (fragmentThemColorCustomBinding != null) {
            GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgToggleBottonColor.getBackground();
            bgShape.setColor(Color.parseColor(G.toggleButtonColor));
        }
    }

    public void headerColorClick(int color, boolean updateUi) {

        if (fragmentThemColorCustomBinding != null) {
            GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgDefaultHeaderFontColor.getBackground();
            bgShape.setColor(Color.parseColor(G.headerTextColor));
        }
    }

    public void sendAndAttachColorClick(int color) {

        if (fragmentThemColorCustomBinding != null) {
            GradientDrawable bgShape = (GradientDrawable) fragmentThemColorCustomBinding.asnImgSendAndAttachColor.getBackground();
            bgShape.setColor(Color.parseColor(G.attachmentColor));
        }

    }
}
