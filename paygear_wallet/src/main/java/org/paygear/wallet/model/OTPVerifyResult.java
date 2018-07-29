package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Software1 on 8/28/2017.
 */

public class OTPVerifyResult {

    @SerializedName("is_new")
    public boolean isNew;
    @SerializedName("two_step_verification")
    public boolean hasPassword;
}
