/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module.structs;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmAttachmentFields;
import net.iGap.realm.RealmAvatar;

import java.io.File;

import io.realm.Realm;

public class StructMessageAttachment implements Parcelable {
    public static final Parcelable.Creator<StructMessageAttachment> CREATOR = new Parcelable.Creator<StructMessageAttachment>() {
        @Override
        public StructMessageAttachment createFromParcel(Parcel source) {
            return new StructMessageAttachment(source);
        }

        @Override
        public StructMessageAttachment[] newArray(int size) {
            return new StructMessageAttachment[size];
        }
    };
    public String token;
    public String cashID;
    public String name;
    public long size;
    public int width;
    public int height;
    public double duration;
    public StructMessageThumbnail largeThumbnail;
    public StructMessageThumbnail smallThumbnail;
    @Nullable
    public String localThumbnailPath;
    @Nullable
    public String localFilePath;
    public String compressing = ""; // use for show compressing text when video is in compressing state

    public StructMessageAttachment(String token, String name, long size, int width, int height, double duration, @Nullable String localThumbnailPath, @Nullable String localFilePath,
                                   StructMessageThumbnail largeThumbnail, StructMessageThumbnail smallThumbnail) {
        this.token = token;
        this.name = name;
        this.size = size;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.localThumbnailPath = localThumbnailPath;
        this.localFilePath = localFilePath;
        this.largeThumbnail = largeThumbnail;
        this.smallThumbnail = smallThumbnail;
        this.compressing = "";
    }

    public StructMessageAttachment(RealmAttachment realmAttachment) {
        this.token = realmAttachment.getToken();
        this.name = realmAttachment.getName();
        this.size = realmAttachment.getSize();
        this.width = realmAttachment.getWidth();
        this.height = realmAttachment.getHeight();
        this.duration = realmAttachment.getDuration();
        this.localThumbnailPath = realmAttachment.getLocalThumbnailPath();
        this.localFilePath = realmAttachment.getLocalFilePath();
        this.compressing = "";
    }

    public StructMessageAttachment() {
    }

    protected StructMessageAttachment(Parcel in) {
        this.token = in.readString();
        this.name = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.duration = in.readDouble();
        this.largeThumbnail = in.readParcelable(StructMessageThumbnail.class.getClassLoader());
        this.smallThumbnail = in.readParcelable(StructMessageThumbnail.class.getClassLoader());
        this.localThumbnailPath = in.readString();
        this.localFilePath = in.readString();
        this.compressing = in.readString();
    }

    public static StructMessageAttachment convert(ProtoGlobal.File attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getToken(), attachment.getName(), attachment.getSize(), attachment.getWidth(), attachment.getHeight(), attachment.getDuration(), null, null,
                StructMessageThumbnail.convert(attachment.getLargeThumbnail()), StructMessageThumbnail.convert(attachment.getSmallThumbnail()));
    }

    public static StructMessageAttachment convert(ProtoGlobal.Avatar attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getFile().getToken(), attachment.getFile().getName(), attachment.getFile().getSize(), attachment.getFile().getWidth(),
                attachment.getFile().getHeight(), attachment.getFile().getDuration(), null, null, StructMessageThumbnail.convert(attachment.getFile().getLargeThumbnail()),
                StructMessageThumbnail.convert(attachment.getFile().getSmallThumbnail()));
    }

    public static StructMessageAttachment convert(RealmAttachment attachment) {
        if (attachment == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getToken(), attachment.getName(), attachment.getSize(), attachment.getWidth(), attachment.getHeight(), attachment.getDuration(),
                attachment.getLocalThumbnailPath(), attachment.getLocalFilePath(), StructMessageThumbnail.convert(attachment.getLargeThumbnail()),
                StructMessageThumbnail.convert(attachment.getSmallThumbnail()));
    }

    public static StructMessageAttachment convert(RealmAvatar attachment) {
        if (attachment == null || attachment.getFile() == null) {
            return new StructMessageAttachment();
        }
        return new StructMessageAttachment(attachment.getFile().getToken(), attachment.getFile().getName(), attachment.getFile().getSize(), attachment.getFile().getWidth(),
                attachment.getFile().getHeight(), attachment.getFile().getDuration(), attachment.getFile().getLocalThumbnailPath(), attachment.getFile().getLocalFilePath(),
                StructMessageThumbnail.convert(attachment.getFile().getLargeThumbnail()), StructMessageThumbnail.convert(attachment.getFile().getSmallThumbnail()));
    }

    @Nullable
    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(final long messageId, @Nullable final String path) {
        this.localFilePath = path;
        Realm realm = Realm.getDefaultInstance();
        final RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, messageId).findFirst();
        if (realmAttachment == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmAttachment messageAttachment = realm.createObject(RealmAttachment.class, messageId);
                    messageAttachment.setLocalFilePath(path);
                }
            });
        } else {
            if (realmAttachment.getLocalFilePath() == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realmAttachment.setLocalFilePath(path);
                    }
                });
            }
        }
        realm.close();
    }

    @Nullable
    public String getLocalThumbnailPath() {
        return localThumbnailPath;
    }

    public void setLocalThumbnailPath(final long messageId, @Nullable final String localPath) {
        this.localThumbnailPath = localPath;
        Realm realm = Realm.getDefaultInstance();
        final RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, messageId).findFirst();
        if (realmAttachment == null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmAttachment messageAttachment = realm.createObject(RealmAttachment.class, messageId);
                    messageAttachment.setLocalThumbnailPath(localPath);
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmAttachment.setLocalThumbnailPath(localPath);
                }
            });
        }
        realm.close();
    }

    public boolean isFileExistsOnLocal() {
        return localFilePath != null && new File(localFilePath).exists();
    }

    public boolean isThumbnailExistsOnLocal() {
        return localThumbnailPath != null && new File(localThumbnailPath).exists();
    }

    public boolean isFileExistsOnLocalAndIsThumbnail() {
        assert localFilePath != null;
        return isFileExistsOnLocal() && localFilePath.endsWith(".jpg");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.name);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeDouble(this.duration);
        dest.writeParcelable(this.largeThumbnail, flags);
        dest.writeParcelable(this.smallThumbnail, flags);
        dest.writeString(this.localThumbnailPath);
        dest.writeString(this.localFilePath);
        dest.writeString(this.compressing);
    }
}
