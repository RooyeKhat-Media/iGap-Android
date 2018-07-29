package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Software1 on 9/2/2017.
 */

public class PaymentAuth implements Serializable {

    public String token;
    @SerializedName(value="pub_key", alternate = {"key"})
    public String publicKey;

}
