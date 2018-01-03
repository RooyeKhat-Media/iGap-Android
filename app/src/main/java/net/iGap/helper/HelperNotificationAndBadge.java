/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.widget.RemoteViews;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;
import me.leolin.shortcutbadger.ShortcutBadger;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.activities.ActivityPopUpNotification;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.TimeUtils;
import net.iGap.module.enums.StructPopUp;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;

import static net.iGap.G.context;

/**
 * manage all notification and badge in app
 */

public class HelperNotificationAndBadge {

    private static final String strClose = "close";
    private static final int DEFAULT = 0;
    private static final int ENABLE = 1;
    private static final int DISABLE = 2;
    public static boolean isChatRoomNow = false;
    private int unreadMessageCount = 0;
    private String messageOne = "";
    private boolean isFromOnRoom = true;
    private long roomId = 0;
    private long senderId = 0;
    private ArrayList<Item> list = new ArrayList<>();
    private boolean isEnablepopUpSettin = false;

    private NotificationManager notificationManager;
    private Notification notification;
    private int notificationId = 20;
    private RemoteViews remoteViews;
    private RemoteViews remoteViewsLarge;
    private SharedPreferences sharedPreferences;
    private int led;
    private int vibrator = 1;
    private int popupNotification;
    private int sound = 0;
    private int messagePeriview;
    private boolean isMute;
    private String inRoomVibrator;  //specially for each room
    private int inRoomSound;        //specially for each room
    private int inRoomLedColor;     //specially for each room
    private int inAppSound;
    private int inAppVibrator;
    private int inAppPreview;
    private int inChat_Sound;
    private int countUnicChat = 0;
    private long idRoom;
    private int delayAlarm = 5000;
    private long currentAlarm;
    public ArrayList<StructPopUp> popUpList = new ArrayList<>();

    private String mHeader = "";
    private String mContent = "";
    private Bitmap mBitmapIcon = null;

