/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.module;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public final class ContactUtils {
    private Context mContext;
    private Uri mContactUri;
    private String mContactID;

    public ContactUtils(Context context, Uri contactUri) {
        this.mContext = context;
        this.mContactUri = contactUri;

        // getting contact ID
        Cursor cursorID = mContext.getContentResolver().query(mContactUri, new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (cursorID != null && cursorID.moveToFirst()) {
            mContactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            cursorID.close();
        }
    }

    @Nullable
    public Uri getPhotoUri() {
        ContentResolver contentResolver = mContext.getContentResolver();

        try {
            Cursor cursor = contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + "=" + mContactID + " AND "

                    + ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null, null);

            if (cursor != null) {
                if (!cursor.moveToFirst()) {
                    return null; // no photo
                }
            } else {
                return null; // error in cursor process
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(mContactID));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public Bitmap retrievePhoto() {
        try {
            Bitmap photo = null;
            InputStream inputStream =
                    ContactsContract.Contacts.openContactPhotoInputStream(mContext.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(mContactID)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();
            return photo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String retrieveNumber() {
        Cursor c = mContext.getContentResolver().query(mContactUri, null, null, null, null);
        String cNumber = null;
        if (c.moveToFirst()) {
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (hasPhone.equalsIgnoreCase("1")) {
                cNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }
        c.close();
        return cNumber;
    }

    public String retrieveName() {
        String contactName = null;

        // querying contact data store
        Cursor cursor = mContext.getContentResolver().query(mContactUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }

        return contactName;
    }
}
