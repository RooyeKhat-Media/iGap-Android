package net.iGap.viewmodel;
/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
*/

import android.content.Intent;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentWalletAgrementBinding;
import net.iGap.helper.HelperError;
import net.iGap.request.RequestWalletRegister;

import org.paygear.wallet.WalletActivity;

public class FragmentWalletAgreementViewModel {


    public ObservableField<String> callbackTxtAgreement = new ObservableField<>(G.context.getResources().getString(R.string.loading_wallet_agreement));
    private FragmentWalletAgrementBinding fragmentWalletAgrementBinding;
    private String phone;

    public FragmentWalletAgreementViewModel(FragmentWalletAgrementBinding fragmentWalletAgrementBinding, String mPhone) {
        this.fragmentWalletAgrementBinding = fragmentWalletAgrementBinding;
        phone = mPhone;
    }

    public void checkBoxAgreement(View v, boolean checked) {
        if (checked) {
            new MaterialDialog.Builder(G.fragmentActivity).title(R.string.accept_the_terms).
                    content(R.string.are_you_sure)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (G.userLogin) {
                                new RequestWalletRegister().walletRegister();
                                Intent intent = new Intent(G.currentActivity, WalletActivity.class);
                                intent.putExtra("Language", "fa");
                                intent.putExtra("Mobile", "0" + phone);
                                intent.putExtra("PrimaryColor", G.appBarColor);
                                intent.putExtra("DarkPrimaryColor", G.appBarColor);
                                intent.putExtra("AccentColor", G.appBarColor);
                                intent.putExtra("IS_DARK_THEME", G.isDarkTheme);
                                intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
                                intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                                intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
                                intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme_2);
                                intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                                intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                                (G.currentActivity).startActivity(intent);

                                G.fragmentActivity.onBackPressed();
                            } else {
                                HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server), false);
                            }

                        }
                    }).show();
        }
    }

}