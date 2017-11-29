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

import net.iGap.Config;
import net.iGap.G;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChatSetAction;
import net.iGap.request.RequestGroupSetAction;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import io.realm.Realm;

public class HelperSetAction {

    private static ArrayList<StructAction> structActions = new ArrayList<>();

    /**
     * set action for showing audience typing.
     *
     * @param roomId roomId that send action from that
     */

    public static void setActionTyping(final long roomId, ProtoGlobal.Room.Type chatType) {

        if (!checkExistAction(roomId, ProtoGlobal.ClientAction.TYPING) && chatType != null && chatType != ProtoGlobal.Room.Type.CHANNEL) {
            int randomNumber = HelperNumerical.generateRandomNumber(8);

            final StructAction structAction = new StructAction();
            structAction.roomId = roomId;
            structAction.currentTime = System.currentTimeMillis();
            structAction.randomKey = randomNumber;
            structAction.action = ProtoGlobal.ClientAction.TYPING;
            structAction.chatType = chatType;

            structActions.add(structAction);

            if (chatType.toString().equals(ProtoGlobal.Room.Type.GROUP.toString())) {
                new RequestGroupSetAction().groupSetAction(roomId, ProtoGlobal.ClientAction.TYPING, randomNumber);
            } else if (chatType.toString().equals(ProtoGlobal.Room.Type.CHAT.toString())) {
                new RequestChatSetAction().chatSetAction(roomId, ProtoGlobal.ClientAction.TYPING, randomNumber);
            }
            timeOutChecking(structAction);
        }
    }

    /**
     * set action for cancel audience typing.
     *
     * @param roomId roomId that send action from that
     */

    public static void setCancel(final long roomId) {
        if (checkExistAction(roomId, ProtoGlobal.ClientAction.TYPING)) {
            for (int i = structActions.size() - 1; i >= 0; i--) {
                StructAction action = structActions.get(i);
                if (action.action == ProtoGlobal.ClientAction.TYPING) {
                    if (action.chatType.toString().equals(ProtoGlobal.Room.Type.GROUP.toString())) {
                        new RequestGroupSetAction().groupSetAction(roomId, ProtoGlobal.ClientAction.CANCEL, action.randomKey);
                    } else {
                        new RequestChatSetAction().chatSetAction(roomId, ProtoGlobal.ClientAction.CANCEL, action.randomKey);
                    }
                    removeStruct(action.randomKey);
                    break;
                }
            }
        }
    }

    /**
     * set action for showing audience action.
     *
     * @param roomId    roomId that send action from that
     * @param messageId unique number that we have in start upload and end of that
     * @param action    action that doing
     */

    public static void setActionFiles(long roomId, long messageId, ProtoGlobal.ClientAction action, ProtoGlobal.Room.Type chatType) {

        /**
         * if chatType not exist and can't be detected return and don't send action
         */
        if (chatType == null) {
            if (roomId == 0 || messageId == 0) {
                return;
            }

            Realm realm = Realm.getDefaultInstance();
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
            if (realmRoom != null && realmRoom.getType() != null) {
                chatType = realmRoom.getType();
                realm.close();
            } else {
                realm.close();
                return;
            }
        }

        /**
         * channel don't have set action
         */
        if (chatType == ProtoGlobal.Room.Type.CHANNEL) {
            return;
        }

        if (!checkExistAction(roomId, action)) {
            int randomNumber = HelperNumerical.generateRandomNumber(8);

            final StructAction structAction = new StructAction();
            structAction.roomId = roomId;
            structAction.currentTime = System.currentTimeMillis();
            structAction.randomKey = randomNumber;
            structAction.action = action;
            structAction.chatType = chatType;
            structAction.messageId = messageId;

            structActions.add(structAction);

            if (chatType != null) {
                if (chatType.toString().equals(ProtoGlobal.Room.Type.GROUP.toString())) {
                    new RequestGroupSetAction().groupSetAction(roomId, action, randomNumber);
                } else {
                    new RequestChatSetAction().chatSetAction(roomId, action, randomNumber);
                }
            }
        }
    }

