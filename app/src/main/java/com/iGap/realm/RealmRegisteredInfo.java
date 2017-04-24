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

import com.iGap.module.AppUtils;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class RealmRegisteredInfo extends RealmObject {
    @PrimaryKey private long id;
    private String username;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String displayName;
    private String initials;
    private String color;
    private String status;
    private String cacheId;
    private int lastSeen;
    private int avatarCount;

    private boolean mutual;
    private boolean blockUser = false;
    private boolean DoNotshowSpamBar = false;

    public static RealmRegisteredInfo putOrUpdate(ProtoGlobal.RegisteredUser input) {
        Realm realm = Realm.getDefaultInstance();

        RealmRegisteredInfo registeredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, input.getId()).findFirst();
        if (registeredInfo == null) {
            registeredInfo = realm.createObject(RealmRegisteredInfo.class, input.getId());
            registeredInfo.setDoNotshowSpamBar(false);
        }

        registeredInfo.setUsername(input.getUsername());
        registeredInfo.setDisplayName(input.getDisplayName());
        registeredInfo.setStatus(input.getStatus().toString());
        registeredInfo.setAvatarCount(input.getAvatarCount());
        registeredInfo.setCacheId(input.getCacheId());
        registeredInfo.setColor(input.getColor());
        registeredInfo.setFirstName(input.getFirstName());
        registeredInfo.setInitials(input.getInitials());
        registeredInfo.setLastName(input.getLastName());
        registeredInfo.setLastSeen(input.getLastSeen());
        registeredInfo.setMutual(input.getMutual());
        registeredInfo.setPhoneNumber(Long.toString(input.getPhone()));
        registeredInfo.setUsername(input.getUsername());

        realm.close();
        return registeredInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStatus() {
        return AppUtils.getStatsForUser(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    public int getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(int lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getAvatarCount() {
        return avatarCount;
    }

    public void setAvatarCount(int avatarCount) {
        this.avatarCount = avatarCount;
    }

    public boolean isMutual() {
        return mutual;
    }

    public void setMutual(boolean mutual) {
        this.mutual = mutual;
    }

    public boolean isBlockUser() {
        return blockUser;
    }

    public void setBlockUser(boolean blockUser) {
        this.blockUser = blockUser;
    }

    public boolean getDoNotshowSpamBar() {
        return DoNotshowSpamBar;
    }

    public void setDoNotshowSpamBar(boolean doNotshowSpamBar) {
        DoNotshowSpamBar = doNotshowSpamBar;
    }

    public RealmList<RealmAvatar> getAvatars() {
        RealmList<RealmAvatar> avatars = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, id).findAllSorted(RealmAvatarFields.ID, Sort.ASCENDING)) {
            avatars.add(avatar);
        }
        realm.close();
        return avatars;
    }

    public RealmAvatar getLastAvatar() {
        RealmList<RealmAvatar> avatars = getAvatars();
        if (avatars.isEmpty()) {
            return null;
        }
        // make sure return last avatar which has attachment
        for (int i = avatars.size() - 1; i >= 0; i--) {
            RealmAvatar avatar = getAvatars().get(i);
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
    }

    /**
     * fill object from proto to realm
     *
     * @param registeredUser proto that get from server
     * @param info object from RealmRegisteredInfo
     */

    public void fillRegisteredUserInfo(ProtoGlobal.RegisteredUser registeredUser, RealmRegisteredInfo info) {
        info.setUsername(registeredUser.getUsername());
        info.setPhoneNumber(Long.toString(registeredUser.getPhone()));
        info.setFirstName(registeredUser.getFirstName());
        info.setLastName(registeredUser.getLastName());
        info.setDisplayName(registeredUser.getDisplayName());
        info.setInitials(registeredUser.getInitials());
        info.setColor(registeredUser.getColor());
        info.setStatus(registeredUser.getStatus().toString());
        info.setLastName(registeredUser.getLastName());
        info.setAvatarCount(registeredUser.getAvatarCount());
        info.setMutual(registeredUser.getMutual());
        info.setLastSeen(registeredUser.getLastSeen());
        info.setCacheId(registeredUser.getCacheId());
    }
}
