/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


package com.iGap.realm;

import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class RealmAvatar extends RealmObject {

    @PrimaryKey private long id;
    private long uid; // id for sorting avatars
    private long ownerId; // userId for users and roomId for rooms
    private RealmAttachment file;

    public RealmAvatar() {
    }

    public RealmAvatar(long id) {
        this.id = id;
    }

    /**
     * if file is repetitious send it to bottom for detect it later
     * for main avatar
     *
     * @param sendAvatarToBottom if need send avatar to bottom of avatars for that user
     */

    public static RealmAvatar put(long ownerId, ProtoGlobal.Avatar input, boolean sendAvatarToBottom) {
        Realm realm = Realm.getDefaultInstance();
        if (!input.hasFile()) {
            deleteAllAvatars(ownerId, realm);
            return null;
        }

        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();

        boolean exists = false;
        for (RealmAvatar avatar : ownerAvatars) {

            if (avatar.getFile() != null && avatar.getFile().getToken().equalsIgnoreCase(input.getFile().getToken())) {
                exists = true;
                break;
            }
        }

        RealmAvatar avatar;
        if (!exists) {
            RealmResults<RealmAvatar> avatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findAll();
            if (avatars.size() == 0) {
                avatar = realm.createObject(RealmAvatar.class, input.getId());
                avatar.setOwnerId(ownerId);
                avatar.setFile(RealmAttachment.build(input.getFile(), AttachmentFor.AVATAR, null));
                avatar.setUid(SUID.id().get());
            } else {
                avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
                if (sendAvatarToBottom) {
                    updateAvatarUid(input.getId());
                }
            }
        } else {
            avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findFirst();
            if (sendAvatarToBottom) {
                updateAvatarUid(input.getId());
            }
        }
        realm.close();
        return avatar;
    }

    /**
     * update uid for avatar for send it to bottom
     * hint : i need do this action because client read avatars from RealmAvatar and sort descending
     * avatars for get latest avatar
     */

    private static void updateAvatarUid(final long avatarId) {
        Realm realm = Realm.getDefaultInstance();
        RealmAvatar avatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, avatarId).findFirst();
        if (avatar != null) {
            avatar.setUid(SUID.id().get());
        }

        realm.close();
    }

    /**
     * delete avatar with avatarId and add avatar with that id that
     * cleared before do this action for send avatar to latest position
     * hint : i need do this action because client read avatars from RealmAvatar and sort descending
     * avatars for get latest avatar
     *
     * @param ownerId id for add avatar
     * @param input   avatar that get from server
     */

   /* private static void sendAvatarToBottom(final long ownerId, final ProtoGlobal.Avatar input) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.ID, input.getId()).findAll();
                        if (realmAvatar.size() > 0) {
                            Log.i("LOG", "1");
                            realmAvatar.deleteAllFromRealm();
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Realm realm1 = Realm.getDefaultInstance();
                        realm1.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Log.i("LOG", "2");
                                RealmAvatar avatar = realm.createObject(RealmAvatar.class, input.getId());
                                avatar.setOwnerId(ownerId);
                                avatar.setFile(RealmAttachment.build(input.getFile(), AttachmentFor.AVATAR, null));
                            }
                        });
                        realm1.close();
                    }
                });
                realm.close();
            }
        });
    }*/

    /**
     * delete all avatars from RealmAvatar
     *
     * @param ownerId use this id for delete from RealmAvatar
     */
    private static void deleteAllAvatars(final long ownerId, Realm realm) {
        RealmResults<RealmAvatar> ownerAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, ownerId).findAll();
        if (ownerAvatars.size() > 0) {
            ownerAvatars.deleteAllFromRealm();
        }
    }

    /**
     * use this method just for group or channel , because chat don't have avatar in room(chat
     * avatar exist in channel info)
     */


    /*public static RealmAvatar convert(final ProtoGlobal.Room room) {
        ProtoGlobal.Avatar avatar = null;
        switch (room.getType()) {
            case GROUP:
                ProtoGlobal.GroupRoom groupRoom = room.getGroupRoomExtra();
                avatar = groupRoom.getAvatar();
                break;
            case CHANNEL:
                ProtoGlobal.ChannelRoom channelRoom = room.getChannelRoomExtra();
                avatar = channelRoom.getAvatar();
                break;
        }

        return RealmAvatar.put(room.getId(), avatar);
    }*/
    public static RealmAvatar convert(long userId, final RealmAttachment attachment) {
        Realm realm = Realm.getDefaultInstance();

        // don't put it into transaction
        RealmAvatar realmAvatar = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, userId).findFirst();
        if (realmAvatar == null) {
            realmAvatar = realm.createObject(RealmAvatar.class, attachment.getId());
            realmAvatar.setOwnerId(userId);
            realmAvatar.setUid(SUID.id().get());
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

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public RealmAttachment getFile() {
        return file;
    }

    public void setFile(RealmAttachment file) {
        this.file = file;
    }
}
