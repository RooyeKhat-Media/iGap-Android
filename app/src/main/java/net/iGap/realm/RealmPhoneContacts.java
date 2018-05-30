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

import net.iGap.G;
import net.iGap.helper.HelperString;
import net.iGap.interfaces.OnQueueSendContact;
import net.iGap.module.Contacts;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.request.RequestUserContactImport;
import net.iGap.request.RequestUserContactsGetList;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmPhoneContacts extends RealmObject {

    @PrimaryKey
    private String phone;
    private String firstName;
    private String lastName;

//    public static void sendContactList(final List<StructListOfContact> list, final boolean force) {
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                List<StructListOfContact> _list = fillContactsToDB(list);
//                if (_list.size() > 0) {
//                    RequestUserContactImport listContact = new RequestUserContactImport();
//                    listContact.contactImport(_list, force);
//                } else {
//                    new RequestUserContactsGetList().userContactGetList();
//                }
//            }
//        }).start();
//    }


    private static List<StructListOfContact> tmpList;

    public static void sendContactList(final List<StructListOfContact> list, final boolean force, final boolean getContactList) {

        if (Contacts.isSendingContactToServer) {
            return;
        }

        final List<StructListOfContact> _list = fillContactsToDB(list);

        if (_list.size() > 0) {

            if (_list.size() > Contacts.PHONE_CONTACT_FETCH_LIMIT) {
                RequestUserContactImport listContact = new RequestUserContactImport();
                tmpList = _list.subList(0, Contacts.PHONE_CONTACT_FETCH_LIMIT);
                Contacts.isSendingContactToServer = true;

                G.onQueueSendContact = new OnQueueSendContact() {
                    @Override
                    public void sendContact() {

                        if (tmpList.size() > 0) {
                            addListToDB(tmpList);
                            tmpList.clear();
                        } else {
                            addListToDB(_list);
                            _list.clear();
                        }


                        if (_list.size() == 0) {
                            G.onQueueSendContact = null;
                            Contacts.isSendingContactToServer = false;
                            return;
                        }

                        if (_list.size() > Contacts.PHONE_CONTACT_FETCH_LIMIT) {
                            RequestUserContactImport listContact = new RequestUserContactImport();
                            tmpList = _list.subList(0, Contacts.PHONE_CONTACT_FETCH_LIMIT);
                            listContact.contactImport(tmpList, force, false);

                        } else {
                            RequestUserContactImport listContact = new RequestUserContactImport();
                            listContact.contactImport(_list, force, true);
                        }

                    }
                };

                listContact.contactImport(tmpList, force, false);

            } else {
                RequestUserContactImport listContact = new RequestUserContactImport();
                listContact.contactImport(_list, force, true);
                G.onQueueSendContact = new OnQueueSendContact() {
                    @Override
                    public void sendContact() {
                        addListToDB(_list);
                        G.onQueueSendContact = null;
                        Contacts.isSendingContactToServer = false;
                    }
                };
            }
        } else if (getContactList) {
            new RequestUserContactsGetList().userContactGetList();
        }
    }

    private static void addListToDB(final List<StructListOfContact> list) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < list.size(); i++) {
                    addContactToDB(list.get(i), realm);
                }
            }
        });

        realm.close();
    }

    private static void addContactToDB(final StructListOfContact item, Realm realm) {
        try {
            RealmPhoneContacts realmPhoneContacts = new RealmPhoneContacts();
            realmPhoneContacts.setPhone(checkString(item));
//            realmPhoneContacts.setFirstName(item.firstName);
//            realmPhoneContacts.setLastName(item.lastName);
            realm.copyToRealmOrUpdate(realmPhoneContacts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<StructListOfContact> fillContactsToDB(final List<StructListOfContact> list) {

        final List<StructListOfContact> notImportedList = new ArrayList<>();

        if (list == null) {
            return notImportedList;
        }

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < list.size(); i++) {
                    StructListOfContact _item = list.get(i);
                    if (_item == null || _item.getPhone() == null || _item.getPhone().length() == 0) {
                        continue;
                    }

                    if (realm.where(RealmPhoneContacts.class).equalTo(RealmPhoneContactsFields.PHONE, checkString(_item)).findFirst() == null) {
                        notImportedList.add(_item);
                    }
                }
            }
        });

        realm.close();

        return notImportedList;
    }

    private static String checkString(StructListOfContact item) {
        String phoneText = item.getPhone() + "_" + item.firstName + item.lastName;

        try {
            phoneText.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            phoneText = item.getPhone();
        }
        return phoneText;
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
