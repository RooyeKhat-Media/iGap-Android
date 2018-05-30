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

import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;

public class RealmContacts extends RealmObject {

    private long id;
    private String username;
    private long phone;
    private String first_name;
    private String last_name;
    private String display_name;
    private String initials;
    private String color;
    private String status;
    private String cacheId;
    private long last_seen;
    private int avatarCount;
    private String bio;
    private boolean verified;
    private boolean mutual;
    private RealmAvatar avatar;
    private boolean blockUser = false;

    public static void putOrUpdate(Realm realm, ProtoGlobal.RegisteredUser registerUser) {
        RealmContacts listResponse = realm.createObject(RealmContacts.class);
        listResponse.setId(registerUser.getId());
        listResponse.setUsername(registerUser.getUsername());
        listResponse.setPhone(registerUser.getPhone());
        listResponse.setFirst_name(registerUser.getFirstName());
        listResponse.setLast_name(registerUser.getLastName());
        listResponse.setDisplay_name(registerUser.getDisplayName());
        listResponse.setInitials(registerUser.getInitials());
        listResponse.setColor(registerUser.getColor());
        listResponse.setStatus(registerUser.getStatus().toString());
        listResponse.setLast_seen(registerUser.getLastSeen());
        listResponse.setAvatarCount(registerUser.getAvatarCount());
        listResponse.setCacheId(registerUser.getCacheId());
        listResponse.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, registerUser.getId(), registerUser.getAvatar()));
        listResponse.setBio(registerUser.getBio());
        listResponse.setVerified(registerUser.getVerified());
        listResponse.setMutual(registerUser.getMutual());
    }

    public static void deleteContact(final String phone) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmContacts contact = realm.where(RealmContacts.class).equalTo(RealmContactsFields.PHONE, Long.parseLong(phone)).findFirst();
                if (contact != null) {
                    contact.deleteFromRealm();
                }
            }
        });
        realm.close();
    }

    public static void updateName(final long userId, final String firstName, final String lastName, final String initials) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmContacts contact = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
                if (contact != null) {
                    contact.setFirst_name(firstName);
                    contact.setLast_name(lastName);
                    contact.setDisplay_name((firstName + " " + lastName).trim());
                    contact.setInitials(initials);
                }
            }
        });
        realm.close();
    }

    public static void updateBlock(final long userId, final boolean block) {
        Realm realm = Realm.getDefaultInstance();
        final RealmContacts realmContacts = realm.where(RealmContacts.class).equalTo(RealmContactsFields.ID, userId).findFirst();
        if (realmContacts != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmContacts.setBlockUser(block);
                }
            });
        }
        realm.close();
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
        try {
            this.username = username;
        } catch (Exception e) {
            this.username = HelperString.getUtf8String(username);
        }
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        try {
            this.first_name = first_name;
        } catch (Exception e) {
            this.first_name = HelperString.getUtf8String(first_name);
        }
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        try {
            this.last_name = last_name;
        } catch (Exception e) {
            this.last_name = HelperString.getUtf8String(last_name);
        }
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        try {
            this.display_name = display_name;
        } catch (Exception e) {
            this.display_name = HelperString.getUtf8String(display_name);
        }
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

    public long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(long last_seen) {
        this.last_seen = last_seen;
    }

    public String getStatus() {
        return status;
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

    public int getAvatarCount() {
        return avatarCount;
    }

    public void setAvatarCount(int avatarCount) {
        this.avatarCount = avatarCount;
    }

    public RealmAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(RealmAvatar avatar) {
        this.avatar = avatar;
    }

    public boolean isBlockUser() {
        return blockUser;
    }

    public void setBlockUser(boolean blockUser) {
        this.blockUser = blockUser;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isMutual() {
        return mutual;
    }

    public void setMutual(boolean mutual) {
        this.mutual = mutual;
    }
}
