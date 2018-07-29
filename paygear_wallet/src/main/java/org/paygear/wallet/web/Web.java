package org.paygear.wallet.web;

import org.paygear.wallet.WalletActivity;

import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.web.WebBase;
import okhttp3.Request;

/**
 * Created by Software1 on 8/12/2017.
 */

public class Web extends WebBase<WebService> {

    //public static final String API_KEY = "598ed92d3a4d0e000138b063db918de0bd1a40c073ce8ff4c150aa47";
    public static final String API_KEY = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
    public static final String APP_ID = "59bec3fa0eca810001ceeb86";
    public static String token;
    private static Web mInstance;

    private Web(Class<WebService> webInterfaceType) {
        super(webInterfaceType);
    }

    public synchronized static Web getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        return new Web(WebService.class);
    }

    public void release() {
        mInstance = null;
    }


    @Override
    protected void onSetHeaders(Request.Builder requestBuilder) {
        super.onSetHeaders(requestBuilder);
        requestBuilder.addHeader("Accept-Language", Raad.language);
    }
}
