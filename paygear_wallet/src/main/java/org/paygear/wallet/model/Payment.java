package org.paygear.wallet.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.web.PostRequest;
import okhttp3.RequestBody;

public class Payment implements Serializable {

    public static final long MAX_PRICE_CVV2 = 2000000L;

    public Account account;
    public PaymentAuth paymentAuth;
    public long price;
    public int orderType = -1;
    public String orderId;
    public boolean isCredit;

    public long getTotalPrice() {
        return 0;
    }

    public long getPaymentPrice() {
        return price;
    }


    public RequestBody getRequestBody() {
        Map<String, String> podMap = null;

        Map<String, Object> map = new HashMap<>();
        map.put("to", account.id);
        map.put("amount", getPaymentPrice());

        if (orderType > -1) {
            //map.put("pre_order", true);
            map.put("order_type", orderType);
        }

        map.put("credit", isCredit);

        return PostRequest.getRequestBody(map);
    }
}
