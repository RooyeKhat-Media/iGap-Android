/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.google.protobuf.InvalidProtocolBufferException;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.module.AppUtils;
import net.iGap.module.SerializationUtils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestUserInfo;

import java.io.Serializable;
import java.util.HashMap;

import io.realm.Realm;

import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.PINNED_MESSAGE;

/**
 * return correct log message with author and target
 */
public class HelperLogMessage {

    public static HashMap<Long, StructMyLog> logMessageUpdateList = new HashMap<>();

    static class StructMyLog implements Serializable {
        long roomId;
        byte[] author;
        byte[] messageLog;
        long messageID;
        String message;
    }

    public static byte[] serializeLog(long roomId, ProtoGlobal.RoomMessage.Author author, ProtoGlobal.RoomMessageLog messageLog, long messageID, String message, ProtoGlobal.RoomMessageType messageType) {

        StructMyLog log = new StructMyLog();
        log.roomId = roomId;
        log.author = author.toByteArray();
        log.messageLog = messageLog.toByteArray();
        log.messageID = messageID;

        if (messageLog.getType() == PINNED_MESSAGE) {
            if (message.length() > 0) {
                log.message = message.length() > 30 ? message.substring(0, 30) : message;
            } else if (messageType != null) {
                log.message = AppUtils.conversionMessageType(messageType);
            }
        }

        return SerializationUtils.serialize(log);
    }

    public static SpannableStringBuilder deserializeLog(byte[] logs, boolean withLink) {


        if (logs == null) {
            return new SpannableStringBuilder("");
        }
        try {
            StructMyLog log = (StructMyLog) SerializationUtils.deserialize(logs);
            if (log != null) {
                return extractLog(log, withLink);
            }
        } catch (Exception e) {
            HelperLog.setErrorLog(" helper log message     deserializeLog()       " + e.toString());
            return new SpannableStringBuilder("");
        }

        return new SpannableStringBuilder("");
    }

