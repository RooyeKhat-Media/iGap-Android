/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.webrtc;

import android.util.Log;
import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentCall;
import net.iGap.helper.HelperError;
import net.iGap.interfaces.ISignalingAccept;
import net.iGap.interfaces.ISignalingCandidate;
import net.iGap.interfaces.ISignalingErrore;
import net.iGap.interfaces.ISignalingLeave;
import net.iGap.interfaces.ISignalingOffer;
import net.iGap.interfaces.ISignalingRinging;
import net.iGap.interfaces.ISignalingSessionHold;
import net.iGap.module.enums.CallState;
import net.iGap.proto.ProtoSignalingLeave;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.request.RequestSignalingRinging;
import org.webrtc.IceCandidate;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import static net.iGap.G.iSignalingAccept;
import static net.iGap.G.iSignalingCandidate;
import static net.iGap.G.iSignalingLeave;
import static net.iGap.G.iSignalingOffer;
import static net.iGap.G.iSignalingRinging;
import static net.iGap.G.iSignalingSessionHold;
import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;

public class CallObserver implements ISignalingOffer, ISignalingErrore, ISignalingRinging, ISignalingAccept, ISignalingCandidate, ISignalingLeave, ISignalingSessionHold {

    public CallObserver() {
        iSignalingOffer = this;
        iSignalingRinging = this;
        iSignalingAccept = this;
        iSignalingCandidate = this;
        iSignalingLeave = this;
        iSignalingSessionHold = this;
        G.iSignalingErrore = this;
    }

    @Override
    public void onOffer(final long called_userId, ProtoSignalingOffer.SignalingOffer.Type type, final String callerSdp) {
        new RequestSignalingRinging().signalingRinging();

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (new WebRTC().peerConnectionInstance() != null) {
                    new WebRTC().peerConnectionInstance().setRemoteDescription(new SdpObserver() {
                        @Override
                        public void onCreateSuccess(SessionDescription sessionDescription) {

                        }

                        @Override
                        public void onSetSuccess() {
                            FragmentCall.call(called_userId, true);
                        }

                        @Override
                        public void onCreateFailure(String s) {
                            Log.i("WWW", "onOffer onCreateFailure : " + s);
                        }

                        @Override
                        public void onSetFailure(String s) {
                            Log.i("WWW", "onOffer onSetFailure : " + s);
                        }
                    }, new SessionDescription(OFFER, callerSdp));
                }
            }
        });
    }

    @Override
    public void onAccept(final String called_sdp) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                new WebRTC().setOfferLocalDescription();

                new WebRTC().peerConnectionInstance().setRemoteDescription(new SdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription sessionDescription) {

                    }

                    @Override
                    public void onSetSuccess() {
                        Log.i("WWW", "onSetSuccess");
                    }

                    @Override
                    public void onCreateFailure(String s) {
                        Log.i("WWW", "onAccept onCreateFailure : " + s);
                    }

                    @Override
                    public void onSetFailure(String s) {
                        Log.i("WWW", "onAccept onSetFailure : " + s);
                    }
                }, new SessionDescription(ANSWER, called_sdp));
            }
        });
    }

    @Override
    public void onCandidate(final String peerSdpMId, final int peerSdpMLineIndex, final String peerCandidate) {
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("WWW_Candidate", "onCandidate server : " + peerCandidate);
                new WebRTC().peerConnectionInstance().addIceCandidate(new IceCandidate(peerSdpMId, peerSdpMLineIndex, peerCandidate));
            }
        });
    }


    @Override
    public void onLeave(final ProtoSignalingLeave.SignalingLeaveResponse.Type type) {
        new WebRTC().close();
        new WebRTC().dispose();
        /**
         * set peer connection null for try again
         */
        new WebRTC().clearConnection();

        if (G.iSignalingCallBack != null) {

            switch (type) {

                case REJECTED:
                    G.iSignalingCallBack.onStatusChanged(CallState.REJECT);
                    break;
                case NOT_ANSWERED:
                    G.iSignalingCallBack.onStatusChanged(CallState.NOT_ANSWERED);
                    break;
                case UNAVAILABLE:
                    G.iSignalingCallBack.onStatusChanged(CallState.UNAVAILABLE);
                    break;
                case TOO_LONG:
                    G.iSignalingCallBack.onStatusChanged(CallState.TOO_LONG);
                    break;
            }
        }

        if (G.onCallLeaveView != null) {
            G.onCallLeaveView.onLeaveView("");
        }
    }

    @Override
    public void onRinging() {
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.iSignalingCallBack != null) {
                    G.iSignalingCallBack.onStatusChanged(CallState.RINGING);
                }
            }
        }, 1000);
    }

    @Override
    public void onHold(Boolean hold) {

    }

    @Override
    public void onErrore(int major, int minor) {

        String message = G.context.getString(R.string.e_call_permision);

        switch (major) {

            case 904:

                if (minor == 6) {
                    message = G.context.getString(R.string.e_904_6);
                    if (G.iSignalingCallBack != null) {
                        G.iSignalingCallBack.onStatusChanged(CallState.UNAVAILABLE);
                    }
                } else if (minor == 7) {
                    message = G.context.getString(R.string.e_904_7);
                    if (G.iSignalingCallBack != null) {
                        G.iSignalingCallBack.onStatusChanged(CallState.UNAVAILABLE);
                    }
                } else if (minor == 8) {
                    message = G.context.getString(R.string.e_904_8);
                    if (G.iSignalingCallBack != null) {
                        G.iSignalingCallBack.onStatusChanged(CallState.UNAVAILABLE);
                    }
                } else if (minor == 9) {
                    message = G.context.getString(R.string.e_904_9);
                    if (G.iSignalingCallBack != null) {
                        G.iSignalingCallBack.onStatusChanged(CallState.BUSY);
                    }
                } else {
                    if (G.iSignalingCallBack != null) {
                        G.iSignalingCallBack.onStatusChanged(CallState.UNAVAILABLE);
                    }
                }
                break;
            case 905:
            case 906:
                message = G.context.getString(R.string.e_906_1);
                break;

        }

        if (G.onCallLeaveView != null) {
            G.onCallLeaveView.onLeaveView("error");
        }

        final String finalMessage = message;
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HelperError.showSnackMessage(finalMessage, false);
            }
        }, 2500);


    }
}
