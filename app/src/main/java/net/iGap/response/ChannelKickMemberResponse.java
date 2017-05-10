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
import net.iGap.proto.ProtoChannelKickMember;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class ChannelKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean isDeleted = false;

    public ChannelKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        super.handler();

        final ProtoChannelKickMember.ChannelKickMemberResponse.Builder builder = (ProtoChannelKickMember.ChannelKickMemberResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
                for (RealmMember realmMember : realmRoom.getChannelRoom().getMembers()) {
                    if (realmMember.getPeerId() == builder.getMemberId()) {
                        realmMember.deleteFromRealm();
                        isDeleted = true;
                        break;
                    }
                }
            }
        });
        realm.close();

        if (isDeleted) {
            if (G.onChannelKickMember != null) {
                G.onChannelKickMember.onChannelKickMember(builder.getRoomId(), builder.getMemberId());
            }
        }
    }

    @Override public void timeOut() {
        super.timeOut();
        if (G.onChannelKickMember != null) {
            G.onChannelKickMember.onTimeOut();
        }
    }

    @Override public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onChannelKickMember != null) {
            G.onChannelKickMember.onError(majorCode, minorCode);
        }
    }
}


