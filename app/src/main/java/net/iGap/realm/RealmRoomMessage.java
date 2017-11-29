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
import android.text.format.DateUtils;

import com.vanniktech.emoji.EmojiUtils;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperDownloadFile;
import net.iGap.helper.HelperLogMessage;
import net.iGap.helper.HelperString;
import net.iGap.helper.HelperTimeOut;
import net.iGap.helper.HelperUploadFile;
import net.iGap.helper.HelperUrl;
import net.iGap.interfaces.OnActivityChatStart;
import net.iGap.interfaces.OnActivityMainStart;
import net.iGap.module.SUID;
import net.iGap.module.TimeUtils;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.module.enums.ClientConditionOffline;
import net.iGap.module.enums.ClientConditionVersion;
import net.iGap.module.enums.EnumCustomMessageId;
import net.iGap.module.enums.LocalFileType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoResponse;
import net.iGap.request.RequestChannelDeleteMessage;
import net.iGap.request.RequestChatDeleteMessage;
import net.iGap.request.RequestGroupDeleteMessage;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.RealmRoomMessageRealmProxy;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

import static net.iGap.fragments.FragmentChat.compressingFiles;
import static net.iGap.fragments.FragmentChat.getRealmChat;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHANNEL;
import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;
import static net.iGap.proto.ProtoGlobal.Room.Type.GROUP;

@Parcel(implementations = {RealmRoomMessageRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmRoomMessage.class})
public class RealmRoomMessage extends RealmObject {
    @PrimaryKey
    private long messageId;
    @Index
    private long roomId;
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
    //if it was needed in the future we can use RealmAuthor instead of author hash and also maybe authorRoomId
    private long authorRoomId;
    // for channel message should be exist in other rooms (forwarded message)
    private RealmChannelExtra channelExtra;
    private long previousMessageId;
    private long futureMessageId;
    private String linkInfo;

    public long getUpdateOrCreateTime() {
        return updateTime >= createTime ? updateTime : createTime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
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

        message = message.replaceAll("[\\u2063]", "");


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
        boolean output = getUserId() == G.userId;
        return output;
    }

    public boolean isAuthorMe() {

        boolean output = false;
        if (getAuthorHash() != null) {
            output = getAuthorHash().equals(G.authorHash);
        }

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
                this.attachment = RealmAttachment.putOrUpdate(realm, messageId, null, attachment);
            } else {
                if (this.attachment.isValid()) {
                    this.attachment = RealmAttachment.putOrUpdate(realm, messageId, this.attachment, attachment);
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


    /**
     * if has forward return that otherwise return enter value
     */
    public static RealmRoomMessage getFinalMessage(RealmRoomMessage realmRoomMessage) {
        if (realmRoomMessage != null && realmRoomMessage.getForwardMessage() != null) {
            return realmRoomMessage.getForwardMessage();
        }
        return realmRoomMessage;
    }

    public static RealmRoomMessage findMessage(Realm realm, long messageId) {
        return realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.DELETED, false).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
    }

    public static RealmRoomMessage findLastMessage(Realm realm, long roomId) {
        //TODO [Saeed Mozaffari] [2017-10-23 11:24 AM] - Can Write Better Code?
        RealmResults<RealmRoomMessage> realmRoomMessages = findDescending(realm, roomId);
        RealmRoomMessage realmRoomMessage = null;
        if (realmRoomMessages.size() > 0) {
            realmRoomMessage = realmRoomMessages.first();
        }
        return realmRoomMessage;
    }

    public static RealmResults<RealmRoomMessage> findDescending(Realm realm, long roomId) {
        return findSorted(realm, roomId, RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
    }

    public static RealmResults<RealmRoomMessage> findAscending(Realm realm, long roomId) {
        return findSorted(realm, roomId, RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
    }

    public static RealmResults<RealmRoomMessage> findSorted(Realm realm, long roomId, String fieldName, Sort sortOrder) {
        return realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).equalTo(RealmRoomMessageFields.DELETED, false).findAllSorted(fieldName, sortOrder);
    }

    public static RealmResults<RealmRoomMessage> findNotificationMessage(Realm realm) {
        return realm.where(RealmRoomMessage.class)
                .equalTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SENT.toString())
                .or()
                .equalTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.DELIVERED.toString())
                .equalTo(RealmRoomMessageFields.DELETED, false)
                .notEqualTo(RealmRoomMessageFields.AUTHOR_HASH, G.authorHash)
                .notEqualTo(RealmRoomMessageFields.USER_ID, G.userId)
                .notEqualTo(RealmRoomMessageFields.MESSAGE_TYPE, ProtoGlobal.RoomMessageType.LOG.toString())
                .findAllSorted(RealmRoomMessageFields.UPDATE_TIME, Sort.DESCENDING);
    }

