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

import com.iGap.module.enums.ChannelChatRole;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class RealmChannelRoom extends RealmObject {
    private String role;
    private String participants_count_label;
    private String participants_count_limit_label;
    private String description;
    private String inviteLink;
    private int avatarCount;
    private RealmNotificationSetting realmNotificationSetting;
    private RealmList<RealmMember> members;
    private String invite_token;
    private String username;
    private boolean isPrivate;
    private boolean isSignature = false;
    private long seenId;

    /**
     * convert ProtoGlobal.ChannelRoom to RealmChannelRoom
     *
     * @param room ProtoGlobal.ChannelRoom
     * @return RealmChannelRoom
     */
    public static RealmChannelRoom convert(ProtoGlobal.ChannelRoom room,
                                           RealmChannelRoom realmChannelRoom, Realm realm) {
        if (realmChannelRoom == null) {
            realmChannelRoom = realm.createObject(RealmChannelRoom.class);
        }
        realmChannelRoom.setParticipantsCountLabel(room.getParticipantsCountLabel());
        realmChannelRoom.setRole(ChannelChatRole.convert(room.getRole()));
        realmChannelRoom.setInviteLink(room.getPrivateExtra().getInviteLink());
        realmChannelRoom.setInvite_token(room.getPrivateExtra().getInviteToken());
        realmChannelRoom.setUsername(room.getPublicExtra().getUsername());
        return realmChannelRoom;
    }

    public ChannelChatRole getRole() {
        return (role != null) ? ChannelChatRole.valueOf(role) : null;
    }

    public void setRole(ChannelChatRole role) {
        this.role = role.toString();
    }

    public String getParticipantsCountLabel() {
        return participants_count_label;
    }

    public void setParticipantsCountLabel(String participants_count_label) {
        this.participants_count_label = participants_count_label;
    }

    public String getParticipants_count_limit_label() {
        return participants_count_limit_label;
    }

    public void setParticipants_count_limit_label(String participants_count_limit_label) {
        this.participants_count_limit_label = participants_count_limit_label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvatarCount() {
        return avatarCount;
    }

    public void setAvatarCount(int avatarCount) {
        this.avatarCount = avatarCount;
    }

    public RealmNotificationSetting getRealmNotificationSetting() {
        return realmNotificationSetting;
    }

    public void setRealmNotificationSetting(RealmNotificationSetting realmNotificationSetting) {
        this.realmNotificationSetting = realmNotificationSetting;
    }

    public String getInviteLink() {
        return inviteLink;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    public RealmList<RealmMember> getMembers() {
        return members;
    }

    public void setMembers(RealmList<RealmMember> members) {
        this.members = members;
    }

//    public String getInvite_link() {
//        return invite_link;
//    }
//
//    public void setInvite_link(String invite_link) {
//        this.invite_link = invite_link;
//    }

    public String getInvite_token() {
        return invite_token;
    }

    public void setInvite_token(String invite_token) {
        this.invite_token = invite_token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isSignature() {
        return isSignature;
    }

    public void setSignature(boolean signature) {
        isSignature = signature;
    }

    public long getSeenId() {
        return seenId;
    }

    public void setSeenId(long seenId) {
        this.seenId = seenId;
    }
}
