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
import android.text.format.DateUtils;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperString;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestClientGetRoom;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

import static net.iGap.G.context;
import static net.iGap.G.userId;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

public class RealmRoom extends RealmObject {
    @PrimaryKey
    private long id;
    private String type;
    private String title;
    private String initials;
    private String color;
    private int unreadCount;
    private boolean readOnly;
    private RealmChatRoom chatRoom;
    private boolean mute;
    private RealmGroupRoom groupRoom;
    private RealmChannelRoom channelRoom;
    private RealmRoomMessage lastMessage;
    private RealmRoomMessage firstUnreadMessage;
    private RealmRoomDraft draft;
    private RealmDraftFile draftFile;
    private RealmAvatar avatar;
    private long updatedTime;
    private String sharedMediaCount = "";
    //if it was needed in the future we can combine this two under fields in RealmAction (actionStateUserId and actionState).
    private long actionStateUserId;
    private String actionState;
    private boolean isDeleted = false;
    private boolean isPinned;
    private long pinId;
    /**
     * client need keepRoom info for show in forward message that forward
     * from a room that user don't have that room
     */
    private boolean keepRoom = false;
    private long lastScrollPositionMessageId;
    private int lastScrollPositionOffset;

    public RealmRoom() {

    }

    public RealmRoom(long id) {
        this.id = id;
    }

    public static RealmRoom getRealmRoom(Realm realm, long roomId) {
        return realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
    }

