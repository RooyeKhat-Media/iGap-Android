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

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class RealmMigration implements io.realm.RealmMigration {

    @Override public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 1) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("keepRoom", boolean.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmRoomMessageSchema = schema.get("RealmRoomMessage");
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("authorRoomId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 2) {
            RealmObjectSchema roomSchema = schema.get("RealmRoom");
            if (roomSchema != null) {
                roomSchema.addField("actionStateUserId", long.class, FieldAttribute.REQUIRED);
            }

            RealmObjectSchema realmChannelRoomSchema = schema.get("RealmChannelRoom");
            if (realmChannelRoomSchema != null) {
                realmChannelRoomSchema.addField("seenId", long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 3) {
            schema.create(RealmWallpaper.class.getSimpleName())
                .addField(RealmWallpaperFields.LAST_TIME_GET_LIST, long.class, FieldAttribute.REQUIRED)
                .addField(RealmWallpaperFields.WALL_PAPER_LIST, byte[].class)
                .addField(RealmWallpaperFields.LOCAL_LIST, byte[].class);
            oldVersion++;
        }

        if (oldVersion == 4) {
            schema.create(RealmPrivacy.class.getSimpleName())
                .addField("whoCanSeeMyAvatar", String.class)
                .addField("whoCanInviteMeToChannel", String.class)
                .addField("whoCanInviteMeToGroup", String.class)
                .addField("whoCanSeeMyLastSeen", String.class);
            oldVersion++;
        }

        if (oldVersion == 5) {
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField(RealmRoomMessageFields.PREVIOUS_MESSAGE_ID, long.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.SHOW_TIME, boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.HAS_EMOJI_IN_TEXT, boolean.class, FieldAttribute.REQUIRED);
                realmRoomMessageSchema.addField(RealmRoomMessageFields.LINK_INFO, String.class);
            }
            oldVersion++;
        }

        if (oldVersion == 6) {
            RealmObjectSchema realmRoomSchema = schema.get(RealmRoom.class.getSimpleName());
            if (realmRoomSchema != null) {
                realmRoomSchema.addField(RealmRoomFields.LAST_SCROLL_POSITION_MESSAGE_ID, long.class, FieldAttribute.REQUIRED);
            }
            oldVersion++;
        }

        if (oldVersion == 7) {
            RealmObjectSchema realmPhoneContacts = schema.create(RealmPhoneContacts.class.getSimpleName())
                .addField(RealmPhoneContactsFields.PHONE, String.class)
                .addField(RealmPhoneContactsFields.FIRST_NAME, String.class)
                .addField(RealmPhoneContactsFields.LAST_NAME, String.class);
            realmPhoneContacts.addPrimaryKey(RealmPhoneContactsFields.PHONE);
            oldVersion++;
        }

        if (oldVersion == 8) {
            RealmObjectSchema roomSchema = schema.get(RealmRoom.class.getSimpleName());
            RealmObjectSchema realmRoomMessageSchema = schema.get(RealmRoomMessage.class.getSimpleName());
            if (roomSchema != null) {
                roomSchema.addRealmObjectField("firstUnreadMessage", realmRoomMessageSchema);
            }

            if (realmRoomMessageSchema != null) {
                realmRoomMessageSchema.addField("futureMessageId", long.class, FieldAttribute.REQUIRED);
            }
        }
    }
}
