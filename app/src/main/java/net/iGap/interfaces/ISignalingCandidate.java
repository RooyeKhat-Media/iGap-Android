package net.iGap.interfaces;

/**
 * Created by android3 on 5/8/2017.
 */

public interface ISignalingCandidate {

    void onCandidate(String peerSdpMId, int peerSdpMLineIndex, String peerCandidate);
}
