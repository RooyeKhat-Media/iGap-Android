package org.paygear.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.paygear.wallet.databinding.ActivityPaymentBinding;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Order;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.model.PaymentResult;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;

import java.util.ArrayList;

import ir.radsense.raadcore.Raad;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.WebBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    ActivityPaymentBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Raad.init(getApplicationContext());
//        Utils.setLocale(getApplicationContext(), "fa");
//        Auth auth = new Auth("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1laWQiOiI2MTQiLCJ1bmlxdWVfbmFtZSI6InVua25vd24oNjE0KSIsInN1YiI6InVua25vd24oNjE0KSIsInJvbGUiOlsiemV1cyJdLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0LyIsImF1ZCI6ImI5ZGM3MTJjOTUyYjRhYWZiNDgxYWJlZGUwZmVjNGQ4IiwiZXhwIjoxNTM3NjMyNTMzLCJuYmYiOjE1Mjk4NTY1MzMsImlkIjoiNWFhZTRlMzEzZWZjZDYwMDBiZDc5ZDM4IiwidXNlcm5hbWUiOiJhbGkubWFuZGVnYXJpIiwibWVyY2hhbnRfcm9sZXMiOnsiNWIyZTQzOTA0NTU5NjMwMDA1YmUyMmU5IjpbImFkbWluIl0sIjViMmU1ZWJiM2ZhMzE2MDAwY2YxMjc5ZiI6WyJjYXNoaWVyIl19LCJhcHAiOiI1OWJlYzNmYTBlY2E4MTAwMDFjZWViODYiLCJzdmMiOnsiYWNjb3VudCI6eyJwZXJtIjowfSwiY2FzaGllciI6eyJwZXJtIjowfSwiY2x1YiI6eyJwZXJtIjowfSwiY291cG9uIjp7InBlcm0iOjB9LCJjcmVkaXQiOnsicGVybSI6MH0sImRlbGl2ZXJ5Ijp7InBlcm0iOjB9LCJldmVudCI6eyJwZXJtIjowfSwiZmlsZSI6eyJwZXJtIjowfSwiZ2FtaWZpY2F0aW9uIjp7InBlcm0iOjB9LCJnZW8iOnsicGVybSI6MH0sIm1lc3NhZ2luZyI6eyJwZXJtIjowfSwicGF5bWVudCI6eyJwZXJtIjowfSwicHJvZHVjdCI6eyJwZXJtIjowfSwicHVzaCI6eyJwZXJtIjowfSwicXIiOnsicGVybSI6MH0sInNlYXJjaCI6eyJwZXJtIjowfSwic29jaWFsIjp7InBlcm0iOjB9LCJzeW5jIjp7InBlcm0iOjB9LCJ0cmFuc3BvcnQiOnsicGVybSI6MH19fQ.DEtbQ-H07u5ko91ZW5cU2d0GqX34OvotR4aXvuelNk4", "bearer");
//        if (auth.getJWT() == null) {
//            return;
//        }
//        auth.save();
//        WebBase.apiKey = "5aa7e856ae7fbc00016ac5a01c65909797d94a16a279f46a4abb5faa";
//
//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
//
//        Payment payment = new Payment();
//        Account account = new Account();
//        account.id = "5aae4e313efcd6000bd79d38";
//        payment.account = account;
//        payment.price = 1000;
//        payment.orderType = Order.ORDER_TYPE_P2P;
//        payment.isCredit = false;
//
//        Web.getInstance().getWebService().getCredit("5aae4e313efcd6000bd79d38").enqueue(new Callback<ArrayList<Card>>() {
//            @Override
//            public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
//                Card card=response.body().get(0);
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
//
//            }
//        });
//
//
//        Intent intent = new Intent(PaymentActivity.this, WalletActivity.class);
//        intent.putExtra("Language", "fa");
//        intent.putExtra("Mobile", "0" + "9332399949");
////                        intent.putExtra("IsP2P",true);
////                        intent.putExtra("Payment",payment);
//        intent.putExtra("PrimaryColor", "#f69228");
//        intent.putExtra("DarkPrimaryColor", "#f9cbff");
//        intent.putExtra("AccentColor", "#123f2e");
//        startActivityForResult(intent, 66);
//
////        initPay(payment);
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 66) {
//            if (resultCode == RESULT_OK) {
//                PaymentResult paymentResult = (PaymentResult) data.getSerializableExtra("result");
//                if (paymentResult != null)
//                    Toast.makeText(this, "trace number:"+String.valueOf(paymentResult.traceNumber) + "amount :"+ String.valueOf(paymentResult.amount), Toast.LENGTH_SHORT).show();
//                else {
//                    Toast.makeText(this, "ناموفق", Toast.LENGTH_SHORT).show();
//                }
//            }else {
//                Toast.makeText(this, "payment is canceled", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void initPay(final Payment payment) {
//        Web.getInstance().getWebService().initPayment(payment.getRequestBody()).enqueue(new Callback<PaymentAuth>() {
//            @Override
//            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
//                if (response != null)
//                    if (response.isSuccessful()) {
//                        payment.paymentAuth = response.body();
//
//                        Intent intent = new Intent(PaymentActivity.this, WalletActivity.class);
//                        intent.putExtra("Language", "fa");
//                        intent.putExtra("Mobile", "0" + "9332399949");
////                        intent.putExtra("IsP2P",true);
////                        intent.putExtra("Payment",payment);
//                        intent.putExtra("PrimaryColor", "#fad20d");
//                        intent.putExtra("DarkPrimaryColor", "#f9cbff");
//                        intent.putExtra("AccentColor", "#123f2e");
//                        startActivityForResult(intent, 66);
//
//                    }
//
//            }
//
//            @Override
//            public void onFailure(Call<PaymentAuth> call, Throwable t) {
//                Toast.makeText(PaymentActivity.this, "initPay failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
    }

}