    public HelperNotificationAndBadge() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification_small);
        remoteViewsLarge = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

        Intent intentClose = new Intent(context, RemoteActionReceiver.class);
        intentClose.putExtra("Action", "strClose");
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(context, 1, intentClose, 0);
        remoteViewsLarge.setOnClickPendingIntent(R.id.mln_btn_close, pendingIntentClose);
    }

    //private void setOnTextClick(int resLayot, int indexItem) {
    //    Intent intent = new Intent(context, ActivityChat.class);
    //    intent.putExtra("RoomId", list.get(indexItem).roomId);
    //    PendingIntent pendingIntent = PendingIntent.getActivity(context, 30 + indexItem, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    //    ActivityPopUpNotification.isGoingToChatFromPopUp = true;
    //    remoteViewsLarge.setOnClickPendingIntent(resLayot, pendingIntent);
    //}

    private NotificationCompat.InboxStyle getBigStyle() {

        NotificationCompat.InboxStyle _style = new NotificationCompat.InboxStyle();

        if (isFromOnRoom) {
            _style.setBigContentTitle(list.get(0).name);

            for (int i = 0; i < unreadMessageCount && i < 3; i++) {
                _style.addLine(list.get(i).message);
            }
        } else {
            for (int i = 0; i < unreadMessageCount && i < 3; i++) {
                _style.addLine(list.get(i).name + " " + list.get(i).message);
            }
        }

        if (unreadMessageCount > 3) {
            _style.addLine("....");
        }

        String chatCount = "";

        if (countUnicChat == 1) {
            chatCount = context.getString(R.string.from) + " " + countUnicChat + " " + context.getString(R.string.chat);
        } else if (countUnicChat > 1) {
            chatCount = context.getString(R.string.from) + " " + countUnicChat + " " + context.getString(R.string.chats);
        }

        String newMessage = "";
        if (unreadMessageCount == 1) {
            newMessage = context.getString(R.string.new_message);
            chatCount = "";
        } else {
            newMessage = context.getString(R.string.new_messages);
        }

        String _summary = unreadMessageCount + " " + newMessage + " " + chatCount;
        _style.setSummaryText(_summary);

        return _style;
    }

    private void getNotificationSmallInfo() {

        String avatarPath = null;
        if (unreadMessageCount == 1) {

            mHeader = list.get(0).name;
            mContent = list.get(0).message;
        } else {
            mHeader = context.getString(R.string.igap);
            mContent = list.get(0).message;

            String s = "";
            if (countUnicChat == 1) {
                s = " " + context.getString(R.string.chat);
            } else if (countUnicChat > 1) {
                s = " " + context.getString(R.string.chats);
            }

            String str = String.format(" %d " + context.getString(R.string.new_messages_from) + " %d " + s, unreadMessageCount, countUnicChat);

            mContent = str;
        }

        mBitmapIcon = BitmapFactory.decodeResource(null, R.mipmap.icon);

        if (isFromOnRoom) {
            Realm realm = Realm.getDefaultInstance();
            RealmAvatar realmAvatarPath = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, senderId).findFirst();
            if (realmAvatarPath != null) {
                if (realmAvatarPath.getFile().isFileExistsOnLocal()) {
                    avatarPath = realmAvatarPath.getFile().getLocalFilePath();
                } else if (realmAvatarPath.getFile().isThumbnailExistsOnLocal()) {
                    avatarPath = realmAvatarPath.getFile().getLocalThumbnailPath();
                }
            }
            if (avatarPath != null) {
                try {
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.outHeight = 64;
                    option.outWidth = 64;

                    Bitmap bitmap = BitmapFactory.decodeFile(avatarPath, option);
                    if (bitmap != null) {
                        mBitmapIcon = bitmap;
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
            realm.close();
        }
    }

    //************************** notification ***********************

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.white_icon : R.mipmap.iconsmal;
    }

    private void setNotification() {

        PendingIntent pi;
        Intent intent = new Intent(context, ActivityMain.class);

        if (isFromOnRoom) {
            intent.putExtra(ActivityMain.openChat, roomId);
        }

        pi = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        getNotificationSmallInfo();

        String messageToShow = list.get(0).message;
        if (list.get(0).message.length() > 40) {
            messageToShow = messageToShow.substring(0, 40);
        }

        notification = new NotificationCompat.Builder(context).setSmallIcon(getNotificationIcon()).setLargeIcon(mBitmapIcon).setContentTitle(mHeader).setContentText(mContent).setCategory(NotificationCompat.CATEGORY_MESSAGE).setStyle(getBigStyle()).setContentIntent(pi).build();

        if (currentAlarm + delayAlarm < System.currentTimeMillis()) {
            if (isMute) {
                return;
            }
            alarmNotification(messageToShow);
        }

        notificationManager.notify(notificationId, notification);
    }

    private void alarmNotification(String messageToShow) {

        if (G.isAppInFg) {
            if (!isChatRoomNow) {

                if (inAppVibrator == 1) {
                    notification.vibrate = setVibrator(vibrator);
                }
                if (inAppSound == 1) {
                    notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
                }
                if (inAppPreview == 1) {
                    notification.tickerText = list.get(0).name + " " + messageToShow;
                }
            } else if (inChat_Sound == 1) {
                notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));
            }
        } else {
            notification.vibrate = setVibrator(vibrator);
            notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + setSound(sound));

            if (messagePeriview == 1) {
                notification.tickerText = list.get(0).name + " " + messageToShow;
            } else {
                notification.tickerText = "";
            }
        }

        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.ledARGB = led;
        notification.ledOnMS = 1000;
        notification.ledOffMS = 2000;

        currentAlarm = System.currentTimeMillis();
    }

    public void checkAlert(boolean updateNotification, ProtoGlobal.Room.Type type, long roomId) {

        idRoom = roomId;
        int vipCheck = checkSpecialNotification(updateNotification, type, roomId);
        SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        int checkAlert = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_ALERT_MESSAGE, 1);
        if (vipCheck == ENABLE) {
            updateNotificationAndBadge(updateNotification, type);
        } else if (vipCheck == DEFAULT) {
            if (checkAlert == 1) {
                updateNotificationAndBadge(updateNotification, type);
            }
        } else if (vipCheck == DISABLE) {
            // do nothing
        }
    }

    private void updateNotificationAndBadge(boolean updateNotification, ProtoGlobal.Room.Type type) {

        sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, Context.MODE_PRIVATE);
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, idRoom).findFirst();
        switch (type) {
            case CHAT:
                if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getRealmNotificationSetting() != null) {
                    if (realmRoom.getChatRoom().getRealmNotificationSetting().getLedColor() != -1) {
                        led = realmRoom.getChatRoom().getRealmNotificationSetting().getLedColor();
                    } else {
                        led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                    }

                    if (realmRoom.getChatRoom().getRealmNotificationSetting().getVibrate() != -1) {
                        vibrator = realmRoom.getChatRoom().getRealmNotificationSetting().getVibrate();
                    } else {
                        vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
                    }

                    if (realmRoom.getChatRoom().getRealmNotificationSetting().getIdRadioButtonSound() != -1) {
                        sound = realmRoom.getChatRoom().getRealmNotificationSetting().getIdRadioButtonSound();
                    } else {
                        sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                    }
                } else {
                    led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                    vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
                    sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                }
                messagePeriview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_MESSAGE, 1);
                break;

            case GROUP:
                if (realmRoom != null && realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRealmNotificationSetting() != null) {
                    if (realmRoom.getGroupRoom().getRealmNotificationSetting().getLedColor() != -1) {
                        led = realmRoom.getGroupRoom().getRealmNotificationSetting().getLedColor();
                    } else {
                        led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                    }

                    if (realmRoom.getGroupRoom().getRealmNotificationSetting().getVibrate() != -1) {
                        vibrator = realmRoom.getGroupRoom().getRealmNotificationSetting().getVibrate();
                    } else {
                        vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_GROUP, 1);
                    }

                    if (realmRoom.getGroupRoom().getRealmNotificationSetting().getIdRadioButtonSound() != -1) {
                        sound = realmRoom.getGroupRoom().getRealmNotificationSetting().getIdRadioButtonSound();
                    } else {
                        sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_GROUP_POSITION, 0);
                    }
                } else {
                    led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                    vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
                    sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                }
                messagePeriview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);

                break;
            case CHANNEL:

                if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null) {
                    if (realmRoom.getChannelRoom().getRealmNotificationSetting().getLedColor() != -1) {
                        led = realmRoom.getChannelRoom().getRealmNotificationSetting().getLedColor();
                    } else {
                        led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_GROUP, -8257792);
                    }

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
                    led = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_LED_COLOR_MESSAGE, -8257792);
                    vibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_VIBRATE_MESSAGE, 1);
                    sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_SOUND_MESSAGE_POSITION, 0);
                }
                messagePeriview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_MESSAGE_PREVIEW_GROUP, 1);
                break;
        }

        if (realmRoom != null) {
            isMute = realmRoom.getMute();
        }
        inAppSound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_SOUND, 0);
        inAppVibrator = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_VIBRATE, 0);
        inAppPreview = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_APP_PREVIEW, 0);
        inChat_Sound = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_CHAT_SOUND, 0);

        list.clear();
        unreadMessageCount = 0;
        popUpList.clear();

        isEnablepopUpSettin = checkPopUpSetting(type);


        RealmResults<RealmRoomMessage> realmRoomMessages = RealmRoomMessage.findNotificationMessage(realm);

        if (!realmRoomMessages.isEmpty()) {
            for (RealmRoomMessage roomMessage : realmRoomMessages) {
                RealmRoom realmRoom1 = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                if (realmRoom1 != null && realmRoom1.getType() != null && realmRoom1.getType() != ProtoGlobal.Room.Type.CHANNEL) {
                    unreadMessageCount++;
                    messageOne = roomMessage.getMessage();

                    if (unreadMessageCount > 3 && (!isEnablepopUpSettin || G.isAppInFg || AttachFile.isInAttach)) {
                        break;
                    }


                    addItemToPopUPList(roomMessage);

                    if (popUpList.size() > 50) {// show item in popup activity with limit count
                        break;
                    }

                    if (unreadMessageCount <= 3) {
                        Item item = new Item();

                        item.name = realmRoom1.getTitle() + " : ";
                        item.roomId = realmRoom1.getId();

                        String text = "";
                        try {
                            if (roomMessage.getLogMessage() != null) {
                                text = roomMessage.getLogMessage();
                            } else {
                                text = RealmRoomMessage.getFinalMessage(roomMessage).getMessage();
                            }

                            if (text.length() < 1) if (roomMessage.getReplyTo() != null) text = roomMessage.getReplyTo().getMessage();
                            if (text.length() < 1) text = ActivityPopUpNotification.getTextOfMessageType(roomMessage.getMessageType());
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        item.message = text;
                        item.time = TimeUtils.toLocal(roomMessage.getUpdateTime(), G.CHAT_MESSAGE_TIME);
                        list.add(item);

                        if (unreadMessageCount == 1) {
                            roomId = roomMessage.getRoomId();

                            if (realmRoom1.getType() == ProtoGlobal.Room.Type.GROUP) {
                                senderId = realmRoom1.getId();
                            } else {
                                senderId = realmRoom1.getChatRoom().getPeerId();
                            }

                        }


                    }

                }
            }

            startActivityPopUpNotification(popUpList);

            isFromOnRoom = false;

            unreadMessageCount = 0;
            countUnicChat = 0;

            RealmResults<RealmRoom> realmRooms = realm.where(RealmRoom.class).findAll();
            for (RealmRoom realmRoom1 : realmRooms) {
                //  realmRoom1.getType() == ProtoGlobal.Room.Type.CHANNEL &&
                if (realmRoom1.getUnreadCount() > 0) {
                    unreadMessageCount += realmRoom1.getUnreadCount();
                    ++countUnicChat;
                }
            }

            if (countUnicChat == 1) {
                isFromOnRoom = true;
            } else {
                isFromOnRoom = false;
            }
        }

        if (unreadMessageCount == 0) {
            return;
        }

        try {
            ShortcutBadger.applyCount(context, unreadMessageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (updateNotification) {
                setNotification();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        realm.close();
    }

    public void cancelNotification() {
        notificationManager.cancel(notificationId);
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

    public long[] setVibrator(int vb) {
        long[] intVibrator = new long[]{};

        switch (vb) {
            case 0:
                intVibrator = new long[]{0, 0, 0};
                break;
            case 1:
                intVibrator = new long[]{0, 300, 0};
                break;
            case 2:
                intVibrator = new long[]{0, 200, 0};
                break;
            case 3:
                intVibrator = new long[]{0, 700, 0};
                break;
            case 4:
                AudioManager am2 = (AudioManager) G.context.getSystemService(Context.AUDIO_SERVICE);
                switch (am2.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        intVibrator = new long[]{0, 300, 0};
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.i("MyApp", "Vibrate mode");
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.i("MyApp", "Normal mode");
                        break;
                }
                break;
        }
        return intVibrator;
    }

    private boolean checkPopUpSetting(ProtoGlobal.Room.Type type) {

        SharedPreferences sharedPreferences;
        boolean popUpSetting = false;
        int mode = 0;

        switch (type) {
            case CHAT:
                sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, context.MODE_PRIVATE);
                mode = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_MESSAGE, 0);
                break;
            case GROUP:
                sharedPreferences = context.getSharedPreferences(SHP_SETTING.FILE_NAME, context.MODE_PRIVATE);
                mode = sharedPreferences.getInt(SHP_SETTING.KEY_STNS_POPUP_NOTIFICATION_GROUP, 0);
                break;
            case CHANNEL:
                break;
        }

        switch (mode) {

            case 0:
                // no popup
                break;
            case 1:
                //only when screen on
                if (isScreenOn(context)) popUpSetting = true;
                break;
            case 2:
                //only when screen off
                if (!isScreenOn(context)) popUpSetting = true;
                break;
            case 3:
                //always
                popUpSetting = true;
                break;
        }

        return popUpSetting;
    }

    private void startActivityPopUpNotification(ArrayList<StructPopUp> poList) {

        if (!G.isAppInFg) {
            if (!AttachFile.isInAttach) {
                if (isEnablepopUpSettin) {
                    if (getForegroundApp() || ActivityPopUpNotification.isPopUpVisible) { //check that any other program is in background

                        goToPopUpActivity(poList);
                    }
                }
            }
        }
    }

    private boolean getForegroundApp() {

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        try {
            if (taskInfo.get(0).topActivity.getClassName().toString().toLowerCase().contains("launcher")) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private void addItemToPopUPList(RealmRoomMessage roomMessage) {

        String text = "";

        if (roomMessage.getLogMessage() != null) {
            return;
        }

        StructPopUp sp = new StructPopUp(roomMessage.getRoomId());

        try {

            text = RealmRoomMessage.getFinalMessage(roomMessage).getMessage();
            if (text.length() < 1) if (roomMessage.getReplyTo() != null) text = roomMessage.getReplyTo().getMessage();
            if (text.length() < 1) {
                ProtoGlobal.RoomMessageType rmt = RealmRoomMessage.getFinalMessage(roomMessage).getMessageType();
                text = ActivityPopUpNotification.getTextOfMessageType(rmt);
            }

            sp.setMessage(text);

            popUpList.add(sp);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void goToPopUpActivity(ArrayList<StructPopUp> poList) {

        if (poList == null) return;
        if (poList.size() == 0) return;

        if (ActivityPopUpNotification.isPopUpVisible) {
            if (ActivityPopUpNotification.popUpListener != null) {
                ActivityPopUpNotification.popUpListener.onMessageRecive(poList);
            }
        } else {
            Intent intent = new Intent(context, ActivityPopUpNotification.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(ActivityPopUpNotification.ARGUMENTLIST, poList);
            context.startActivity(intent);
        }
    }

    /**
     * Is the screen of the device on.
     *
     * @param context the context
     * @return true when (at least one) screen is on
     */
    public boolean isScreenOn(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            boolean screenOn = false;
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    screenOn = true;
                }
            }
            return screenOn;
        } else {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return pm.isScreenOn();
        }
    }

    public static class RemoteActionReceiver extends BroadcastReceiver {

        public RemoteActionReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            G.helperNotificationAndBadge.cancelNotification();
        }
    }

    private class Item {

        String name = "";
        String message = "";
        String time = "";
        long roomId;
    }

    private int checkSpecialNotification(boolean updateNotification, ProtoGlobal.Room.Type type, long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        switch (type) {
            case CHAT:
                if (realmRoom != null && realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getRealmNotificationSetting() != null) {
                    switch (realmRoom.getChatRoom().getRealmNotificationSetting().getNotification()) {
                        case DEFAULT:
                            return DEFAULT;
                        case ENABLE:
                            return ENABLE;
                        case DISABLE:
                            return DISABLE;
                    }
                } else {
                    return DEFAULT;
                }
                break;
            case GROUP:

                if (realmRoom != null && realmRoom.getGroupRoom() != null && realmRoom.getGroupRoom().getRealmNotificationSetting() != null) {

                    switch (realmRoom.getGroupRoom().getRealmNotificationSetting().getNotification()) {
                        case DEFAULT:
                            return DEFAULT;
                        case ENABLE:
                            return ENABLE;
                        case DISABLE:
                            return DISABLE;
                    }
                } else {
                    return DEFAULT;
                }
            case CHANNEL:
                if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().getRealmNotificationSetting() != null) {

                    switch (realmRoom.getChannelRoom().getRealmNotificationSetting().getNotification()) {
                        case DEFAULT:
                            return DEFAULT;
                        case ENABLE:
                            return ENABLE;
                        case DISABLE:
                            return DISABLE;
                    }
                } else {
                    return DEFAULT;
                }

            default:
                return DEFAULT;
        }
        realm.close();
        return 0;
    }

    public static void updateBadgeOnly() {

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Realm realm = Realm.getDefaultInstance();

                int unreadMessageCount = 0;

                RealmResults<RealmRoom> realmRooms = realm.where(RealmRoom.class).findAll();
                if (realmRooms != null) {
                    for (RealmRoom realmRoom1 : realmRooms) {
                        if (realmRoom1.getUnreadCount() > 0) {
                            unreadMessageCount += realmRoom1.getUnreadCount();
                        }
                    }
                }
                realm.close();

                try {
                    ShortcutBadger.applyCount(G.context, unreadMessageCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 200);
    }
}