    public static void putOrUpdate(final ProtoGlobal.Room room) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                putOrUpdate(room, realm);
            }
        });
        realm.close();
    }

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     * hint : call this method in execute transaction
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom putOrUpdate(ProtoGlobal.Room room, Realm realm) {

        RealmClientCondition.putOrUpdateIncomplete(realm, room.getId());

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom == null) {
            realmRoom = realm.createObject(RealmRoom.class, room.getId());
        }

        realmRoom.isDeleted = false;
        realmRoom.keepRoom = false;

        realmRoom.setColor(room.getColor());
        realmRoom.setInitials(room.getInitials());
        realmRoom.setTitle(room.getTitle());
        realmRoom.setType(RoomType.convert(room.getType()));
        realmRoom.setUnreadCount(room.getUnreadCount());
        realmRoom.setReadOnly(room.getReadOnly());
        realmRoom.setMute(room.getRoomMute());
        realmRoom.setPinId(room.getPinId());
        if (room.getPinId() > 0) {
            realmRoom.setPinned(true);
        } else {
            realmRoom.setPinned(false);
        }
        realmRoom.setActionState(null, 0);
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(RealmChannelRoom.convert(room.getChannelRoomExtra(), realmRoom.getChannelRoom(), realm));
                realmRoom.getChannelRoom().setDescription(room.getChannelRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, realmRoom.getId(), room.getChannelRoomExtra().getAvatar()));
                realmRoom.getChannelRoom().setInviteLink(room.getChannelRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getChannelRoom().setInvite_token(room.getChannelRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getChannelRoom().setUsername(room.getChannelRoomExtra().getPublicExtra().getUsername());
                realmRoom.getChannelRoom().setSeenId(room.getChannelRoomExtra().getSeenId());
                realmRoom.getChannelRoom().setPrivate(room.getChannelRoomExtra().hasPrivateExtra());
                realmRoom.getChannelRoom().setVerified(room.getChannelRoomExtra().getVerified());
                realmRoom.getChannelRoom().setReactionStatus(room.getChannelRoomExtra().getReactionStatus());
                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(room.getChatRoomExtra()));
                /**
                 * update user info for detect current status(online,offline,...)
                 * and also update another info
                 */

                RealmRegisteredInfo.putOrUpdate(realm, room.getChatRoomExtra().getPeer());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, room.getChatRoomExtra().getPeer().getId(), room.getChatRoomExtra().getPeer().getAvatar()));
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(RealmGroupRoom.putOrUpdate(room.getGroupRoomExtra(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putOrUpdateAndManageDelete(realm, realmRoom.getId(), room.getGroupRoomExtra().getAvatar()));
                realmRoom.getGroupRoom().setInvite_token(room.getGroupRoomExtra().getPrivateExtra().getInviteToken());
                if (!room.getGroupRoomExtra().getPrivateExtra().getInviteLink().isEmpty()) {
                    realmRoom.getGroupRoom().setInvite_link(room.getGroupRoomExtra().getPrivateExtra().getInviteLink());
                }
                realmRoom.getGroupRoom().setUsername(room.getGroupRoomExtra().getPublicExtra().getUsername());
                realmRoom.getGroupRoom().setPrivate(room.getGroupRoomExtra().hasPrivateExtra());
                break;
        }

        /**
         * set setFirstUnreadMessage
         */
        if (room.hasFirstUnreadMessage()) {
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(room.getFirstUnreadMessage(), room.getId(), false, false, realm);
            realmRoomMessage.setFutureMessageId(room.getFirstUnreadMessage().getMessageId());
            realmRoom.setFirstUnreadMessage(realmRoomMessage);
        }

        if (room.hasLastMessage()) {
            /**
             * if this message not exist set gap otherwise don't change in gap state
             */
            boolean setGap = false;
            if (!RealmRoomMessage.existMessage(room.getLastMessage().getMessageId())) {
                setGap = true;
            }
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdate(room.getLastMessage(), room.getId(), false, false, realm);
            if (setGap) {
                realmRoomMessage.setPreviousMessageId(room.getLastMessage().getMessageId());
                realmRoomMessage.setFutureMessageId(room.getLastMessage().getMessageId());
            }
            realmRoom.setLastMessage(realmRoomMessage);
            if (room.getLastMessage().getUpdateTime() == 0) {
                realmRoom.setUpdatedTime(room.getLastMessage().getCreateTime() * (DateUtils.SECOND_IN_MILLIS));
            } else {
                realmRoom.setUpdatedTime(room.getLastMessage().getUpdateTime() * (DateUtils.SECOND_IN_MILLIS));
            }
        }

        realmRoom.setDraft(RealmRoomDraft.putOrUpdate(realm, realmRoom.getDraft(), room.getDraft().getMessage(), room.getDraft().getReplyTo()));

        return realmRoom;
    }

    /**
     * put fetched chat to database
     *
     * @param rooms ProtoGlobal.Room
     */
    public static void putChatToDatabase(final List<ProtoGlobal.Room> rooms, final boolean deleteBefore, final boolean cleanDeletedRoomMessage) {

        /**
         * (( hint : i don't used from mRealm instance ,because i have an error
         * that realm is closed, and for avoid from that error i used from
         * new instance for this action ))
         */

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        if (deleteBefore) {
                            RealmResults<RealmRoom> list = realm.where(RealmRoom.class).findAll();
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).setDeleted(true);
                            }
                        }

                        for (ProtoGlobal.Room room : rooms) {
                            RealmRoom.putOrUpdate(room, realm);
                        }

                        if (cleanDeletedRoomMessage) {
                            // delete messages and rooms that was deleted
                            RealmResults<RealmRoom> deletedRoomsList = realm.where(RealmRoom.class).equalTo(RealmRoomFields.IS_DELETED, true).equalTo(RealmRoomFields.KEEP_ROOM, false).findAll();
                            for (RealmRoom item : deletedRoomsList) {
                                /**
                                 * delete all message in deleted room
                                 *
                                 * hint: {@link RealmRoom#deleteRoom(long)} also do following actions but it is in
                                 * transaction and client can't use a transaction inside another
                                 */
                                RealmRoomMessage.deleteAllMessage(realm, item.getId());
                                RealmClientCondition.deleteCondition(realm, item.getId());
                                item.deleteFromRealm();
                            }
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {

                        realm.close();
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

    public static void convertAndSetDraft(final long roomId, final String message, final long replyToMessageId) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setDraft(RealmRoomDraft.put(realm, message, replyToMessageId));

                    if (G.onDraftMessage != null) {
                        G.onDraftMessage.onDraftMessage(roomId, message);
                    }
                }
            }
        });

        realm.close();
    }

    /**
     * create RealmRoom without info ,just have roomId and type
     * use this for detect that a room is a private channel
     * set deleted true and keep true for not showing in room list
     * and keep info for use in another subjects
     */
    public static void createEmptyRoom(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom == null) {
                    realmRoom = realm.createObject(RealmRoom.class, roomId);
                }
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setTitle("private channel");
                realmRoom.setDeleted(true);
                realmRoom.setKeepRoom(true);
            }
        });
        realm.close();
    }

    public static void needGetRoom(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom == null) {
            new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);
        }
        realm.close();
    }

    /**
     * check with this roomId that room is showing in room list or no
     */
    public static boolean isMainRoom(long roomId) {
        boolean isMainRoom = false;
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).equalTo(RealmRoomFields.IS_DELETED, false).findFirst();
        if (realmRoom != null) {
            isMainRoom = true;
        }
        realm.close();
        return isMainRoom;
    }

    /**
     * check updater author for detect that updater is another device for
     * this account and finally update unread count if another account
     * was saw message for this room
     *
     * @param roomId     roomId for room that get update status from that
     * @param authorHash updater author hash
     */
    public static void clearUnreadCount(long roomId, String authorHash, ProtoGlobal.RoomMessageStatus messageStatus, long messageId) {
        if (G.authorHash.equals(authorHash) && messageStatus == ProtoGlobal.RoomMessageStatus.SEEN) {

            Realm realm = Realm.getDefaultInstance();
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null && (realmRoom.getLastMessage() != null && realmRoom.getLastMessage().getMessageId() <= messageId)) {
                realmRoom.setUnreadCount(0);
            }
            realm.close();
        }
    }

    public static void updateMineRole(long roomId, long memberId, final String role) {

        Realm realm = Realm.getDefaultInstance();

        if (memberId == userId) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom == null) {
                realm.close();
                return;
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                        GroupChatRole mRole;
                        if (role.contains(GroupChatRole.ADMIN.toString())) {
                            mRole = GroupChatRole.ADMIN;
                        } else if (role.contains(GroupChatRole.MODERATOR.toString())) {
                            mRole = GroupChatRole.MODERATOR;
                        } else {
                            mRole = GroupChatRole.MEMBER;
                        }
                        if (realmRoom.getGroupRoom() != null) {
                            realmRoom.getGroupRoom().setRole(mRole);
                        }
                    } else {
                        ChannelChatRole mRole;
                        if (role.contains(ChannelChatRole.ADMIN.toString())) {
                            mRole = ChannelChatRole.ADMIN;
                        } else if (role.contains(ChannelChatRole.MODERATOR.toString())) {
                            mRole = ChannelChatRole.MODERATOR;
                        } else {
                            mRole = ChannelChatRole.MEMBER;
                        }
                        if (realmRoom.getChannelRoom() != null) {
                            realmRoom.getChannelRoom().setRole(mRole);
                        }

                        updateReadOnlyChannel(mRole, realmRoom);

                    }
                }
            });
        }

        realm.close();
    }

    private static void updateReadOnlyChannel(ChannelChatRole role, RealmRoom realmRoom) {
        switch (role) {
            case MODERATOR:
            case ADMIN:
            case OWNER:
                realmRoom.setReadOnly(false);
                break;
            default:
                realmRoom.setReadOnly(true);
                break;
        }
    }

    public static void updateMemberRole(final long roomId, final long userId, final String role) {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmList<RealmMember> realmMemberRealmList = null;
                    if (realmRoom.getType() == GROUP) {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmMemberRealmList = realmGroupRoom.getMembers();
                        }
                    } else if (realmRoom.getType() == CHANNEL) {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmMemberRealmList = realmChannelRoom.getMembers();
                        }
                    }

                    if (realmMemberRealmList != null) {
                        for (RealmMember member : realmMemberRealmList) {
                            if (member.getPeerId() == userId) {
                                member.setRole(role);
                                break;
                            }
                        }
                    }
                }
            });
        }
        realm.close();
    }

    /**
     * delete room with transaction from realm and also delete all messages
     * from this room and finally delete RealmClientCondition
     */
    public static void deleteRoom(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.deleteFromRealm();
                }

                RealmClientCondition.deleteCondition(realm, roomId);
                RealmRoomMessage.deleteAllMessage(realm, roomId);
            }
        });
        realm.close();

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /** call this listener for update tab bars unread count */
                if (G.onUnreadChange != null) {
                    G.onUnreadChange.onChange();
                }
            }
        }, 100);
    }

    public static void addOwnerToDatabase(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            if (realmRoom.getType() == CHANNEL) {
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    final RealmList<RealmMember> members = realmChannelRoom.getMembers();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            members.add(RealmMember.put(realm, userId, ProtoGlobal.ChannelRoom.Role.OWNER.toString()));
                        }
                    });
                }
            } else if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                if (realmGroupRoom != null) {
                    final RealmList<RealmMember> members = realmGroupRoom.getMembers();

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            members.add(RealmMember.put(realm, userId, ProtoGlobal.GroupRoom.Role.OWNER.toString()));
                        }
                    });
                }
            }
        }

        realm.close();
    }

    public static boolean showSignature(long roomId) {
        boolean signature = false;

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null && realmRoom.getChannelRoom() != null && realmRoom.getChannelRoom().isSignature()) {
            signature = true;
        }
        realm.close();

        return signature;
    }

    /**
     * if room isn't exist get info from server
     */
    public static boolean needUpdateRoomInfo(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realm.close();
            return false;
        }
        new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.justInfo);

        realm.close();
        return true;
    }

    public static void updateChatTitle(final long userId, final String title) {// TODO [Saeed Mozaffari] [2017-10-24 3:36 PM] - Can Write Better Code?
        Realm realm1 = Realm.getDefaultInstance();
        realm1.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).equalTo(RealmRoomFields.TYPE, ProtoGlobal.Room.Type.CHAT.toString()).findAll()) {
                    if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == userId) {
                        realmRoom.setTitle(title.trim());
                    }
                }
            }
        });
        realm1.close();
    }

    public static void updateMemberCount(long roomId, final ProtoGlobal.Room.Type roomType, final long memberCount) {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (roomType == CHANNEL) {
                    if (realmRoom != null && realmRoom.getChannelRoom() != null) {
                        realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                    }
                } else {
                    if (realmRoom != null && realmRoom.getGroupRoom() != null) {
                        realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                    }
                }
            }
        });
        realm.close();
    }

    public static void updateMemberCount(final long roomId, final boolean plus) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                updateMemberCount(realm, roomId, plus);
            }
        });
        realm.close();
    }

    public static int updateMemberCount(Realm realm, final long roomId, final boolean plus) {
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            String participantsCountLabel;
            if (realmRoom.getType() == GROUP) {
                if (realmRoom.getGroupRoom() == null) {
                    return 0;
                }
                participantsCountLabel = realmRoom.getGroupRoom().getParticipantsCountLabel();
            } else {
                if (realmRoom.getChannelRoom() == null) {
                    return 0;
                }
                participantsCountLabel = realmRoom.getChannelRoom().getParticipantsCountLabel();
            }

            if (HelperString.isNumeric(participantsCountLabel)) {
                int memberCount = Integer.parseInt(participantsCountLabel);
                if (plus) {
                    memberCount++;
                } else {
                    memberCount--;
                }

                if (realmRoom.getType() == GROUP) {
                    realmRoom.getGroupRoom().setParticipantsCountLabel(memberCount + "");
                } else {
                    realmRoom.getChannelRoom().setParticipantsCountLabel(memberCount + "");
                }
                return memberCount;
            }
        }
        return 0;
    }

    public static void updateMute(final long roomId, final ProtoGlobal.RoomMute muteState) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                        if (room != null) {
                            room.setMute(muteState);
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        /** call this listener for update tab bars unread count */
                        if (G.onUnreadChange != null) {
                            G.onUnreadChange.onChange();
                        }
                        realm.close();
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

    public static void updatePin(final long roomId, final boolean pin, final long pinId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = RealmRoom.getRealmRoom(realm, roomId);
                if (room != null) {
                    room.setPinned(pin);
                    room.setPinId(pinId);
                }
            }
        });
        realm.close();
    }

    public static void updateSignature(final long roomId, final boolean signature) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                    if (realmChannelRoom != null) {
                        realmChannelRoom.setSignature(signature);
                    }
                }
            }
        });
        realm.close();
    }

    public static void updateUsername(final long roomId, final String username) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    if (realmRoom.getType() == GROUP) {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.setUsername(username);
                            realmGroupRoom.setPrivate(false);
                        }
                    } else {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.setUsername(username);
                            realmChannelRoom.setPrivate(false);
                        }
                    }
                }
            }
        });
        realm.close();
    }

    /**
     * check exist chat room with userId(peerId) and set a value for notify room item
     */
    public static void updateChatRoom(final long userId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.TYPE, CHAT.toString()).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, userId).findFirst();
                if (room != null) {
                    room.setReadOnly(room.getReadOnly());// set data for update room item
                }
            }
        });
        realm.close();
    }

    public static void updateTime(Realm realm, long roomId, long time) {
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realmRoom.setUpdatedTime(time);
        }
    }

    public static void setPrivate(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    if (realmRoom.getType() == GROUP) {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.setPrivate(true);
                        }
                    } else {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.setPrivate(true);
                        }
                    }
                }
            }
        });
        realm.close();
    }

    public static void setCountShearedMedia(final long roomId, final String count) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (room != null) {
                    room.setSharedMediaCount(count);
                }
            }
        });
        realm.close();
    }

    public static void setCount(final long roomId, final int count) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                setCount(realm, roomId, count);
            }
        });
        realm.close();
    }

    public static RealmRoom setCount(Realm realm, final long roomId, final int count) {
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (room != null) {
            room.setUnreadCount(count);
        }
        return room;
    }

    public static void setAction(final long roomId, final long userId, final String action) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setActionState(action, userId);
                }
            }
        });
        realm.close();
    }

    public static void setLastScrollPosition(final long roomId, final long messageId, final int offset) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setLastScrollPositionMessageId(messageId);
                    realmRoom.setLastScrollPositionOffset(offset);
                }
            }
        });
        realm.close();
    }

    public static void clearAllScrollPositions() {
        Realm realm = Realm.getDefaultInstance();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
            clearScrollPosition(realmRoom.id);
        }
        realm.close();
    }

    public static void clearScrollPosition(long roomId) {
        setLastScrollPosition(roomId, 0, 0);
    }

    public static void setDraft(final long roomId, final String message, final long replyToMessageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setDraft(RealmRoomDraft.put(realm, message, replyToMessageId));
                }
            }
        });
        realm.close();
    }

    public static void editRoom(final long roomId, final String title, final String description) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setTitle(title);
                    if (realmRoom.getType() == GROUP) {
                        RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                        if (realmGroupRoom != null) {
                            realmGroupRoom.setDescription(description);
                        }
                    } else {
                        RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                        if (realmChannelRoom != null) {
                            realmChannelRoom.setDescription(description);
                        }
                    }
                }
            }
        });
        realm.close();
    }

    public static void clearDraft(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setDraft(null);
                    realmRoom.setDraftFile(null);
                }
            }
        });
        realm.close();
    }

    /**
     * clear all actions from RealmRoom for all rooms
     */
    public static void clearAllActions() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                    realmRoom.setActionState(null, 0);
                }
            }
        });
        realm.close();
    }

    public static void joinRoom(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realmRoom != null && realmRoom.isValid()) {
                    realmRoom.setDeleted(false);
                    if (realmRoom.getType() == GROUP) {
                        realmRoom.setReadOnly(false);
                    }
                } else {
                    new RequestClientGetRoom().clientGetRoom(roomId, RequestClientGetRoom.CreateRoomMode.requestFromOwner);
                }
            }
        });


        realm.close();
    }

    public static void joinByInviteLink(long roomId) {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                        realmRoom.setReadOnly(false);
                    }
                    realmRoom.setDeleted(false);
                }
            });
        }
        realm.close();
    }

    public static boolean isNotificationServices(long roomId) {
        boolean isNotificationService = false;

        Realm realm = Realm.getDefaultInstance();
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (room != null && room.getType() == CHAT && room.getChatRoom() != null) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, room.getChatRoom().getPeerId());
            if (realmRegisteredInfo.getMainStatus().equals(ProtoGlobal.RegisteredUser.Status.SERVICE_NOTIFICATIONS.toString())) {
                isNotificationService = true;
            }
        }
        realm.close();

        return isNotificationService;
    }

    public static ProtoGlobal.Room.Type detectType(long roomId) {
        ProtoGlobal.Room.Type roomType = ProtoGlobal.Room.Type.UNRECOGNIZED;
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            roomType = realmRoom.getType();
        }
        realm.close();
        return roomType;
    }

    public static String detectTitle(long roomId) {
        String title = "";
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            title = realmRoom.getTitle();
        }
        realm.close();
        return title;
    }

    public static void setLastMessageWithRoomMessage(Realm realm, long roomId, RealmRoomMessage roomMessage) {
        if (roomMessage != null) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setLastMessage(roomMessage);
            }
        }
    }

    public static void setLastMessageWithRoomMessage(final long roomId, final RealmRoomMessage roomMessage) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                setLastMessageWithRoomMessage(realm, roomId, roomMessage);
            }
        });
        realm.close();
    }

    public static void setLastMessageAfterLocalDelete(final long roomId, final long messageId) { // FragmentChat, is need this method?
        //TODO [Saeed Mozaffari] [2017-10-23 9:38 AM] - Write Better Code
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    RealmRoomMessage realmRoomMessage = null;
                    RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.EDITED, false).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, messageId).findAll();
                    if (realmRoomMessages.size() > 0) {
                        realmRoomMessage = realmRoomMessages.last();
                    }

                    if (realmRoom != null && realmRoomMessage != null) {
                        realmRoom.setLastMessage(realmRoomMessage);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        realm.close();
    }

    public static void convertChatToGroup(final long roomId, final String title, final String description, final ProtoGlobal.GroupRoom.Role role) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    realmRoom.setType(RoomType.GROUP);
                    realmRoom.setTitle(title);
                    realmRoom.setGroupRoom(RealmGroupRoom.putIncomplete(realm, role, description, "2"));
                    realmRoom.setChatRoom(null);
                }
            }
        });
        realm.close();
    }

    public static void clearMessage(final long roomId, final long clearId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null && ((realmRoom.getLastMessage() == null) || (realmRoom.getLastMessage().getMessageId() <= clearId))) {
                    realmRoom.setUnreadCount(0);
                    realmRoom.setLastMessage(null);
                }
            }
        });
        realm.close();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProtoGlobal.Room.Type getType() {
        return (type != null) ? ProtoGlobal.Room.Type.valueOf(type) : null;
    }

    public void setType(RoomType type) {
        this.type = type.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        try {
            this.title = title;
        } catch (Exception e) {
            this.title = HelperString.getUtf8String(title);
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.onUnreadChange != null) {
                    G.onUnreadChange.onChange();
                }
            }
        }, 100);
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean getMute() {
        return mute;
    }

    public void setMute(ProtoGlobal.RoomMute muteState) {
        if (muteState == ProtoGlobal.RoomMute.MUTE) {
            this.mute = true;
        } else {
            this.mute = false;
        }
    }

    public RealmChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(RealmChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public RealmGroupRoom getGroupRoom() {
        return groupRoom;
    }

    public void setGroupRoom(RealmGroupRoom groupRoom) {
        this.groupRoom = groupRoom;
    }

    public RealmChannelRoom getChannelRoom() {
        return channelRoom;
    }

    public void setChannelRoom(RealmChannelRoom channelRoom) {
        this.channelRoom = channelRoom;
    }

    public RealmRoomDraft getDraft() {
        return draft;
    }

    public void setDraft(RealmRoomDraft draft) {
        this.draft = draft;
    }

    public RealmDraftFile getDraftFile() {
        return draftFile;
    }

    public void setDraftFile(RealmDraftFile draftFile) {
        this.draftFile = draftFile;
    }

    public RealmAvatar getAvatar() {
        return avatar;
    }

    public void setAvatar(RealmAvatar avatar) {
        this.avatar = avatar;
    }

    public String getSharedMediaCount() {

        if (sharedMediaCount == null || sharedMediaCount.length() == 0) {
            return context.getString(R.string.there_is_no_sheared_media);
        }

        String countList[] = sharedMediaCount.split("\n");
        try {

            int countOFImage = Integer.parseInt(countList[0]);
            int countOFVIDEO = Integer.parseInt(countList[1]);
            int countOFAUDIO = Integer.parseInt(countList[2]);
            int countOFVOICE = Integer.parseInt(countList[3]);
            int countOFGIF = Integer.parseInt(countList[4]);
            int countOFFILE = Integer.parseInt(countList[5]);
            int countOFLink = Integer.parseInt(countList[6]);

            String result = "";

            if (countOFImage > 0)
                result += "\n" + countOFImage + " " + context.getString(R.string.shared_image);
            if (countOFVIDEO > 0)
                result += "\n" + countOFVIDEO + " " + context.getString(R.string.shared_video);
            if (countOFAUDIO > 0)
                result += "\n" + countOFAUDIO + " " + context.getString(R.string.shared_audio);
            if (countOFVOICE > 0)
                result += "\n" + countOFVOICE + " " + context.getString(R.string.shared_voice);
            if (countOFGIF > 0)
                result += "\n" + countOFGIF + " " + context.getString(R.string.shared_gif);
            if (countOFFILE > 0)
                result += "\n" + countOFFILE + " " + context.getString(R.string.shared_file);
            if (countOFLink > 0)
                result += "\n" + countOFLink + " " + context.getString(R.string.shared_links);

            result = result.trim();

            if (result.length() < 1) {
                result = context.getString(R.string.there_is_no_sheared_media);
            }

            return result;
        } catch (Exception e) {

            return sharedMediaCount;
        }


    }

    public void setSharedMediaCount(String sharedMediaCount) {
        this.sharedMediaCount = sharedMediaCount;
    }

    public String getActionState() {
        return actionState;
    }

    public void setActionState(String actionState, long userId) {
        this.actionState = actionState;
        this.actionStateUserId = userId;
    }

    public long getActionStateUserId() {
        return actionStateUserId;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public long getPinId() {
        return pinId;
    }

    public void setPinId(long pinId) {
        this.pinId = pinId;
    }

    public long getUpdatedTime() {
        if (getLastMessage() != null && getLastMessage().isValid()) {
            if (getLastMessage().getUpdateOrCreateTime() > updatedTime) {
                return getLastMessage().getUpdateOrCreateTime();
            }
        }
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isKeepRoom() {
        return keepRoom;
    }

    public void setKeepRoom(boolean keepRoom) {
        this.keepRoom = keepRoom;
    }

    public long getLastScrollPositionMessageId() {
        return lastScrollPositionMessageId;
    }

    public void setLastScrollPositionMessageId(long lastScrollPositionMessageId) {
        this.lastScrollPositionMessageId = lastScrollPositionMessageId;
    }

    public int getLastScrollPositionOffset() {
        return lastScrollPositionOffset;
    }

    public void setLastScrollPositionOffset(int lastScrollPositionOffset) {
        this.lastScrollPositionOffset = lastScrollPositionOffset;
    }

    public RealmRoomMessage getFirstUnreadMessage() {
        return firstUnreadMessage;
    }

    public void setFirstUnreadMessage(RealmRoomMessage firstUnreadMessage) {
        this.firstUnreadMessage = firstUnreadMessage;
    }

    public RealmRoomMessage getLastMessage() {
        return lastMessage;
    }

    public static void setLastMessage(final long roomId) {
        //TODO [Saeed Mozaffari] [2017-10-22 5:26 PM] - Write Better Code
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        if (realmRoomMessages.size() > 0 && realmRoomMessages.first() != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setLastMessage(realmRoomMessages.first());
                    }
                }
            });
        }
        realm.close();
    }

    public void setLastMessage(RealmRoomMessage lastMessage) {
        if (lastMessage != null) {
            setUpdatedTime(lastMessage.getUpdateOrCreateTime());
        }
        this.lastMessage = lastMessage;
    }

    public long getOwnerId() {
        switch (ProtoGlobal.Room.Type.valueOf(type)) {
            case CHAT:
                return getChatRoom().getPeerId();
            default:
                return id;
        }
    }

    public static String[] getUnreadCountPages() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoom> results = realm.where(RealmRoom.class).equalTo(RealmRoomFields.KEEP_ROOM, false).equalTo(RealmRoomFields.MUTE, false).equalTo(RealmRoomFields.IS_DELETED, false).findAll();
        int all = 0, chat = 0, group = 0, channel = 0;
        for (RealmRoom rm : results) {
            switch (rm.getType()) {
                case CHANNEL:
                    channel += rm.getUnreadCount();
                    break;
                case CHAT:
                    chat += rm.getUnreadCount();
                    break;
                case GROUP:
                    group += rm.getUnreadCount();
                    break;
            }
            all += rm.getUnreadCount();
        }
        String ar[];
        if (HelperCalander.isPersianUnicode) {
            ar = new String[]{"0", channel + "", group + "", chat + "", all + ""};
        } else {
            ar = new String[]{all + "", chat + "", group + "", channel + "", "0"};
        }
        realm.close();
        return ar;
    }

}
