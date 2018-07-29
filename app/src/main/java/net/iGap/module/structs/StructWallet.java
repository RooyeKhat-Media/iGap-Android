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

import net.iGap.G;
import net.iGap.realm.RealmChannelExtra;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessageWallet;

import org.parceler.Parcel;

import io.realm.annotations.PrimaryKey;

@Parcel
public class StructWallet {

    public long id;
    public String type;
    public long fromUserId;
    public long toUserId;
    public long amount;
    public long traceNumber;
    public long invoiceNumber;
    public int payTime;
    public String description;

    public static StructWallet convert(RealmRoomMessageWallet realmRoomMessageWallet) {

        StructWallet structWallet = new StructWallet();
        structWallet.id = realmRoomMessageWallet.getId();
        structWallet.fromUserId = realmRoomMessageWallet.getFromUserId();
        structWallet.toUserId = realmRoomMessageWallet.getToUserId();
        structWallet.amount = realmRoomMessageWallet.getAmount();
        structWallet.traceNumber = realmRoomMessageWallet.getTraceNumber();
        structWallet.invoiceNumber = realmRoomMessageWallet.getInvoiceNumber();
        structWallet.payTime = realmRoomMessageWallet.getPayTime();
        structWallet.description = realmRoomMessageWallet.getDescription();
        return structWallet;
    }

}
