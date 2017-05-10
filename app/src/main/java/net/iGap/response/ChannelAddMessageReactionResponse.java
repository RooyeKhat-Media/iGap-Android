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
import net.iGap.proto.ProtoChannelAddMessageReaction;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmChannelExtraFields;

public class ChannelAddMessageReactionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMessageReactionResponse(int actionId, Object protoClass, String identity) { // here identity is roomId and messageId
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();

        final ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder builder = (ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder) message;
        if (G.onChannelAddMessageReaction != null && identity != null) {

            final String[] identityParams = identity.split("\\*");
            String roomId = identityParams[0];
            final String messageId = identityParams[1];
            final String messageReaction = identityParams[2];

            ProtoGlobal.RoomMessageReaction reaction = null;
            if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_UP.toString())) {
                reaction = ProtoGlobal.RoomMessageReaction.THUMBS_UP;
            } else if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN.toString())) {
                reaction = ProtoGlobal.RoomMessageReaction.THUMBS_DOWN;
            }

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {

                    ProtoGlobal.RoomMessageReaction reaction1 = null;
                    if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_UP.toString())) {
                        reaction1 = ProtoGlobal.RoomMessageReaction.THUMBS_UP;
                    } else if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_DOWN.toString())) {
                        reaction1 = ProtoGlobal.RoomMessageReaction.THUMBS_DOWN;
                    }

                    /**
                     * vote in chat or group to forwarded message from channel
                     */
                    if (identityParams.length > 3) {
                        long forwardMessageId = Long.parseLong(identityParams[3]);
                        //RealmRoomMessage realmRoomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, forwardMessageId).findFirst();
                        RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, forwardMessageId).findFirst();
                        if (realmChannelExtra != null) {
                            if (messageReaction.equals(ProtoGlobal.RoomMessageReaction.THUMBS_UP.toString())) {
                                realmChannelExtra.setThumbsUp(builder.getReactionCounterLabel());
                            } else {
                                realmChannelExtra.setThumbsDown(builder.getReactionCounterLabel());
                            }
                        }
                    } else {
                        RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, Long.parseLong(messageId)).findFirst();
                        if (realmChannelExtra != null) {
                            realmChannelExtra.setVote(reaction1, builder.getReactionCounterLabel());
                        }
                    }
                }
            });
            realm.close();

            if (identityParams.length > 3) {
                String forwardedMessageId = identityParams[3];
                G.onChannelAddMessageReaction.onChannelAddMessageReaction(Long.parseLong(roomId), Long.parseLong(messageId), builder.getReactionCounterLabel(), reaction,
                    Long.parseLong(forwardedMessageId));
            } else {
                G.onChannelAddMessageReaction.onChannelAddMessageReaction(Long.parseLong(roomId), Long.parseLong(messageId), builder.getReactionCounterLabel(), reaction, 0);
            }
        }
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onChannelAddMessageReaction != null) {
            G.onChannelAddMessageReaction.onError(majorCode, minorCode);
        }
    }
}


