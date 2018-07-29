package net.iGap;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.one.EmojiOneProvider;

import net.iGap.module.SHP_SETTING;

import static net.iGap.G.appBarColor;
import static net.iGap.G.attachmentColor;
import static net.iGap.G.context;
import static net.iGap.G.headerTextColor;
import static net.iGap.G.notificationColor;
import static net.iGap.G.toggleButtonColor;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */
public class Theme extends Application {

    public static final int CUSTOM = 0;
    public static final int DEFAULT = 1;
    public static final int DARK = 2;
    public static final int RED = 3;
    public static final int PINK = 4;
    public static final int PURPLE = 5;
    public static final int DEEPPURPLE = 6;
    public static final int INDIGO = 7;
    public static final int BLUE = 8;
    public static final int LIGHT_BLUE = 9;
    public static final int CYAN = 10;
    public static final int TEAL = 11;
    public static final int GREEN = 12;
    public static final int LIGHT_GREEN = 13;
    public static final int LIME = 14;
    public static final int YELLLOW = 15;
    public static final int AMBER = 16;
    public static final int ORANGE = 17;
    public static final int DEEP_ORANGE = 18;
    public static final int BROWN = 19;
    public static final int GREY = 20;
    public static final int BLUE_GREY = 21;
    public static final int BLUE_GREY_COMPLETE = 22;
    public static final int INDIGO_COMPLETE = 23;
    public static final int BROWN_COMPLETE = 24;
    public static final int TEAL_COMPLETE = 25;
    public static final int GREY_COMPLETE = 26;


    public static String default_appBarColor = "#00B0BF";
    public static String default_notificationColor = "#e51c23";
    public static String default_toggleButtonColor = "#00B0BF";
    public static String default_attachmentColor = "#00B0BF";
    public static String default_headerTextColor = "#00B0BF";
    public static String default_progressColor = "#00B0BF";

    public static String default_dark_appBarColor = "#000000";
    public static String default_dark_notificationColor = "#000000";
    public static String default_dark_toggleButtonColor = "#000000";
    public static String default_dark_attachmentColor = "#ffffff";
    public static String default_dark_menuBackgroundColor = "#000000";
    public static String default_dark_headerTextColor = "#ffffff";
    public static String default_dark_progressColor = "#ffffff";

    public static String default_red_appBarColor = "#F44336";
    public static String default_Pink_appBarColor = "#E91E63";
    public static String default_purple_appBarColor = "#9C27B0";
    public static String default_deepPurple_appBarColor = "#673AB7";
    public static String default_indigo_appBarColor = "#3F51B5";
    public static String default_blue_appBarColor = "#2196F3";
    public static String default_lightBlue_appBarColor = "#03A9F4";
    public static String default_cyan_appBarColor = "#00BCD4";
    public static String default_teal_appBarColor = "#009688";
    public static String default_green_appBarColor = "#388E3C";
    public static String default_lightGreen_appBarColor = "#689F38";
    public static String default_lime_appBarColor = "#AFB42B";
    public static String default_yellow_appBarColor = "#FBC02D";
    public static String default_amber_appBarColor = "#FFA000";
    public static String default_orange_appBarColor = "#F57C00";
    public static String default_deepOrange_appBarColor = "#E64A19";
    public static String default_brown_appBarColor = "#5D4037";
    public static String default_grey_appBarColor = "#616161";
    public static String default_blueGrey_appBarColor = "#455A64";
    public static String lineView = "#52afafaf";


