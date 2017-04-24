/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.request;

import com.iGap.Config;
import com.iGap.G;
import com.iGap.proto.ProtoClientCondition;

public class RequestClientCondition {

    public void clientCondition(ProtoClientCondition.ClientCondition.Builder clientCondition) {

        if (G.onUpdating != null) {
            G.onUpdating.onUpdating();
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    G.onUpdating.onCancelUpdating();
                }
            }, Config.UPDATING_TIME_SHOWING);
        }

        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /*
    public void clientCondition() {
        Realm realm = Realm.getDefaultInstance();

        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();

        for (RealmClientCondition realmClientCondition : realm.where(RealmClientCondition.class).findAll()) {
            ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();
            room.setRoomId(realmClientCondition.getRoomId());
            room.setMessageVersion(realmClientCondition.getMessageVersion());//Done
            room.setStatusVersion(realmClientCondition.getStatusVersion());//Done
            room.setDeleteVersion(realmClientCondition.getDeleteVersion());//DONE

            for (RealmOfflineDelete offlineDeleted : realmClientCondition.getOfflineDeleted()) { //DONE
                room.addOfflineDeleted(offlineDeleted.getOfflineDelete());
            }

            for (RealmOfflineEdited realmOfflineEdited : realmClientCondition.getOfflineEdited()) { // server have problem
                ProtoClientCondition.ClientCondition.Room.OfflineEdited.Builder offlineEdited = ProtoClientCondition.ClientCondition.Room.OfflineEdited.newBuilder();
                offlineEdited.setMessageId(realmOfflineEdited.getMessageId());
                offlineEdited.setMessage(realmOfflineEdited.getMessage());
                room.addOfflineEdited(offlineEdited);
            }

            for (RealmOfflineSeen offlineSeen : realmClientCondition.getOfflineSeen()) { // DONE
                room.addOfflineSeen(offlineSeen.getOfflineSeen());
            }

            room.setClearId(realmClientCondition.getClearId()); //DONE

            RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findFirst();
            Log.i("CLI1", "realmChatHistory : " + realmRoomMessage);
            if (realmRoomMessage != null) {
                Log.i("CLI1", "start ID : " + realmRoomMessage.getMessageId());
                room.setCacheStartId(realmRoomMessage.getMessageId());//Done

                List<RealmRoomMessage> allItems = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, realmClientCondition.getRoomId()).findAll().sort(RealmRoomMessageFields.MESSAGE_ID, Sort.DESCENDING);

                for (RealmRoomMessage item : allItems) {
                    Log.i("CLI1", "End 1");
                    if (item != null) {
                        Log.i("CLI1", "End ID : " + item.getMessageId());
                        room.setCacheEndId(item.getMessageId());//Done
                        break;
                    }
                }
            }

            if (realmClientCondition.getOfflineMute() != null) {
                if (realmClientCondition.getOfflineMute() == ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED.toString()) {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.MUTED);
                } else {
                    room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNMUTED);
                }
            } else {
                room.setOfflineMute(ProtoClientCondition.ClientCondition.Room.OfflineMute.UNCHANGED);
            }

            clientCondition.addRooms(room);

            clearOffline(realmClientCondition, realm);
        }
        Log.i("CLI1", "clientCondition.build() : " + clientCondition.build());

        realm.close();

        clientConditionGlobal = clientCondition;

       *//* RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }*//*
    }

    private void clearOffline(final RealmClientCondition realmClientCondition, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmClientCondition.setOfflineEdited(new RealmList<RealmOfflineEdited>());
                realmClientCondition.setOfflineDeleted(new RealmList<RealmOfflineDelete>());
                realmClientCondition.setOfflineSeen(new RealmList<RealmOfflineSeen>());
            }
        });
    }*/
}