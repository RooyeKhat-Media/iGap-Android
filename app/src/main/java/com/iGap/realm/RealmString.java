/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package com.iGap.realm;

import io.realm.RealmObject;
import io.realm.RealmStringRealmProxy;
import org.parceler.Parcel;

@Parcel(implementations = {RealmStringRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmString.class})
public class RealmString extends RealmObject {

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String s) {
        this.string = s;
    }
}
