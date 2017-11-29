/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;

public class RealmNotificationSetting extends RealmObject {

    private int notification;
    private int vibrate;
    private String sound;
    private int idRadioButtonSound;
    private String smartNotification;
    private int minutes;
    private int times;
    private int ledColor;

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public int getVibrate() {
        return vibrate;
    }

    public void setVibrate(int vibrate) {
        this.vibrate = vibrate;
    }

    public String getSound() {
        return sound;
    }

    public void sound(String sound) {
        this.sound = sound;
    }

    public int getIdRadioButtonSound() {
        return idRadioButtonSound;
    }

    public void setIdRadioButtonSound(int idRadioButtonSound) {
        this.idRadioButtonSound = idRadioButtonSound;
    }

    public String getSmartNotification() {
        return smartNotification;
    }

    public void setSmartNotification(String smartNotification) {
        this.smartNotification = smartNotification;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getLedColor() {
        return ledColor;
    }

    public void setLedColor(int ledColor) {
        this.ledColor = ledColor;
    }


    public static RealmNotificationSetting put(Realm realm, final RealmChatRoom realmChatRoom, final RealmGroupRoom realmGroupRoom, final RealmChannelRoom realmChannelRoom) {
        RealmNotificationSetting realmNotificationSetting = realm.createObject(RealmNotificationSetting.class);
        realmNotificationSetting.setNotification(0);
        realmNotificationSetting.setVibrate(-1);
        realmNotificationSetting.sound(G.fragmentActivity.getResources().getString(R.string.array_Default_Notification_tone));
        realmNotificationSetting.setIdRadioButtonSound(-1);
        realmNotificationSetting.setSmartNotification(G.fragmentActivity.getResources().getString(R.string.array_Default));
        realmNotificationSetting.setTimes(-1);
        realmNotificationSetting.setMinutes(-1);
        realmNotificationSetting.setLedColor(-1);

        if (realmChatRoom != null) {
            realmChatRoom.setRealmNotificationSetting(realmNotificationSetting);
        } else if (realmGroupRoom != null) {
            realmGroupRoom.setRealmNotificationSetting(realmNotificationSetting);
        } else if (realmChannelRoom != null) {
            realmChannelRoom.setRealmNotificationSetting(realmNotificationSetting);
        }
        return realmNotificationSetting;
    }

    public static void sound(final long roomId, final String sound, final int which, final ProtoGlobal.Room.Type roomType) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    switch (roomType) {
                        case CHAT:
                            RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                            if (realmChatRoom != null) {
                                realmChatRoom.getRealmNotificationSetting().sound(sound);
                                realmChatRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                            }
                            break;
                        case GROUP:
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.getRealmNotificationSetting().sound(sound);
                                realmGroupRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                            }
                            break;
                        case CHANNEL:
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.getRealmNotificationSetting().sound(sound);
                                realmChannelRoom.getRealmNotificationSetting().setIdRadioButtonSound(which);
                            }
                            break;
                    }
                }
            }
        });
        realm.close();
    }

    public static void popupNotification(final long roomId, final ProtoGlobal.Room.Type roomType, final int notification) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    switch (roomType) {
                        case CHAT: {
                            RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                            if (realmChatRoom != null) {
                                realmChatRoom.getRealmNotificationSetting().setNotification(notification);
                            }
                            break;
                        }
                        case GROUP: {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.getRealmNotificationSetting().setNotification(notification);
                            }
                            break;
                        }
                        case CHANNEL: {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.getRealmNotificationSetting().setNotification(notification);
                            }
                            break;
                        }
                    }
                }
            }
        });
        realm.close();
    }

    public static void vibrate(final long roomId, final ProtoGlobal.Room.Type roomType, final int vibrateLevel) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    switch (roomType) {
                        case CHAT: {
                            RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                            if (realmChatRoom != null) {
                                realmChatRoom.getRealmNotificationSetting().setVibrate(vibrateLevel);
                            }
                            break;
                        }
                        case GROUP: {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.getRealmNotificationSetting().setVibrate(vibrateLevel);
                            }
                            break;
                        }
                        case CHANNEL: {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.getRealmNotificationSetting().setVibrate(vibrateLevel);
                            }
                            break;
                        }

                    }
                }
            }
        });
        realm.close();
    }

    public static void ledColor(final long roomId, final ProtoGlobal.Room.Type roomType, final int ledColor) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    switch (roomType) {
                        case CHAT: {
                            RealmChatRoom realmChatRoom = realmRoom.getChatRoom();
                            if (realmChatRoom != null) {
                                realmChatRoom.getRealmNotificationSetting().setLedColor(ledColor);
                            }
                            break;
                        }
                        case GROUP: {
                            RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                            if (realmGroupRoom != null) {
                                realmGroupRoom.getRealmNotificationSetting().setLedColor(ledColor);
                            }
                            break;
                        }
                        case CHANNEL: {
                            RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                            if (realmChannelRoom != null) {
                                realmChannelRoom.getRealmNotificationSetting().setLedColor(ledColor);
                            }
                            break;
                        }
                    }
                }
            }
        });
        realm.close();
    }
}
