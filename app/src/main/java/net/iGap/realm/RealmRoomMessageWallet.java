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

import net.iGap.module.AppUtils;
import net.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessageWallet extends RealmObject {

    @PrimaryKey
    private long id;
    private String type;
    private long fromUserId;
    private long toUserId;
    private long amount;
    private long traceNumber;
    private long invoiceNumber;
    private int payTime;
    private String description;

    public static RealmRoomMessageWallet put(final ProtoGlobal.RoomMessageWallet input) {
        Realm realm = Realm.getDefaultInstance();
        RealmRoomMessageWallet messageWallet = null;
        messageWallet = realm.createObject(RealmRoomMessageWallet.class, AppUtils.makeRandomId());

        messageWallet.setType(input.getType().toString());
        messageWallet.setFromUserId(input.getMoneyTransfer().getFromUserId());
        messageWallet.setToUserId(input.getMoneyTransfer().getToUserId());
        messageWallet.setAmount(input.getMoneyTransfer().getAmount());
        messageWallet.setTraceNumber(input.getMoneyTransfer().getTraceNumber());
        messageWallet.setInvoiceNumber(input.getMoneyTransfer().getInvoiceNumber());
        messageWallet.setPayTime(input.getMoneyTransfer().getPayTime());
        messageWallet.setDescription(input.getMoneyTransfer().getDescription());

        realm.close();

        return messageWallet;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTraceNumber() {
        return traceNumber;
    }

    public void setTraceNumber(long traceNumber) {
        this.traceNumber = traceNumber;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public int getPayTime() {
        return payTime;
    }

    public void setPayTime(int payTime) {
        this.payTime = payTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
