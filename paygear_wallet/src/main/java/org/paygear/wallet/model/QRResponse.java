package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ghaisar on 3/17/2018 AD.
 */

public class QRResponse implements Serializable {

    public static final int QR_TYPE_DIRECT_PAY = 8;
    public static final int QR_TYPE_TAXI = 50;

    @SerializedName("_id")
    public String id;
    @SerializedName("qr_type")
    public int type;
    @SerializedName("account_id")
    public String accountId;
    public String value;
    @SerializedName("sequence_number")
    public String sequenceNumber;
    @SerializedName("disabled")
    public boolean isDisabled;

}
