package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ir.radsense.raadcore.model.KeyValue;

/**
 * Created by Software1 on 9/2/2017.
 */

public class PaymentResult implements Serializable {

    @SerializedName("trace_no")
    public long traceNumber;
    @SerializedName("invoice_number")
    public long invoiceNumber;
    public long amount;
    public KeyValue[] result;
    @SerializedName("callback_url")
    public String callbackUrl;
}


