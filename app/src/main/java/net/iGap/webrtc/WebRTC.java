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


import android.os.Build;
import android.util.Log;

import net.iGap.G;
import net.iGap.proto.ProtoSignalingGetConfiguration;
import net.iGap.proto.ProtoSignalingOffer;
import net.iGap.realm.RealmCallConfig;
import net.iGap.request.RequestSignalingAccept;
import net.iGap.request.RequestSignalingLeave;
import net.iGap.request.RequestSignalingOffer;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.voiceengine.WebRtcAudioManager;
import org.webrtc.voiceengine.WebRtcAudioUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import io.realm.Realm;

public class WebRTC {
    private static PeerConnection peerConnection;
    private static PeerConnectionFactory peerConnectionFactory;
    private static MediaStream mediaStream;
    private MediaConstraints mediaConstraints;
    private MediaConstraints audioConstraints;
    private AudioTrack audioTrack;
    private AudioSource audioSource;
    private static String offerSdp;

    public WebRTC() {
        peerConnectionInstance();
    }

    private void addAudioTrack(MediaStream mediaStream) {
        audioSource = peerConnectionFactoryInstance().createAudioSource(audioConstraintsGetInstance());
        audioTrack = peerConnectionFactoryInstance().createAudioTrack("ARDAMSa0", audioSource);
        audioTrack.setEnabled(true);
        mediaStream.addTrack(audioTrack);
    }

    /**
     * First, we initiate the PeerConnectionFactory with our application context and some options.
     */
    private PeerConnectionFactory peerConnectionFactoryInstance() {
        if (peerConnectionFactory == null) {

            Set<String> HARDWARE_AEC_WHITELIST = new HashSet<String>() {{
                add("D5803");
                add("FP1");
                add("SM-A500FU");
                add("XT1092");
            }};

            Set<String> OPEN_SL_ES_WHITELIST = new HashSet<String>() {{
            }};

            if (Build.VERSION.SDK_INT >= 11) {
                if (HARDWARE_AEC_WHITELIST.contains(Build.MODEL)) {
                    WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
                } else {
                    WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
                }

                if (OPEN_SL_ES_WHITELIST.contains(Build.MODEL)) {
                    WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);
                } else {
                    WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true);
                }
            }

            PeerConnectionFactory.initializeAndroidGlobals(G.context,  // Context
                    true,  // Audio Enabled
                    false,  // Video Enabled
                    true); // Hardware Acceleration Enabled

            peerConnectionFactory = new PeerConnectionFactory();
        }
        return peerConnectionFactory;
    }

    PeerConnection peerConnectionInstance() {
        if (peerConnection == null) {
            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();
            for (ProtoSignalingGetConfiguration.SignalingGetConfigurationResponse.IceServer ice : realmCallConfig.getIceServer()) {
                iceServers.add(new PeerConnection.IceServer(ice.getUrl(), ice.getUsername(), ice.getCredential()));
            }
            realm.close();

            PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);
            configuration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
            configuration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
            configuration.iceTransportsType = PeerConnection.IceTransportsType.RELAY;
            peerConnection = peerConnectionFactoryInstance().createPeerConnection(iceServers, mediaConstraintsGetInstance(), new PeerConnectionObserver());

            mediaStream = peerConnectionFactoryInstance().createLocalMediaStream("ARDAMS");
            addAudioTrack(mediaStream);
            peerConnection.addStream(mediaStream);
        }

        return peerConnection;
    }

    public static void muteSound() {

        if (mediaStream == null) {
            return;
        }

        for (AudioTrack audioTrack : mediaStream.audioTracks) {
            audioTrack.setEnabled(false);
        }
    }

    public static void unMuteSound() {

        if (mediaStream == null) {
            return;
        }

        for (AudioTrack audioTrack : mediaStream.audioTracks) {
            audioTrack.setEnabled(true);
        }
    }


    public void createOffer(final long userIdCallee) {
        peerConnectionInstance().createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                offerSdp = sessionDescription.description;
                new RequestSignalingOffer().signalingOffer(userIdCallee, ProtoSignalingOffer.SignalingOffer.Type.VOICE_CALLING, sessionDescription.description);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraintsGetInstance());
    }

    public void setOfferLocalDescription() {
        setLocalDescription(SessionDescription.Type.OFFER, offerSdp);
    }

    public void createAnswer() {

        peerConnectionInstance().createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                setLocalDescription(SessionDescription.Type.ANSWER, sessionDescription.description);
                Log.i("WWW", "onCreateSuccess sessionDescription.description : " + sessionDescription.description);
                Log.i("WWW", "onCreateSuccess sessionDescription.type : " + sessionDescription.type);
                acceptCall(sessionDescription.description);
            }

            @Override
            public void onSetSuccess() {

            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, mediaConstraintsGetInstance());
    }

    private void setLocalDescription(final SessionDescription.Type type, String sdp) {
        peerConnectionInstance().setLocalDescription(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
            }

            @Override
            public void onSetSuccess() {
                Log.i("WWW", "onSetSuccess");
            }

            @Override
            public void onCreateFailure(String s) {

            }

            @Override
            public void onSetFailure(String s) {

            }
        }, new SessionDescription(type, sdp));
    }

    private MediaConstraints mediaConstraintsGetInstance() {
        if (mediaConstraints == null) {
            mediaConstraints = new MediaConstraints();
            mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return mediaConstraints;
    }

    private MediaConstraints audioConstraintsGetInstance() {
        if (audioConstraints == null) {
            audioConstraints = new MediaConstraints();
            audioConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
        return audioConstraints;
    }

    private void acceptCall(String sdp) {
        new RequestSignalingAccept().signalingAccept(sdp);
    }

    public void leaveCall() {
        //don't need for close/dispose here, this action will be doing in onLeave callback
        //close();
        //dispose();
        /**
         * set peer connection null for try again
         */
        //clearConnection();
        new RequestSignalingLeave().signalingLeave();
    }

    public void close() {
        peerConnectionInstance().close();
    }

    void dispose() {
        try {
            peerConnectionInstance().dispose();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    void clearConnection() {
        peerConnection = null;
    }
}