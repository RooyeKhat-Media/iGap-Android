package net.iGap.libs.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.iGap.R;
import net.iGap.activities.ActivityMain;


public class NotificationService extends FirebaseMessagingService {

    private final static String ROOM_ID = "roomId";
    private final static String MESSAGE_ID = "messageId";
    private final static String MESSAGE_TYPE = "type";
    private final static String PAYLOAD = "payload";
    public static int NOTIFICATION_ID = 1000;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//            if (remoteMessage.getData().size() > 0) {
//                Map<String , String> date = remoteMessage.getData();
//                if (date.containsKey(MESSAGE_TYPE)) {
//                    switch (date.get(MESSAGE_TYPE)){
//                        case "ROOM_SEND_MESSAGE":
//                            if (date.containsKey(PAYLOAD)) {
//                                try {
//                                    JSONObject payload = new JSONObject(date.get(PAYLOAD));
//                                    String  roomId = payload.getString(ROOM_ID);
//                                    String messageId = payload.getString(MESSAGE_ID);
//                                    Log.e("ddd","romid : "+roomId+"       messageId :  "+messageId);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                                generateNotification("ROOM_SEND_MESSAGE");
//                            }
//                            break;
//                        case "SIGNALING_OFFER":
//                            Log.e("ddd"," push SIGNALING_OFFER ");
//                            break;
//                    }
//                }
//            }
    }


    private void generateNotification(String messageBody) {

        Intent intent = new Intent(this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("IGap")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
    }

}