package net.iGap.libs.notification;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import net.iGap.realm.RealmUserInfo;


public class GettingDeviceTokenService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        RealmUserInfo.setPushNotification(FirebaseInstanceId.getInstance().getToken());
    }
}