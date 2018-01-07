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

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import java.util.ArrayList;
import net.iGap.Config;
import net.iGap.G;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.realm.RealmPhoneContacts;

import static net.iGap.G.context;

public class ServiceContact extends Service {

    private MyContentObserver contentObserver;
    private long fetchContactTime;

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        getApplicationContext().getContentResolver().unregisterContentObserver(contentObserver);
        contentObserver = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (contentObserver == null) {
            contentObserver = new MyContentObserver();
            getApplicationContext().getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contentObserver);
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        if (preferences.getInt(SHP_SETTING.KEY_STNS_KEEP_ALIVE_SERVICE, 1) == 1) {
            return Service.START_STICKY;
        }
        return Service.START_NOT_STICKY;
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver() {
            super(null);
        }

        @Override public void onChange(boolean selfChange) {

            final int permissionReadContact = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
            if ((permissionReadContact == PackageManager.PERMISSION_GRANTED)) {
                /**
                 * for avoid from run multiple this code at the same time
                 * because sometimes onChange was run multiple times
                 */
                if (HelperTimeOut.timeoutChecking(0, fetchContactTime, Config.FETCH_CONTACT_TIME_OUT)) {
                    fetchContactTime = System.currentTimeMillis();
                    fetchContacts();
                }
            }
        }

        private void fetchContacts() {
                    try {
                        ArrayList<StructListOfContact> contactList = new ArrayList<>();
                        ContentResolver cr = G.context.getContentResolver();
                        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                        if (cur != null && !cur.isClosed()) {
                            if (cur.getCount() > 0) {
                                while (cur.moveToNext()) {
                                    StructListOfContact itemContact = new StructListOfContact();
                                    itemContact.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] {
                                            id
                                        }, null);
                                        if (pCur != null) {
                                            while (pCur.moveToNext()) {
                                                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                                    itemContact.setPhone(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                                                }
                                            }
                                            pCur.close();
                                        }
                                    }
                                    contactList.add(itemContact);
                                }
                            }
                            cur.close();
                        }
                        ArrayList<StructListOfContact> resultContactList = new ArrayList<>();
                        for (int i = 0; i < contactList.size(); i++) {

                            if (contactList.get(i).getPhone() != null) {
                                StructListOfContact itemContact = new StructListOfContact();
                                if (contactList.get(i) != null && contactList.get(i).getDisplayName() != null) {
                                    String[] sp = contactList.get(i).getDisplayName().split(" ");
                                    if (sp.length == 1) {

                                        itemContact.setFirstName(sp[0]);
                                        itemContact.setLastName("");
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    } else if (sp.length == 2) {
                                        itemContact.setFirstName(sp[0]);
                                        itemContact.setLastName(sp[1]);
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    } else if (sp.length == 3) {
                                        itemContact.setFirstName(sp[0]);
                                        itemContact.setLastName(sp[1] + sp[2]);
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    } else if (sp.length >= 3) {
                                        itemContact.setFirstName(contactList.get(i).getDisplayName());
                                        itemContact.setLastName("");
                                        itemContact.setPhone(contactList.get(i).getPhone());
                                        itemContact.setDisplayName(contactList.get(i).displayName);
                                    }
                                    resultContactList.add(itemContact);
                                }
                            }
                        }

                        RealmPhoneContacts.sendContactList(resultContactList, false);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e1) {
                        e1.printStackTrace();
                    }
        }
    }

}
