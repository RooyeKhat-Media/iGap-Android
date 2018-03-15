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

import android.os.Parcel;

import net.iGap.fragments.FragmentChat;
import net.iGap.realm.RealmString;

import org.parceler.Parcels;

// Specific class for a RealmList<Bar> field
public class StringListParcelConverter extends RealmListParcelConverter<RealmString> {

    @Override
    public void itemToParcel(RealmString input, Parcel parcel) {
        parcel.writeParcelable(Parcels.wrap(input), 0);
    }

    @Override
    public RealmString itemFromParcel(Parcel parcel) {
        return Parcels.unwrap(parcel.readParcelable(FragmentChat.class.getClassLoader()));
    }
}