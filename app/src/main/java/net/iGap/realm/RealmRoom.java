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
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import java.util.List;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperString;
import net.iGap.module.SUID;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.module.enums.GroupChatRole;
import net.iGap.module.enums.RoomType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestClientGetRoom;

public class RealmRoom extends RealmObject {
    @PrimaryKey private long id;
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
    private long updatedTime;
    private String sharedMediaCount = "";
    //if it was needed in the future we can combine this two under fields in RealmAction (actionStateUserId and actionState).
    private long actionStateUserId;
    private String actionState;
    private boolean isDeleted = false;
    private boolean isPinned;
    /**
     * client need keepRoom info for show in forward message that forward
     * from a room that user don't have that room
     */
    private boolean keepRoom = false;
    private long lastScrollPositionMessageId;

    public RealmRoom() {

    }

    public RealmRoom(long id) {
        this.id = id;
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

    public void setMute(boolean mute) {
        this.mute = mute;
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
            return G.context.getString(R.string.there_is_no_sheared_media);
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

            if (countOFImage > 0) result += "\n" + countOFImage + " " + G.context.getString(R.string.shared_image);
            if (countOFVIDEO > 0) result += "\n" + countOFVIDEO + " " + G.context.getString(R.string.shared_video);
            if (countOFAUDIO > 0) result += "\n" + countOFAUDIO + " " + G.context.getString(R.string.shared_audio);
            if (countOFVOICE > 0) result += "\n" + countOFVOICE + " " + G.context.getString(R.string.shared_voice);
            if (countOFGIF > 0) result += "\n" + countOFGIF + " " + G.context.getString(R.string.shared_gif);
            if (countOFFILE > 0) result += "\n" + countOFFILE + " " + G.context.getString(R.string.shared_file);
            if (countOFLink > 0) result += "\n" + countOFLink + " " + G.context.getString(R.string.shared_links);

            result = result.trim();

            if (result.length() < 1) {
                result = G.context.getString(R.string.there_is_no_sheared_media);
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

    public long getUpdatedTime() {
        if (getLastMessage() != null && getLastMessage().isValid()) {
            if (getLastMessage().getUpdateOrCreateTime() > updatedTime) {
                return getLastMessage().getUpdateOrCreateTime();
            }
        }
        return updatedTime;
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

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getLastScrollPositionMessageId() {
        return lastScrollPositionMessageId;
    }

    public void setLastScrollPositionMessageId(long lastScrollPositionMessageId) {
        this.lastScrollPositionMessageId = lastScrollPositionMessageId;
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

    public void setLastMessage(RealmRoomMessage lastMessage) {
        if (lastMessage != null) {
            setUpdatedTime(lastMessage.getUpdateOrCreateTime());
        }
        this.lastMessage = lastMessage;
    }

    private RealmDraftFile draftFile;
    private RealmAvatar avatar;

    public long getOwnerId() {
        switch (ProtoGlobal.Room.Type.valueOf(type)) {
            case CHAT:
                return getChatRoom().getPeerId();
            default:
                return id;
        }
    }

    /**
     * convert ProtoGlobal.Room to RealmRoom for saving into database
     * hint : call this method in execute transaction
     *
     * @param room ProtoGlobal.Room
     * @return RealmRoom
     */
    public static RealmRoom putOrUpdate(ProtoGlobal.Room room, Realm realm) {

        putChatToClientCondition(realm, room);

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
        //realmRoom.setMute(false);
        realmRoom.setActionState(null, 0);
        switch (room.getType()) {
            case CHANNEL:
                realmRoom.setType(RoomType.CHANNEL);
                realmRoom.setChannelRoom(RealmChannelRoom.convert(room.getChannelRoomExtra(), realmRoom.getChannelRoom(), realm));
                realmRoom.getChannelRoom().setDescription(room.getChannelRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putAndGet(realm, realmRoom.getId(), room.getChannelRoomExtra().getAvatar()));
                realmRoom.getChannelRoom().setInviteLink(room.getChannelRoomExtra().getPrivateExtra().getInviteLink());
                realmRoom.getChannelRoom().setInvite_token(room.getChannelRoomExtra().getPrivateExtra().getInviteToken());
                realmRoom.getChannelRoom().setUsername(room.getChannelRoomExtra().getPublicExtra().getUsername());
                realmRoom.getChannelRoom().setSeenId(room.getChannelRoomExtra().getSeenId());
                realmRoom.getChannelRoom().setPrivate(room.getChannelRoomExtra().hasPrivateExtra());
                break;
            case CHAT:
                realmRoom.setType(RoomType.CHAT);
                realmRoom.setChatRoom(RealmChatRoom.convert(room.getChatRoomExtra()));
                /**
                 * update user info for detect current status(online,offline,...)
                 * and also update another info
                 */

                RealmRegisteredInfo.putOrUpdate(room.getChatRoomExtra().getPeer());
                realmRoom.setAvatar(RealmAvatar.putAndGet(realm, room.getChatRoomExtra().getPeer().getId(), room.getChatRoomExtra().getPeer().getAvatar()));
                break;
            case GROUP:
                realmRoom.setType(RoomType.GROUP);
                realmRoom.setGroupRoom(RealmGroupRoom.convert(room.getGroupRoomExtra(), realmRoom.getGroupRoom(), realm));
                realmRoom.getGroupRoom().setDescription(room.getGroupRoomExtra().getDescription());
                realmRoom.setAvatar(RealmAvatar.putAndGet(realm, realmRoom.getId(), room.getGroupRoomExtra().getAvatar()));
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
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdateGetRoom(room.getFirstUnreadMessage(), room.getId());
            //realmRoomMessage.setPreviousMessageId(room.getFirstUnreadMessage().getMessageId());
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
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.putOrUpdateGetRoom(room.getLastMessage(), room.getId());
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

        RealmRoomDraft realmRoomDraft = realmRoom.getDraft();
        if (realmRoomDraft == null) {
            realmRoomDraft = realm.createObject(RealmRoomDraft.class);
        }
        realmRoomDraft.setMessage(room.getDraft().getMessage());
        realmRoomDraft.setReplyToMessageId(room.getDraft().getReplyTo());

        realmRoom.setDraft(realmRoomDraft);



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
                                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, item.getId()).findAll().deleteAllFromRealm();
                                realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, item.getId()).findAll().deleteAllFromRealm();
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

    private static void putChatToClientCondition(Realm realm, final ProtoGlobal.Room room) {
        if (realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, room.getId()).findFirst() == null) {
            realm.createObject(RealmClientCondition.class, room.getId());
        }
    }

    public static void convertAndSetDraft(final long roomId, final String message, final long replyToMessageId) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                if (realmRoom != null) {
                    RealmRoomDraft realmRoomDraft = realm.createObject(RealmRoomDraft.class);
                    realmRoomDraft.setMessage(message);
                    realmRoomDraft.setReplyToMessageId(replyToMessageId);
                    realmRoom.setDraft(realmRoomDraft);

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
     * check updater author for detect that updater is another device for
     * this account and finally update unread count if another account
     * was saw message for this room
     *
     * @param roomId roomId for room that get update status from that
     * @param authorHash updater author hash
     */
    public static void clearUnreadCount(long roomId, String authorHash, ProtoGlobal.RoomMessageStatus messageStatus) {

        if (G.authorHash.equals(authorHash) && messageStatus == ProtoGlobal.RoomMessageStatus.SEEN) {

            Realm realm = Realm.getDefaultInstance();

            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null) {
                realmRoom.setUnreadCount(0);
            }

            realm.close();
        }
    }

    public static void updateRole(final ProtoGlobal.Room.Type type, long roomId, long memberId, final String role) {

        Realm realm = Realm.getDefaultInstance();

        if (memberId == G.userId) {
            final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if (type == ProtoGlobal.Room.Type.GROUP) {
                        GroupChatRole mRole;
                        if (role.contains(GroupChatRole.ADMIN.toString())) {
                            mRole = GroupChatRole.ADMIN;
                        } else if (role.contains(GroupChatRole.MODERATOR.toString())) {
                            mRole = GroupChatRole.MODERATOR;
                        } else {
                            mRole = GroupChatRole.MEMBER;
                        }
                        realmRoom.getGroupRoom().setRole(mRole);
                    } else {
                        ChannelChatRole mRole;
                        if (role.contains(ChannelChatRole.ADMIN.toString())) {
                            mRole = ChannelChatRole.ADMIN;
                        } else if (role.contains(ChannelChatRole.MODERATOR.toString())) {
                            mRole = ChannelChatRole.MODERATOR;
                        } else {
                            mRole = ChannelChatRole.MEMBER;
                        }
                        realmRoom.getChannelRoom().setRole(mRole);
                    }
                }
            });
        }

        realm.close();
    }

    /**
     * delete room from realm and also delete all messages
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

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.deleteFromRealm();
                }

                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().deleteAllFromRealm();
            }
        });
        realm.close();
    }

    public static void addOwnerToDatabase(Long roomId, ProtoGlobal.Room.Type roomType) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    final RealmList<RealmMember> members = realmChannelRoom.getMembers();

                    final RealmMember realmMember = new RealmMember();
                    realmMember.setId(SUID.id().get());
                    realmMember.setPeerId(G.userId);
                    realmMember.setRole(ProtoGlobal.ChannelRoom.Role.OWNER.toString());

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            members.add(realmMember);
                        }
                    });
                }
            } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                if (realmGroupRoom != null) {
                    final RealmList<RealmMember> members = realmGroupRoom.getMembers();

                    final RealmMember realmMember = new RealmMember();
                    realmMember.setId(SUID.id().get());
                    realmMember.setPeerId(G.userId);
                    realmMember.setRole(ProtoGlobal.GroupRoom.Role.OWNER.toString());

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            members.add(realmMember);
                        }
                    });
                }
            }
        }

        realm.close();
    }

    public static void updateMemberCount(long roomId, final ProtoGlobal.Room.Type roomType, final long memberCount) {
        Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (roomType == ProtoGlobal.Room.Type.CHANNEL) {
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
}
