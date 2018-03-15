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

import io.realm.RealmObject;

public class RealmInviteFriend extends RealmObject {

    private String phone;
    private String firstName;
    private String lastName;
    private String displayName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        try {
            this.phone = phone;
        } catch (Exception e) {
            this.phone = HelperString.getUtf8String(phone);
        }
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        try {
            this.displayName = displayName;
        } catch (Exception e) {
            this.displayName = HelperString.getUtf8String(displayName);
        }
    }
}
