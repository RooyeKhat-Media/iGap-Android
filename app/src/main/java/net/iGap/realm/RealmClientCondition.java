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

import android.util.Log;

import net.iGap.module.enums.ClientConditionOffline;
import net.iGap.module.enums.ClientConditionVersion;
import net.iGap.proto.ProtoClientCondition;
import net.iGap.proto.ProtoGlobal;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

// note: realm doesn't support enum
// as a workaround, we save its toString() value
// https://github.com/realm/realm-java/issues/776
public class RealmClientCondition extends RealmObject {
    @PrimaryKey
    private long roomId;
    private long messageVersion;
    private long statusVersion;
    private long deleteVersion;
    private RealmList<RealmOfflineDelete> offlineDeleted;
    private RealmList<RealmOfflineEdited> offlineEdited;
    private RealmList<RealmOfflineSeen> offlineSeen;
    private RealmList<RealmOfflineListen> offlineListen;
    private long clearId;
    private long cacheStartId;
    private long cacheEndId;
    private String offlineMute;

    /**
     * Hint: use this method in Transaction
     */
    public static RealmClientCondition putOrUpdateIncomplete(Realm realm, long roomId) {
        RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
        if (realmClientCondition == null) {
            realmClientCondition = realm.createObject(RealmClientCondition.class, roomId);
        }
        return realmClientCondition;
    }

