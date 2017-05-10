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
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.RealmRoomMessageRealmProxy;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import java.util.Calendar;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.emoji.EmojiHandler;
import net.iGap.emoji.EmojiSpan;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnActivityMainStart;
import net.iGap.module.SUID;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import org.parceler.Parcel;

@Parcel(implementations = { RealmRoomMessageRealmProxy.class }, value = Parcel.Serialization.BEAN, analyze = { RealmRoomMessage.class }) public class RealmRoomMessage extends RealmObject {
    @PrimaryKey private long messageId;
    @Index private long roomId;
    private long messageVersion;
    private String status;
    private long statusVersion;
    private String messageType;
    private String message;
    private boolean hasMessageLink = false;
    private RealmAttachment attachment;
    private long userId;
    private RealmRoomMessageLocation location;
    private RealmRoomMessageLog log;
    private String logMessage;
    private RealmRoomMessageContact roomMessageContact;
    private boolean edited;
    private long createTime;
    private long updateTime;
    private boolean deleted = false;
    private RealmRoomMessage forwardMessage;
    private RealmRoomMessage replyTo;
    private boolean showMessage = true;
    private String authorHash;
    private boolean hasEmojiInText;
    private boolean showTime = false;
    private long authorRoomId;
    // for channel message should be exist in other rooms (forwarded message)
    private RealmChannelExtra channelExtra;
    private long previousMessageId;
    private long futureMessageId;
    private String linkInfo;

    public long getUpdateOrCreateTime() {
        return updateTime >= createTime ? updateTime : createTime;
    }

