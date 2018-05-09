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

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentContactsProfile;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestUserInfo;

import io.realm.Realm;

/**
 * return correct log message with author and target
 */
public class HelperLogMessage {

    public static void updateLogMessageAfterGetUserInfo(final long id) {

        final StructLog item;

        if (G.logMessageUpdatList.containsKey(id)) {

            item = G.logMessageUpdatList.get(id);
        } else {
            return;
        }

        String logMessage = HelperLogMessage.logMessage(item.roomId, item.author, item.messageLog, item.messageID);
        RealmRoomMessage.setLogMessage(item.messageID, logMessage);

        G.logMessageUpdatList.remove(item.updateID);
        if (FragmentChat.iUpdateLogItem != null) {
            FragmentChat.iUpdateLogItem.onUpdate(logMessage, item.messageID);
        }
    }

    public static String logMessage(long roomId, ProtoGlobal.RoomMessage.Author author, ProtoGlobal.RoomMessageLog messageLog, long messageID) {

        String authorName = "";
        String targetName = "";
        String logMessage;
        String persianResult = null;
        Realm realm = Realm.getDefaultInstance();
        ProtoGlobal.Room.Type typeRoom = null;

        String linkInfoEnglish = "";
        String linlInfoPersian = "";

        long updateID = 0;

        /**
         * detect authorName
         */
        if (author.hasUser()) {

            updateID = author.getUser().getUserId();

            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, author.getUser().getUserId());
            if (realmRegisteredInfo != null) {
                authorName = realmRegisteredInfo.getDisplayName();
            } else {

                StructLog item = new StructLog();
                item.roomId = roomId;
                item.messageID = messageID;
                item.author = author;
                item.messageLog = messageLog;
                item.updateID = updateID;

                G.logMessageUpdatList.put(updateID, item);

                new RequestUserInfo().userInfoAvoidDuplicate(author.getUser().getUserId());
            }
        } else if (author.hasRoom()) {

            updateID = author.getRoom().getRoomId();

            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, author.getRoom().getRoomId()).findFirst();
            if (realmRoom != null) {
                authorName = realmRoom.getTitle();
            } else {
                StructLog item = new StructLog();
                item.roomId = roomId;
                item.messageID = messageID;
                item.author = author;
                item.messageLog = messageLog;
                item.updateID = updateID;

                G.logMessageUpdatList.put(updateID, item);

                RealmRoom.needUpdateRoomInfo(author.getRoom().getRoomId());

                new RequestClientGetRoom().clientGetRoom(author.getRoom().getRoomId(), RequestClientGetRoom.CreateRoomMode.justInfo);
            }
        }

        /**
         * detect targetName
         */
        if (messageLog.hasTargetUser()) {

            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, messageLog.getTargetUser().getId());
            if (realmRegisteredInfo != null) {
                targetName = realmRegisteredInfo.getDisplayName();
            } else {

                StructLog item = new StructLog();
                item.roomId = roomId;
                item.messageID = messageID;
                item.author = author;
                item.messageLog = messageLog;
                item.updateID = messageLog.getTargetUser().getId();
                G.logMessageUpdatList.put(messageLog.getTargetUser().getId(), item);
                new RequestUserInfo().userInfo(messageLog.getTargetUser().getId());
            }
        }

        /**
         * detect log message
         */
        logMessage = logMessageString(messageLog.getType(), author);
        String englishResult = "";

        /**
         * final message
         */
        String finalTypeRoom;
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null && realmRoom.getType() != null) {
            typeRoom = realmRoom.getType();

            if (typeRoom.toString().equals("CHANNEL")) {
                //   finalTypeRoom = G.fragmentActivity.getResources().getString(R.string.channel);
                finalTypeRoom = "کانال";
            } else if (typeRoom.toString().equals("GROUP")) {
                // finalTypeRoom = G.fragmentActivity.getResources().getString(R.string.group);
                finalTypeRoom = "گروه";
            } else {
                //  finalTypeRoom = G.fragmentActivity.getResources().getString(R.string.conversation);
                finalTypeRoom = "صفحه";
            }
        } else {
            // finalTypeRoom = G.fragmentActivity.getResources().getString(R.string.conversation);
            finalTypeRoom = "صفحه";
        }

        if (authorName == null || authorName.length() == 0) {
            return "";
        }

        englishResult = "\u200E" + authorName + " " + logMessage + " " + targetName;

        linkInfoEnglish = englishResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser() + "@" + englishResult.lastIndexOf(targetName) + "@" + targetName.length() + "@" + messageLog.getTargetUser().getId();

        switch (messageLog.getType()) {
            case USER_JOINED:
                persianResult = "\u200F" + authorName + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case USER_DELETED:
                persianResult = "\u200F" + authorName + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case ROOM_CREATED:

                if ((typeRoom == null) || (typeRoom.toString().equals("CHANNEL"))) {
                    persianResult = finalTypeRoom + " " + authorName + " " + logMessage;

                    englishResult = "Channel" + " " + logMessage.replace("Created Room", "Created") + " " + targetName;
                } else {
                    persianResult = finalTypeRoom + " توسط " + authorName + " " + logMessage;

                    englishResult = "Group" + " " + logMessage.replace("Room", "") + " " + targetName;
                }

                linkInfoEnglish = "";
                linlInfoPersian = "";

                break;
            case MEMBER_ADDED:
                persianResult = "\u200F" + targetName + " توسط " + authorName + " " + logMessage;
                linlInfoPersian = persianResult.lastIndexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser() + "@" + persianResult.indexOf(targetName) + "@" + targetName.length() + "@" + messageLog.getTargetUser().getId();

                break;
            case MEMBER_KICKED:
                persianResult = "\u200F" + targetName + " توسط " + authorName + " " + logMessage;
                linlInfoPersian = persianResult.lastIndexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser() + "@" + persianResult.indexOf(targetName) + "@" + targetName.length() + "@" + messageLog.getTargetUser().getId();

                break;
            case MEMBER_LEFT:
                persianResult = "\u200F" + authorName + " " + finalTypeRoom + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case ROOM_CONVERTED_TO_PUBLIC:

                if ((typeRoom == null) || (typeRoom.toString().equals("CHANNEL"))) {
                    persianResult = finalTypeRoom + " " + logMessage;

                    englishResult = "Channel" + " " + logMessage + " " + targetName;
                } else {
                    persianResult = finalTypeRoom + " " + logMessage;

                    englishResult = "Group" + " " + logMessage + " " + targetName;
                }
                linlInfoPersian = "";
                linkInfoEnglish = "";
                break;
            case ROOM_CONVERTED_TO_PRIVATE:

                if ((typeRoom == null) || (typeRoom.toString().equals("CHANNEL"))) {
                    persianResult = finalTypeRoom + " " + logMessage;
                    englishResult = "Channel" + " " + logMessage + " " + targetName;
                } else {
                    persianResult = finalTypeRoom + " " + logMessage;
                    englishResult = "Group" + " " + logMessage + " " + targetName;
                }

                linlInfoPersian = "";
                linkInfoEnglish = "";

                break;
            case MEMBER_JOINED_BY_INVITE_LINK:
                persianResult = "\u200F" + authorName + " " + logMessage + " " + finalTypeRoom + " اضافه شد ";
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();

                break;
            case ROOM_DELETED:
                persianResult = finalTypeRoom + " توسط " + authorName + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case MISSED_VOICE_CALL:
                // persianResult = authorName + " " + logMessage;
                //  linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();

                persianResult = englishResult = logMessage;
                linkInfoEnglish = linlInfoPersian = "";

                break;
        }

        realm.close();

        return (englishResult + "\n" + persianResult + "\n" + linkInfoEnglish + "\n" + linlInfoPersian);
    }

    private static String logMessageString(ProtoGlobal.RoomMessageLog.Type type, ProtoGlobal.RoomMessage.Author author) {
        String message;
        if (type == ProtoGlobal.RoomMessageLog.Type.MISSED_VOICE_CALL) {
            if (G.authorHash.equals(author.getHash())) {
                message = ProtoGlobal.RoomMessageLog.Type.MISSED_VOICE_CALL + "1";
            } else {
                message = ProtoGlobal.RoomMessageLog.Type.MISSED_VOICE_CALL + "2";
            }
        } else {
            message = type + "";
        }
        return "*" + message + "*";
    }

    public static String getStringFromLog(String text) {
        int messageID = 0;
        switch (text) {
            case "USER_JOINED":
                messageID = R.string.USER_JOINED;
                break;
            case "USER_DELETED":
                messageID = R.string.USER_DELETED;
                break;
            case "ROOM_CREATED":
                messageID = R.string.ROOM_CREATED;
                break;
            case "MEMBER_ADDED":
                //message = "member added";
                messageID = R.string.MEMBER_ADDED;
                break;
            case "MEMBER_KICKED":
                //message = "member kicked";
                messageID = R.string.MEMBER_KICKED;
                break;
            case "MEMBER_LEFT":
                //message = "member left";
                messageID = R.string.MEMBER_LEFT;
                break;
            case "ROOM_CONVERTED_TO_PUBLIC":
                messageID = R.string.ROOM_CONVERTED_TO_PUBLIC;
                break;
            case "ROOM_CONVERTED_TO_PRIVATE":
                messageID = R.string.ROOM_CONVERTED_TO_PRIVATE;
                break;
            case "MEMBER_JOINED_BY_INVITE_LINK":
                messageID = R.string.MEMBER_JOINED_BY_INVITE_LINK;
                break;
            case "ROOM_DELETED":
                messageID = R.string.Room_Deleted_log;
                break;
            case "MISSED_SCREEN_SHARE":
                messageID = R.string.MISSED_SCREEN_SHARE;
                break;
            case "MISSED_VOICE_CALL1":
                messageID = R.string.not_answerd_call;
                break;
            case "MISSED_VOICE_CALL2":
                messageID = R.string.MISSED_VOICE_CALL;
                break;
            case "MISSED_VIDEO_CALL":
                messageID = R.string.MISSED_VIDEO_CALL;
                break;
        }

        if (messageID > 0) {
            return G.context.getString(messageID);
        } else {

            try {
                return G.context.getString(Integer.parseInt(text));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String convertLogmessage(String message) {
        if (message == null || message.length() == 0) return null;
        String result = "";
        String str[] = message.split("\n");
        String tmp;
        try {
            if (HelperCalander.isPersianUnicode) {
                tmp = str[1];
            } else {
                tmp = str[0];
            }
            int indexFirst = tmp.indexOf("*");
            int indexLast = tmp.lastIndexOf("*");
            result = tmp.substring(0, indexFirst) + getStringFromLog(tmp.substring(indexFirst + 1, indexLast)) + tmp.substring(indexLast + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static SpannableStringBuilder getLogMessageWithLink(String text) {

        if (text == null || text.length() == 0) {
            return null;
        }

        String str[] = text.split("\n");
        String tmp = null;

        try {
            if (HelperCalander.isPersianUnicode) {
                tmp = str[1];
            } else {
                tmp = str[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (str.length < 3) {
            return new SpannableStringBuilder(convertLogmessage(text));
        }

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(tmp);

        String linkInfo = "";

        try {

            if (HelperCalander.isPersianUnicode) {
                linkInfo = str[3];
            } else {
                linkInfo = str[2];
            }

            String splitText[] = linkInfo.split("@");

            int autherIndex = Integer.parseInt(splitText[0]);
            int authLeng = Integer.parseInt(splitText[1]);
            long id = Long.parseLong(splitText[2]);
            boolean isAutherUser = Boolean.parseBoolean(splitText[3]);

            insertClickSpanLink(strBuilder, autherIndex, autherIndex + authLeng, isAutherUser, id);

            if (splitText.length > 4) {

                int targetIndex = Integer.parseInt(splitText[4]);
                int targetLeng = Integer.parseInt(splitText[5]);
                long targetId = Long.parseLong(splitText[6]);

                insertClickSpanLink(strBuilder, targetIndex, targetIndex + targetLeng, true, targetId);
            }

            int indexFirst = tmp.indexOf("*");
            int indexLast = tmp.lastIndexOf("*");

            strBuilder.replace(indexFirst, indexLast + 1, getStringFromLog(tmp.substring(indexFirst + 1, indexLast)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strBuilder;
    }

    private static void insertClickSpanLink(SpannableStringBuilder builder, int start, int end, final boolean isUser, final long id) {

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

        builder.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    public static class StructLog {

        long roomId;
        ProtoGlobal.RoomMessage.Author author;
        ProtoGlobal.RoomMessageLog messageLog;
        long messageID;

        long updateID;
    }
}
