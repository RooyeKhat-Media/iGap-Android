package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ghaisar on 3/15/2018 AD.
 */

public class CashoutUserConfirm {

    @SerializedName("transfer_fee")
    public long transferFee;
    public CardUser owner;


    public class CardUser {
        @SerializedName("bank_name")
        public String bankName;
        @SerializedName("first_name")
        public String firstName;
        @SerializedName("last_name")
        public String lastName;
    }
}
