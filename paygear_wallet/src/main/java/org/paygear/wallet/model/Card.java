package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Software1 on 8/3/2016.
 */
public class Card implements Serializable {

    @SerializedName("card_number")
    public String cardNumber;
    @SerializedName("token")
    public String token;
    @SerializedName("bank_code")
    public int bankCode;
    @SerializedName("type")
    public int type;
    @SerializedName("exp_m")
    public int expireMonth;
    @SerializedName("exp_y")
    public int expireYear;
    public String cvv2;
    @SerializedName("background_image")
    public String backgroundImage;
    @SerializedName("text_color")
    public String textColor;

    @SerializedName("default")
    public boolean isDefault;
    @SerializedName("protected")
    public boolean isProtected;
    @SerializedName("cash_in")
    public boolean isCashIn;
    @SerializedName("cash_out")
    public boolean isCashOut;
    @SerializedName("cashable_balance")
    public long cashOutBalance;
    public long balance;



    public String getCardNumberForTitle() {
        int len = cardNumber.length();
        if (len == 16)
            return "**** **** **** " + cardNumber.substring(12);
        else if (len == 4)
            return "**** **** **** " + cardNumber;
        else
            return cardNumber;
    }

    public boolean isRaadCard() {
        return bankCode == 69;
    }
}
