/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.G;
import net.iGap.fragments.FragmentWallet;
import net.iGap.helper.HelperFragment;
import net.iGap.proto.ProtoWalletRegister;
import net.iGap.request.RequestWalletGetAccessToken;

public class WalletRegisterResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public WalletRegisterResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
        new RequestWalletGetAccessToken().walletGetAccessToken();
    }

    @Override
    public void handler() {
        super.handler();
        ProtoWalletRegister.WalletRegisterResponse.Builder builder = (ProtoWalletRegister.WalletRegisterResponse.Builder) message;
        G.isWalletRegister = true;
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}