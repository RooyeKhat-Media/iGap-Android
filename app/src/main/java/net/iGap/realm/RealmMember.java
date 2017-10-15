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

import android.os.Handler;
import android.os.Looper;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGroupGetMemberList;

public class RealmMember extends RealmObject {

    @PrimaryKey private long id;

    private long peerId;
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeerId() {
        return peerId;
    }

    public void setPeerId(long peerId) {
        this.peerId = peerId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static void convertProtoMemberListToRealmMember(final ProtoChannelGetMemberList.ChannelGetMemberListResponse.Builder builder, final String identity) {
        final RealmList<RealmMember> newMembers = new RealmList<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                final List<ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member> members = new ArrayList<>();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                        if (realmRoom != null) {

                            members.clear();
                            newMembers.clear();
                            for (ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member : builder.getMemberList()) {

                                final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, member.getUserId());
                                if (realmRegisteredInfo != null) {
                                    RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                    realmMem.setRole(member.getRole().toString());
                                    realmMem.setPeerId(member.getUserId());
                                    newMembers.add(realmMem);
                                } else {
                                    members.add(member);
                                }
                            }

                            newMembers.addAll(0, realmRoom.getChannelRoom().getMembers());
                            realmRoom.getChannelRoom().setMembers(newMembers);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        realm.close();
                        if (G.onChannelGetMemberList != null) {
                            G.onChannelGetMemberList.onChannelGetMemberList(members);
                        }
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    public static void convertProtoMemberListToRealmMember(final ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder builder, final String identity) {
        final RealmList<RealmMember> newMembers = new RealmList<>();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                final List<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member> members = new ArrayList<ProtoGroupGetMemberList.GroupGetMemberListResponse.Member>();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, Long.parseLong(identity)).findFirst();
                        if (realmRoom != null) {

                            members.clear();
                            newMembers.clear();
                            for (ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member : builder.getMemberList()) {

                                final RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, member.getUserId());
                                if (realmRegisteredInfo != null) {
                                    RealmMember realmMem = realm.createObject(RealmMember.class, SUID.id().get());
                                    realmMem.setRole(member.getRole().toString());
                                    realmMem.setPeerId(member.getUserId());
                                    newMembers.add(realmMem);
                                } else {
                                    members.add(member);
                                }
                            }

                            newMembers.addAll(0, realmRoom.getGroupRoom().getMembers());
                            realmRoom.getGroupRoom().setMembers(newMembers);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        realm.close();
                        if (G.onGroupGetMemberList != null) {
                            G.onGroupGetMemberList.onGroupGetMemberList(members);
                        }
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }
}
