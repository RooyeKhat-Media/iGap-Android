package net.iGap.module;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperLogout;
import net.iGap.interfaces.OnContactFetchForServer;
import net.iGap.interfaces.OnSecuring;
import net.iGap.interfaces.OnUserInfoResponse;
import net.iGap.interfaces.OnUserLogin;
import net.iGap.module.structs.StructListOfContact;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserUpdateStatus;
import net.iGap.realm.RealmClientCondition;
import net.iGap.realm.RealmPhoneContacts;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestGeoGetRegisterStatus;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestUserContactsGetBlockedList;
import net.iGap.request.RequestUserContactsGetList;
import net.iGap.request.RequestUserInfo;
import net.iGap.request.RequestUserLogin;
import net.iGap.request.RequestUserUpdateStatus;
import net.iGap.request.RequestWrapper;

import java.util.ArrayList;

import io.realm.Realm;

import static net.iGap.G.firstEnter;
import static net.iGap.G.firstTimeEnterToApp;
import static net.iGap.G.isAppInFg;

/**
 * all actions that need doing after login
 */
public class LoginActions extends Application {

    public LoginActions() {
        initSecureInterface();
    }

    /**
     * try login to server and do common actions
     */
    public static void login() {

        G.onUserLogin = new OnUserLogin() {
            @Override
            public void onLogin() {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.clientConditionGlobal = RealmClientCondition.computeClientCondition(null);
                        /**
                         * in first enter to app client send clientCondition after get room list
                         * but, in another login when user not closed app after login client send
                         * latest state to server without get room list , also if
                         * app in background is running now if firstTimeEnterToApp is false we send
                         * client condition because this field(firstTimeEnterToApp) will be lost value
                         * in close app and after app start running in background we don't send
                         * client condition!!! for avoid from this problem we checked isAppInFg state
                         * app is background send clientCondition (: .
                         */
                        if (!firstTimeEnterToApp || !isAppInFg) {
                            new RequestClientGetRoomList().clientGetRoomList(0, Config.LIMIT_LOAD_ROOM, "0");
                        }

                        if (firstEnter) {
                            firstEnter = false;
                            new RequestUserContactsGetBlockedList().userContactsGetBlockedList();
                            importContact();
                        }


                        getUserInfo();
                        if (G.isAppInFg) {
                            new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE);
                        } else {
                            new RequestUserUpdateStatus().userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status.OFFLINE);
                        }

                        new RequestGeoGetRegisterStatus().getRegisterStatus();
                        //sendWaitingRequestWrappers();

                        HelperCheckInternetConnection.detectConnectionTypeForDownload();
                    }
                });


            }

            @Override
            public void onLoginError(int majorCode, int minorCode) {

            }
        };

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (G.isSecure) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
                    if (!G.userLogin && userInfo != null && userInfo.getUserRegistrationState()) {
                        new RequestUserLogin().userLogin(userInfo.getToken());
                    }
                    realm.close();
                } else {
                    login();
                }
            }
        }, 500);
    }

    private static void getUserInfo() {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        if (realmUserInfo == null) {
            HelperLogout.logout();
            return;
        }
        final long userId = realmUserInfo.getUserId();
        realm.close();

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {
                // fill own user info
                if (userId == user.getId()) {
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmRegisteredInfo.putOrUpdate(realm, user);
                        }
                    });

                    realm.close();
                }
            }

            @Override
            public void onUserInfoTimeOut() {

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {

            }
        };
        new RequestUserInfo().userInfo(userId);
    }

    public static void importContact() {
        /**
         * just import contact in each enter to app
         * when user login was done
         */
        //if (isSendContact) {
        //    return;
        //}
        Contacts.onlinePhoneContactId = 0;
        G.onContactFetchForServer = new OnContactFetchForServer() {
            @Override
            public void onFetch(ArrayList<StructListOfContact> contacts, boolean getContactList) {
                RealmPhoneContacts.sendContactList(contacts, false, getContactList);
            }
        };

        if (G.userLogin) {
            /**
             * this can be go in the activity for check permission in api 6+
             */

            try {
                if (ContextCompat.checkSelfPermission(G.context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new Contacts.FetchContactForServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                } else {
                    new RequestUserContactsGetList().userContactGetList();
                }
                G.isSendContact = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    importContact();
                }
            }, 2000);
        }
    }

    /**
     * resend some of requests
     */
    public static void sendWaitingRequestWrappers() {
        for (RequestWrapper requestWrapper : RequestQueue.WAITING_REQUEST_WRAPPERS) {
            RequestQueue.RUNNING_REQUEST_WRAPPERS.add(requestWrapper);
        }
        RequestQueue.WAITING_REQUEST_WRAPPERS.clear();

        for (int i = 0; i < RequestQueue.RUNNING_REQUEST_WRAPPERS.size(); i++) {
            final int j = i;
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        RequestQueue.sendRequest(RequestQueue.RUNNING_REQUEST_WRAPPERS.get(j));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (j == (RequestQueue.RUNNING_REQUEST_WRAPPERS.size() - 1)) {
                        RequestQueue.RUNNING_REQUEST_WRAPPERS.clear();
                    }
                }
            }, 1000 * j);
        }
    }

    /**
     * initialize securing interface for detecting
     * securing is done and continue login actions
     */
    private void initSecureInterface() {
        G.onSecuring = new OnSecuring() {
            @Override
            public void onSecure() {
                login();
            }
        };
    }
}