    private static SpannableStringBuilder extractLog(StructMyLog log, boolean withLink) throws InvalidProtocolBufferException {
        Realm realm = Realm.getDefaultInstance();
        String authorName = getAuthorName(log, realm);
        String targetName = getTargetName(log, realm);
        String finalTypeRoom = getRoomTypeString(log, realm);
        String LogMessageTypeString = getLogTypeString(ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).getType(), ProtoGlobal.RoomMessage.Author.parseFrom(log.author));
        realm.close();
        return getLogMessage(authorName, targetName, finalTypeRoom, LogMessageTypeString, log, withLink);
    }

    private static String getAuthorName(StructMyLog log, Realm realm) throws InvalidProtocolBufferException {
        String result = "";

        if (ProtoGlobal.RoomMessage.Author.parseFrom(log.author).hasUser()) {
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getUser().getUserId());
            if (realmRegisteredInfo != null) {
                result = " " + realmRegisteredInfo.getDisplayName() + " ";
            } else {
                logMessageUpdateList.put(ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getUser().getUserId(), log);
                new RequestUserInfo().userInfoAvoidDuplicate(ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getUser().getUserId());
            }
        } else if (ProtoGlobal.RoomMessage.Author.parseFrom(log.author).hasRoom()) {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getRoom().getRoomId()).findFirst();
            if (realmRoom != null) {
                result = " " + realmRoom.getTitle() + " ";
            } else {
                logMessageUpdateList.put(ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getRoom().getRoomId(), log);
                RealmRoom.needUpdateRoomInfo(ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getRoom().getRoomId());
                new RequestClientGetRoom().clientGetRoom(ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getRoom().getRoomId(), RequestClientGetRoom.CreateRoomMode.justInfo);
            }
        }

        return result;
    }

    private static String getTargetName(StructMyLog log, Realm realm) throws InvalidProtocolBufferException {
        String result = "";
        if (ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).hasTargetUser()) {
            long userId = ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).getTargetUser().getId();
            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, userId);
            if (realmRegisteredInfo != null) {
                result = " " + realmRegisteredInfo.getDisplayName() + " ";
            } else {
                logMessageUpdateList.put(ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).getTargetUser().getId(), log);
                new RequestUserInfo().userInfo(userId);
            }
        }
        return result;
    }

    private static String getRoomTypeString(StructMyLog log, Realm realm) {
        String result = G.fragmentActivity.getResources().getString(R.string.conversation);
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, log.roomId).findFirst();
        if (realmRoom != null && realmRoom.getType() != null) {
            switch (realmRoom.getType()) {
                case GROUP:
                    result = G.fragmentActivity.getResources().getString(R.string.group);
                    break;
                case CHANNEL:
                    result = G.fragmentActivity.getResources().getString(R.string.channel);
                    break;
            }
        }
        return " " + result + " ";
    }

    private static String getLogTypeString(ProtoGlobal.RoomMessageLog.Type type, ProtoGlobal.RoomMessage.Author author) {
        int messageID = 0;

        switch (type) {
            case USER_JOINED:
                messageID = R.string.USER_JOINED;
                break;
            case USER_DELETED:
                messageID = R.string.USER_DELETED;
                break;
            case ROOM_CREATED:
                messageID = R.string.ROOM_CREATED;
                break;
            case MEMBER_ADDED:
                messageID = R.string.MEMBER_ADDED;
                break;
            case MEMBER_KICKED:
                messageID = R.string.MEMBER_KICKED;
                break;
            case MEMBER_LEFT:
                messageID = R.string.MEMBER_LEFT;
                break;
            case ROOM_CONVERTED_TO_PUBLIC:
                messageID = R.string.ROOM_CONVERTED_TO_PUBLIC;
                break;
            case ROOM_CONVERTED_TO_PRIVATE:
                messageID = R.string.ROOM_CONVERTED_TO_PRIVATE;
                break;
            case MEMBER_JOINED_BY_INVITE_LINK:
                messageID = R.string.MEMBER_JOINED_BY_INVITE_LINK;
                break;
            case ROOM_DELETED:
                messageID = R.string.Room_Deleted_log;
                break;
            case MISSED_VOICE_CALL:
                if (G.authorHash.equals(author.getHash())) {
                    messageID = R.string.not_answerd_call;
                } else {
                    messageID = R.string.MISSED_VOICE_CALL;
                }
                break;
            case MISSED_VIDEO_CALL:
                messageID = R.string.MISSED_VIDEO_CALL;
                break;
            case MISSED_SCREEN_SHARE:
                messageID = R.string.MISSED_SCREEN_SHARE;
                break;
            case MISSED_SECRET_CHAT:
                break;
            case PINNED_MESSAGE:
                messageID = R.string.pined_message;
                break;
            case UNRECOGNIZED:
                break;
        }

        if (messageID > 0) {
            return G.context.getString(messageID);
        }

        return "";
    }

    private static SpannableStringBuilder getLogMessage(String authorName, String targetName, String finalTypeRoom, String LogMessageTypeString, StructMyLog log, boolean withLink) throws InvalidProtocolBufferException {

//        if (authorName == null || authorName.length() == 0) {
//            return "";
//        }

        long authorId = 0;
        boolean isAuthorUser = false;
        long targetId = 0;

        if (withLink) {
            if (ProtoGlobal.RoomMessage.Author.parseFrom(log.author).hasUser()) {
                authorId = ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getUser().getUserId();
                isAuthorUser = true;
            } else if (ProtoGlobal.RoomMessage.Author.parseFrom(log.author).hasRoom()) {
                authorId = ProtoGlobal.RoomMessage.Author.parseFrom(log.author).getRoom().getRoomId();
                isAuthorUser = false;
            }

            if (ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).hasTargetUser()) {
                targetId = ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).getTargetUser().getId();
            }
        }


        SpannableStringBuilder strBuilder = new SpannableStringBuilder();

        strBuilder.append("\u200E");
        insertClickSpanLink(strBuilder, authorName, isAuthorUser, authorId);
        strBuilder.append(LogMessageTypeString);
        insertClickSpanLink(strBuilder, targetName, true, targetId);

        switch (ProtoGlobal.RoomMessageLog.parseFrom(log.messageLog).getType()) {

            case ROOM_CREATED:
                strBuilder.clear();
                if (HelperCalander.isPersianUnicode) {
                    strBuilder.append(finalTypeRoom);
                    strBuilder.append(G.context.getString(R.string.prefix));
                    insertClickSpanLink(strBuilder, authorName, isAuthorUser, authorId);
                    strBuilder.append(LogMessageTypeString);
                } else {
                    insertClickSpanLink(strBuilder, targetName, true, targetId);
                    strBuilder.append(LogMessageTypeString);
                    strBuilder.append(finalTypeRoom);
                }
                break;
            case MEMBER_ADDED:
            case MEMBER_KICKED:
                if (HelperCalander.isPersianUnicode) {
                    strBuilder.clear();
                    strBuilder.append("\u200E");
                    insertClickSpanLink(strBuilder, targetName, true, targetId);
                    strBuilder.append(G.context.getString(R.string.prefix));
                    insertClickSpanLink(strBuilder, authorName, isAuthorUser, authorId);
                    strBuilder.append(LogMessageTypeString);
                }
                break;
            case MEMBER_LEFT:
                if (HelperCalander.isPersianUnicode) {
                    strBuilder.clear();
                    strBuilder.append("\u200E");
                    insertClickSpanLink(strBuilder, authorName, isAuthorUser, authorId);
                    strBuilder.append(finalTypeRoom);
                    strBuilder.append(LogMessageTypeString);
                }
                break;
            case ROOM_CONVERTED_TO_PUBLIC:
            case ROOM_CONVERTED_TO_PRIVATE:
                strBuilder.clear();
                if (HelperCalander.isPersianUnicode) {
                    strBuilder.append(finalTypeRoom);
                    strBuilder.append(LogMessageTypeString);
                } else {
                    strBuilder.append(finalTypeRoom);
                    strBuilder.append(LogMessageTypeString);
                    insertClickSpanLink(strBuilder, targetName, true, targetId);
                }
                break;
            case MEMBER_JOINED_BY_INVITE_LINK:
                if (HelperCalander.isPersianUnicode) {
                    strBuilder.clear();
                    strBuilder.append("\u200E");
                    insertClickSpanLink(strBuilder, authorName, isAuthorUser, authorId);
                    strBuilder.append(LogMessageTypeString);
                    strBuilder.append(finalTypeRoom);
                    strBuilder.append(G.context.getString(R.string.MEMBER_ADDED));
                }
                break;
            case ROOM_DELETED:
                if (HelperCalander.isPersianUnicode) {
                    strBuilder.clear();
                    strBuilder.append(finalTypeRoom);
                    strBuilder.append(G.context.getString(R.string.prefix));
                    insertClickSpanLink(strBuilder, authorName, isAuthorUser, authorId);
                    strBuilder.append(LogMessageTypeString);
                }
                break;
            case MISSED_VOICE_CALL:
            case MISSED_VIDEO_CALL:
            case MISSED_SCREEN_SHARE:
            case MISSED_SECRET_CHAT:
                strBuilder.clear();
                strBuilder.append(LogMessageTypeString);
                break;
            case PINNED_MESSAGE:
                strBuilder.clear();
                String temp = log.message + "  " + LogMessageTypeString;
                strBuilder.append(temp);
                break;
            case UNRECOGNIZED:
                strBuilder.clear();
                break;
        }


        return strBuilder;
    }

    public static void updateLogMessageAfterGetUserInfo(long id) {
        if (logMessageUpdateList.containsKey(id)) {
            if (FragmentChat.iUpdateLogItem != null) {
                StructMyLog log = logMessageUpdateList.get(id);
                FragmentChat.iUpdateLogItem.onUpdate(SerializationUtils.serialize(log), log.messageID);
            }
            logMessageUpdateList.remove(id);
        }
    }


    private static void insertClickSpanLink(SpannableStringBuilder strBuilder, String message, final boolean isUser, final long id) {

        if (message.length() == 0) {
            return;
        }

        strBuilder.append(message);

        if (id != 0) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {

                    if (isUser) {

                        if (id > 0) {
                            gotToUserRoom(id);
                        }
                    } else {
                        if (id > 0) {
                            goToRoom(id);
                        }
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    if (G.isDarkTheme) {
                        ds.linkColor = Color.parseColor(G.textTitleTheme);
                    } else {
                        ds.linkColor = Color.DKGRAY;
                    }

                    super.updateDrawState(ds);
                }
            };
            strBuilder.setSpan(clickableSpan, strBuilder.length() - message.length(), strBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

    }

    private static void gotToUserRoom(final long id) {

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).findFirst();
        if (realmRoom != null) {
            //Intent intent = new Intent(G.currentActivity, ActivityChat.class);
            //intent.putExtra("RoomId", realmRoom.getId());
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //G.currentActivity.startActivity(intent);

            //Intent intent = new Intent(G.context, ActivityContactsProfile.class);
            //intent.putExtra("peerId", id);
            //intent.putExtra("RoomId", realmRoom.getId());
            //intent.putExtra("enterFrom", "GROUP");
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //G.currentActivity.startActivity(intent);

            FragmentContactsProfile contactsProfile = new FragmentContactsProfile();
            Bundle bundle = new Bundle();
            bundle.putLong("peerId", id);
            bundle.putLong("RoomId", realmRoom.getId());
            bundle.putString("enterFrom", "GROUP");
            contactsProfile.setArguments(bundle);

            new HelperFragment(contactsProfile).setReplace(false).load();

        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final ProtoGlobal.Room room) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            FragmentContactsProfile contactsProfile = new FragmentContactsProfile();
                            Bundle bundle = new Bundle();
                            bundle.putLong("peerId", id);
                            bundle.putLong("RoomId", room.getId());
                            bundle.putString("enterFrom", "GROUP");
                            contactsProfile.setArguments(bundle);
                            new HelperFragment(contactsProfile).setReplace(false).load();
                            G.onChatGetRoom = null;
                        }
                    });
                }

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {

                }
            };

            new RequestChatGetRoom().chatGetRoom(id);
        }

        realm.close();
    }

    private static void goToRoom(Long roomId) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null) {
            new GoToChatActivity(realmRoom.getId()).startActivity();
        } else {
            RealmRoom.needUpdateRoomInfo(roomId);
        }
        realm.close();
    }


}
