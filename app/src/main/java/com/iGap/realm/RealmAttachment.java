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

import android.support.annotation.Nullable;
import com.iGap.G;
import com.iGap.module.AndroidUtils;
import com.iGap.module.AppUtils;
import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmAttachmentRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import java.io.File;
import org.parceler.Parcel;

@Parcel(implementations = {RealmAttachmentRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmAttachment.class})
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
            RealmThumbnail.create(largeId, id, file.getLargeThumbnail());
            long smallId = SUID.id().get();
            RealmThumbnail.create(smallId, id, file.getSmallThumbnail());

            RealmThumbnail largeThumbnail = realm.where(RealmThumbnail.class).equalTo("id", largeId).findFirst();
            realmAttachment.setLargeThumbnail(largeThumbnail);
            RealmThumbnail smallThumbnail = realm.where(RealmThumbnail.class).equalTo("id", smallId).findFirst();
            realmAttachment.setSmallThumbnail(smallThumbnail);

            String tempFilePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_TEMP, true);
            String filePath = "";
            switch (attachmentFor) {
                case MESSAGE_ATTACHMENT:
                    filePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), messageType);
                    break;
                case AVATAR:
                    filePath = AndroidUtils.getFilePathWithCashId(file.getCacheId(), file.getName(), G.DIR_IMAGE_USER, false);
                    break;
            }

            realmAttachment.setLocalFilePath(new File(filePath).exists() ? filePath : null);
            realmAttachment.setLocalThumbnailPath(new File(tempFilePath).exists() ? tempFilePath : null);
            realmAttachment.setName(file.getName());
            realmAttachment.setSize(file.getSize());
            realmAttachment.setToken(file.getToken());
            realmAttachment.setWidth(file.getWidth());
        }

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
        this.name = name;
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

    public boolean isFileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    public boolean isFileExistsOnLocalAndIsThumbnail() {
        assert localFilePath != null;
        return isFileExistsOnLocal() && isFileImage();
    }

    public boolean isThumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
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
