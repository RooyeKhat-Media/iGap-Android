/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/


//public static ISignalingOffer iSignalingOffer;
//public static ISignalingRinging iSignalingRinging;
//public static ISignalingAccept iSignalingAccept;
//public static ISignalingCandidate iSignalingCandidate;
//public static ISignalingLeave iSignalingLeave;
//public static ISignalingSessionHold iSignalingSessionHold;
//public static ISignalingGetCallLog iSignalingGetCallLog;

package net.iGap.webrtc;

import android.util.Log;

import net.iGap.G;
import net.iGap.module.enums.CallState;
import net.iGap.request.RequestSignalingCandidate;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoTrack;

import static org.webrtc.PeerConnection.IceConnectionState.CHECKING;
import static org.webrtc.PeerConnection.IceConnectionState.CLOSED;
import static org.webrtc.PeerConnection.IceConnectionState.COMPLETED;
import static org.webrtc.PeerConnection.IceConnectionState.CONNECTED;
import static org.webrtc.PeerConnection.IceConnectionState.DISCONNECTED;
import static org.webrtc.PeerConnection.IceConnectionState.FAILED;

public class PeerConnectionObserver implements PeerConnection.Observer, VideoRenderer.Callbacks {


    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.i("WWW", "onSignalingChange : " + signalingState);
    }

    @Override
    public void onIceConnectionChange(final PeerConnection.IceConnectionState iceConnectionState) {
        Log.i("WWW", "onIceConnectionChange : " + iceConnectionState);
        if (G.iSignalingCallBack != null) {
            if (iceConnectionState == CLOSED || iceConnectionState == DISCONNECTED) {
                G.iSignalingCallBack.onStatusChanged(CallState.DISCONNECTED);
            } else if (iceConnectionState == FAILED) {
                G.iSignalingCallBack.onStatusChanged(CallState.FAILD);
            } else if (iceConnectionState == CHECKING) {
                G.iSignalingCallBack.onStatusChanged(CallState.CONNECTING);
            } else if (iceConnectionState == CONNECTED || iceConnectionState == COMPLETED) {
                G.iSignalingCallBack.onStatusChanged(CallState.CONNECTED);
            }
        }
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.i("WWW", "onIceConnectionReceivingChange : " + b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.i("WWW", "onIceGatheringChange : " + iceGatheringState);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.i("WWW", "WebRtc onIceCandidate : " + iceCandidate.toString());
        new RequestSignalingCandidate().signalingCandidate(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp);

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {

    }


    @Override
    public void onAddStream(MediaStream stream) {
        for (AudioTrack audioTrack : stream.audioTracks) {
            audioTrack.setEnabled(true);
        }

        if (stream.videoTracks != null && stream.videoTracks.size() == 1) {
            VideoTrack videoTrack = stream.videoTracks.getFirst();
            videoTrack.setEnabled(true);
            videoTrack.addRenderer(new VideoRenderer(this));
        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.i("WWW", "onRemoveStream : " + mediaStream);
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.i("WWW", "onDataChannel : " + dataChannel);
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.i("WWW", "onRenegotiationNeeded");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {

    }

    @Override
    public void renderFrame(VideoRenderer.I420Frame i420Frame) {
        Log.i("WWW", "renderFrame : " + i420Frame);
    }

}
