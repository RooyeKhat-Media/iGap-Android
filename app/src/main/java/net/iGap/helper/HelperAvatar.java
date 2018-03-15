/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import net.iGap.G;
import net.iGap.interfaces.OnAvatarAdd;
import net.iGap.interfaces.OnAvatarDelete;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.interfaces.OnDownload;
import net.iGap.interfaces.OnFileDownloaded;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestFileDownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;

import static net.iGap.realm.RealmAvatar.getLastAvatar;

/**
 * helper avatar for add or delete avatars for user or room
 */
public class HelperAvatar {

    private static HashMap<Long, ArrayList<OnAvatarGet>> onAvatarGetHashMap = new HashMap<>();
    private static HashMap<Long, ArrayList<OnAvatarGet>> onAvatarSync = new HashMap<>();
    private static HashMap<Long, Boolean> mRepeatList = new HashMap<>();
    private static ArrayList<String> reDownloadFiles = new ArrayList<>();

    /**
     * add avatar in RealmAvatar and after copy avatar
     * to final path call callback for return final path
     *
     * @param ownerId user id for users and roomId for rooms
     */
    public static void avatarAdd(final long ownerId, final String src, final ProtoGlobal.Avatar avatar, final OnAvatarAdd onAvatarAdd) {
        Realm realm = Realm.getDefaultInstance();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (src == null) {
                    return;
                }

                String avatarPath = copyAvatar(src, avatar);
                RealmAvatar.putOrUpdate(realm, ownerId, avatar).getFile().setLocalFilePath(avatarPath);

                if (onAvatarAdd != null && avatarPath != null) {
                    onAvatarAdd.onAvatarAdd(avatarPath);
                }

                syncAvatarAdd(ownerId, avatarPath);
            }
        });
        realm.close();
    }

    /**
     * delete avatar and if another avatar is exist for this user
     * call latestAvatarPath latest avatar and if isn't exist call showInitials
     *
     * @param ownerId    if is user set userId and if is room set roomId
     * @param avatarType set USER for user and ROOM for chat or group or channel
     * @param avatarId   id avatar for delete from RealmAvatar
     */
    public static void avatarDelete(final long ownerId, final long avatarId, final AvatarType avatarType, @Nullable final OnAvatarDelete onAvatarDelete) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmAvatar.deleteAvatar(realm, avatarId);
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        if (onAvatarDelete != null) {
                            getAvatar(ownerId, avatarType, false, new OnAvatarGet() {
                                @Override
                                public void onAvatarGet(String avatarPath, long ownerId) {
                                    onAvatarDelete.latestAvatarPath(avatarPath);
                                }

                                @Override
                                public void onShowInitials(String initials, String color) {
                                    onAvatarDelete.showInitials(initials, color);
                                }
                            });
                        }
                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        if (onAvatarDelete != null) {
                            String[] initials = showInitials(ownerId, avatarType);
                            if (initials != null) {
                                onAvatarDelete.showInitials(initials[0], initials[1]);
                            }
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    /**
     * use this method if have realm instance
     * read avatarPath from realm avatar and return latest avatarPath
     *
     * @param registeredUser if avatar not detect will be used from this params for add to realm and after than find avatar
     * @param ownerId        if is user set userId and if is room set roomId
     * @param avatarType     USER for contacts and chat , ROOM for group and channel
     * @param showMain       true for set main avatar and false for show thumbnail
     * @param onAvatarGet    callback for return info
     */
    public static void getAvatar(@Nullable ProtoGlobal.RegisteredUser registeredUser, final long ownerId, AvatarType avatarType, boolean showMain, Realm _realm, final OnAvatarGet onAvatarGet) {
        /**
         * first show user initials and after that show avatar if exist
         */
        String[] initialsStart = showInitials(ownerId, avatarType);
        if (initialsStart != null) {
            onAvatarGet.onShowInitials(initialsStart[0], initialsStart[1]);
        }
        getAvatarImage(registeredUser, ownerId, avatarType, showMain, _realm, onAvatarGet);
    }

    /**
     * use this method if don't have realm instance
     * read avatarPath from realm avatar and return latest avatarPath
     *
     * @param ownerId     if is user set userId and if is room set roomId
     * @param avatarType  USER for contacts and chat , ROOM for group and channel
     * @param showMain    true for set main avatar and false for show thumbnail
     * @param onAvatarGet callback for return info
     */
    public static void getAvatar(final long ownerId, AvatarType avatarType, boolean showMain, final OnAvatarGet onAvatarGet) {
        Realm realm = Realm.getDefaultInstance();
        getAvatar(null, ownerId, avatarType, showMain, realm, onAvatarGet);
        realm.close();
    }

    /**
     * check avatar in Realm and download if needed
     */
    private static void getAvatarImage(ProtoGlobal.RegisteredUser registeredUser, final long ownerId, AvatarType avatarType, boolean showMain, Realm _realm, final OnAvatarGet onAvatarGet) {
        RealmAvatar realmAvatar = getLastAvatar(ownerId, _realm);

        if (realmAvatar == null && registeredUser != null) {
            insertRegisteredInfoToDB(registeredUser, _realm);
            realmAvatar = getLastAvatar(ownerId, _realm);
        }

        fillAvatarSyncList(ownerId, onAvatarGet);

        if (realmAvatar != null) {

            if (showMain && realmAvatar.getFile().isFileExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalFilePath(), ownerId);
            } else if (realmAvatar.getFile().isThumbnailExistsOnLocal()) {
                onAvatarGet.onAvatarGet(realmAvatar.getFile().getLocalThumbnailPath(), ownerId);
            } else {

                if (onAvatarGetHashMap.containsKey(ownerId)) {
                    ArrayList<OnAvatarGet> listeners = onAvatarGetHashMap.get(ownerId);
                    listeners.add(onAvatarGet);
                } else {
                    ArrayList<OnAvatarGet> listeners = new ArrayList<>();
                    listeners.add(onAvatarGet);
                    onAvatarGetHashMap.put(ownerId, listeners);
                }

                new AvatarDownload().avatarDownload(realmAvatar.getFile(), ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL, new OnDownload() {
                    @Override
                    public void onDownload(final String filepath, final String token) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                final Realm realm = Realm.getDefaultInstance();
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        for (RealmAvatar realmAvatar1 : realm.where(RealmAvatar.class).equalTo("file.token", token).findAll()) {
                                            realmAvatar1.getFile().setLocalThumbnailPath(filepath);
                                        }
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {

                                        G.handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Realm realm1 = Realm.getDefaultInstance();
                                                for (RealmAvatar realmAvatar1 : realm1.where(RealmAvatar.class).equalTo("file.token", token).findAll()) {

                                                    //onAvatarGet.onAvatarGet(filepath, realmAvatar1.getOwnerId());
                                                    ArrayList<OnAvatarGet> listeners = (onAvatarGetHashMap.get(realmAvatar1.getOwnerId()));

                                                    if (listeners != null) {
                                                        for (OnAvatarGet listener : listeners) {
                                                            if (listener != null) {
                                                                listener.onAvatarGet(filepath, realmAvatar1.getOwnerId());
                                                            } else {
                                                                onAvatarGet.onAvatarGet(filepath, realmAvatar1.getOwnerId());
                                                            }
                                                        }

                                                        onAvatarGetHashMap.remove(realmAvatar1.getOwnerId());
                                                    }

                                                    //  break;

                                                }
                                                realm1.close();
                                            }
                                        });

                                        realm.close();
                                    }
                                }, new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        realm.close();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });

                String[] initials = showInitials(ownerId, avatarType);
                if (initials != null) {
                    onAvatarGet.onShowInitials(initials[0], initials[1]);
                }
            }
        } else {
            String[] initials = showInitials(ownerId, avatarType);
            if (initials != null) {
                onAvatarGet.onShowInitials(initials[0], initials[1]);
            } else {
                getAvatarAfterTime(ownerId, avatarType, onAvatarGet);
            }
        }
    }

    public static void getAvatarCall(final ProtoGlobal.RegisteredUser registeredUser, final long ownerId, final AvatarType avatarType, final boolean showMain, final OnAvatarGet onAvatarGet) {

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                getAvatar(registeredUser, ownerId, avatarType, showMain, realm, onAvatarGet);
                realm.close();
            }
        });
    }

    private static void getAvatarAfterTime(final long ownerId, final AvatarType avatarType, final OnAvatarGet onAvatarGet) {

        try {
            if (mRepeatList.containsKey(ownerId)) {
                return;
            }
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRepeatList.put(ownerId, true);

                    HelperAvatar.getAvatar(ownerId, avatarType, false, new OnAvatarGet() {
                        @Override
                        public void onAvatarGet(final String avatarPath, final long ownerId) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onAvatarGet.onAvatarGet(avatarPath, ownerId);
                                }
                            });
                        }

                        @Override
                        public void onShowInitials(final String initials, final String color) {
                            G.handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onAvatarGet.onShowInitials(initials, color);
                                }
                            });
                        }
                    });
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get temp address for source and get token and name
     * from avatar for file destination
     *
     * @param src    temp address
     * @param avatar avatar that want copy
     * @return return destination path if copy was successfully
     */
    private static String copyAvatar(String src, ProtoGlobal.Avatar avatar) {
        try {
            /**
             * G.DIR_IMAGE_USER use for all avatars , user or room
             */
            String avatarPath = AndroidUtils.getFilePathWithCashId(avatar.getFile().getCacheId(), avatar.getFile().getName(), G.DIR_IMAGE_USER, false);

            AndroidUtils.copyFile(new File(src), new File(avatarPath));

            return avatarPath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void insertRegisteredInfoToDB(final ProtoGlobal.RegisteredUser registeredUser, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRegisteredInfo.putOrUpdate(realm, registeredUser);
                RealmAvatar.putOrUpdateAndManageDelete(realm, registeredUser.getId(), registeredUser.getAvatar());
            }
        });
    }

    /**
     * read from user and room db in local for find initials and color
     *
     * @param ownerId if is user set userId and if is room set roomId
     * @return initials[0] , color[1]
     */
    public static String[] showInitials(long ownerId, AvatarType avatarType) {
        Realm realm = Realm.getDefaultInstance();
        String initials = null;
        String color = null;
        if (avatarType == AvatarType.USER) {

            RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, ownerId);
            if (realmRegisteredInfo != null) {
                initials = realmRegisteredInfo.getInitials();
                color = realmRegisteredInfo.getColor();
            } else {
                for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
                    if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
                        initials = realmRoom.getInitials();
                        color = realmRoom.getColor();
                    }
                }
            }
        } else if (avatarType == AvatarType.ROOM) {

            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, ownerId).findFirst();
            if (realmRoom != null) {
                initials = realmRoom.getInitials();
                color = realmRoom.getColor();
            }
        }
        realm.close();

        if (initials != null && color != null) {
            return new String[]{initials, color};
        }

        return null;
    }

    private static long getOwnerId(long ownerId) {
        Realm realm = Realm.getDefaultInstance();
        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {
            if (realmRoom.getChatRoom() != null && realmRoom.getChatRoom().getPeerId() == ownerId) {
                return realmRoom.getId();
            }
        }
        realm.close();
        return ownerId;
    }

    private static void fillAvatarSyncList(long ownerId, OnAvatarGet onAvatarGet) {
        if (G.twoPaneMode) {
            if (onAvatarSync.containsKey(ownerId)) {
                ArrayList<OnAvatarGet> listeners = onAvatarSync.get(ownerId);
                listeners.add(onAvatarGet);
            } else {
                ArrayList<OnAvatarGet> listeners = new ArrayList<>();
                listeners.add(onAvatarGet);
                onAvatarSync.put(ownerId, listeners);
            }
        }
    }

    private static void syncAvatarAdd(long ownerId, String avatarPath) {
        if (G.twoPaneMode && onAvatarSync.get(ownerId) != null) {
            for (OnAvatarGet listener : onAvatarSync.get(ownerId)) {
                listener.onAvatarGet(avatarPath, ownerId);
            }
        }
    }

    public static String[] getAvatarSync(ProtoGlobal.RegisteredUser registeredUser, final long ownerId, AvatarType avatarType, boolean showMain, Realm _realm, final OnAvatarGet onAvatarGet) {
        getAvatarImage(registeredUser, ownerId, avatarType, false, _realm, onAvatarGet);

        /**
         * first show user initials and after that show avatar if exist
         */
        return showInitials(ownerId, avatarType);
    }

    public enum AvatarType {
        USER, ROOM
    }

    private static class AvatarDownload implements OnFileDownloaded {

        private static OnDownload onDownload;

        private void avatarDownload(RealmAttachment realmAttachment, ProtoFileDownload.FileDownload.Selector selector, OnDownload onDownload) {

            try {
                this.onDownload = onDownload;
                long fileSize = 0;
                String filePath = "";

                G.onFileDownloaded = this;
                if (selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                    filePath = AndroidUtils.getFilePathWithCashId(realmAttachment.getCacheId(), realmAttachment.getName(), G.DIR_TEMP, false);
                    fileSize = realmAttachment.getSize();
                } else if (selector == ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL) {
                    filePath = AndroidUtils.getFilePathWithCashId(realmAttachment.getCacheId(), realmAttachment.getName(), G.DIR_TEMP, true);
                    fileSize = realmAttachment.getLargeThumbnail().getSize();
                } else if (selector == ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL) {
                    filePath = AndroidUtils.getFilePathWithCashId(realmAttachment.getCacheId(), realmAttachment.getName(), G.DIR_TEMP, true);
                    fileSize = realmAttachment.getSmallThumbnail().getSize();
                }
                new RequestFileDownload().download(realmAttachment.getToken(), 0, (int) fileSize, selector,
                        new RequestFileDownload.IdentityFileDownload(realmAttachment.getToken(), filePath, selector, fileSize, 0, false));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFileDownload(String filePath, String token, long fileSize, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress) {
            if (progress == 100) {
                String _newPath = filePath.replace(G.DIR_TEMP, G.DIR_IMAGE_USER);
                try {
                    AndroidUtils.cutFromTemp(filePath, _newPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                onDownload.onDownload(_newPath, token);
            } else {
                /**
                 * don't use offset in getting thumbnail
                 */
                try {
                    new RequestFileDownload().download(token, offset, (int) fileSize, selector, new RequestFileDownload.IdentityFileDownload(token, filePath, selector, fileSize, 0, false));
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onError(int major, Object identity) {
            if (major == 5 && identity != null) { //if is time out reDownload once
                RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
                String token = identityFileDownload.cacheId;
                if (!reDownloadFiles.contains(token)) {
                    reDownloadFiles.add(token);
                    new RequestFileDownload().download(token, 0, (int) identityFileDownload.size, identityFileDownload.selector, identity);
                }
            }
        }
    }
}
