package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Software1 on 3/14/2018.
 */

public class Transaction {

    public long amount;
    @SerializedName("card_title")
    public String cardTitle;
    @SerializedName("trace_no")
    public long traceNumber;
    @SerializedName("transaction_type")
    public int transactionType;

}
