package net.iGap.module;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.pchmn.materialchips.model.ChipInterface;

public class ContactChip implements ChipInterface {

    private long id;
    private Uri avatarUri;
    private String name;
    private Drawable avatarDrawable;
    private String phoneNumber;

    public ContactChip(long id, Uri uri, String name) {
        this.id = id;
        this.name = name;
        this.avatarUri = uri;
    }

    public ContactChip(long id, Drawable avatarDrawable, String name) {
        this.id = id;
        this.name = name;
        this.avatarDrawable = avatarDrawable;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public Uri getAvatarUri() {
        return avatarUri;
    }

    @Override
    public Drawable getAvatarDrawable() {
        return avatarDrawable;
    }

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getInfo() {
        return phoneNumber;
    }
}