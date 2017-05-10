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

import io.realm.Realm;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRegisteredInfoFields;

import static net.iGap.helper.HelperConvertEnumToString.convertActionEnum;

public class HelperGetAction {

    private static CopyOnWriteArrayList<StructAction> structActions = new CopyOnWriteArrayList<>();

    private static class StructAction {
        public long roomId;
        public long userId;
        public ProtoGlobal.ClientAction action;
    }

    public static String getAction(long roomId, ProtoGlobal.Room.Type type, ProtoGlobal.ClientAction clientAction) {
        if (type == ProtoGlobal.Room.Type.CHAT) {
            String action = convertActionEnum(clientAction);
            if (action != null) {
                return action;
            }
            return null;
        } else if (type == ProtoGlobal.Room.Type.GROUP) {

            final String actionText = HelperGetAction.getMultipleAction(roomId);
            if (actionText != null) {
                return actionText;
            }
            return null;
        }
        return null;
    }

    /**
     * search structActions list for this roomId and return correct string for show in toolbar
     *
     * @param roomId current roomId
     * @return text for show in toolbar
     */

    private static String getMultipleAction(long roomId) {

        ProtoGlobal.ClientAction latestAction = getLatestAction(roomId);
        if (latestAction == null) {
            return null;
        } else {
            int count = 0;
            StructAction latestStruct = null;
            Iterator<StructAction> iterator1 = structActions.iterator();
            ArrayList<Long> userIds = new ArrayList<>();
            while (iterator1.hasNext()) {
                StructAction struct = iterator1.next();
                if (struct.roomId == roomId && struct.action == latestAction) {
                    if (!userIds.contains(struct.userId)) {
                        userIds.add(struct.userId);
                        latestStruct = struct;
                        count++;
                    }
                }
            }
            if (count == 1) {

                Realm realm = Realm.getDefaultInstance();

                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, latestStruct.userId).findFirst();
                if (realmRegisteredInfo != null) {
                    String action;
                    if (HelperCalander.isLanguagePersian) {
                        if (Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                            action = realmRegisteredInfo.getDisplayName() + " " + convertActionEnum(latestStruct.action);
                        } else if (Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_EUROPEAN_NUMBER) {
                            action = "\u200F" + realmRegisteredInfo.getDisplayName() + " " + convertActionEnum(latestStruct.action);
                        } else {
                            action = "\u200E" + convertActionEnum(latestStruct.action) + " " + realmRegisteredInfo.getDisplayName();
                        }
                    } else {
                        if (Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {
                            action = "\u200E" + realmRegisteredInfo.getDisplayName() + " " + G.context.getResources().getString(R.string.is) + " " + convertActionEnum(latestStruct.action);
                        } else if (Character.getDirectionality(realmRegisteredInfo.getDisplayName().charAt(0)) == Character.DIRECTIONALITY_EUROPEAN_NUMBER) {
                            action = "\u200E" + realmRegisteredInfo.getDisplayName() + " " + G.context.getResources().getString(R.string.is) + " " + convertActionEnum(latestStruct.action);
                        } else {
                            action = realmRegisteredInfo.getDisplayName() + " " + G.context.getResources().getString(R.string.is) + " " + convertActionEnum(latestStruct.action);
                        }
                    }

                    return action;
                } else {
                    realm.close();
                    return null;
                }
            } else if (count < Config.GROUP_SHOW_ACTIONS_COUNT) {

                String concatenatedNames = "";

                Realm realm = Realm.getDefaultInstance();

                //for (StructAction struct : structActions) {
                Iterator<StructAction> iterator = structActions.iterator();
                while (iterator.hasNext()) {
                    StructAction struct = iterator.next();
                    if (struct.action == latestAction) {
                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, struct.userId).findFirst();
                        if (realmRegisteredInfo != null) {
                            concatenatedNames += realmRegisteredInfo.getDisplayName() + ",";
                        }
                    }
                }

                realm.close();

                if (concatenatedNames.isEmpty() || concatenatedNames.length() == 0) {
                    return null;
                }
                concatenatedNames = concatenatedNames.substring(0, concatenatedNames.length() - 1);

                if (HelperCalander.isLanguagePersian) {
                    if (Character.getDirectionality(concatenatedNames.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC) {

                        return concatenatedNames + " " + HelperConvertEnumToString.convertActionEnum(latestAction);
                    } else {

                        return HelperConvertEnumToString.convertActionEnum(latestAction) + " " + concatenatedNames;
                    }
                } else {
                    return concatenatedNames + " " + G.context.getResources().getString(R.string.are) + " " + convertActionEnum(latestAction);
                }
            } else {
                if (HelperCalander.isLanguagePersian) {

                    return convertActionEnum(latestAction) + " " + count + " " + G.context.getResources().getString(R.string.members_are);
                } else {

                    return count + " " + G.context.getResources().getString(R.string.members_are) + " " + convertActionEnum(latestAction);
                }
            }
        }
    }

    /**
     * get latest action that do in this room
     *
     * @param roomId current roomId
     * @return latest Action
     */

    private static ProtoGlobal.ClientAction getLatestAction(long roomId) {
        //use this commented code
        /*Iterator<StructAction> iterator = structActions.iterator();
        while (iterator.hasNext()) {
            StructAction struct = iterator.next();*/
        for (int i = (structActions.size() - 1); i >= 0; i--) {
            if (structActions.get(i).roomId == roomId) {
                return structActions.get(i).action;
            }
        }
        return null;
    }

    /**
     * after get GroupSetActionResponse fill or clear structActions list
     *
     * @param roomId roomId that setAction in
     * @param userId userId that setAction in
     * @param action action that doing
     */

    public static void fillOrClearAction(long roomId, long userId, ProtoGlobal.ClientAction action) {
        //use this commented code
       /* Iterator<StructAction> iterator = structActions.iterator();
        while (iterator.hasNext()) {
            StructAction struct = iterator.next();*/
        if (action == ProtoGlobal.ClientAction.CANCEL) {
            for (int i = 0; i < structActions.size(); i++) {
                if (structActions.get(i).roomId == roomId && structActions.get(i).userId == userId) {
                    structActions.remove(i);
                }
            }
        } else {

            if (structActions.size() > 0) {
                boolean checkItemExist = false;
                for (StructAction structCheck : structActions) {
                    if (structCheck.roomId == roomId & structCheck.userId == userId & structCheck.action.toString().equals(action.toString())) {
                        checkItemExist = true;
                        break;
                    }
                }
                if (!checkItemExist) {
                    StructAction struct = new StructAction();
                    struct.roomId = roomId;
                    struct.userId = userId;
                    struct.action = action;
                    structActions.add(struct);
                }
            } else {
                StructAction struct = new StructAction();
                struct.roomId = roomId;
                struct.userId = userId;
                struct.action = action;
                structActions.add(struct);
            }
        }
    }
}
