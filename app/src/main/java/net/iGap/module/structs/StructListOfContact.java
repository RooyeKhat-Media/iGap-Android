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

public class StructListOfContact {

    public String phone;
    public String firstName;
    public String lastName;
    public String displayName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {

        if (phone == null) {
            this.phone = "";
        } else {
            this.phone = phone;
        }
    }

    public String getFirstName() {

        return firstName;
    }

    public void setFirstName(String firstName) {

        if (firstName == null) {
            this.firstName = "";
        } else {
            this.firstName = firstName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {

        if (lastName == null) {
            this.lastName = "";
        } else {
            this.lastName = lastName;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
