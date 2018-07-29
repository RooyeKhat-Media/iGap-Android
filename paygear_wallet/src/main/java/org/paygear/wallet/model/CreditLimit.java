package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Software1 on 3/13/2018.
 */

public class CreditLimit implements Serializable {

    @SerializedName("today_limit")
    public long todayLimit;
    @SerializedName("week_limit")
    public long weekLimit;

}
