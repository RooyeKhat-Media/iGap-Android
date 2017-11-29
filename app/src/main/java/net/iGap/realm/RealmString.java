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

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmStringRealmProxy;
import net.iGap.helper.HelperString;
import org.parceler.Parcel;

@Parcel(implementations = {RealmStringRealmProxy.class}, value = Parcel.Serialization.BEAN, analyze = {RealmString.class}) public class RealmString extends RealmObject {

    private String string;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        try {
            this.string = string;
        } catch (Exception e) {
            this.string = HelperString.getUtf8String(string);
        }
    }

    public static RealmString string(Realm realm, String string) {
        RealmString realmString = realm.createObject(RealmString.class);
        realmString.setString(string);
        return realmString;
    }
}
