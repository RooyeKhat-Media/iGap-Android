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

import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityChat;
import net.iGap.activities.ActivityContactsProfile;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestUserInfo;

import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_ADDED;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_JOINED_BY_INVITE_LINK;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_KICKED;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.MEMBER_LEFT;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_CONVERTED_TO_PRIVATE;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_CONVERTED_TO_PUBLIC;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_CREATED;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.ROOM_DELETED;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.USER_DELETED;
import static net.iGap.proto.ProtoGlobal.RoomMessageLog.Type.USER_JOINED;

/**
 * return correct log message with author and target
 */
public class HelperLogMessage {

    public static class StructLog {

        long roomId;
        ProtoGlobal.RoomMessage.Author author;
        ProtoGlobal.RoomMessageLog messageLog;
        long messageID;

        long updateID;
    }

    public static void updateLogMessageAfterGetUserInfo(final StructLog item) {

        Realm realm = Realm.getDefaultInstance();

        try {

            final RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, item.messageID).findFirst();

            if (roomMessage != null) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {
                        roomMessage.setLogMessage(HelperLogMessage.logMessage(item.roomId, item.author, item.messageLog, item.messageID));

                        G.logMessageUpdatList.remove(item.updateID);
                    }
                });
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        realm.close();
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

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, author.getUser().getUserId()).findFirst();
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

                HelperInfo.needUpdateUser(author.getUser().getUserId(), author.getUser().getCacheId());
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

                HelperInfo.needUpdateRoomInfo(author.getRoom().getRoomId());
            }
        }

        /**
         * detect targetName
         */
        if (messageLog.hasTargetUser()) {

            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, messageLog.getTargetUser().getId()).findFirst();
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
        logMessage = logMessageString(messageLog.getType());
        String englishResult = "";

        /**
         * final message
         */
        String finalTypeRoom;
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
        if (realmRoom != null && realmRoom.getType() != null) {
            typeRoom = realmRoom.getType();

            if (typeRoom.toString().equals("CHANNEL")) {
                //   finalTypeRoom = G.context.getResources().getString(R.string.channel);
                finalTypeRoom = "کانال";
            } else if (typeRoom.toString().equals("GROUP")) {
                // finalTypeRoom = G.context.getResources().getString(R.string.group);
                finalTypeRoom = "گروه";
            } else {
                //  finalTypeRoom = G.context.getResources().getString(R.string.conversation);
                finalTypeRoom = "صفحه";
            }
        } else {
            // finalTypeRoom = G.context.getResources().getString(R.string.conversation);
            finalTypeRoom = "صفحه";
        }

        if (authorName == null || authorName.length() == 0) {
            return "";
        }

        englishResult = authorName + " " + logMessage + " " + targetName;

        linkInfoEnglish = englishResult.indexOf(authorName)
            + "@"
            + authorName.length()
            + "@"
            + updateID
            + "@"
            + author.hasUser()
            + "@"
            + englishResult.lastIndexOf(targetName)
            + "@"
            + targetName.length()
            + "@"
            + messageLog.getTargetUser().getId();

        switch (messageLog.getType()) {
            case USER_JOINED:
                persianResult = authorName + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case USER_DELETED:
                persianResult = authorName + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case ROOM_CREATED:

                if ((typeRoom == null) || (typeRoom.toString().equals("CHANNEL"))) {
                    persianResult = logMessage + " " + finalTypeRoom + " " + authorName;
                } else {
                    persianResult = logMessage + " " + finalTypeRoom + " توسط " + authorName;
                }

                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();

                break;
            case MEMBER_ADDED:
                persianResult = logMessage + " " + targetName + " توسط " + authorName;

                linlInfoPersian = persianResult.lastIndexOf(authorName)
                    + "@"
                    + authorName.length()
                    + "@"
                    + updateID
                    + "@"
                    + author.hasUser()
                    + "@"
                    + persianResult.indexOf(targetName)
                    + "@"
                    + targetName.length()
                    + "@"
                    + messageLog.getTargetUser().getId();

                break;
            case MEMBER_KICKED:
                persianResult = logMessage + " " + targetName + " توسط " + authorName;

                linlInfoPersian = persianResult.lastIndexOf(authorName)
                    + "@"
                    + authorName.length()
                    + "@"
                    + updateID
                    + "@"
                    + author.hasUser()
                    + "@"
                    + persianResult.indexOf(targetName)
                    + "@"
                    + targetName.length()
                    + "@"
                    + messageLog.getTargetUser().getId();

                break;
            case MEMBER_LEFT:
                persianResult = authorName + " " + finalTypeRoom + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case ROOM_CONVERTED_TO_PUBLIC:
                persianResult = finalTypeRoom + " " + authorName + " " + logMessage;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
            case ROOM_CONVERTED_TO_PRIVATE:
                persianResult = finalTypeRoom + " " + authorName + " " + logMessage;
                break;
            case MEMBER_JOINED_BY_INVITE_LINK:
                persianResult = authorName + " " + logMessage + " " + finalTypeRoom + " اضافه شد ";
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();

                break;
            case ROOM_DELETED:
                persianResult = logMessage + " " + finalTypeRoom + " توسط " + authorName;
                linlInfoPersian = persianResult.indexOf(authorName) + "@" + authorName.length() + "@" + updateID + "@" + author.hasUser();
                break;
        }

        realm.close();

        return (englishResult + "\n" + persianResult + "\n" + linkInfoEnglish + "\n" + linlInfoPersian);
    }

    private static String logMessageString(ProtoGlobal.RoomMessageLog.Type type) {

        int message = 0;

        if (type == USER_JOINED) {
            message = R.string.USER_JOINED;
        } else if (type == USER_DELETED) {
            message = R.string.USER_DELETED;
        } else if (type == ROOM_CREATED) {
            message = R.string.ROOM_CREATED;
        } else if (type == MEMBER_ADDED) {
            //message = "member added";
            message = R.string.MEMBER_ADDED;
        } else if (type == MEMBER_KICKED) {
            //message = "member kicked";
            message = R.string.MEMBER_KICKED;
        } else if (type == MEMBER_LEFT) {
            //message = "member left";
            message = R.string.MEMBER_LEFT;
        } else if (type == ROOM_CONVERTED_TO_PUBLIC) {
            message = R.string.ROOM_CONVERTED_TO_PUBLIC;
        } else if (type == ROOM_CONVERTED_TO_PRIVATE) {
            message = R.string.ROOM_CONVERTED_TO_PRIVATE;
        } else if (type == MEMBER_JOINED_BY_INVITE_LINK) {
            message = R.string.MEMBER_JOINED_BY_INVITE_LINK;
        } else if (type == ROOM_DELETED) {
            message = R.string.Room_Deleted;
        }

        return "*" + message + "*";
    }

    public static String convertLogmessage(String message) {

        if (message == null || message.length() == 0) return null;

        String result = "";

        String str[] = message.split("\n");
        String tmp;

        try {
            if (HelperCalander.isLanguagePersian) {
                tmp = str[1];
            } else {
                tmp = str[0];
            }
            int indexFirst = tmp.indexOf("*");
            int indexLast = tmp.lastIndexOf("*");
            result = tmp.substring(0, indexFirst) + G.context.getString(Integer.parseInt(tmp.substring(indexFirst + 1, indexLast))) + tmp.substring(indexLast + 1);
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
            if (HelperCalander.isLanguagePersian) {
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

            if (HelperCalander.isLanguagePersian) {
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
            String stringValue = G.context.getString(Integer.parseInt(tmp.substring(indexFirst + 1, indexLast)));

            strBuilder.replace(indexFirst, indexLast + 1, stringValue);
        } catch (Exception e) {

        }

        return strBuilder;
    }

    public static void insertClickSpanLink(SpannableStringBuilder builder, int start, int end, final boolean isUser, final long id) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override public void onClick(View widget) {

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

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = Color.DKGRAY;
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

            Intent intent = new Intent(G.context, ActivityContactsProfile.class);
            intent.putExtra("peerId", id);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.putExtra("enterFrom", "GROUP");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.currentActivity.startActivity(intent);
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override public void onChatGetRoom(final long roomId) {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override public void run() {

                            //Intent intent = new Intent(G.currentActivity, ActivityChat.class);
                            //intent.putExtra("peerId", id);
                            //intent.putExtra("RoomId", roomId);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //G.currentActivity.startActivity(intent);

                            Intent intent = new Intent(G.context, ActivityContactsProfile.class);
                            intent.putExtra("peerId", id);
                            intent.putExtra("RoomId", roomId);
                            intent.putExtra("enterFrom", "GROUP");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.currentActivity.startActivity(intent);

                            G.onChatGetRoom = null;
                        }
                    });
                }

                @Override public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                }

                @Override public void onChatGetRoomTimeOut() {

                }

                @Override public void onChatGetRoomError(int majorCode, int minorCode) {

                }
            };

            new RequestChatGetRoom().chatGetRoom(id);
        }

        realm.close();
    }

    private static void goToRoom(Long id) {

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
        if (realmRoom != null) {
            Intent intent = new Intent(G.currentActivity, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.currentActivity.startActivity(intent);
        } else {
            HelperInfo.needUpdateRoomInfo(id);
        }
        realm.close();
    }
}
