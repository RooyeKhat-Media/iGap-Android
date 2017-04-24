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

import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoChannelAvatarGetList;
import com.iGap.realm.RealmAttachment;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import io.realm.Realm;

public class ChannelAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        //ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder builder = (ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder) message;
        //builder.getAvatarList();

        Realm realm = Realm.getDefaultInstance();
        final long roomId = Long.parseLong(identity);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                // delete all avatar in roomid
                realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, roomId).findAll().deleteAllFromRealm();

                ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder builder = (ProtoChannelAvatarGetList.ChannelAvatarGetListResponse.Builder) message;

                // add all list to realm avatar
                for (int i = (builder.getAvatarList().size() - 1); i >= 0; i--) {
                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, builder.getAvatarList().get(i).getId());
                    realmAvatar.setOwnerId(roomId);
                    realmAvatar.setUid(SUID.id().get());
                    realmAvatar.setFile(RealmAttachment.build(builder.getAvatarList().get(i).getFile(), AttachmentFor.AVATAR, null));
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


