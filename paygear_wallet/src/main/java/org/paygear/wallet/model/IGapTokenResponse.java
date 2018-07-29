package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import ir.radsense.raadcore.model.KeyValue;

public class IGapTokenResponse {
    @SerializedName("trace_no")
    public long traceNumber;
    @SerializedName("invoice_number")
    public long invoiceNumber;
    public long amount;
    public KeyValue[] result;
}
