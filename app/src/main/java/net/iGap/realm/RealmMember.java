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
import android.support.annotation.Nullable;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;
import net.iGap.G;
import net.iGap.module.SUID;
import net.iGap.proto.ProtoChannelGetMemberList;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupGetMemberList;

import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

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




    public static RealmMember put(Realm realm, long userId, String role) {
        RealmMember realmMember = realm.createObject(RealmMember.class, SUID.id().get());
        realmMember.setRole(role);
        realmMember.setPeerId(userId);
        realmMember = realm.copyToRealm(realmMember);
        return realmMember;
    }

    public static RealmMember put(Realm realm, ProtoGroupGetMemberList.GroupGetMemberListResponse.Member member) {
        RealmMember realmMember = realm.createObject(RealmMember.class, SUID.id().get());
        realmMember.setRole(member.getRole().toString());
        realmMember.setPeerId(member.getUserId());
        realmMember = realm.copyToRealm(realmMember);
        return realmMember;
    }

    public static RealmMember put(Realm realm, ProtoChannelGetMemberList.ChannelGetMemberListResponse.Member member) {
        RealmMember realmMember = realm.createObject(RealmMember.class, SUID.id().get());
        realmMember.setRole(member.getRole().toString());
        realmMember.setPeerId(member.getUserId());
        realmMember = realm.copyToRealm(realmMember);
        return realmMember;
    }

    public static void deleteAllMembers(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realmRoom.getGroupRoom().getMembers() != null) {
                            realmRoom.getGroupRoom().getMembers().deleteAllFromRealm();
                        }
                    }
                });
            } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (realmRoom.getChannelRoom().getMembers() != null) {
                            realmRoom.getChannelRoom().getMembers().deleteAllFromRealm();
                        }
                    }
                });
            }
        }
        realm.close();
    }

    public static void addMember(final long roomId, final long userId, final String role) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                if (realmRoom != null) {
                    RealmList<RealmMember> members = new RealmList<>();
                    if (realmRoom.getType() == GROUP) {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            members = realmGroupRoom.getMembers();
                        }
                    } else {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            members = realmChannelRoom.getMembers();
                        }
                    }
                    members.add(RealmMember.put(realm, userId, role));
                }
            }
        });
        realm.close();
    }

    public static void updateMemberRole(final long roomId, final long memberId, final String role) {
        //TODO [Saeed Mozaffari] [2017-10-24 6:05 PM] - Can Write Better Code?
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    RealmList<RealmMember> realmMemberRealmList = null;
                    if (realmRoom.getType() == GROUP) {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmMemberRealmList = realmGroupRoom.getMembers();
                        }
                    } else {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmMemberRealmList = realmChannelRoom.getMembers();
                        }
                    }

                    if (realmMemberRealmList != null) {
                        for (RealmMember member : realmMemberRealmList) {
                            if (member.getPeerId() == memberId) {
                                member.setRole(role);
                                break;
                            }
                        }
                    }
                }
            }
        });
        realm.close();
    }

    public static void kickMember(final long roomId, final long userId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                kickMember(realm, roomId, userId);
            }
        });
        realm.close();
    }

    public static boolean kickMember(Realm realm, final long roomId, final long userId) {

        //test this <code>realmRoom.getGroupRoom().getMembers().where().equalTo(RealmMemberFields.PEER_ID, builder.getMemberId()).findFirst();</code> for kick member
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            RealmList<RealmMember> realmMembers = new RealmList<>();
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() != null) {
                    realmMembers = realmRoom.getGroupRoom().getMembers();
                }
            } else {
                if (realmRoom.getChannelRoom() != null) {
                    realmMembers = realmRoom.getChannelRoom().getMembers();
                }
            }
            for (RealmMember realmMember : realmMembers) {
                if (realmMember.getPeerId() == userId) {
                    realmMember.deleteFromRealm();
                    return true;
                }
            }
        }
        return false;
    }

    public static RealmList<RealmMember> getMembers(Realm realm, long roomId) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        RealmList<RealmMember> memberList = new RealmList<>();
        if (realmRoom != null) {
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() != null) {
                    memberList = realmRoom.getGroupRoom().getMembers();
                }
            } else {
                if (realmRoom.getChannelRoom() != null) {
                    memberList = realmRoom.getChannelRoom().getMembers();
                }
            }
        }
        return memberList;
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
                                    newMembers.add(RealmMember.put(realm, member.getUserId(), member.getRole().toString()));
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
                                    newMembers.add(RealmMember.put(realm, member.getUserId(), member.getRole().toString()));
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

    public static RealmResults<RealmMember> filterMember(long roomId, @Nullable String filter) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null) {
            return emptyResult(realm);
        }

        RealmResults<RealmMember> searchMember = emptyResult(realm);
        RealmResults<RealmRegisteredInfo> findMember;

        if (filter != null && filter.length() > 0) {
            findMember = realm.where(RealmRegisteredInfo.class).contains(RealmRegisteredInfoFields.DISPLAY_NAME, filter, Case.INSENSITIVE).findAllSorted(RealmRegisteredInfoFields.DISPLAY_NAME);
        } else {
            findMember = realm.where(RealmRegisteredInfo.class).equalTo(RealmRoomFields.ID, roomId).findAllSorted(RealmRegisteredInfoFields.DISPLAY_NAME);
        }
        try {
            RealmQuery<RealmMember> query;
            if (realmRoom.getType() == GROUP) {
                query = realmRoom.getGroupRoom().getMembers().where();
            } else {
                query = realmRoom.getChannelRoom().getMembers().where();
            }

            for (int i = 0; i < findMember.size(); i++) {
                if (i != 0) {
                    query = query.or();
                }
                query = query.equalTo(RealmMemberFields.PEER_ID, findMember.get(i).getId());
            }
            if (findMember.size() > 0 || (filter == null || filter.length() == 0)) {
                searchMember = query.findAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchMember;
    }

    /**
     * make empty result for avoid from null state
     */
    public static RealmResults<RealmMember> emptyResult(Realm realm) {
        return realm.where(RealmMember.class).equalTo(RealmMemberFields.ID, -1).findAll();
    }

    public static RealmResults<RealmMember> filterRole(long roomId, ProtoGlobal.Room.Type roomType, String role) {
        Realm realm = Realm.getDefaultInstance();
        RealmList<RealmMember> memberList = null;
        RealmResults<RealmMember> mList = emptyResult(realm);
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            if (roomType == ProtoGlobal.Room.Type.GROUP) {
                memberList = realmRoom.getGroupRoom().getMembers();
            } else if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
                memberList = realmRoom.getChannelRoom().getMembers();
            }

            if (memberList != null && memberList.size() > 0) {
                if (role.equals(ProtoGroupGetMemberList.GroupGetMemberList.FilterRole.ALL.toString())) {
                    mList = memberList.where().findAll();
                } else {
                    mList = memberList.where().equalTo(RealmMemberFields.ROLE, role).findAll();
                }
            }
        }
        return mList;
    }
}
