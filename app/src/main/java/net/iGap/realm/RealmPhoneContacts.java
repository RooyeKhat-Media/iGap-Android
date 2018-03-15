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

import net.iGap.helper.HelperString;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsGetList;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmPhoneContacts extends RealmObject {

    @PrimaryKey
    private String phone;
    private String firstName;
    private String lastName;

    public static void sendContactList(final ArrayList<StructListOfContact> list, final boolean force) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<StructListOfContact> _list = fillContactsToDB(list);
                if (_list.size() > 0) {
                    RequestUserContactImport listContact = new RequestUserContactImport();
                    listContact.contactImport(_list, force);
                } else {
                    new RequestUserContactsGetList().userContactGetList();
                }
            }
        }).start();
    }

    public static void sendContactList(final ArrayList<StructListOfContact> list, final boolean force, final boolean getContactList) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<StructListOfContact> _list = fillContactsToDB(list);
                if (_list.size() > 0) {
                    RequestUserContactImport listContact = new RequestUserContactImport();
                    listContact.contactImport(_list, force, getContactList);
                } else if (getContactList) {
                    new RequestUserContactsGetList().userContactGetList();
                }
            }
        }).start();
    }

    private static void addContactToDB(final StructListOfContact item, Realm realm) {
        try {
            RealmPhoneContacts realmPhoneContacts = new RealmPhoneContacts();
            realmPhoneContacts.setPhone(item.getPhone());
            realmPhoneContacts.setFirstName(item.firstName);
            realmPhoneContacts.setLastName(item.lastName);
            realm.copyToRealmOrUpdate(realmPhoneContacts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<StructListOfContact> fillContactsToDB(final ArrayList<StructListOfContact> list) {

        final ArrayList<StructListOfContact> notImportedList = new ArrayList<>();

        if (list == null) {
            return notImportedList;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < list.size(); i++) {

                    boolean _addItem = false;
                    final StructListOfContact _item = list.get(i);

                    if (_item.getPhone() == null || _item.getPhone().length() == 0) {
                        continue;
                    }

                    final RealmPhoneContacts _realmPhoneContacts = realm.where(RealmPhoneContacts.class).equalTo(RealmPhoneContactsFields.PHONE, _item.getPhone()).findFirst();

                    if (_realmPhoneContacts == null) {
                        _addItem = true;
                        //addContactToDB(_item, realm);
                    } else {
                        if (!_item.getFirstName().equals(_realmPhoneContacts.getFirstName()) || !_item.getLastName().equals(_realmPhoneContacts.getLastName())) {
                            _addItem = true;

                            // if one number save with tow or more different name
                            int count = 0;
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).getPhone().equals(_item.getPhone())) {
                                    count++;
                                    if (count > 1) {
                                        _addItem = false;
                                        break;
                                    }
                                }
                            }

                            if (_addItem) {
                                _realmPhoneContacts.setFirstName(_item.getFirstName());
                                _realmPhoneContacts.setLastName(_item.getLastName());
                            }
                        }
                    }

                    if (_addItem) {
                        notImportedList.add(_item);
                    }
                }
            }
        });

        realm.close();

        return notImportedList;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {

        try {
            this.firstName = firstName;
        } catch (Exception e) {
            this.firstName = HelperString.getUtf8String(firstName);
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {

        try {
            this.lastName = lastName;
        } catch (Exception e) {
            this.lastName = HelperString.getUtf8String(lastName);
        }
    }
}
