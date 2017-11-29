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
import net.iGap.helper.HelperString;
import net.iGap.proto.ProtoGlobal;

public class RealmUserInfo extends RealmObject {

    private RealmRegisteredInfo userInfo;
    private boolean registrationStatus;
    private String email;
    private int gender;
    private boolean isPassCode;
    private boolean isFingerPrint;
    private String passCode;
    private int kindPassCode;
    private int selfRemove;
    private String token;
    private String authorHash;

    public RealmRegisteredInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(RealmRegisteredInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean getUserRegistrationState() {
        return this.registrationStatus;
    }

    public void setUserRegistrationState(boolean value) {
        this.registrationStatus = value;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {

        try {
            this.email = email;
        } catch (Exception e) {
            this.email = HelperString.getUtf8String(email);
        }
    }

    public boolean isFingerPrint() {
        return isFingerPrint;
    }

    public int getKindPassCode() {
        return kindPassCode;
    }

    public void setKindPassCode(int kindPassCode) {
        this.kindPassCode = kindPassCode;
    }

    public void setFingerPrint(boolean fingerPrint) {
        isFingerPrint = fingerPrint;
    }

    public ProtoGlobal.Gender getGender() {
        return ProtoGlobal.Gender.valueOf(this.gender);
    }

    public void setGender(ProtoGlobal.Gender value) {
        this.gender = value.getNumber();
    }

    public boolean isPassCode() {
        return isPassCode;
    }

    public void setPassCode(boolean passCode) {
        isPassCode = passCode;
    }

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public int getSelfRemove() {
        return selfRemove;
    }

    public void setSelfRemove(int selfRemove) {
        this.selfRemove = selfRemove;
    }

    public long getUserId() {
        return this.userInfo.getId();
    }

    public String getAuthorHash() {
        return authorHash;
    }

    public void setAuthorHash(String authorHash) {
        this.authorHash = authorHash;
    }

    public boolean isAuthorMe(String author) {
        if (author.equals(authorHash)) {
            return true;
        }
        return false;
    }

    public static RealmUserInfo getRealmUserInfo(Realm realm) {
        return realm.where(RealmUserInfo.class).findFirst();
    }

    public static RealmUserInfo putOrUpdate(Realm realm, long userId, String username, String phoneNumber, String token, String authorHash) {
        RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
        if (userInfo == null) {
            userInfo = realm.createObject(RealmUserInfo.class);
            RealmRegisteredInfo registeredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (registeredInfo == null) {
                registeredInfo = realm.createObject(RealmRegisteredInfo.class, userId);
            }
            userInfo.setUserInfo(registeredInfo);
        }
        userInfo.getUserInfo().setUsername(username);
        userInfo.getUserInfo().setPhoneNumber(phoneNumber);
        userInfo.setToken(token);
        userInfo.setAuthorHash(authorHash);
        userInfo.setUserRegistrationState(true);
        return userInfo;
    }

    public static RealmUserInfo putOrUpdate(Realm realm, ProtoGlobal.RegisteredUser registeredUser) {
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo != null) {
            realmUserInfo.setUserInfo(RealmRegisteredInfo.putOrUpdate(realm, registeredUser));
            realmUserInfo.getUserInfo().setDisplayName(registeredUser.getDisplayName());
            realmUserInfo.getUserInfo().setInitials(registeredUser.getInitials());
            realmUserInfo.getUserInfo().setColor(registeredUser.getColor());
            realmUserInfo.setUserRegistrationState(true);
        }
        return realmUserInfo;
    }

    public static void updateGender(final ProtoGlobal.Gender gender) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setGender(gender);
                }
            }
        });
        realm.close();
    }

    public static void updateSelfRemove(final int selfRemove) {
        Realm realm1 = Realm.getDefaultInstance();
        realm1.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setSelfRemove(selfRemove);
                }
            }
        });
        realm1.close();
    }

    public static void updateEmail(final String email) {
        Realm realm1 = Realm.getDefaultInstance();
        realm1.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    realmUserInfo.setEmail(email);
                }
            }
        });
        realm1.close();
    }

    public static void updateNickname(final String displayName, final String initials) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    RealmRegisteredInfo realmRegisteredInfo = realmUserInfo.getUserInfo();
                    if (realmRegisteredInfo != null) {
                        realmRegisteredInfo.setDisplayName(displayName);
                        realmRegisteredInfo.setInitials(initials);
                    }
                }
            }
        });
        realm.close();
    }

    public static void updateUsername(final String username) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
                if (realmUserInfo != null) {
                    RealmRegisteredInfo realmRegisteredInfo = realmUserInfo.getUserInfo();
                    if (realmRegisteredInfo != null) {
                        realmRegisteredInfo.setUsername(username);
                    }
                }
            }
        });
        realm.close();
    }
}