    public static RealmResults<RealmRoomMessage> filterMessage(Realm realm, long roomId, ProtoGlobal.RoomMessageType messageType) {
        RealmResults<RealmRoomMessage> results;
        if (messageType == ProtoGlobal.RoomMessageType.TEXT) {
            results = realm.where(RealmRoomMessage.class).
                    equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                    equalTo(RealmRoomMessageFields.MESSAGE_TYPE, messageType.toString()).
                    equalTo(RealmRoomMessageFields.DELETED, false).
                    equalTo(RealmRoomMessageFields.HAS_MESSAGE_LINK, true).
                    findAllSorted(RealmRoomMessageFields.UPDATE_TIME, Sort.DESCENDING);
        } else {
            //TODO [Saeed Mozaffari] [2017-10-28 9:59 AM] - Can Write Better Code?
            results = realm.where(RealmRoomMessage.class).
                    equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                    equalTo(RealmRoomMessageFields.MESSAGE_TYPE, messageType.toString()).
                    equalTo(RealmRoomMessageFields.DELETED, false).
                    or().
                    equalTo(RealmRoomMessageFields.ROOM_ID, roomId).
                    equalTo(RealmRoomMessageFields.MESSAGE_TYPE, messageType.toString() + "_TEXT").
                    equalTo(RealmRoomMessageFields.DELETED, false).
                    findAllSorted(RealmRoomMessageFields.UPDATE_TIME, Sort.DESCENDING);
        }
        return results;
    }