    public static void setThemeColor() {

        SharedPreferences preferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        G.themeColor = preferences.getInt(SHP_SETTING.KEY_THEME_COLOR, DEFAULT);
        G.isDarkTheme = preferences.getBoolean(SHP_SETTING.KEY_THEME_DARK, false);

        /*if (G.themeColor == DARK) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }*/
        EmojiManager.install(new EmojiOneProvider());


        switch (G.themeColor) {
            case CUSTOM:
                setColor(false,
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, default_appBarColor),
                        preferences.getString(SHP_SETTING.KEY_NOTIFICATION_COLOR, default_notificationColor),
                        preferences.getString(SHP_SETTING.KEY_TOGGLE_BOTTON_COLOR, default_toggleButtonColor),
                        preferences.getString(SHP_SETTING.KEY_SEND_AND_ATTACH_ICON_COLOR, default_attachmentColor),
                        preferences.getString(SHP_SETTING.KEY_FONT_HEADER_COLOR, default_headerTextColor),
                        preferences.getString(SHP_SETTING.KEY_PROGRES_COLOR, default_progressColor),
                        lineView,
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#e679dde6",
                        "#FFFFFF",
                        preferences.getString(SHP_SETTING.KEY_APP_BAR_COLOR, default_appBarColor),
                        "#000000",
                        "#2A2A2A",
                        "#bfefef",
                        "#303F9F",
                        "#212121"

                );

                break;
            case DEFAULT:
                setColor(false,
                        default_appBarColor,
                        default_notificationColor,
                        default_toggleButtonColor,
                        default_attachmentColor,
                        default_headerTextColor,
                        default_progressColor,
                        lineView,
                        "#FFFFFF",
                        "#f9f9f9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#e679dde6",
                        "#FFFFFF",
                        "#00BCD4",
                        "#000000",
                        "#2A2A2A",
                        "#bfefef",
                        "#303F9F",
                        "#212121"
                );

                break;
            case DARK:
                setColor(true,
                        default_dark_appBarColor,
                        default_dark_notificationColor,
                        default_dark_toggleButtonColor,
                        default_dark_attachmentColor,
                        default_dark_headerTextColor,
                        default_dark_progressColor,
                        "#313131",
                        "#151515",
                        "#000000",
                        "#ffffff",
                        "#ffffff",
                        "#ffffff",
                        "#4b4b4b",
                        "#cacaca",
                        "#151515",
                        "#c7101010",
                        "#2A2A2A",
                        "#ffffff",
                        "#ffffff",
                        "#313131",
                        "#00BCD4",
                        "#ffffff"
                );

                break;
            case RED:
                setColor(false,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        default_red_appBarColor,
                        "#ffcdd2",// is set
                        "#FFFFFF",
                        "#ef9a9a",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#D32F2F",
                        "#FF5252",
                        "#FF5252",
                        "#FFFFFF",
                        "#FFFFFF",
                        "#ffcdd2",
                        "#283593",
                        "#212121"
                );
                break;
            case PINK:
                setColor(false,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        default_Pink_appBarColor,
                        "#f8bbd0",// is set
                        "#FFFFFF",
                        "#f48fb1",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#C2185B",
                        "#FF4081",
                        "#FF4081",
                        "#FFFFFF",
                        "#FFFFFF",
                        "#f8bbd0",
                        "#283593",
                        "#212121"
                );
                break;
            case PURPLE:
                setColor(false,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        default_purple_appBarColor,
                        "#e1bee7",// is set
                        "#FFFFFF",
                        "#ce93d8",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#7B1FA2",
                        "#E040FB",
                        "#E040FB",
                        "#FFFFFF",
                        "#FFFFFF",
                        "#e1bee7",
                        "#303F9F",
                        "#212121"
                );
                break;
            case DEEPPURPLE:
                setColor(false,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        default_deepPurple_appBarColor,
                        "#d1c4e9",// is set
                        "#FFFFFF",
                        "#b39ddb",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#512DA8",
                        "#7C4DFF",
                        "#7C4DFF",
                        "#FFFFFF",
                        "#FFFFFF",
                        "#d1c4e9",
                        "#303F9F",
                        "#212121"
                );
                break;
            case INDIGO:
                setColor(false,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        "#c5cae9",// line
                        "#FFFFFF",
                        "#9fa8da",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#303F9F",
                        "#536DFE",
                        "#536DFE",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#c5cae9",
                        "#00BCD4",
                        "#212121"
                );
                break;
            case BLUE:
                setColor(false,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        default_blue_appBarColor,
                        "#bbdefb",// line
                        "#FFFFFF",
                        "#90caf9",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#1976D2",
                        "#448AFF",
                        "#03A9F4",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#bbdefb",
                        "#283593",
                        "#212121"
                );
                break;

            case LIGHT_BLUE:
                setColor(false,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        default_lightBlue_appBarColor,
                        "#b3e5fc",// line
                        "#FFFFFF",
                        "#81d4fa",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#0288D1",
                        "#03A9F4",
                        "#03A9F4",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#b3e5fc",
                        "#283593",
                        "#212121"
                );
                break;

            case CYAN:
                setColor(false,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        default_cyan_appBarColor,
                        "#b2ebf2",// line
                        "#FFFFFF",
                        "#81d4fa",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#0097A7",
                        "#00BCD4",
                        "#00BCD4",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#b2ebf2",
                        "#283593",
                        "#212121"
                );
                break;
            case TEAL:
                setColor(false,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        "#b2dfdb",// line
                        "#FFFFFF",
                        "#80cbc4",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#00796B",
                        "#009688",
                        "#009688",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#b2dfdb",
                        "#303F9F",
                        "#212121"
                );
                break;
            case GREEN:
                setColor(false,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        default_green_appBarColor,
                        "#c8e6c9",// line
                        "#FFFFFF",
                        "#a5d6a7",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#388E3C",
                        "#4CAF50",
                        "#4CAF50",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#c8e6c9",
                        "#283593",
                        "#212121"
                );
                break;
            case LIGHT_GREEN:
                setColor(false,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        default_lightGreen_appBarColor,
                        "#dcedc8",// line
                        "#FFFFFF",
                        "#c5e1a5",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#689F38",
                        "#8BC34A",
                        "#8BC34A",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#dcedc8",
                        "#283593",
                        "#212121"
                );
                break;
            case LIME:
                setColor(false,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        default_lime_appBarColor,
                        "#f0f4c3",// line
                        "#FFFFFF",
                        "#e6ee9c",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#AFB42B",
                        "#CDDC39",
                        "#CDDC39",//fab bottom
                        "#212121",
                        "#212121",
                        "#f0f4c3",
                        "#283593",
                        "#212121"
                );
                break;

            case YELLLOW:
                setColor(false,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        default_yellow_appBarColor,
                        "#fff9c4",// line
                        "#FFFFFF",
                        "#fff59d",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#FBC02D",
                        "#FFEB3B",
                        "#FFEB3B",//fab bottom
                        "#212121",
                        "#212121",
                        "#fff9c4",
                        "#00BCD4",
                        "#212121"
                );
                break;
            case AMBER:
                setColor(false,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        default_amber_appBarColor,
                        "#ffecb3",// line
                        "#FFFFFF",
                        "#ffe082",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#FFA000",
                        "#FFC107",
                        "#FFC107",//fab bottom
                        "#212121",
                        "#212121",
                        "#ffecb3",
                        "#283593",
                        "#212121"
                );
                break;
            case ORANGE:
                setColor(false,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        default_orange_appBarColor,
                        "#ffe0b2",// line
                        "#FFFFFF",
                        "#ffcc80",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#F57C00",
                        "#FF9800",
                        "#FF9800",//fab bottom
                        "#212121",
                        "#212121",
                        "#ffe0b2",
                        "#283593",
                        "#212121"
                );
                break;

            case DEEP_ORANGE:
                setColor(false,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        default_deepOrange_appBarColor,
                        "#ffccbc",// line
                        "#FFFFFF",
                        "#ffab91",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#E64A19",
                        "#FF5722",
                        "#FF5722",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#ffccbc",
                        "#536DFE",
                        "#212121"

                );
                break;

            case BROWN:
                setColor(false,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        "#d7ccc8",// line
                        "#FFFFFF",
                        "#bcaaa4",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#5D4037",
                        "#795548",
                        "#795548",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#d7ccc8",
                        "#303F9F",
                        "#212121"
                );
                break;
            case GREY:
                setColor(false,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        "#f5f5f5",// line
                        "#FFFFFF",
                        "#e0e0e0",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#616161",
                        "#9E9E9E",
                        "#9E9E9E",//fab bottom
                        "#ffffff",
                        "#212121",
                        "#f5f5f5",
                        "#536DFE",
                        "#212121"

                );
                break;
            case BLUE_GREY:
                setColor(false,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        "#cfd8dc",// line
                        "#FFFFFF",
                        "#b0bec5",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#455A64",
                        "#607D8B",
                        "#607D8B",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#cfd8dc",
                        "#303F9F",
                        "#212121"
                );
                break;
            case BLUE_GREY_COMPLETE:
                setColor(false,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        default_blueGrey_appBarColor,
                        "#607D8B",// line
                        "#CFD8DC",
                        "#b0bec5",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#455A64",
                        "#607D8B",
                        "#607D8B",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#cfd8dc",
                        "#00bcd4",
                        "#212121"
                );
                break;

            case INDIGO_COMPLETE:
                setColor(false,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        default_indigo_appBarColor,
                        "#3f51b5",// line
                        "#C5CAE9",
                        "#9fa8da",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#303F9F",
                        "#3f51b5",
                        "#3f51b5",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#C5CAE9",
                        "#448AFF",
                        "#212121"

                );
                break;
            case BROWN_COMPLETE:
                setColor(false,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        default_brown_appBarColor,
                        "#795548",// line
                        "#D7CCC8",
                        "#bcaaa4",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#5D4037",
                        "#795548",
                        "#795548",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#D7CCC8",
                        "#00bcd4",
                        "#212121"
                );
                break;
            case TEAL_COMPLETE:
                setColor(false,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        default_teal_appBarColor,
                        "#009688",// line
                        "#B2DFDB",
                        "#80cbc4",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#00796B",
                        "#009688",
                        "#009688",//fab bottom
                        "#FFFFFF",
                        "#FFFFFF",
                        "#B2DFDB",
                        "#303F9F",
                        "#212121"
                );
                break;

            case GREY_COMPLETE:
                setColor(false,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        default_grey_appBarColor,
                        "#e0e0e0",// line
                        "#F5F5F5",
                        "#e0e0e0",
                        "#000000",
                        "#bbbbbb",
                        "#000000",
                        "#e9e9e9",
                        "#FFFFFF",
                        "#616161",
                        "#9E9E9E",
                        "#9E9E9E",//fab bottom
                        "#ffffff",
                        "#212121",
                        "#F5F5F5",
                        "#2196f3",
                        "#212121"
                );
                break;
        }
    }

    private static void setColor(boolean isDarkTheme, String... color) {

        G.isDarkTheme = isDarkTheme;

        appBarColor = color[0];
        notificationColor = color[1];
        toggleButtonColor = color[2];
        attachmentColor = color[3];
        headerTextColor = color[4];
        G.progressColor = color[5];

        G.lineBorder = color[6];// ok
        G.backgroundTheme = color[7];
        G.backgroundTheme_2 = color[8];
        G.textTitleTheme = color[9];
        G.textSubTheme = color[10];
        G.tintImage = color[11];
        G.logLineTheme = color[12];
        G.voteIconTheme = color[13];
        G.bubbleChatSend = color[14];
        G.bubbleChatReceive = color[15];
        G.fabBottom = color[16];
        G.textBubble = color[17];
        G.txtIconCheck = color[18];
        G.bubbleChatMusic = color[19];
        G.linkColor = color[20];
        G.textChatMusic = color[21];

    }

}
