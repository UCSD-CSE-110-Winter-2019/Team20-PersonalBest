package cse110.ucsd.team20_personalbest.chat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import cse110.ucsd.team20_personalbest.activities.ChatActivity;
import cse110.ucsd.team20_personalbest.util.Ntfc;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        sendNotification(data);
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param data FCM data payload received.
     */
    private void sendNotification(Map<String, String> data) {

        String email = data.get("title");
        email = email.substring(0,email.indexOf(' '));
        email = email + "@";
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend", email);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Ntfc n = new Ntfc(this,data.get("title"), data.get("body"), "msg", notificationManager,pendingIntent);
        n.push();
    }
}
