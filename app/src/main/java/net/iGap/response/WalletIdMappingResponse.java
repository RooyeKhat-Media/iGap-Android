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

import android.util.Log;

import net.iGap.helper.HelperPublicMethod;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoWalletIdMapping;

import org.paygear.wallet.RaadApp;

public class WalletIdMappingResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean isDeleted = false;

    public WalletIdMappingResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoWalletIdMapping.WalletIdMappingResponse.Builder builder = (ProtoWalletIdMapping.WalletIdMappingResponse.Builder) message;
        builder.getUserId();

        HelperPublicMethod.goToChatRoom(builder.getUserId(), new HelperPublicMethod.OnComplete() {
            @Override
            public void complete() {

                if (RaadApp.paygearHistoryCloseWallet != null)
                    RaadApp.paygearHistoryCloseWallet.closeWallet();

            }
        }, new HelperPublicMethod.OnError() {
            @Override
            public void error() {

            }
        });
        Log.i("CCCCCCCCC", "4 handler:+ " + builder.getUserId());
    }

    @Override
    public void timeOut() {
        super.timeOut();
        Log.i("CCCCCCCCC", "6 majorCode:+ ");
        if (RaadApp.paygearHistoryCloseWallet != null) RaadApp.paygearHistoryCloseWallet.error();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("CCCCCCCCC", "5 majorCode:+ " + majorCode);
        if (RaadApp.paygearHistoryCloseWallet != null) RaadApp.paygearHistoryCloseWallet.error();
    }
}


