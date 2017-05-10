/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.response;

import io.realm.Realm;
import net.iGap.G;
import net.iGap.proto.ProtoChatDeleteMessage;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmClientConditionFields;
import net.iGap.realm.RealmOfflineDelete;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmRoomMessageFields;

public class ChatDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();
        final ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder chatDeleteMessage = (ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {

                /**
                 * if another account deleted this message set deleted true
                 * otherwise before this state was set
                 */
                if (chatDeleteMessage.getResponse().getId().isEmpty()) {
                    RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, chatDeleteMessage.getMessageId()).findFirst();
                    if (roomMessage != null) {
                        roomMessage.setDeleted(true);
                    }
                }

                RealmClientCondition realmClientCondition = realm.where(RealmClientCondition.class).equalTo(RealmClientConditionFields.ROOM_ID, chatDeleteMessage.getRoomId()).findFirst();
                if (realmClientCondition != null) {
                    realmClientCondition.setDeleteVersion(chatDeleteMessage.getDeleteVersion());
                    for (RealmOfflineDelete realmOfflineDeleted : realmClientCondition.getOfflineDeleted()) {
                        if (realmOfflineDeleted.getOfflineDelete() == chatDeleteMessage.getMessageId()) {
                            realmOfflineDeleted.deleteFromRealm();
                            break;
                        }
                    }
                }
                if (G.onChatDeleteMessageResponse != null) {
                    G.onChatDeleteMessageResponse.onChatDeleteMessage(chatDeleteMessage.getDeleteVersion(), chatDeleteMessage.getMessageId(), chatDeleteMessage.getRoomId(),
                        chatDeleteMessage.getResponse());
                }
            }
        });

        realm.close();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


