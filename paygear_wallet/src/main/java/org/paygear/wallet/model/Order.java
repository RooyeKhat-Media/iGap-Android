package org.paygear.wallet.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;

/**
 * Created by Software1 on 9/2/2017.
 */

public class Order implements Serializable {

    public static final int ORDER_TYPE_DEFAULT = 0;
    public static final int ORDER_TYPE_SALE_SHARE = 1;
    public static final int ORDER_TYPE_CHARGE_CREDIT = 2;
    public static final int ORDER_TYPE_REQUEST_MONEY = 3;
    public static final int ORDER_TYPE_BUY_CLUB_PLAN = 4;
    public static final int ORDER_TYPE_P2P = 5;
    public static final int ORDER_TYPE_PARTIAL_PAY = 6;
    public static final int ORDER_TYPE_MULTIPLE_PAY = 7;
    public static final int ORDER_TYPE_CASH_OUT = 8;

    public static final int TRANSACTION_TYPE_DIRECT_CARD = 0;
    public static final int TRANSACTION_TYPE_CREDIT = 1;
    public static final int TRANSACTION_TYPE_CASH = 2;
    public static final int TRANSACTION_TYPE_POS = 3;

    @SerializedName("_id")
    public String id;
    public long amount;
    @SerializedName("payed_price")
    public long paidAmount;
    @SerializedName("card_number")
    public String cardNumber;
    @SerializedName("target_card_number")
    public String targetCardNumber;
    @SerializedName("target_sheba_number")
    public String targetShebaNumber;
    @SerializedName("trace_no")
    public long traceNumber;
    @SerializedName("invoice_number")
    public long invoiceNumber;
    public String description;
    @SerializedName("has_coupon")
    public boolean hasCoupon;
    @SerializedName("discount_price")
    public long discountPrice;
    @SerializedName("created_at_timestamp")
    public long createdMicroTime;
    @SerializedName("pay_date")
    public long paidMicroTime;

    @SerializedName("additional_fee")
    public long additionalFee;
    @SerializedName("delivery_price")
    public long deliveryPrice;
    public long tax;

    @SerializedName("is_paid")
    public boolean isPaid;
    @SerializedName("is_pre_order")
    public boolean isPreOrder;

    @SerializedName("order_id")
    public long orderId;
    @SerializedName("order_type")
    public int orderType;
    @SerializedName("transaction_type")
    public int transactionType;

    @SerializedName("is_verified")
    public boolean isVerified;

    public Account receiver;
    public Account sender;

    //public OrderedProduct[] cart;
    //public POD pod;

    public Transaction[] transactions;

    public long getTotalPrice() {
        /*if (cart != null) {
            long total = 0;
            for (OrderedProduct p : cart) {
                total += p.discountedPrice * p.quantity;
            }
            return total;
        }*/
        return amount;
    }

    public Boolean isPay() {
        if (sender != null) {
            return sender.id.equals(Auth.getCurrentAuth().getId());
        } else if (receiver != null) {
            return !receiver.id.equals(Auth.getCurrentAuth().getId());
        }

        return null;
    }
}
