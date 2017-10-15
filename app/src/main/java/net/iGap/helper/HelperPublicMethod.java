/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.helper;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import io.realm.Realm;
import net.iGap.G;
import net.iGap.activities.ActivityMain;
import net.iGap.interfaces.OnChatGetRoom;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestUserInfo;


public class HelperPublicMethod {

    public interface OnComplete {
        void complete();
    }

    public interface OnError {
        void error();
    }

    //**************************************************************************************************************************************

    public static void goToChatRoom(final long peerId, final OnComplete onComplete, final OnError onError) {

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {

            if (onComplete != null) {
                onComplete.complete();
            }

            goToRoom(realmRoom.getId(), -1);
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override public void onChatGetRoom(final long roomId) {

                    if (onError != null) {
                        onError.error();
                    }

                    getUserInfo(peerId, roomId, onComplete, onError);

                    G.onChatGetRoom = null;
                }

                @Override public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                }

                @Override public void onChatGetRoomTimeOut() {

                    if (onError != null) {
                        onError.error();
                    }
                }

                @Override public void onChatGetRoomError(int majorCode, int minorCode) {

                    if (onError != null) {
                        onError.error();
                    }
                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    private static void getUserInfo(final long peerId, final long roomId, final OnComplete onComplete, final OnError onError) {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override public void run() {

                        if (user.getId() == peerId) {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override public void execute(Realm realm) {
                                    RealmRegisteredInfo realmRegisteredInfo = RealmRegisteredInfo.getRegistrationInfo(realm, user.getId());
                                    if (realmRegisteredInfo == null) {
                                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                                        realmRegisteredInfo.setId(user.getId());
                                        realmRegisteredInfo.setDoNotshowSpamBar(false);
                                    }

                                    RealmAvatar.putAndGet(realm, user.getId(), user.getAvatar());
                                    realmRegisteredInfo.setUsername(user.getUsername());
                                    realmRegisteredInfo.setPhoneNumber(Long.toString(user.getPhone()));
                                    realmRegisteredInfo.setFirstName(user.getFirstName());
                                    realmRegisteredInfo.setLastName(user.getLastName());
                                    realmRegisteredInfo.setDisplayName(user.getDisplayName());
                                    realmRegisteredInfo.setInitials(user.getInitials());
                                    realmRegisteredInfo.setColor(user.getColor());
                                    realmRegisteredInfo.setStatus(user.getStatus().toString());
                                    realmRegisteredInfo.setAvatarCount(user.getAvatarCount());
                                    realmRegisteredInfo.setMutual(user.getMutual());
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override public void onSuccess() {
                                    try {

                                        if (onComplete != null) {
                                            onComplete.complete();
                                        }

                                        goToRoom(roomId, peerId);

                                        G.onUserInfoResponse = null;

                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            realm.close();
                        }
                    }
                });
            }

            @Override public void onUserInfoTimeOut() {

                if (onError != null) {
                    onError.error();
                }
            }

            @Override public void onUserInfoError(int majorCode, int minorCode) {

                if (onError != null) {
                    onError.error();
                }
            }
        };

        new RequestUserInfo().userInfo(peerId);
    }

    private static void goToRoom(long roomid, long peerId) {

        Intent intent = new Intent(G.context, ActivityMain.class);
        intent.putExtra(ActivityMain.openChat, roomid);
        if (peerId >= 0) {
            intent.putExtra("PeerID", peerId);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        G.context.startActivity(intent);
    }

    //**************************************************************************************************************************************
}
