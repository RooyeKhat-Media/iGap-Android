/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module.structs;

import android.os.Parcel;
import android.os.Parcelable;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoomMessageContact;

public class StructRegisteredInfo implements Parcelable {
    public static final Parcelable.Creator<StructRegisteredInfo> CREATOR = new Parcelable.Creator<StructRegisteredInfo>() {
        @Override
        public StructRegisteredInfo createFromParcel(Parcel source) {
            return new StructRegisteredInfo(source);
        }

        @Override
        public StructRegisteredInfo[] newArray(int size) {
            return new StructRegisteredInfo[size];
        }
    };
    public long id;
    public String username;
    public String phone;
    public String firstName;
    public String lastName;
    public String displayName;
    public String initials;
    public String color;
    public String status;
    public int lastSeen;
    public int avatarCount;
    public StructMessageAttachment avatar;

    public StructRegisteredInfo() {
    }

    public StructRegisteredInfo(String lastName, String firstName, String phone, long id) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.displayName = firstName + " " + lastName;
        this.phone = phone;
        this.id = id;
    }

    protected StructRegisteredInfo(Parcel in) {
        this.id = in.readLong();
        this.username = in.readString();
        this.phone = in.readString();
        this.firstName = in.readString();
        this.lastName = in.readString();
        this.displayName = in.readString();
        this.initials = in.readString();
        this.color = in.readString();
        this.status = in.readString();
        this.lastSeen = in.readInt();
        this.avatarCount = in.readInt();
        this.avatar = in.readParcelable(StructMessageAttachment.class.getClassLoader());
    }

    public static StructRegisteredInfo build(RealmRoomMessageContact messageContact) {
        if (messageContact == null) {
            return null;
        }
        StructRegisteredInfo userInfo = new StructRegisteredInfo();
        //userInfo.imageSource=;
        userInfo.firstName = messageContact.getFirstName();
        userInfo.lastName = messageContact.getLastName();
        if (messageContact.getPhones() != null && messageContact.getPhones().first() != null) {
            userInfo.phone = messageContact.getPhones().first().getString();
        }
        userInfo.displayName = userInfo.firstName + " " + userInfo.lastName;
        //userInfo.userName=;

        return userInfo;
    }

    public static StructRegisteredInfo build(ProtoGlobal.RoomMessageContact messageContact) {
        if (messageContact == null) {
            return null;
        }
        StructRegisteredInfo userInfo = new StructRegisteredInfo();
        //userInfo.imageSource=;
        userInfo.firstName = messageContact.getFirstName();
        userInfo.lastName = messageContact.getLastName();
        if (messageContact.getPhoneList() != null && messageContact.getPhoneList().size() > 0) {
            userInfo.phone = messageContact.getPhoneList().get(messageContact.getPhoneList().size() - 1);
        }
        userInfo.displayName = userInfo.firstName + " " + userInfo.lastName;
        //userInfo.userName=;

        return userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.username);
        dest.writeString(this.phone);
        dest.writeString(this.firstName);
        dest.writeString(this.lastName);
        dest.writeString(this.displayName);
        dest.writeString(this.initials);
        dest.writeString(this.color);
        dest.writeString(this.status);
        dest.writeInt(this.lastSeen);
        dest.writeInt(this.avatarCount);
        dest.writeParcelable(this.avatar, flags);
    }
}