    public static void setClearId(final long roomId, final long clearId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setClearId(clearId);
                }
            }
        });
        realm.close();
    }

    public static void setVersion(final long roomId, final long version, final ClientConditionVersion conditionVersion) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    if (conditionVersion == ClientConditionVersion.EDIT) {
                        realmClientCondition.setMessageVersion(version);
                    } else if (conditionVersion == ClientConditionVersion.STATUS) {
                        realmClientCondition.setStatusVersion(version);
                    } else if (conditionVersion == ClientConditionVersion.DELETE) {
                        realmClientCondition.setDeleteVersion(version);
                    }
                }
            }
        });
        realm.close();
    }

    public static void addOfflineEdit(long roomId, final long messageId, final String message) {
        Realm realm = Realm.getDefaultInstance();
        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realmClientCondition != null) {
                    realmClientCondition.getOfflineEdited().add(RealmOfflineEdited.put(realm, messageId, message));
                }
            }
        });
        realm.close();
    }

    public static void addOfflineDelete(Realm realm, RealmClientCondition realmClientCondition, long messageId, ProtoGlobal.Room.Type roomType, boolean bothDelete) {
        realmClientCondition.getOfflineDeleted().add(RealmOfflineDelete.setOfflineDeleted(realm, messageId, roomType, bothDelete));
    }

    public static void addOfflineSeen(final long roomId, final long messageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.getOfflineSeen().add(RealmOfflineSeen.put(realm, messageId));
                }
            }
        });
        realm.close();
    }

    public static void addOfflineSeen(Realm realm, RealmClientCondition realmClientCondition, long messageId) {
        realmClientCondition.getOfflineSeen().add(RealmOfflineSeen.put(realm, messageId));
    }

    public static void addOfflineListen(final long roomId, final long messageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.getOfflineListen().add(RealmOfflineListen.put(realm, messageId));
                }
            }
        });
        realm.close();
    }

    public static void deleteOfflineAction(final long messageId, ProtoGlobal.RoomMessageStatus messageStatus) {
        if (messageStatus == ProtoGlobal.RoomMessageStatus.SEEN) {
            deleteOfflineAction(messageId, ClientConditionOffline.SEEN);
        } else if (messageStatus == ProtoGlobal.RoomMessageStatus.LISTENED) {
            deleteOfflineAction(messageId, ClientConditionOffline.LISTEN);
        }
    }

    public static void deleteOfflineAction(final long messageId, final ClientConditionOffline messageStatus) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmClientCondition realmClientCondition;
                if (messageStatus == ClientConditionOffline.DELETE) {
                    realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.OFFLINE_DELETED.OFFLINE_DELETE, messageId).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.getOfflineDeleted().deleteFirstFromRealm();
                    }
                } else if (messageStatus == ClientConditionOffline.EDIT) {
                    realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.OFFLINE_EDITED.MESSAGE_ID, messageId).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.getOfflineEdited().deleteFirstFromRealm();
                    }
                } else if (messageStatus == ClientConditionOffline.SEEN) {
                    realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.OFFLINE_SEEN.OFFLINE_SEEN, messageId).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.getOfflineSeen().deleteFirstFromRealm();
                    }
                } else if (messageStatus == ClientConditionOffline.LISTEN) {
                    realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.OFFLINE_LISTEN.OFFLINE_LISTEN, messageId).findFirst();
                    if (realmClientCondition != null) {
                        realmClientCondition.getOfflineListen().deleteFirstFromRealm();
                    }
                }
            }
        });
        realm.close();
    }

    public static void clearOfflineAction() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (final RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
                    realmClientCondition.setOfflineEdited(new RealmList<RealmOfflineEdited>());
                    realmClientCondition.setOfflineDeleted(new RealmList<RealmOfflineDelete>());
                    realmClientCondition.setOfflineSeen(new RealmList<RealmOfflineSeen>());
                    realmClientCondition.setOfflineListen(new RealmList<RealmOfflineListen>());
                }
            }
        });
        realm.close();
    }

    /**
     * Hint: call this method in Transaction
     */
    public static void deleteCondition(Realm realm, long roomId) {
        realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findAll().deleteAllFromRealm();
    }

    public static ProtoClientCondition.ClientCondition.Builder computeClientCondition(Long roomId) {

        Realm realm = Realm.getDefaultInstance();
        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).count() == 0) {
            return clientCondition;
        }

        RealmResults<RealmClientCondition> clientConditionList;

        if (roomId != null) {
            clientConditionList = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findAll();
        } else {
            /**
             * find all client condition that deleted is false
             *
             * hint: we use {@link net.iGap.realm.RealmRoom#putChatToDatabase(List, boolean, boolean)} for add room to realm
             * and in this method also we called {@link net.iGap.realm.RealmRoom#putChatToClientCondition(Realm, ProtoGlobal.Room)}
             * so for each room exist a RealmClientCondition, but we just need RealmClientCondition for rooms that aren't deleted.
             *
             * it is better that client just create RealmClientCondition for rooms that need really.
             */
            RealmQuery<RealmClientCondition> conditionQuery = realm.where(RealmClientCondition.class);
            if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll().size() > 1) {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
                    conditionQuery.equalTo(RealmClientConditionFields.ROOM_ID, realmRoom.getId()).or();
                }
            } else {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, false).findAll()) {
                    conditionQuery.equalTo(RealmClientConditionFields.ROOM_ID, realmRoom.getId());
                }
            }
            clientConditionList = conditionQuery.findAll();
        }

        for (RealmClientCondition realmClientCondition : clientConditionList) {
            if (realmClientCondition.isManaged()) {
                ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
                room.setRoomId(realmClientCondition.getRoomId());

                Number messageVersion = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().max(RealmRoomMessageFields.MESSAGE_VERSION);
                if (messageVersion != null) {
                    room.setMessageVersion((long) messageVersion);
                }

                Number statusVersion = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().max(RealmRoomMessageFields.STATUS_VERSION);
                if (statusVersion != null) {
                    room.setStatusVersion((long) statusVersion);
                }

                room.setDeleteVersion(realmClientCondition.getDeleteVersion());

                for (RealmOfflineDelete offlineDeleted : realmClientCondition.getOfflineDeleted()) {
                    ProtoClientCondition.ClientCondition.Room.OfflineDeleted.Builder offlineDeletedBuilder = ProtoClientCondition.ClientCondition.Room.OfflineDeleted.newBuilder();
                    offlineDeletedBuilder.setMessageId(offlineDeleted.getOfflineDelete());
                    offlineDeletedBuilder.setBoth(offlineDeleted.isBoth());
                    room.addOfflineDeleted(offlineDeletedBuilder);
                    //room.addOfflineDeleted(offlineDeleted.getOfflineDelete()); //DEPRECATED
                }

                for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) {
                    ProtoClientCondition.ClientCondition.Room.OfflineEdited.Builder offlineEdited = ProtoClientCondition.ClientCondition.Room.OfflineEdited.newBuilder();
                    offlineEdited.setMessageId(realmOfflineEdited.getMessageId());
                    offlineEdited.setMessage(realmOfflineEdited.getMessage());
                    room.addOfflineEdited(offlineEdited);
                }

                for (RealmOfflineSeen offlineSeen : realmClientCondition.getOfflineSeen()) {
                    room.addOfflineSeen(offlineSeen.getOfflineSeen());
                }

                for (RealmOfflineListen offlineListen : realmClientCondition.getOfflineListen()) {
                    room.addOfflineListened(offlineListen.getOfflineListen());
                }

                room.setClearId(realmClientCondition.getClearId());

                List<RealmRoomMessage> allItemsAscending = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                for (RealmRoomMessage item : allItemsAscending) {
                    if (item != null) {
                        room.setCacheStartId(item.getMessageId());
                        break;
                    }
                }

                List<RealmRoomMessage> allItems = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                for (RealmRoomMessage item : allItems) {
                    if (item != null) {
                        room.setCacheEndId(item.getMessageId());//Done
                        break;
                    }
                }

                if (realmClientCondition.getOfflineMute() != null) {
                    if (realmClientCondition.getOfflineMute().equals(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString())) {
                        room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
                    } else {
                        room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
                    }
                } else {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
                }

                clientCondition.addRooms(room);
                Log.i("CLI", "room : " + room);
            }
        }
        realm.close();

        return clientCondition;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(long messageVersion) {
        this.messageVersion = messageVersion;
    }

    public long getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(long statusVersion) {
        this.statusVersion = statusVersion;
    }

    public long getDeleteVersion() {
        return deleteVersion;
    }

    public void setDeleteVersion(long deleteVersion) {
        this.deleteVersion = deleteVersion;
    }

    public RealmList<RealmOfflineDelete> getOfflineDeleted() {
        return offlineDeleted;
    }

    public void setOfflineDeleted(RealmList<RealmOfflineDelete> offlineDeleted) {
        this.offlineDeleted = offlineDeleted;
    }

    public RealmList<RealmOfflineEdited> getOfflineEdited() {
        return offlineEdited;
    }

    public void setOfflineEdited(RealmList<RealmOfflineEdited> offlineEdited) {
        this.offlineEdited = offlineEdited;
    }

    public RealmList<RealmOfflineSeen> getOfflineSeen() {
        return offlineSeen;
    }

    public void setOfflineSeen(RealmList<RealmOfflineSeen> offlineSeen) {
        this.offlineSeen = offlineSeen;
    }

    public boolean containsOfflineSeen(long l) {
        for (RealmOfflineSeen r : offlineSeen) {
            if (r.getOfflineSeen() == l) {
                return true;
            }
        }
        return false;
    }

    public long getClearId() {
        return clearId;
    }

    public void setClearId(long clearId) {
        this.clearId = clearId;
    }

    public long getCacheStartId() {
        return cacheStartId;
    }

    public void setCacheStartId(long cacheStartId) {
        this.cacheStartId = cacheStartId;
    }

    public long getCacheEndId() {
        return cacheEndId;
    }

    public void setCacheEndId(long cacheEndId) {
        this.cacheEndId = cacheEndId;
    }

    public String getOfflineMute() {
        return offlineMute;
    }

    public void setOfflineMute(String offlineMute) {
        this.offlineMute = offlineMute;
    }

    public RealmList<RealmOfflineListen> getOfflineListen() {
        return offlineListen;
    }

    public void setOfflineListen(RealmList<RealmOfflineListen> offlineListen) {
        this.offlineListen = offlineListen;
    }
}