    /**
     * @param messageType if set null will be checked both VIDEO and IMAGE type
     */
    public static boolean isImageOrVideo(RealmRoomMessage roomMessage, @Nullable ProtoGlobal.RoomMessageType messageType) {
        boolean isImageOrVideo = false;
        if (messageType == null) {
            if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                isImageOrVideo = true;
            } else if (roomMessage.getForwardMessage() != null) {
                ProtoGlobal.RoomMessageType messageTypeForward = roomMessage.getForwardMessage().getMessageType();
                if (messageTypeForward == ProtoGlobal.RoomMessageType.IMAGE || messageTypeForward == ProtoGlobal.RoomMessageType.IMAGE_TEXT || messageTypeForward == ProtoGlobal.RoomMessageType.VIDEO || messageTypeForward == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                    isImageOrVideo = true;
                }
            }
        } else if (messageType == ProtoGlobal.RoomMessageType.VIDEO) {
            if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.VIDEO_TEXT) {
                isImageOrVideo = true;
            }
        } else if (messageType == ProtoGlobal.RoomMessageType.IMAGE) {
            if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE || roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                isImageOrVideo = true;
            }
        }

        return isImageOrVideo;
    }

    public static long findCustomMessageId(long roomId, EnumCustomMessageId customMessageId, int count) {
        long messageId = 0;
        Realm realm = Realm.getDefaultInstance();
        if (customMessageId == EnumCustomMessageId.FROM_BEGIN) {
            messageId = 0;
        } else if (customMessageId == EnumCustomMessageId.FROM_NOW) {
            RealmRoomMessage realmRoomMessage = RealmRoomMessage.findLastMessage(realm, roomId);
            if (realmRoomMessage != null) {
                messageId = realmRoomMessage.getMessageId();
            } else {
                messageId = 0;
            }
        } else {
            if (count > 0) {
                RealmResults<RealmRoomMessage> realmRoomMessages = RealmRoomMessage.findDescending(realm, roomId);
                if (realmRoomMessages.size() <= count) {
                    messageId = 0;
                } else {
                    RealmRoomMessage message = realmRoomMessages.get(count);
                    if (message != null) {
                        messageId = message.getMessageId();
                    }
                }
            }
        }
        realm.close();
        return messageId;
    }

    public static void fetchNotDeliveredMessages(final OnActivityMainStart callback) {
        // if client get message and before send status lost connection, will be need to this method
        // and this step is very uncommon so don't need to do this action and get performance,if client
        // needed this method will be used in ActivityMain later
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> sentMessages = realm.where(RealmRoomMessage.class).notEqualTo(RealmRoomMessageFields.USER_ID, G.userId).equalTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SENT.toString()).findAllSortedAsync(new String[]{RealmRoomMessageFields.ROOM_ID, RealmRoomMessageFields.MESSAGE_ID}, new Sort[]{Sort.DESCENDING, Sort.ASCENDING});
        sentMessages.addChangeListener(new RealmChangeListener<RealmResults<RealmRoomMessage>>() {
            @Override
            public void onChange(RealmResults<RealmRoomMessage> element) {
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

    public static void fetchMessages(final Realm realm, final long roomId, final OnActivityChatStart callback) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (realm.isClosed()) {
                    return;
                }
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmResults<RealmRoomMessage> realmRoomMessages =
                                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                        RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();

                        int count = 0;
                        if (realmClientCondition != null) {
                            for (RealmRoomMessage roomMessage : realmRoomMessages) {
                                if (roomMessage != null) {
                                    /**
                                     * don't send seen for own message
                                     */
                                    if (roomMessage.getUserId() != G.userId && !realmClientCondition.containsOfflineSeen(roomMessage.getMessageId())) {
                                        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                                        RealmClientCondition.addOfflineSeen(realm, realmClientCondition, roomMessage.getMessageId());
                                        callback.sendSeenStatus(roomMessage);
                                        count++;
                                        if (count >= 100) { // do this block for 100 item, (client need to send all status in one request, wait for server change...)
                                            break;
                                        }
                                    } else {
                                        if (G.userLogin) {
                                            if (ProtoGlobal.RoomMessageStatus.valueOf(roomMessage.getStatus()) == ProtoGlobal.RoomMessageStatus.SENDING) {
                                                if (compressingFiles != null && compressingFiles.containsKey(roomMessage.messageId)) {
                                                    return;
                                                }
                                                /**
                                                 * check timeout, because when forward message to room ,message state is sending
                                                 * and add forward message to Realm from here and finally client have duplicated message
                                                 */
                                                if ((System.currentTimeMillis() - roomMessage.getCreateTime()) > Config.TIME_OUT_MS) {
                                                    if (roomMessage.getAttachment() != null) {
                                                        if (!HelperUploadFile.isUploading(roomMessage.getMessageId() + "")) {
                                                            callback.resendMessageNeedsUpload(roomMessage, roomMessage.getMessageId());
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
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        //realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        //realm.close();
                    }
                });
            }
        });
    }

    private static RealmRoomMessage putOrUpdateForwardOrReply(ProtoGlobal.RoomMessage input, long roomId, Realm realm) {
        return putOrUpdate(input, roomId, true, true, realm);
    }

    public static RealmRoomMessage putOrUpdate(ProtoGlobal.RoomMessage input, long roomId, boolean forwardOrReply, boolean setGap, Realm realm) {
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
                message.setForwardMessage(RealmRoomMessage.putOrUpdateForwardOrReply(input.getForwardFrom(), -1, realm));
            }
            if (input.hasReplyTo()) {
                message.setReplyTo(RealmRoomMessage.putOrUpdateForwardOrReply(input.getReplyTo(), -1, realm));
            }
            message.setShowMessage(true);
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
                RealmThumbnail smallThumbnail = RealmThumbnail.put(smallId, message.getAttachment().getId(), input.getAttachment().getSmallThumbnail());
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

            message.setLocation(RealmRoomMessageLocation.put(input.getLocation(), id));
        }
        if (input.hasLog()) {
            message.setLog(RealmRoomMessageLog.put(input.getLog()));
            message.setLogMessage(HelperLogMessage.logMessage(roomId, input.getAuthor(), input.getLog(), message.getMessageId()));

        }
        if (input.hasContact()) {
            message.setRoomMessageContact(RealmRoomMessageContact.put(input.getContact()));
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

        if (setGap) {
            message.setPreviousMessageId(input.getPreviousMessageId());
        }

        if (input.hasChannelExtra()) {
            RealmChannelExtra.putOrUpdate(realm, input.getMessageId(), input.getChannelExtra());
        }

        addTimeIfNeed(message, realm);

        isEmojiInText(message, input.getMessage());

        return message;
    }

    public static RealmRoomMessage getRealmRoomMessage(Realm realm, long messageId) {
        return realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
    }

    public static void ClearAllMessage(Realm realm, boolean deleteAllMessage, final long roomId) {

        //+Realm realm = Realm.getDefaultInstance();
        if (deleteAllMessage) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmQuery<RealmRoomMessage> roomRealmQuery = realm.where(RealmRoomMessage.class);
                    for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                        if (realmRoom.getLastMessage() != null) {
                            roomRealmQuery.notEqualTo(RealmRoomMessageFields.MESSAGE_ID, realmRoom.getLastMessage().getMessageId());
                        }

                        if (realmRoom.getFirstUnreadMessage() != null) {
                            roomRealmQuery.notEqualTo(RealmRoomMessageFields.MESSAGE_ID, realmRoom.getFirstUnreadMessage().getMessageId());
                        }
                    }
                    roomRealmQuery.findAll().deleteAllFromRealm();
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                    if (realmRoom != null) {
                        if (realmRoom.getLastMessage() != null) {
                            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo(RealmRoomMessageFields.MESSAGE_ID, realmRoom.getLastMessage().getMessageId()).findAll().deleteAllFromRealm();
                        } else {
                            realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().deleteAllFromRealm();
                        }
                    }
                }
            });
        }

        //realm.close();
    }

    public static void addTimeIfNeed(RealmRoomMessage message, Realm realm) {

        if (!message.isShowMessage()) {
            return;
        }

        RealmRoomMessage nextMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, message.getRoomId()).equalTo(RealmRoomMessageFields.SHOW_TIME, true).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.DELETED, false).greaterThan(RealmRoomMessageFields.MESSAGE_ID, message.getMessageId()).findFirst();

        RealmRoomMessage lastMessage = null;

        RealmResults<RealmRoomMessage> list = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, message.getRoomId()).equalTo(RealmRoomMessageFields.SHOW_TIME, true).equalTo(RealmRoomMessageFields.SHOW_MESSAGE, true).equalTo(RealmRoomMessageFields.DELETED, false).lessThan(RealmRoomMessageFields.MESSAGE_ID, message.getMessageId()).findAll();

        if (list.size() > 0) {
            lastMessage = list.last();
        }

        if (lastMessage == null) {
            message.setShowTime(true);
        } else {
            message.setShowTime(isTimeDayDifferent(message.getUpdateTime(), lastMessage.getUpdateTime()));
        }

        if (nextMessage != null && message.isShowTime()) {

            boolean difTime = isTimeDayDifferent(message.getUpdateTime(), nextMessage.getUpdateTime());
            nextMessage.setShowTime(difTime);
        }
    }

    public static boolean isTimeDayDifferent(long time, long nextTime) {

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
        try {
            if (EmojiUtils.emojisCount(message) > 0) {
                roomMessage.setHasEmojiInText(true);
            } else {
                roomMessage.setHasEmojiInText(false);
            }
        } catch (Exception e) {
            roomMessage.setHasEmojiInText(true);
        }
    }

    public static boolean isEmojiInText(long messageId) {
        boolean hasEmoji = true;
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (realmRoomMessage != null && realmRoomMessage.getMessage() != null) {
            if (EmojiUtils.emojisCount(realmRoomMessage.getMessage()) <= 0) {
                hasEmoji = false;
            }
        }
        realm.close();
        return hasEmoji;
    }

    public static boolean isEmojiInText(String message) {
        return EmojiUtils.emojisCount(message) > 0;
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
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
                            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
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

    /**
     * detect that message is exist in realm or no
     *
     * @param messageId messageId for checking
     */
    public static boolean existMessage(long messageId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (realmRoomMessage != null) {
            realm.close();
            return true;
        }
        realm.close();
        return false;
    }

    public static void clearHistoryMessage(final long roomId) {
        final Realm realm = Realm.getDefaultInstance();

        final RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, roomId).findFirst();
        if (realmClientCondition != null && realmClientCondition.isLoaded() && realmClientCondition.isValid()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

                    if (realmRoom == null || !realmRoom.isLoaded() || !realmRoom.isValid()) {
                        return;
                    }

                    long clearMessageId = 0;
                    if (realmRoom.getLastMessage() != null) {
                        clearMessageId = realmRoom.getLastMessage().getMessageId();
                    } else {
                        RealmResults<RealmRoomMessage> results = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                        if (results.size() > 0) {
                            if (results.first() != null) {
                                clearMessageId = results.first().getMessageId();
                            }
                        }
                    }
                    realmClientCondition.setClearId(clearMessageId);
                    G.clearMessagesUtil.clearMessages(realmRoom.getType(), roomId, clearMessageId);

                    realmRoom.setUnreadCount(0);
                    realmRoom.setLastMessage(null);
                    realmRoom.setFirstUnreadMessage(null);
                    realmRoom.setUpdatedTime(0);
                    realmRoom.setLastScrollPositionMessageId(0);

                    RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll();
                    realmRoomMessages.deleteAllFromRealm();
                }
            });

            if (G.onClearChatHistory != null) {
                G.onClearChatHistory.onClearChatHistory();
            }
        }
        realm.close();
    }

    /**
     * delete message from realm. don't need to transaction
     */
    public static void deleteMessage(final long messageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                deleteMessage(realm, messageId);
            }
        });
        realm.close();
    }

    /**
     * delete message from realm. call in transaction
     */
    public static RealmRoomMessage deleteMessage(Realm realm, long messageId) {
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (message != null) {
            message.deleteFromRealm();
        }
        return message;
    }

    public static void deleteAllMessage(Realm realm, long roomId) {
        realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).findAll().deleteAllFromRealm();
    }

    public static void deleteAllMessage(final long roomId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                deleteAllMessage(realm, roomId);
            }
        });
        realm.close();
    }

    public static void deleteAllMessageLessThan(final long roomId, final long lessThan) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, lessThan).findAll().deleteAllFromRealm();
            }
        });
        realm.close();
    }

    public static void deleteSelectedMessages(Realm realm, final long RoomId, final ArrayList<Long> list, final ArrayList<Long> bothDeleteMessageId, final ProtoGlobal.Room.Type roomType) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // get offline delete list , add new deleted list and update in
                // client condition , then send request for delete message to server
                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, RoomId).findFirst();

                for (final Long messageId : list) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();

                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);

                        // stop download
                        if (roomMessage.getAttachment() != null) {
                            HelperDownloadFile.stopDownLoad(roomMessage.getAttachment().getCacheId());
                        }
                    }

                    boolean bothDelete = false;
                    if (bothDeleteMessageId != null && bothDeleteMessageId.contains(messageId)) {
                        bothDelete = true;
                    }

                    // check here with bothDeleteMessageId for send both or no

                    if (realmClientCondition != null) {
                        RealmClientCondition.addOfflineDelete(realm, realmClientCondition, messageId, roomType, bothDelete);
                    }

                    if (roomType == GROUP) {
                        new RequestGroupDeleteMessage().groupDeleteMessage(RoomId, messageId);
                    } else if (roomType == CHAT) {
                        new RequestChatDeleteMessage().chatDeleteMessage(RoomId, messageId, bothDelete);
                    } else if (roomType == CHANNEL) {
                        new RequestChannelDeleteMessage().channelDeleteMessage(RoomId, messageId);
                    }
                }
            }
        });
    }

    public static void deleteMessageServerResponse(final long roomId, final long messageId, final long deleteVersion, final ProtoResponse.Response response) {
        RealmClientCondition.setVersion(roomId, deleteVersion, ClientConditionVersion.DELETE);
        RealmClientCondition.deleteOfflineAction(messageId, ClientConditionOffline.DELETE);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                /**
                 * if another account deleted this message set deleted true
                 * otherwise before this state was set
                 */
                if (response.getId().isEmpty()) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);
                    }
                }

                if (G.onChatDeleteMessageResponse != null) {
                    G.onChatDeleteMessageResponse.onChatDeleteMessage(deleteVersion, messageId, roomId, response);
                }
            }
        });

        realm.close();
    }

    public static boolean isBothDelete(long messageTime) {
        long currentTime;
        if (G.userLogin) {
            currentTime = G.currentServerTime * DateUtils.SECOND_IN_MILLIS;
        } else {
            currentTime = System.currentTimeMillis();
        }

        return !HelperTimeOut.timeoutChecking(currentTime, messageTime, G.bothChatDeleteTime);
    }

    public static long getMessageTime(long messageId) {
        Realm realm = Realm.getDefaultInstance();
        long messageTime = RealmRoomMessage.getRealmRoomMessage(realm, messageId).getUpdateTime();
        realm.close();

        return messageTime;
    }

    public static void editMessageClient(final long roomId, final long messageId, final String message) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                if (roomMessage != null) {
                    RealmRoom.updateTime(realm, roomId, TimeUtils.currentLocalTime());

                    roomMessage.setMessage(message);
                    roomMessage.setEdited(true);
                    RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                    RealmRoomMessage.isEmojiInText(roomMessage, message);
                }
            }
        });
        realm.close();
    }

    public static void editMessageServerResponse(final long messageId, final long messageVersion, final String message, final ProtoGlobal.RoomMessageType messageType) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                if (roomMessage != null) {
                    roomMessage.setMessage(message);
                    roomMessage.setMessageVersion(messageVersion);
                    roomMessage.setEdited(true);
                    roomMessage.setMessageType(messageType);
                }
            }
        });
        realm.close();
    }

    /**
     * check SEEN and LISTENED for avoid from duplicate send status request in enter to chat because in
     * send status again enter ro chat fetchMessage method will be send status so client shouldn't
     */
    public static void setStatusSeenInChat(final long messageId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findFirst();
                if (realmRoomMessage != null) {
                    if (!realmRoomMessage.getStatus().equalsIgnoreCase(ProtoGlobal.RoomMessageStatus.SEEN.toString())) {
                        realmRoomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SEEN.toString());
                    }
                }
            }
        });
        realm.close();
    }

    public static void setStatusFailedInChat(Realm realm, long messageId) {
        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (message != null && message.getStatus().equals(ProtoGlobal.RoomMessageStatus.SENDING.toString())) {
            message.setStatus(ProtoGlobal.RoomMessageStatus.FAILED.toString());
        }
    }

    public static void setStatus(final long messageId, final ProtoGlobal.RoomMessageStatus messageStatus) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                setStatus(realm, messageId, messageStatus);
            }
        });
        realm.close();
    }

    public static void setStatus(Realm realm, long messageId, ProtoGlobal.RoomMessageStatus messageStatus) {
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (roomMessage != null) {
            roomMessage.setStatus(messageStatus.toString());
        }
    }

    public static RealmRoomMessage setStatusServerResponse(Realm realm, long messageId, long statusVersion, ProtoGlobal.RoomMessageStatus messageStatus) {
        RealmRoomMessage roomMessage;
        if (messageStatus != ProtoGlobal.RoomMessageStatus.LISTENED) {
            roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.SEEN.toString()).notEqualTo(RealmRoomMessageFields.STATUS, ProtoGlobal.RoomMessageStatus.LISTENED.toString()).findFirst();
        } else {
            roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        }

        if (roomMessage != null) {
            roomMessage.setStatus(messageStatus.toString());
            roomMessage.setStatusVersion(statusVersion);
            realm.copyToRealmOrUpdate(roomMessage);
        }
        return roomMessage;
    }

    public static void setLogMessage(final long messageId, final String logMessage) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
                if (roomMessage != null) {
                    roomMessage.setLogMessage(logMessage);
                }
            }
        });
        realm.close();
    }

    public static RealmRoomMessage makeUnreadMessage(int countNewMessage) {
        RealmRoomMessage message = new RealmRoomMessage();
        message.setMessageId(TimeUtils.currentLocalTime());
        message.setUserId(-1); // -1 means time message or unread message
        message.setMessage(countNewMessage + " " + G.fragmentActivity.getResources().getString(R.string.unread_message));
        message.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
        return message;
    }

    public static RealmRoomMessage makeTimeMessage(long time, String message) {
        RealmRoomMessage timeMessage = new RealmRoomMessage();
        timeMessage.setMessageId(SUID.id().get());
        timeMessage.setUserId(-1); // -1 means time message or unread message
        timeMessage.setUpdateTime(time);
        timeMessage.setMessage(message);
        timeMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
        return timeMessage;
    }

    public static RealmRoomMessage makeTextMessage(final long roomId, final String message, final long replyMessageId) {
        if (message.isEmpty()) {
            return null;
        }

        final long messageId = SUID.id().get();
        final long currentTime = TimeUtils.currentLocalTime();

        getRealmChat().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);

                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                roomMessage.setMessage(message);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                RealmRoomMessage.addTimeIfNeed(roomMessage, realm);
                RealmRoomMessage.isEmojiInText(roomMessage, message);
                roomMessage.setRoomId(roomId);
                roomMessage.setShowMessage(true);
                roomMessage.setUserId(G.userId);
                roomMessage.setAuthorHash(G.authorHash);
                roomMessage.setCreateTime(currentTime);

                /**
                 *  user wants to replay to a message
                 */
                if (replyMessageId > 0) {
                    RealmRoomMessage messageToReplay = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replyMessageId).findFirst();
                    if (messageToReplay != null) {
                        roomMessage.setReplyTo(messageToReplay);
                    }
                }

            }
        });

        RealmRoomMessage roomMessage = getRealmChat().where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        RealmRoom.setLastMessageWithRoomMessage(roomId, roomMessage);

        if (RealmRoom.detectType(roomId) == CHANNEL) {
            RealmChannelExtra.putDefault(roomId, messageId);
        }

        return roomMessage;
    }

    public static void makeTextMessage(final long roomId, final long messageId, final String message) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);
                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
                roomMessage.setRoomId(roomId);
                roomMessage.setMessage(message);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
                roomMessage.setUserId(G.userId);
                roomMessage.setCreateTime(TimeUtils.currentLocalTime());
            }
        });
        realm.close();
    }

    public static RealmRoomMessage makeVoiceMessage(Realm realm, final long roomId, final long messageId, final long duration, final long updateTime, final String filepath, final String message) {
        RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);
        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.VOICE);
        roomMessage.setMessage(message);
        roomMessage.setRoomId(roomId);
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        roomMessage.setAttachment(messageId, filepath, 0, 0, 0, null, duration, LocalFileType.FILE);
        roomMessage.setUserId(G.userId);
        roomMessage.setCreateTime(updateTime);
        return roomMessage;
    }

    public static void makeVoiceMessage(final long roomId, final long messageId, final long duration, final long updateTime, final String filepath, final String message) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                makeVoiceMessage(realm, roomId, messageId, duration, updateTime, filepath, message);
            }
        });
        realm.close();
    }

    public static void makePositionMessage(final long roomId, final long messageId, final long replyMessageId, final Double latitude, final Double longitude, final String imagePath) {
        getRealmChat().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRoomMessage roomMessage = realm.createObject(RealmRoomMessage.class, messageId);
                roomMessage.setLocation(RealmRoomMessageLocation.put(realm, latitude, longitude, imagePath));
                roomMessage.setCreateTime(TimeUtils.currentLocalTime());
                roomMessage.setMessageType(ProtoGlobal.RoomMessageType.LOCATION);
                roomMessage.setRoomId(roomId);
                roomMessage.setUserId(G.userId);
                roomMessage.setAuthorHash(G.authorHash);
                roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());

                if (replyMessageId > 0) {
                    RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replyMessageId).findFirst();
                    if (realmRoomMessage != null) {
                        roomMessage.setReplyTo(realmRoomMessage);
                    }
                }

                RealmRoom.setLastMessageWithRoomMessage(realm, roomId, roomMessage);
            }
        });
    }

    public static void makeForwardMessage(Realm realm, long roomId, long messageId, long forwardedMessageId) {
        RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, forwardedMessageId).findFirst();
        if (roomMessage != null) {
            RealmRoomMessage forwardedMessage = realm.createObject(RealmRoomMessage.class, messageId);
            if (roomMessage.getForwardMessage() != null) {
                forwardedMessage.setForwardMessage(roomMessage.getForwardMessage());
                forwardedMessage.setHasMessageLink(roomMessage.getForwardMessage().getHasMessageLink());
            } else {
                forwardedMessage.setForwardMessage(roomMessage);
                forwardedMessage.setHasMessageLink(roomMessage.getHasMessageLink());
            }

            forwardedMessage.setCreateTime(TimeUtils.currentLocalTime());
            forwardedMessage.setMessageType(ProtoGlobal.RoomMessageType.TEXT);
            forwardedMessage.setRoomId(roomId);
            forwardedMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
            forwardedMessage.setUserId(G.userId);
            forwardedMessage.setShowMessage(true);
        }
    }
}
