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

import android.support.annotation.Nullable;

import net.iGap.G;
import net.iGap.helper.HelperString;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.SUID;
import net.iGap.module.enums.AttachmentFor;
import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmAttachmentRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {RealmAttachmentRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmAttachment.class})
public class RealmAttachment extends RealmObject {
    // should be message id for message attachment and user id for avatar
    @PrimaryKey
    private long id;
    private String token;
    private String name;
    private long size;
    private int width;
    private int height;
    private double duration;
    private String cacheId;
    private RealmThumbnail largeThumbnail;
    private RealmThumbnail smallThumbnail;
    @Nullable
    private String localThumbnailPath;
    @Nullable
    private String localFilePath;

    public static void updateToken(long fakeId, String token) {
        Realm realm = Realm.getDefaultInstance();
        RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, fakeId).findFirst();
        if (attachment != null) {
            attachment.setToken(token);
        }
        realm.close();
    }

    public static void updateFileSize(final long messageId, final long fileSize) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmAttachment attachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, messageId).findFirst();
                if (attachment != null) {
                    attachment.setSize(fileSize);
                }
            }
        });
        realm.close();
    }

    public static RealmAttachment putOrUpdate(Realm realm, long messageId, RealmAttachment realmAttachment, ProtoGlobal.File attachment) {
        if (realmAttachment == null) {
            realmAttachment = realm.createObject(RealmAttachment.class, messageId);
        }
        realmAttachment.setCacheId(attachment.getCacheId());
        realmAttachment.setDuration(attachment.getDuration());
        realmAttachment.setHeight(attachment.getHeight());
        realmAttachment.setName(attachment.getName());
        realmAttachment.setSize(attachment.getSize());
        realmAttachment.setToken(attachment.getToken());
        realmAttachment.setWidth(attachment.getWidth());

        long smallMessageThumbnail = SUID.id().get();
        RealmThumbnail.put(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

        long largeMessageThumbnail = SUID.id().get();
        RealmThumbnail.put(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

        realmAttachment.setSmallThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, smallMessageThumbnail).findFirst());
        realmAttachment.setLargeThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, largeMessageThumbnail).findFirst());

        return realmAttachment;
    }

    public static RealmAttachment build(ProtoGlobal.File file, AttachmentFor attachmentFor, @Nullable ProtoGlobal.RoomMessageType messageType) {
        Realm realm = Realm.getDefaultInstance();

        RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.TOKEN, file.getToken()).findFirst();
        if (realmAttachment == null) {
            long id = SUID.id().get();
            realmAttachment = realm.createObject(RealmAttachment.class, id);

            realmAttachment.setCacheId(file.getCacheId());
            realmAttachment.setDuration(file.getDuration());
            realmAttachment.setHeight(file.getHeight());

            long largeId = SUID.id().get();
            RealmThumbnail.put(largeId, id, file.getLargeThumbnail());
            long smallId = SUID.id().get();
            RealmThumbnail.put(smallId, id, file.getSmallThumbnail());

            RealmThumbnail largeThumbnail = realm.where(RealmThumbnail.class).equalTo("id", largeId).findFirst();
            realmAttachment.setLargeThumbnail(largeThumbnail);
            RealmThumbnail smallThumbnail = realm.where(RealmThumbnail.class).equalTo("id", smallId).findFirst();
            realmAttachment.setSmallThumbnail(smallThumbnail);

            String tempFilePath = "";
            String filePath = "";
            switch (attachmentFor) {
                case MESSAGE_ATTACHMENT:
                    filePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), messageType);
                    tempFilePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_TEMP, true);
                    break;
                case AVATAR:
                    filePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, false);
                    tempFilePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, true);
                    break;
            }

            realmAttachment.setLocalFilePath(new File(filePath).exists() ? filePath : null);
            realmAttachment.setLocalThumbnailPath(new File(tempFilePath).exists() ? tempFilePath : null);
            realmAttachment.setName(file.getName());
            realmAttachment.setSize(file.getSize());
            realmAttachment.setToken(file.getToken());
            realmAttachment.setWidth(file.getWidth());
        } else {

            if (realmAttachment.height != file.getHeight()) {
                realmAttachment.setHeight(file.getHeight());
            }

            if (realmAttachment.width != file.getWidth()) {
                realmAttachment.setWidth(file.getWidth());
            }

            String _filePath = realmAttachment.getLocalFilePath();

            String _Dir = "";

            if (_filePath != null && _filePath.length() > 0) {
                if (_filePath.contains(G.DIR_APP)) {
                    String _defaultFilePAth = "";
                    switch (attachmentFor) {
                        case MESSAGE_ATTACHMENT:
                            _defaultFilePAth = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), messageType);
                            _Dir = AndroidUtils.suitableAppFilePath(messageType);
                            break;
                        case AVATAR:
                            _defaultFilePAth = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, false);

                            if (realmAttachment.getLocalThumbnailPath() == null) {
                                realmAttachment.setLocalThumbnailPath(AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, true));
                            }
                            _Dir = G.DIR_IMAGE_USER;

                            break;
                    }

                    if (!_filePath.equals(_defaultFilePAth)) {
                        File _File1 = new File(_filePath);
                        if (_File1.exists()) {

                            if (_filePath.contains(_Dir)) {
                                _File1.renameTo(new File(_defaultFilePAth));
                                realmAttachment.setLocalFilePath(_defaultFilePAth);
                            } else {
                                try {
                                    AndroidUtils.copyFile(_File1, new File(_defaultFilePAth));
                                    realmAttachment.setLocalFilePath(_defaultFilePAth);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }

        realm.close();

        return realmAttachment;
    }

    public RealmThumbnail getLargeThumbnail() {
        return largeThumbnail;
    }

    public void setLargeThumbnail(RealmThumbnail largeThumbnail) {
        this.largeThumbnail = largeThumbnail;
    }

    public RealmThumbnail getSmallThumbnail() {
        return smallThumbnail;
    }

    public void setSmallThumbnail(RealmThumbnail smallThumbnail) {
        this.smallThumbnail = smallThumbnail;
    }

    @Nullable
    public String getLocalThumbnailPath() {
        return localThumbnailPath;
    }

    public void setLocalThumbnailPath(@Nullable String localThumbnailPath) {
        this.localThumbnailPath = localThumbnailPath;
    }

    public boolean thumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }

    public boolean fileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    @Nullable
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(@Nullable String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {

        try {
            this.name = name;
        } catch (Exception e) {
            this.name = HelperString.getUtf8String(name);
        }
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }

    /**
     * check file exist and also user can read this file (permission granted)
     */
    public boolean isFileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists() && new File(localFilePath).canRead();
    }

    public boolean isFileExistsOnLocalAndIsThumbnail() {
        assert localFilePath != null;
        return isFileExistsOnLocal() && isFileImage();
    }

    /**
     * check file thumbnail exist and also user can read this file (permission granted)
     */
    public boolean isThumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists() && new File(localThumbnailPath).canRead();
    }

    private boolean isFileImage() {
        for (String ext : AppUtils.exts) {
            if (localFilePath.endsWith(ext)) {
                return true;
            }
        }

        return false;
    }
}
