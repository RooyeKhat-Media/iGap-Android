package net.iGap.interfaces;

import net.iGap.proto.ProtoClientCheckInviteLink;
import net.iGap.proto.ProtoClientGetPromote;

public interface Ipromote {
    public void onGetPromoteResponse(ProtoClientGetPromote.ClientGetPromoteResponse.Builder builder);

}