    private static void timeOutChecking(final StructAction structAction) {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 * check that this action exist in structAction or not.
                 * if not exist don't try for cancel that ,because this
                 * means that this randomKey removed before
                 */
                if (!existStruct(structAction.randomKey)) {
                    return;
                }

                if (autoCancel(structAction.currentTime)) {
                    removeStruct(structAction.randomKey);

                    if (structAction.chatType.toString().equals(ProtoGlobal.Room.Type.GROUP.toString())) {
                        new RequestGroupSetAction().groupSetAction(structAction.roomId, ProtoGlobal.ClientAction.CANCEL, structAction.randomKey);
                    } else {
                        new RequestChatSetAction().chatSetAction(structAction.roomId, ProtoGlobal.ClientAction.CANCEL, structAction.randomKey);
                    }
                } else {
                    timeOutChecking(structAction);
                }
            }
        }, Config.ACTION_CHECKING);
    }

    /**
     * send cancel for files
     *
     * @param messageId unique number that we have in start upload and end of that
     */

    public static void sendCancel(final long messageId) {
        try {
            for (StructAction struct : structActions) {
                if (struct.messageId == messageId) {

                    if (struct.chatType.toString().equals(ProtoGlobal.Room.Type.GROUP.toString())) {
                        new RequestGroupSetAction().groupSetAction(struct.roomId, ProtoGlobal.ClientAction.CANCEL, struct.randomKey);
                    } else {
                        new RequestChatSetAction().chatSetAction(struct.roomId, ProtoGlobal.ClientAction.CANCEL, struct.randomKey);
                    }

                    removeStruct(struct.randomKey);
                }
            }
        } catch (ConcurrentModificationException e) {
            G.handler.postDelayed(new Runnable() { // resend after one second
                @Override
                public void run() {
                    sendCancel(messageId);
                }
            }, 1000);
            e.printStackTrace();
        }
    }

    /**
     * check difference time from latest set action .
     * if time difference was more than Config.ACTION_TIME_OUT second
     * return true for send cancel request for that action
     *
     * @param startActionTime currentTimeMillis for start action or update action(when repeat it)
     */

    private static boolean autoCancel(long startActionTime) {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - startActionTime);

        if (difference >= Config.ACTION_TIME_OUT) {
            return true;
        }

        return false;
    }

    private static class StructAction {

        public long roomId;
        public long currentTime;
        public long messageId; // messageId is a unique number that we have it in start and end of upload
        public int randomKey;
        public ProtoGlobal.Room.Type chatType;
        public ProtoGlobal.ClientAction action;
    }

    /**
     * check that action with same roomId is exist or not.
     * return true if exist and update time otherwise just return false.
     *
     * @param roomId roomId that send action from that
     * @param action action that send
     */
    private static boolean checkExistAction(long roomId, ProtoGlobal.ClientAction action) {
        for (StructAction struct : structActions) {
            if (struct.roomId == roomId && struct.action == action) {
                struct.currentTime = System.currentTimeMillis();
                return true;
            }
        }
        return false;
    }

    /**
     * remove item from structActions array with randomKey.
     *
     * @param randomKey randomKey that should remove struct with that
     */
    private static void removeStruct(int randomKey) {
        for (int i = 0; i < structActions.size(); i++) {
            if (structActions.get(i).randomKey == randomKey) {
                structActions.remove(i);
                break;
            }
        }
    }

    /**
     * check that exist this value or not
     *
     * @param randomKey check with randomKey that exist in StructAction
     */
    private static boolean existStruct(int randomKey) {
        for (int i = 0; i < structActions.size(); i++) {
            if (structActions.get(i).randomKey == randomKey) {
                return true;
            }
        }
        return false;
    }

    /**
     * check that this room have any action for show to user
     * hint : call this method in start room
     *
     * @param roomId roomId that come to that
     * @return action for this room
     */

    public static String checkExistAction(long roomId) {
        for (StructAction struct : structActions) {
            if (struct.roomId == roomId) {
                return HelperConvertEnumToString.convertActionEnum(struct.action);
            }
        }

        return null;
    }
}
