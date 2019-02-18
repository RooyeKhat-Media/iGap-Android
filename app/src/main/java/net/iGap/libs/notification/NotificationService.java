package net.iGap.libs.notification;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.iGap.G;
import net.iGap.helper.HelperNotification;
import net.iGap.interfaces.OnClientGetRoomMessage;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;
import net.iGap.request.RequestClientGetRoomMessage;

import java.util.Map;

import io.realm.Realm;


public class NotificationService extends FirebaseMessagingService {

    private final static String ROOM_ID = "roomId";
    private final static String MESSAGE_ID = "messageId";
    private final static String MESSAGE_TYPE = "loc_key";
    private static boolean isFirstMessage = true;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (isFirstMessage) {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> date = remoteMessage.getData();
                if (date.containsKey(ROOM_ID) && date.containsKey(MESSAGE_ID)) {
                    //   type of dataMap is     messageId roomId type loc_key loc_args
                    Long roomId = Long.parseLong(date.get(ROOM_ID));
                    Long messageId = Long.parseLong(date.get(MESSAGE_ID));

                    G.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new RequestClientGetRoomMessage().clientGetRoomMessage(roomId, messageId);
                        }
                    }, 2000);

                    G.onClientGetRoomMessage = new OnClientGetRoomMessage() {
                        @Override
                        public void onClientGetRoomMessageResponse(ProtoGlobal.RoomMessage message) {
                            G.onClientGetRoomMessage = null;
                            if (date.containsKey(MESSAGE_TYPE)) {
                                final Realm realm = Realm.getDefaultInstance();
                                RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();
                                if (room != null) {
                                    HelperNotification.getInstance().addMessage(roomId, message, room.getType(), room, realm);
                                }
                                realm.close();
                            }
                        }
                    };
                }
            }

            isFirstMessage = false;
        }


    }


}