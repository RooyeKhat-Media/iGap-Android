/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.realm;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;

public class RealmAvatar extends RealmObject {

    @PrimaryKey private long id;
    private long uid; // id for sorting avatars
    @Index private long ownerId; // userId for users and roomId for rooms
    private RealmAttachment file;

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    /**
     * HINT : use this method in transaction. and never use this method in loop for one userId.
     *
     * put avatar to realm and manage need delete any avatar for this ownerId or no
     */
    public static RealmAvatar putOrUpdateAndManageDelete(Realm realm, final long ownerId, final ProtoGlobal.Avatar input) {
        if (!input.hasFile()) {
            deleteAllAvatars(ownerId, realm);
            return null;
        }

        /**
         * bigger than input.getId() exist avatar means that user deleted an avatar which has more priority.
         */
        realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).greaterThan(RealmAvatarFields.ID, input.getId()).findAll().deleteAllFromRealm();

        RealmAvatar avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
        if (avatar == null) {
            avatar = realm.createObject(RealmAvatar.class, input.getId());
            avatar.setOwnerId(ownerId);
            avatar.setFile(RealmAttachment.build(input.getFile(), AttachmentFor.AVATAR, null));
        }
        return avatar;
    }

    public static RealmAvatar putOrUpdate(Realm realm, final long ownerId, final ProtoGlobal.Avatar input) {
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class, input.getId());
        }
        realmAvatar.setOwnerId(ownerId);
        realmAvatar.setFile(RealmAttachment.build(input.getFile(), AttachmentFor.AVATAR, null));

        return realmAvatar;
    }

    /**
     * Hint:use in transaction
     * delete all avatars from RealmAvatar
     *
     * @param ownerId use this id for delete from RealmAvatar
     */
    public static void deleteAllAvatars(final long ownerId, Realm realm) {
        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();
        if (ownerAvatars.size() > 0) {
            ownerAvatars.deleteAllFromRealm();
        }
    }

    public static void deleteAvatar(Realm realm, final long avatarId) {
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, avatarId).findFirst();
        if (realmAvatar != null) {
            realmAvatar.deleteFromRealm();
        }
    }

    public static void deleteAvatarWithOwnerId(final long ownerId) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findFirst();
                if (realmAvatar != null) {
                    realmAvatar.deleteFromRealm();
                }
            }
        });
        realm.close();
    }


    /**
     * return latest avatar with this ownerId
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @return return latest RealmAvatar for this ownerId
     */
    public static RealmAvatar getLastAvatar(long ownerId, Realm realm) {
        for (RealmAvatar avatar : realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAllSorted(RealmAvatarFields.ID, Sort.DESCENDING)) {
            if (avatar.getFile() != null) {
                return avatar;
            }
        }
        return null;
    }

    public static RealmAvatar convert(long userId, final RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();

        // don't put it into transaction
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class, attachment.getId());
            realmAvatar.setOwnerId(userId);
        }
        realmAvatar.setFile(attachment);

        realm.close();

        return realmAvatar;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }
}
