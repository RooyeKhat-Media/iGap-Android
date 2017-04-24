/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/



package com.iGap.realm;

import io.realm.RealmObject;

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

    public void setSound(String sound) {
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
}
