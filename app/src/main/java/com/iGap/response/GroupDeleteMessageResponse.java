/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGroupDeleteMessage;
import com.iGap.realm.RealmClientCondition;
import com.iGap.realm.RealmClientConditionFields;
import com.iGap.realm.RealmOfflineDelete;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import io.realm.Realm;

public class GroupDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();


        final ProtoGroupDeleteMessage.GroupDeleteMessageResponse.Builder groupDeleteMessage = (ProtoGroupDeleteMessage.GroupDeleteMessageResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                /**
                 * if another account deleted this message set deleted true
                 * otherwise before this state was set
                 */
                if (groupDeleteMessage.getResponse().getId().isEmpty()) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, groupDeleteMessage.getMessageId()).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);
                    }
                }

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, groupDeleteMessage.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setDeleteVersion(groupDeleteMessage.getDeleteVersion());
                    for (RealmOfflineDelete realmOfflineDeleted : realmClientCondition.getOfflineDeleted()) {
                        if (realmOfflineDeleted.getOfflineDelete() == groupDeleteMessage.getMessageId()) {
                            realmOfflineDeleted.deleteFromRealm();
                            break;
                        }
                    }
                }
                if (G.onChatDeleteMessageResponse != null) {
                    G.onChatDeleteMessageResponse.onChatDeleteMessage(groupDeleteMessage.getDeleteVersion(), groupDeleteMessage.getMessageId(), groupDeleteMessage.getRoomId(), groupDeleteMessage.getResponse());
                }
            }
        });
        realm.close();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