    public static RealmRoomMessage updateId(long fakeMessageId, long newMessageId) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, fakeMessageId).findFirst();
        if (message != null) {
            message.deleteFromRealm();
        }
        //message.setMessageId(newMessageId);

        realm.close();
        return message;
    }

    /**
     * delete message from realm
     * hint : use this method in realm transaction
     *
     * @param messageId find this id
     */
    public static RealmRoomMessage deleteMessage(Realm realm, long messageId) {
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (message != null) {
            message.deleteFromRealm();
        }
        return message;
    }

    public static void fetchNotDeliveredMessages(final OnActivityMainStart callback) {
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> sentMessages = realm.where(RealmRoomMessage.class)
            .notEqualTo(RealmRoomMessageFields.USER_ID, G.userId)
            .equalTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SENT.toString())
            .findAllSortedAsync(new String[] { RealmRoomMessageFields.ROOM_ID, RealmRoomMessageFields.MESSAGE_ID }, new Sort[] { Sort.DESCENDING, Sort.ASCENDING });
        sentMessages.addChangeListener(new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override public void onChange(RealmResults<RealmRoomMessage> element) {
                for (RealmRoomMessage roomMessage : element) {
                    if (roomMessage == null) {
                        return;
                    }
                    final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getRoomId()).findFirst();
                    if (realmRoom == null) {
                        return;
                    }

                    callback.sendDeliveredStatus(realmRoom, roomMessage);
                }

                element.removeAllChangeListeners();
                realm.close();
            }
        });
    }

    public static void fetchMessagesBB(final long roomId, final OnActivityChatStart callback) {
        // when user receive message, I send update status as SENT to the message sender
        // but imagine user is not in the room (or he is in another room) and received some messages
        // when came back to the room with new messages, I make new update status request as SEEN to
        // the message sender
        final Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmRoomMessage> realmRoomMessages =
            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSortedAsync(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
        realmRoomMessages.addChangeListener(new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override public void onChange(final RealmResults<RealmRoomMessage> element) {
                //Start ClientCondition OfflineSeen
                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();

                        if (realmClientCondition != null) {
                            for (RealmRoomMessage roomMessage : element) {
                                if (roomMessage != null) {
                                    if (roomMessage.getUserId() != realm.where(RealmUserInfo.class).findFirst().getUserId() && !realmClientCondition.containsOfflineSeen(roomMessage.getMessageId())) {
                                        if (ProtoGlobal.RoomMessageStatus.valueOf(roomMessage.getStatus()) != ProtoGlobal.RoomMessageStatus.SEEN) {
                                            roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                                            RealmOfflineSeen realmOfflineSeen = realm.createObject(RealmOfflineSeen.class, SUID.id().get());
                                            realmOfflineSeen.setOfflineSeen(roomMessage.getMessageId());

                                            realmClientCondition.getOfflineSeen().add(realmOfflineSeen);
                                            callback.sendSeenStatus(roomMessage);
                                        }
                                    } else {
                                        if (ProtoGlobal.RoomMessageStatus.valueOf(roomMessage.getStatus()) == ProtoGlobal.RoomMessageStatus.SENDING) {
                                            /**
                                             * check timeout, because when forward message to room ,message state is sending
                                             * and add forward message to Realm from here and finally client have duplicated message
                                             */
                                            if ((System.currentTimeMillis() - roomMessage.getCreateTime()) > Config.TIME_OUT_MS) {
                                                if (roomMessage.getAttachment() != null) {
                                                    if (!HelperUploadFile.isUploading(roomMessage.getMessageId() + "")) {
                                                        callback.resendMessageNeedsUpload(roomMessage);
                                                    }
                                                } else {
                                                    callback.resendMessage(roomMessage);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });

                element.removeAllChangeListeners();
                realm.close();
            }
        });
    }

    public static void fetchMessages(final long roomId, final OnActivityChatStart callback) {

        if (G.userLogin) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override public void run() {
                    final Realm realm = Realm.getDefaultInstance();

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override public void execute(Realm realm) {

                            long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
                            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class)
                                .equalTo(RealmRoomMessageFields.ROOM_ID, roomId)
                                .notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SEEN.toString())
                                .findAll();
                            RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();

                            if (realmClientCondition != null) {
                                for (RealmRoomMessage roomMessage : realmRoomMessages) {
                                    if (roomMessage != null) {
                                        /**
                                         * don't send seen for own message
                                         */
                                        if (roomMessage.getUserId() != userId && !realmClientCondition.containsOfflineSeen(roomMessage.getMessageId())) {
                                            roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                                            RealmOfflineSeen realmOfflineSeen = realm.createObject(RealmOfflineSeen.class, SUID.id().get());
                                            realmOfflineSeen.setOfflineSeen(roomMessage.getMessageId());

                                            realmClientCondition.getOfflineSeen().add(realmOfflineSeen);
                                            callback.sendSeenStatus(roomMessage);
                                        } else {
                                            if (ProtoGlobal.RoomMessageStatus.valueOf(roomMessage.getStatus()) == ProtoGlobal.RoomMessageStatus.SENDING) {
                                                /**
                                                 * check timeout, because when forward message to room ,message state is sending
                                                 * and add forward message to Realm from here and finally client have duplicated message
                                                 */
                                                if ((System.currentTimeMillis() - roomMessage.getCreateTime()) > Config.TIME_OUT_MS) {
                                                    if (roomMessage.getAttachment() != null) {
                                                        if (!HelperUploadFile.isUploading(roomMessage.getMessageId() + "")) {
                                                            callback.resendMessageNeedsUpload(roomMessage);
                                                        }
                                                    } else {
                                                        callback.resendMessage(roomMessage);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override public void onSuccess() {

                            realm.close();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override public void onError(Throwable error) {
                            realm.close();
                        }
                    });
                }
            });
        }
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public static RealmRoomMessage putOrUpdate(ProtoGlobal.RoomMessage input, long roomId) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage message = putOrUpdate(input, roomId, true, false, realm);

        message.setShowMessage(true);

        realm.close();

        return message;
    }

    public static RealmRoomMessage putOrUpdateForwardOrReply(ProtoGlobal.RoomMessage input, long roomId) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage message = putOrUpdate(input, roomId, true, true, realm);

        message.setShowMessage(true);

        realm.close();

        return message;
    }

    public static RealmRoomMessage putOrUpdate(ProtoGlobal.RoomMessage input, long roomId, boolean showMessage, boolean forwardOrReply, Realm realm) {
        long messageId;
        if (forwardOrReply) {
            /**
             * for forward and reply set new messageId
             * for create new message if before not exist
             */
            messageId = input.getMessageId() * (-1);
        } else {
            messageId = input.getMessageId();
        }
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();

        if (message == null) {
            message = realm.createObject(RealmRoomMessage.class, messageId);
            message.setRoomId(roomId);

            if (input.hasForwardFrom()) {
                message.setForwardMessage(RealmRoomMessage.putOrUpdateForwardOrReply(input.getForwardFrom(), -1));
            }
            if (input.hasReplyTo()) {
                message.setReplyTo(RealmRoomMessage.putOrUpdateForwardOrReply(input.getReplyTo(), -1));
            }
            message.setShowMessage(showMessage);
        }

        message.setMessage(input.getMessage());

        message.setStatus(input.getStatus().toString());

        if (input.getAuthor().hasUser()) {
            message.setUserId(input.getAuthor().getUser().getUserId());
        } else {
            message.setUserId(0);
            message.setAuthorRoomId(input.getAuthor().getRoom().getRoomId());
            /**
             * if message is forward or reply check room exist or not for get info for
             * that room (hint : reply not important for this subject)
             * if this message isn't forward client before got this info and now don't
             * need to get it again
             */
            if (forwardOrReply) {
                RealmRoom.needGetRoom(input.getAuthor().getRoom().getRoomId());
            }
        }
        message.setAuthorHash(input.getAuthor().getHash());

        if (!forwardOrReply) {
            message.setDeleted(input.getDeleted());
        }

        message.setEdited(input.getEdited());

        if (input.hasAttachment()) {
            message.setAttachment(RealmAttachment.build(input.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT, input.getMessageType()));

            if (message.getAttachment().getSmallThumbnail() == null) {
                long smallId = SUID.id().get();
                RealmThumbnail smallThumbnail = RealmThumbnail.create(smallId, message.getAttachment().getId(), input.getAttachment().getSmallThumbnail());
                message.getAttachment().setSmallThumbnail(smallThumbnail);
            }

            message.getAttachment().setDuration(input.getAttachment().getDuration());
            message.getAttachment().setSize(input.getAttachment().getSize());
            message.getAttachment().setCacheId(input.getAttachment().getCacheId());

            if (message.getAttachment().getName() == null) {
                message.getAttachment().setName(input.getAttachment().getName());
            }
        }
        if (input.hasLocation()) {

            Long id = null;
            if (message.getLocation() != null) id = message.getLocation().getId();

            message.setLocation(RealmRoomMessageLocation.build(input.getLocation(), id));
        }
        if (input.hasLog()) {
            message.setLog(RealmRoomMessageLog.build(input.getLog()));
            message.setLogMessage(HelperLogMessage.logMessage(roomId, input.getAuthor(), input.getLog(), message.getMessageId()));
        }
        if (input.hasContact()) {
            message.setRoomMessageContact(RealmRoomMessageContact.build(input.getContact()));
        }
        message.setMessageType(input.getMessageType());
        message.setMessageVersion(input.getMessageVersion());
        message.setStatusVersion(input.getStatusVersion());
        if (input.getUpdateTime() == 0) {
            message.setUpdateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);
        } else {
            message.setUpdateTime(input.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);
        }
        message.setCreateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);

        message.setPreviousMessageId(input.getPreviousMessageId());

        if (input.hasChannelExtra()) {
            RealmChannelExtra realmChannelExtra = realm.createObject(RealmChannelExtra.class);
            realmChannelExtra.setMessageId(input.getMessageId());
            realmChannelExtra.setSignature(input.getChannelExtra().getSignature());
            realmChannelExtra.setThumbsDown(input.getChannelExtra().getThumbsDownLabel());
            realmChannelExtra.setThumbsUp(input.getChannelExtra().getThumbsUpLabel());
            realmChannelExtra.setViewsLabel(input.getChannelExtra().getViewsLabel());
        }

        addTimeIfNeed(message, realm);

        isEmojiInText(message, input.getMessage());

        return message;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(long messageVersion) {
        this.messageVersion = messageVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(long statusVersion) {
        this.statusVersion = statusVersion;
    }

    public ProtoGlobal.RoomMessageType getMessageType() {
        return ProtoGlobal.RoomMessageType.valueOf(messageType);
    }

    public void setMessageType(ProtoGlobal.RoomMessageType messageType) {
        this.messageType = messageType.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {

        try {
            this.message = message;
        } catch (Exception e) {
            this.message = HelperString.getUtf8String(message);
        }

        String linkInfo = HelperUrl.getLinkInfo(message);
        if (linkInfo.length() > 0) {
            setHasMessageLink(true);
            setLinkInfo(linkInfo);
        } else {
            setHasMessageLink(false);
        }
    }

    public boolean getHasMessageLink() {
        return hasMessageLink;
    }

    public void setHasMessageLink(boolean hasMessageLink) {
        this.hasMessageLink = hasMessageLink;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isHasEmojiInText() {
        return hasEmojiInText;
    }

    public void setHasEmojiInText(boolean hasEmojiInText) {
        this.hasEmojiInText = hasEmojiInText;
    }

    public RealmRoomMessageLocation getLocation() {
        return location;
    }

    public void setLocation(RealmRoomMessageLocation location) {
        this.location = location;
    }

    public RealmRoomMessageLog getLog() {
        return log;
    }

    public void setLog(RealmRoomMessageLog log) {
        this.log = log;
    }

    public String getLogMessage() {
        return HelperLogMessage.convertLogmessage(logMessage);
    }

    public String getLogMessageWithLinkInfo() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public RealmRoomMessageContact getRoomMessageContact() {
        return roomMessageContact;
    }

    public void setRoomMessageContact(RealmRoomMessageContact roomMessageContact) {
        this.roomMessageContact = roomMessageContact;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }

    /*public int getVoteUp() {
        return voteUp;
    }

    public void setVoteUp(int voteUp) {
        this.voteUp = voteUp;
    }

    public int getVoteDown() {
        return voteDown;
    }

    public void setVoteDown(int voteDown) {
        this.voteDown = voteDown;
    }

    public int getViewsLabel() {
        return seenCount;
    }

    public void setViewsLabel(int seenCount) {
        this.seenCount = seenCount;
    }*/

    public RealmChannelExtra getChannelExtra() {
        return channelExtra;
    }

    public void setChannelExtra(RealmChannelExtra channelExtra) {
        this.channelExtra = channelExtra;
    }

    public RealmRoomMessage getForwardMessage() {
        return forwardMessage;
    }

    public void setForwardMessage(RealmRoomMessage forwardMessage) {
        this.forwardMessage = forwardMessage;
    }

    public RealmRoomMessage getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(RealmRoomMessage replyTo) {
        this.replyTo = replyTo;
    }

    public long getAuthorRoomId() {
        return authorRoomId;
    }

    public void setAuthorRoomId(long authorRoomId) {
        this.authorRoomId = authorRoomId;
    }

    public String getAuthorHash() {
        return authorHash;
    }

    public void setAuthorHash(String authorHash) {
        this.authorHash = authorHash;
    }

    public long getPreviousMessageId() {
        return previousMessageId;
    }

    public void setPreviousMessageId(long previousMessageId) {
        this.previousMessageId = previousMessageId;
    }

    public long getFutureMessageId() {
        return futureMessageId;
    }

    public void setFutureMessageId(long futureMessageId) {
        this.futureMessageId = futureMessageId;
    }

    public boolean isSenderMe() {
        Realm realm = Realm.getDefaultInstance();
        boolean output = getUserId() == realm.where(RealmUserInfo.class).findFirst().getUserId();
        realm.close();
        return output;
    }

    public boolean isAuthorMe() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        boolean output = false;
        if (realmUserInfo != null && getAuthorHash() != null) {
            output = getAuthorHash().equals(realmUserInfo.getAuthorHash());
        }
        realm.close();
        return output;
    }

    public String getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(String linkInfo) {
        this.linkInfo = linkInfo;
    }

    public boolean isOnlyTime() {
        return userId == -1;
    }

    public RealmAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(RealmAttachment attachment) {
        this.attachment = attachment;
    }

    public void setAttachment(final long messageId, final ProtoGlobal.File attachment) {
        Realm realm = Realm.getDefaultInstance();
        if (!attachment.getToken().isEmpty()) {
            if (this.attachment == null) {
                final RealmAttachment realmAttachment = realm.createObject(RealmAttachment.class, messageId);
                realmAttachment.setCacheId(attachment.getCacheId());
                realmAttachment.setDuration(attachment.getDuration());
                realmAttachment.setHeight(attachment.getHeight());
                realmAttachment.setName(attachment.getName());
                realmAttachment.setSize(attachment.getSize());
                realmAttachment.setToken(attachment.getToken());
                realmAttachment.setWidth(attachment.getWidth());

                long smallMessageThumbnail = SUID.id().get();
                RealmThumbnail.create(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

                long largeMessageThumbnail = SUID.id().get();
                RealmThumbnail.create(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

                realmAttachment.setSmallThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, smallMessageThumbnail).findFirst());
                realmAttachment.setLargeThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, largeMessageThumbnail).findFirst());

                this.attachment = realmAttachment;
            } else {
                if (this.attachment.isValid()) {
                    this.attachment.setCacheId(attachment.getCacheId());
                    this.attachment.setDuration(attachment.getDuration());
                    this.attachment.setHeight(attachment.getHeight());
                    this.attachment.setName(attachment.getName());
                    this.attachment.setSize(attachment.getSize());
                    this.attachment.setToken(attachment.getToken());
                    this.attachment.setWidth(attachment.getWidth());

                    long smallMessageThumbnail = SUID.id().get();
                    RealmThumbnail.create(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

                    long largeMessageThumbnail = SUID.id().get();
                    RealmThumbnail.create(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

                    this.attachment.setSmallThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, smallMessageThumbnail).findFirst());
                    this.attachment.setLargeThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, largeMessageThumbnail).findFirst());
                }
            }
            realm.close();
        }
    }

    public void setAttachment(final long messageId, final String path, int width, int height, long size, String name, double duration, LocalFileType type) {
        if (path == null) {
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        if (attachment == null) {
            RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, messageId).findFirst();
            if (realmAttachment == null) {
                realmAttachment = realm.createObject(RealmAttachment.class, messageId);
            }
            if (type == LocalFileType.THUMBNAIL) {
                realmAttachment.setLocalThumbnailPath(path);
            } else {
                realmAttachment.setLocalFilePath(path);
            }
            realmAttachment.setWidth(width);
            realmAttachment.setSize(size);
            realmAttachment.setHeight(height);
            realmAttachment.setName(name);
            realmAttachment.setDuration(duration);
            attachment = realmAttachment;
        } else {
            if (attachment.isValid()) {
                if (type == LocalFileType.THUMBNAIL) {
                    attachment.setLocalThumbnailPath(path);
                } else {
                    attachment.setLocalFilePath(path);
                }
            }
        }
        realm.close();
    }

    ///**
    // * get latest count for vote and increase it
    // *
    // * @param reaction Up or Down
    // */
    //public void setVote(ProtoGlobal.RoomMessageReaction reaction, String voteCount) {
    //    if (getChannelExtra() != null) {
    //        if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
    //            getChannelExtra().setThumbsUp(voteCount);
    //        } else if (reaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
    //            getChannelExtra().setThumbsDown(voteCount);
    //        }
    //    }
    //}

    public static void ClearAllMessage(boolean deleteAllMessage, final long roomId) {

        Realm realm = Realm.getDefaultInstance();

        if (deleteAllMessage) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    realm.where(RealmRoomMessage.class).findAll().deleteAllFromRealm();
                    RealmResults<RealmRoom> rooms = realm.where(RealmRoom.class).findAll();

                    for (RealmRoom room : rooms) {
                        room.setUnreadCount(0);
                    }
                }
            });
        } else {

            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().deleteAllFromRealm();
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        realmRoom.setUnreadCount(0);
                    }
                }
            });
        }

        realm.close();
    }

    public static void addTimeIfNeed(RealmRoomMessage message, Realm realm) {

        RealmRoomMessage nextMessage = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.ROOM_ID, message.getRoomId())
            .equalTo(RealmRoomMessageFields.SHOW_TIME, true)
            .equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true)
            .equalTo(RealmRoomMessageFields.DELETED, false)
            .greaterThan(RealmRoomMessageFields.MESSAGE_ID, message.getMessageId())
            .findFirst();

        RealmRoomMessage lastMessage = null;

        RealmResults<RealmRoomMessage> list = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.ROOM_ID, message.getRoomId())
            .equalTo(RealmRoomMessageFields.SHOW_TIME, true)
            .equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true)
            .equalTo(RealmRoomMessageFields.DELETED, false)
            .lessThan(RealmRoomMessageFields.MESSAGE_ID, message.getMessageId())
            .findAll();

        if (list.size() > 0) {
            lastMessage = list.last();
        }

        if (lastMessage == null) {
            message.setShowTime(true);
        } else {
            message.setShowTime(isTimeDayDiferent(message.getUpdateTime(), lastMessage.getUpdateTime()));
        }

        if (nextMessage != null && message.isShowTime()) {

            boolean difTime = isTimeDayDiferent(message.getUpdateTime(), nextMessage.getUpdateTime());
            nextMessage.setShowTime(difTime);
        }
    }

    public static boolean isTimeDayDiferent(long time, long nextTime) {

        Calendar date1 = Calendar.getInstance();
        date1.setTimeInMillis(time);

        Calendar date2 = Calendar.getInstance();
        date2.setTimeInMillis(nextTime);

        if (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) && date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)) {
            return false;
        } else {
            return true;
        }
    }

    public static void isEmojiInText(RealmRoomMessage roomMessage, String message) {

        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

        EmojiHandler.addEmojis(G.context, spannableStringBuilder, 30);

        if (spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), EmojiSpan.class).length > 0) {
            roomMessage.setHasEmojiInText(true);
        } else {
            roomMessage.setHasEmojiInText(false);
        }
    }

    public static long getReplyMessageId(RealmRoomMessage realmRoomMessage) {
        if (realmRoomMessage != null && realmRoomMessage.getReplyTo() != null) {
            if (realmRoomMessage.getReplyTo().getMessageId() < 0) {
                return (realmRoomMessage.getReplyTo().getMessageId() * (-1));
            } else {
                return realmRoomMessage.getReplyTo().getMessageId();
            }
        }
        return 0;
    }

    /**
     * make messages failed
     */
    public static void makeFailed(final long messageId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.FAILED.toString())) {
                            G.chatSendMessageUtil.onMessageFailed(message.getRoomId(), message);
                        }

                        realm.close();
                    }
                });
            }
        });
    }
}
