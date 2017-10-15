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
import net.iGap.request.RequestChannelAddMessageReaction;

public class ChannelAddMessageReactionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ChannelAddMessageReactionResponse(int actionId, Object protoClass, Object identity) { // here identity is roomId and messageId
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder builder = (ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder) message;
        if (G.onChannelAddMessageReaction != null && identity != null) {

            final RequestChannelAddMessageReaction.IdentityChannelAddMessageReaction IdentityChannelAddMessageReaction = ((RequestChannelAddMessageReaction.IdentityChannelAddMessageReaction) identity);
            long roomId = IdentityChannelAddMessageReaction.roomId;
            final long messageId = IdentityChannelAddMessageReaction.messageId;
            final ProtoGlobal.RoomMessageReaction messageReaction = IdentityChannelAddMessageReaction.roomMessageReaction;

            ProtoGlobal.RoomMessageReaction reaction = null;
            if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                reaction = ProtoGlobal.RoomMessageReaction.THUMBS_UP;
            } else if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
                reaction = ProtoGlobal.RoomMessageReaction.THUMBS_DOWN;
            }

            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    ProtoGlobal.RoomMessageReaction reaction1 = null;
                    if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                        reaction1 = ProtoGlobal.RoomMessageReaction.THUMBS_UP;
                    } else if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_DOWN) {
                        reaction1 = ProtoGlobal.RoomMessageReaction.THUMBS_DOWN;
                    }

                    /**
                     * vote in chat or group to forwarded message from channel
                     */
                    if (IdentityChannelAddMessageReaction.forwardedMessageId != 0) {
                        long forwardMessageId = IdentityChannelAddMessageReaction.forwardedMessageId;
                        RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, forwardMessageId).findFirst();
                        if (realmChannelExtra != null) {
                            if (messageReaction == ProtoGlobal.RoomMessageReaction.THUMBS_UP) {
                                realmChannelExtra.setThumbsUp(builder.getReactionCounterLabel());
                            } else {
                                realmChannelExtra.setThumbsDown(builder.getReactionCounterLabel());
                            }
                        }
                    } else {
                        RealmChannelExtra realmChannelExtra = realm.where(RealmChannelExtra.class).equalTo(RealmChannelExtraFields.MESSAGE_ID, messageId).findFirst();
                        if (realmChannelExtra != null) {
                            realmChannelExtra.setVote(reaction1, builder.getReactionCounterLabel());
                        }
                    }
                }
            });
            realm.close();

            if (IdentityChannelAddMessageReaction.forwardedMessageId != 0) {
                G.onChannelAddMessageReaction.onChannelAddMessageReaction(roomId, messageId, builder.getReactionCounterLabel(), reaction, IdentityChannelAddMessageReaction.forwardedMessageId);
            } else {
                G.onChannelAddMessageReaction.onChannelAddMessageReaction(roomId, messageId, builder.getReactionCounterLabel(), reaction, 0);
            }
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onChannelAddMessageReaction != null) {
            G.onChannelAddMessageReaction.onError(majorCode, minorCode);
        }
    }
}


