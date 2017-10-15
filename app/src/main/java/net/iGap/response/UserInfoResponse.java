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

import android.os.Handler;
import android.os.Looper;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.adapter.items.chat.AbstractMessage;
import net.iGap.fragments.FragmentChat;
import net.iGap.fragments.FragmentShowMember;
import net.iGap.helper.HelperLogMessage;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserInfo;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.request.RequestUserInfo;

import static net.iGap.G.userId;

public class UserInfoResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean canGetInfo = false;

    public UserInfoResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoUserInfo.UserInfoResponse.Builder builder = (ProtoUserInfo.UserInfoResponse.Builder) message;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                final Realm realm = Realm.getDefaultInstance();
                RealmRegisteredInfo realmRegisteredInfo;
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, builder.getUser().getId());
                        if (realmRegisteredInfo == null) {
                            realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class, builder.getUser().getId());
                        }

                        realmRegisteredInfo.setAvatarCount(builder.getUser().getAvatarCount());
                        realmRegisteredInfo.setColor(builder.getUser().getColor());
                        realmRegisteredInfo.setDisplayName(builder.getUser().getDisplayName());
                        realmRegisteredInfo.setFirstName(builder.getUser().getFirstName());
                        realmRegisteredInfo.setInitials(builder.getUser().getInitials());
                        realmRegisteredInfo.setLastSeen(builder.getUser().getLastSeen());
                        realmRegisteredInfo.setPhoneNumber(Long.toString(builder.getUser().getPhone()));
                        realmRegisteredInfo.setStatus(builder.getUser().getStatus().toString());
                        realmRegisteredInfo.setUsername(builder.getUser().getUsername());
                        realmRegisteredInfo.setMutual(builder.getUser().getMutual());
                        realmRegisteredInfo.setCacheId(builder.getUser().getCacheId());

                        RealmAvatar.putAndGet(realm, builder.getUser().getId(), builder.getUser().getAvatar());
                    }
                });

                G.handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RequestUserInfo.userIdArrayList.remove(String.valueOf(builder.getUser().getId()));
                    }
                }, RequestUserInfo.CLEAR_ARRAY_TIME);

                if (identity != null && identity.equals(RequestUserInfo.InfoType.JUST_INFO.toString())) {
                    G.onRegistrationInfo.onInfo(builder.getUser());
                    return;
                }

                if ((builder.getUser().getId() == userId)) {
                    canGetInfo = true;
                }

                realm.close();

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (canGetInfo) {
                            if (G.onUserInfoMyClient != null) {
                                G.onUserInfoMyClient.onUserInfoMyClient(builder.getUser(), identity);
                            }
                        }

                        if (G.onUserUpdateStatus != null) {
                            G.onUserUpdateStatus.onUserUpdateStatus(builder.getUser().getId(), builder.getUser().getLastSeen(), builder.getUser().getStatus().toString());
                        }

                        if (G.onUserInfoResponse != null) {
                            G.onUserInfoResponse.onUserInfo(builder.getUser(), identity);
                        }

                        if (G.onUserInfoForAvatar != null) {
                            G.onUserInfoForAvatar.onUserInfoForAvatar(builder.getUser());
                        }
                        if (FragmentShowMember.infoUpdateListenerCount != null) {
                            FragmentShowMember.infoUpdateListenerCount.complete(true, "" + builder.getUser().getId(), "OK");
                        }

                        // update chat message header forward after get user or room info
                        if (AbstractMessage.updateForwardInfo != null) {
                            long _id = builder.getUser().getId();
                            if (AbstractMessage.updateForwardInfo.containsKey(_id)) {
                                String messageId = AbstractMessage.updateForwardInfo.get(_id);
                                AbstractMessage.updateForwardInfo.remove(_id);
                                if (FragmentChat.onUpdateUserOrRoomInfo != null) {
                                    FragmentChat.onUpdateUserOrRoomInfo.onUpdateUserOrRoomInfo(messageId);
                                }
                            }
                        }
                    }
                });

                // update log message in realm room message after get user info
                if (G.logMessageUpdatList.containsKey(builder.getUser().getId())) {
                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            HelperLogMessage.updateLogMessageAfterGetUserInfo(builder.getUser().getId());
                        }
                    }, 500);
                }
            }
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onUserInfoResponse.onUserInfoTimeOut();
    }

    @Override
    public void error() {
        super.error();
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (identity != null) {
                    RequestUserInfo.userIdArrayList.remove(identity);
                } else {
                    RequestUserInfo.userIdArrayList.clear();
                }
            }
        }, RequestUserInfo.CLEAR_ARRAY_TIME);

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onUserInfoResponse.onUserInfoError(majorCode, minorCode);
        if (FragmentShowMember.infoUpdateListenerCount != null) {
            FragmentShowMember.infoUpdateListenerCount.complete(true, "", "ERROR");
        }
        if (FragmentShowMember.infoUpdateListenerCount != null) {
            FragmentShowMember.infoUpdateListenerCount.complete(true, "", "");
        }
    }
}


