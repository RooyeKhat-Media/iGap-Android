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
import net.iGap.module.SUID;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoUserAvatarGetList;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmAvatarFields;

public class UserAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        Realm realm = Realm.getDefaultInstance();
        final long userId = Long.parseLong(identity);

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findAll().deleteAllFromRealm();
                ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder builder = (ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder) message;

                for (int i = 0; i < builder.getAvatarList().size(); i++) {
                    RealmAvatar realmAvatar = realm.createObject(RealmAvatar.class, builder.getAvatarList().get(i).getId());
                    realmAvatar.setOwnerId(userId);
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


