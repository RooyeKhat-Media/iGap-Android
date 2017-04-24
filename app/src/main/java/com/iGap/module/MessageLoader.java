/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.module;

import com.iGap.G;
import com.iGap.interfaces.OnClientGetRoomHistoryResponse;
import com.iGap.interfaces.OnMessageReceive;
import com.iGap.module.structs.StructMessageInfo;
import com.iGap.proto.ProtoClientGetRoomHistory;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.request.RequestClientGetRoomHistory;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;

public final class MessageLoader {


    /**
     * fetch local message from RealmRoomMessage.
     * (hint : deleted message doesn't count)
     *
     * @param roomId roomId that want show message for that
     * @param messageId start query with this messageId
     * @param limit limitation for load message
     * @param duplicateMessage if set true return message for messageId that used in this method (will be used "lessThanOrEqualTo") otherwise just return less or greater than messageId(will be used "lessThan" method)
     * @param direction direction for load message up or down
     * @return Object[] ==> [0] -> ArrayList<StructMessageInfo>, [1] -> boolean hasMore, [2] -> boolean hasGap
     */
    public static Object[] getLocalMessage(long roomId, long messageId, long gapMessageId, int limit, boolean duplicateMessage, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {

        Realm realm = Realm.getDefaultInstance();
        limit = 100;
        boolean hasMore = true;
        boolean hasSpaceToGap = true;
        List<RealmRoomMessage> realmRoomMessages;
        ArrayList<StructMessageInfo> structMessageInfos = new ArrayList<>();

        if (messageId == 0) {
            realm.close();
            return new Object[]{structMessageInfos, false, false};
        }


        /**
         * get message from RealmRoomMessage
         */
        if (gapMessageId > 0) {

            if (direction == ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP) {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, gapMessageId, messageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, gapMessageId, messageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                }
            } else {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, messageId, gapMessageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).between(RealmRoomMessageFields.MESSAGE_ID, messageId, gapMessageId).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                }
            }

        } else {

            if (direction == ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP) {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                }

            } else {
                if (duplicateMessage) {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                } else {
                    realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThan(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.DELETED, true).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
                }
            }
        }


        /**
         * manage subList
         */
        if (realmRoomMessages.size() > limit) {
            realmRoomMessages = realmRoomMessages.subList(0, limit);
        } else {
            /**
             * when run this block means that end of message reached
             */
            hasMore = false;
            hasSpaceToGap = false;
            realmRoomMessages = realmRoomMessages.subList(0, realmRoomMessages.size());
        }

        /**
         * convert message from RealmRoomMessage to StructMessageInfo for send to view
         */
        for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
            if (realmRoomMessage.getMessageId() != 0) {
                structMessageInfos.add(StructMessageInfo.convert(realmRoomMessage));
            }
        }

        realm.close();

        return new Object[]{structMessageInfos, hasMore, hasSpaceToGap};
    }


    //*********** get message from server

    public static void getOnlineMessage(final long roomId, final long messageId, final long reachMessageId, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, final OnMessageReceive onMessageReceive) {
        new RequestClientGetRoomHistory().getRoomHistory(roomId, messageId, direction, Long.toString(roomId));

        G.onClientGetRoomHistoryResponse = new OnClientGetRoomHistoryResponse() {
            @Override
            public void onGetRoomHistory(final long roomId, final long startMessageId, long endMessageId) {

                boolean gapReached = false;
                /**
                 * convert message from RealmRoomMessage to StructMessageInfo for send to view
                 */
                if (reachMessageId >= startMessageId) {
                    gapReached = true;
                }

                final boolean gapReachedFinal = gapReached;
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        /**
                         * clear before state gap for avoid compute this message for gap state again
                         */
                        clearGap(roomId, messageId, direction, realm);

                        /**
                         * if not reached to gap yet and exist reachMessageId
                         * set new gap state for compute message for gap
                         */
                        if (!gapReachedFinal && reachMessageId > 0) {
                            setGap(startMessageId, realm);
                        }
                    }
                });
                realm.close();

                onMessageReceive.onMessage(roomId, startMessageId, endMessageId, gapReached);
            }

            @Override
            public void onGetRoomHistoryError(int majorCode, int minorCode) {
                if (majorCode == 617 && minorCode == 8) {
                    /**
                     * clear all gap state because not exist any more message
                     */
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).notEqualTo("previousMessageId", 0).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
                            for (RealmRoomMessage realmRoomMessage : realmRoomMessages) {
                                realmRoomMessage.setPreviousMessageId(0);
                            }
                        }
                    });
                    realm.close();
                }

                onMessageReceive.onError(majorCode, minorCode);
            }
        };
    }

    //*********** detect gap in message

    /**
     * detect first RealmRoomMessage with previousMessageId and check
     * this previousMessageId exist in RealmRoomMessage or not
     * if gap exist this method will be returned reachedId.
     * reachedId will be used for calculate that after get clientGetRoomHistory
     * this history really reached to local message and gap filled or no
     *
     * @param roomId roomId that want show message for that
     * @param messageId start query with this messageId
     * @param direction direction for load message up or down
     * @return [0] -> gapMessageId, [1] -> reachMessageId
     */
    public static Object[] gapExist(long roomId, long messageId, ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage realmRoomMessage = null;
        long gapMessageId = 0;
        long reachMessageId = 0;

        /**
         * detect message that have previousMessageId
         */
        if (direction == ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP) {
            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, 0).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
            if (realmRoomMessages.size() > 0) {
                realmRoomMessage = realmRoomMessages.first();
            }
        } else {
            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo("previousMessageId", 0).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
            if (realmRoomMessages.size() > 0) {
                realmRoomMessage = realmRoomMessages.first();
            }
        }

        /**
         * check that exist any message with (message == previousMessageId) or not
         */
        if (realmRoomMessage != null) {
            RealmRoomMessage realmRoomMessageGap = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getPreviousMessageId()).findFirst();

            /**
             * if any message with previousMessageId isn't exist in local so
             * client don't have this message and should get it from server
             */
            if (realmRoomMessageGap == null) {
                gapMessageId = realmRoomMessage.getPreviousMessageId();
            } else if (realmRoomMessageGap.getMessageId() == realmRoomMessageGap.getPreviousMessageId()) {
                gapMessageId = realmRoomMessageGap.getPreviousMessageId();
            }
        }

        /**
         * if gap exist now detect reachMessageId
         * (query ==> max of messageId that exist in local and also is lower than messageId that come in this method)
         */
        if (gapMessageId > 0) {
            RealmQuery<RealmRoomMessage> realmRoomMessageRealmQuery = realm.where(RealmRoomMessage.class).lessThan(RealmRoomMessageFields.MESSAGE_ID, realmRoomMessage.getMessageId());
            if (realmRoomMessageRealmQuery != null && realmRoomMessageRealmQuery.max(RealmRoomMessageFields.MESSAGE_ID) != null) {
                reachMessageId = (long) realmRoomMessageRealmQuery.max(RealmRoomMessageFields.MESSAGE_ID);
            }
        }

        realm.close();

        return new Object[]{gapMessageId, reachMessageId};
    }


    /**
     * clear before gap state
     * (hint : don't need use from transaction)
     */
    private static void clearGap(final long roomId, final long messageId, final ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction direction, Realm realm) {
        if (direction == ProtoClientGetRoomHistory.ClientGetRoomHistory.Direction.UP) {
            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).lessThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo("previousMessageId", 0).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);
            if (realmRoomMessages.size() > 0) {
                realmRoomMessages.first().setPreviousMessageId(0);
            }
        } else {
            // this step not checked yet
            RealmResults<RealmRoomMessage> realmRoomMessages = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, roomId).greaterThanOrEqualTo(RealmRoomMessageFields.MESSAGE_ID, messageId).notEqualTo("previousMessageId", 0).findAllSorted(RealmRoomMessageFields.MESSAGE_ID, Sort.ASCENDING);
            if (realmRoomMessages.size() > 0) {
                realmRoomMessages.first().setPreviousMessageId(0);
            }
        }
    }

    /**
     * set new gap state. set messageId that find message for that to previousMessageId
     *
     * (hint : don't need use from transaction)
     *
     * @param messageId message that want set gapMessageId to that
     */
    private static void setGap(final long messageId, Realm realm) {
        RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, messageId).findFirst();
        if (realmRoomMessage != null) {
            realmRoomMessage.setPreviousMessageId(messageId);
        }
    }
}