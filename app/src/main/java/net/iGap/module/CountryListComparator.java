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

import net.iGap.module.structs.StructCountry;

import java.util.Comparator;

/**
 * sort list of country
 */

public class CountryListComparator implements Comparator<StructCountry> {
    @Override
    public int compare(StructCountry structCountry, StructCountry t1)

    {
        return structCountry.getName().compareToIgnoreCase(t1.getName());
    }
}
