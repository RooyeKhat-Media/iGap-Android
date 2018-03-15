/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module.structs;

import android.os.Parcel;
import android.os.Parcelable;

import net.iGap.G;
import net.iGap.module.MyType;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelExtraFields;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.realm.RealmRoomMessageLocation;

import org.parceler.Parcels;

import io.realm.Realm;

import static net.iGap.G.userId;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {
    public static final Parcelable.Creator<StructMessageInfo> CREATOR = new Parcelable.Creator<StructMessageInfo>() {
        @Override
        public StructMessageInfo createFromParcel(Parcel source) {
            return new StructMessageInfo(source);
        }

        @Override
        public StructMessageInfo[] newArray(int size) {
            return new StructMessageInfo[size];
        }
    };

    public boolean isSelected = false;
    public long roomId;
    public String messageID = "1";
    public String senderID = "";
    public String senderColor = "";
    public boolean isEdited;
    public String status;
    public String initials;
    public ProtoGlobal.RoomMessageType messageType;
    public MyType.SendType sendType;
    public RealmRoomMessage replayTo;
    public RealmRoomMessage forwardedFrom;
    public String songArtist;
    public long songLength;
    public String messageText = "";
    public boolean hasLinkInMessage = false;
    public RealmRoomMessageLocation location;
    public String linkInfo = "";
    public boolean hasEmojiInText = true;
    public boolean showTime = false;
    public String username = "";
    public boolean showMessage = true;

    public String fileMime = "";
    public String filePic = "";
    public String filePath = "";
    // used for uploading process and getting item from adapter by file hash
    public byte[] fileHash;
    public int uploadProgress;
    public StructMessageAttachment attachment;
    public StructRegisteredInfo userInfo;
    public StructMessageAttachment senderAvatar;
    public long time;
    public String authorHash;
    public StructChannelExtra channelExtra;

    public StructMessageInfo() {
    }

    public StructMessageInfo(Realm realmChat, long roomId, String messageID, String senderID, String messageText, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String fileMime, String filePic, String localThumbnailPath, String localFilePath, byte[] fileHash, long time) {
        this.roomId = roomId;
        this.messageID = messageID;

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, Long.parseLong(senderID));
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;
        //realm.close();

        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileMime = fileMime;
        this.filePic = filePic;
        this.filePath = localThumbnailPath;
        this.messageText = messageText;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.fileHash = fileHash;
        this.time = time;
    }

    public StructMessageInfo(Realm realmChat, long roomId, String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String fileMime, String filePic, String localThumbnailPath, String localFilePath, byte[] fileHash, long time, long replayToMessageId) {
        this.roomId = roomId;
        this.messageID = messageID;
        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, Long.parseLong(senderID));
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileMime = fileMime;
        this.filePic = filePic;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.fileHash = fileHash;
        this.time = time;
        this.replayTo = realmChat.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        //realm.close();
    }

    public StructMessageInfo(Realm realmChat, long roomId, String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String localThumbnailPath, String localFilePath, long time) {
        this.roomId = roomId;
        this.messageID = messageID;

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, Long.parseLong(senderID));
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;
        //realm.close();

        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.time = time;
    }

    public StructMessageInfo(Realm realmChat, long roomId, String messageID, String messageText, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String localThumbnailPath, String localFilePath, long time) {
        this.roomId = roomId;
        this.messageID = messageID;

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, Long.parseLong(senderID));
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;
        //realm.close();

        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.messageText = messageText;
        this.sendType = sendType;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.time = time;
    }

    public StructMessageInfo(Realm realmChat, long roomId, String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String localThumbnailPath, String localFilePath, long time, long replayToMessageId) {
        this.roomId = roomId;
        this.messageID = messageID;

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, Long.parseLong(senderID));
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.time = time;
        this.replayTo = realmChat.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        //realm.close();
    }

    protected StructMessageInfo(Parcel in) {
        //this.view = in.readParcelable(View.class.getClassLoader());
        this.roomId = in.readLong();
        this.messageID = in.readString();
        this.senderID = in.readString();
        this.senderColor = in.readString();
        this.isEdited = in.readByte() != 0;
        this.status = in.readString();
        this.initials = in.readString();
        int tmpMessageType = in.readInt();
        this.messageType = tmpMessageType == -1 ? null : ProtoGlobal.RoomMessageType.values()[tmpMessageType];
        int tmpSendType = in.readInt();
        this.sendType = tmpSendType == -1 ? null : MyType.SendType.values()[tmpSendType];
        this.replayTo = Parcels.unwrap(in.readParcelable(RealmRoomMessage.class.getClassLoader()));
        this.forwardedFrom = Parcels.unwrap(in.readParcelable(RealmRoomMessage.class.getClassLoader()));
        this.songArtist = in.readString();
        this.songLength = in.readLong();
        this.messageText = in.readString();
        this.fileMime = in.readString();
        this.filePic = in.readString();
        this.filePath = in.readString();
        this.fileHash = in.createByteArray();
        this.uploadProgress = in.readInt();
        this.attachment = in.readParcelable(StructMessageAttachment.class.getClassLoader());
        this.userInfo = in.readParcelable(StructRegisteredInfo.class.getClassLoader());
        this.senderAvatar = in.readParcelable(StructMessageAttachment.class.getClassLoader());
        this.time = in.readLong();
        /*this.voteUp = in.readInt();
        this.voteDown = in.readInt();
        this.viewsLabel = in.readInt();*/
        this.channelExtra = Parcels.unwrap(in.readParcelable(StructChannelExtra.class.getClassLoader()));
    }

    public static StructMessageInfo buildForAudio(Realm realmChat, long roomId, long messageID, long senderID, ProtoGlobal.RoomMessageStatus status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, long time, String messageText, String localThumbnailPath, String localFilePath, String songArtist, long songLength, long replayToMessageId) {
        StructMessageInfo info = new StructMessageInfo();

        info.roomId = roomId;
        info.messageID = Long.toString(messageID);

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, senderID);
        info.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

        info.senderID = Long.toString(senderID);
        info.status = status.toString();
        info.messageType = messageType;
        info.sendType = sendType;
        info.attachment = info.attachment == null ? new StructMessageAttachment() : info.attachment;
        info.attachment.setLocalThumbnailPath(messageID, localThumbnailPath);
        info.attachment.setLocalFilePath(messageID, localFilePath);
        info.time = time;
        info.messageText = messageText;
        if (replayToMessageId != -1) {
            info.replayTo = realmChat.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        }
        //realm.close();
        // audio exclusive
        info.songArtist = songArtist;
        info.songLength = songLength;

        return info;
    }

    public static StructMessageInfo buildForContact(Realm realmChat, long roomId, long messageID, long senderID, MyType.SendType sendType, long time, ProtoGlobal.RoomMessageStatus status, String firstName, String lastName, String number, long replayToMessageId) {
        StructMessageInfo info = new StructMessageInfo();
        info.roomId = roomId;
        info.messageID = Long.toString(messageID);
        info.senderID = Long.toString(senderID);

        //+Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, senderID);
        info.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

        info.status = status.toString();
        info.messageType = ProtoGlobal.RoomMessageType.CONTACT;
        info.sendType = sendType;
        info.time = time;
        info.messageText = firstName + " " + number;
        if (replayToMessageId != 0) {
            info.replayTo = realmChat.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        }
        //realm.close();
        // contact exclusive
        info.userInfo = new StructRegisteredInfo(lastName, firstName, number, senderID);
        return info;
    }

    public static StructMessageInfo convert(Realm realmChat, RealmRoomMessage roomMessage) {
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.roomId = roomMessage.getRoomId();
        messageInfo.status = roomMessage.getStatus();
        messageInfo.showMessage = roomMessage.isShowMessage();
        messageInfo.hasLinkInMessage = roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().getHasMessageLink() : roomMessage.getHasMessageLink();
        messageInfo.linkInfo = roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().getLinkInfo() : roomMessage.getLinkInfo();
        messageInfo.hasEmojiInText = roomMessage.getForwardMessage() != null ? roomMessage.getForwardMessage().isHasEmojiInText() : roomMessage.isHasEmojiInText();

        messageInfo.messageID = Long.toString(roomMessage.getMessageId());
        messageInfo.isEdited = roomMessage.isEdited();
        if (!roomMessage.isSenderMe()) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realmChat, roomMessage.getUserId());
            if (realmRegisteredInfo != null) {
                messageInfo.senderAvatar = StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar());
                messageInfo.senderColor = realmRegisteredInfo.getColor();
                messageInfo.initials = realmRegisteredInfo.getInitials();
            }
        }
        messageInfo.messageType = roomMessage.getMessageType();
        messageInfo.time = roomMessage.getUpdateOrCreateTime();
        if (roomMessage.getAttachment() != null) {
            messageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
            messageInfo.uploadProgress = messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100 : 0;
            messageInfo.attachment.cashID = roomMessage.getAttachment().getCacheId();
        }
        messageInfo.messageText = roomMessage.getMessage();
        messageInfo.senderID = Long.toString(roomMessage.getUserId());
        messageInfo.authorHash = roomMessage.getAuthorHash();
        if (roomMessage.getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (roomMessage.getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.CONTACT) {
            messageInfo.userInfo = StructRegisteredInfo.build(roomMessage.getRoomMessageContact());
        }
        if (roomMessage.getForwardMessage() != null) {
            messageInfo.forwardedFrom = roomMessage.getForwardMessage();
            if (roomMessage.getForwardMessage().getAttachment() != null) {
                messageInfo.attachment = StructMessageAttachment.convert(roomMessage.getForwardMessage().getAttachment());
                messageInfo.uploadProgress = messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100 : 0;
            }
        }
        if (roomMessage.getLocation() != null) {
            messageInfo.location = roomMessage.getLocation();
        }

        if (roomMessage.getLog() != null) {
            messageInfo.senderID = "-1";
            //messageInfo.messageText = roomMessage.getLog().getType().toString();
            messageInfo.messageText = roomMessage.getLogMessageWithLinkInfo();
        }

        messageInfo.replayTo = roomMessage.getReplyTo();
        RealmChannelExtra realmChannelExtra = realmChat.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, roomMessage.getMessageId()).findFirst();
        if (realmChannelExtra != null) {
            messageInfo.channelExtra = StructChannelExtra.convert(realmChannelExtra);
        } else {
            messageInfo.channelExtra = new StructChannelExtra();
        }

        messageInfo.showTime = roomMessage.isShowTime();

        return messageInfo;
    }

    public static long getReplyMessageId(StructMessageInfo structMessageInfo) {
        if (structMessageInfo != null && structMessageInfo.replayTo != null) {
            if (structMessageInfo.replayTo.getMessageId() < 0) {
                return (structMessageInfo.replayTo.getMessageId() * (-1));
            } else {
                return structMessageInfo.replayTo.getMessageId();
            }
        }
        return 0;
    }

    public boolean isSenderMe() {

        boolean result = false;

        try {
            result = Long.parseLong(senderID) == userId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public boolean isAuthorMe() {

        boolean output = false;
        if (authorHash != null) {
            output = authorHash.equals(G.authorHash);
        }

        return output;
    }

    public boolean isTimeOrLogMessage() {
        return senderID.equalsIgnoreCase("-1");
    }

    public StructMessageAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(StructMessageAttachment attachment) {
        this.attachment = attachment;
    }

    public boolean hasAttachment() {
        return attachment != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeParcelable(this.view, flags);
        dest.writeLong(this.roomId);
        dest.writeString(this.messageID);
        dest.writeString(this.senderID);
        dest.writeString(this.senderColor);
        dest.writeByte(this.isEdited ? (byte) 1 : (byte) 0);
        dest.writeString(this.status);
        dest.writeString(this.initials);
        dest.writeInt(this.messageType == null ? -1 : this.messageType.ordinal());
        dest.writeInt(this.sendType == null ? -1 : this.sendType.ordinal());
        dest.writeParcelable(Parcels.wrap(this.replayTo), flags);
        dest.writeParcelable(Parcels.wrap(this.forwardedFrom), flags);
        dest.writeString(this.songArtist);
        dest.writeLong(this.songLength);
        dest.writeString(this.messageText);
        dest.writeString(this.fileMime);
        dest.writeString(this.filePic);
        dest.writeString(this.filePath);
        dest.writeByteArray(this.fileHash);
        dest.writeInt(this.uploadProgress);
        dest.writeParcelable(this.attachment, flags);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeParcelable(this.senderAvatar, flags);
        dest.writeLong(this.time);
        /*dest.writeInt(this.voteUp);
        dest.writeInt(this.voteDown);
        dest.writeInt(this.viewsLabel);*/
        dest.writeParcelable(Parcels.wrap(this.channelExtra), flags);
    }
}
