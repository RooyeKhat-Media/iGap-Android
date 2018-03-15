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

import net.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmThumbnailRealmProxy;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {RealmThumbnailRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmThumbnail.class})
public class RealmThumbnail extends RealmObject {
    @PrimaryKey
    private long id;
    private long messageId;
    private long size;
    private int width;
    private int height;
    private String cacheId;

    public static RealmThumbnail put(long id, final long messageId, final ProtoGlobal.Thumbnail thumbnail) {
        Realm realm = Realm.getDefaultInstance();
        RealmThumbnail realmThumbnail = realm.createObject(RealmThumbnail.class, id);
        realmThumbnail.setCacheId(thumbnail.getCacheId());
        realmThumbnail.setWidth(thumbnail.getWidth());
        realmThumbnail.setSize(thumbnail.getSize());
        realmThumbnail.setHeight(thumbnail.getHeight());
        realmThumbnail.setMessageId(messageId);

        realm.close();

        return realmThumbnail;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
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

    public String getCacheId() {
        return cacheId;
    }

    public void setCacheId(String cacheId) {
        this.cacheId = cacheId;
    }
}
