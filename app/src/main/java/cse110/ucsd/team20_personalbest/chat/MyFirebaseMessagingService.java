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

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        need to implement this if you want to do something when you receive a notification while app is in the foreground.
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        Map<String, String> data = remoteMessage.getData();
//
//        sendNotification(notification, data);
    }

//    /**
//     * Create and show a custom notification containing the received FCM message.
//     *
//     * @param notification FCM notification payload received.
//     * @param data FCM data payload received.
//     */
//    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {
//
//        String email = notification.getTitle();
//        email = email.substring(0,email.indexOf(' '));
//        System.out.println("*******************************"+email);
//        Intent intent = new Intent(this, ChatActivity.class);
//        intent.putExtra("friend", email);
//        intent.putExtra("UserName", userName);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Ntfc n = new Ntfc(this,notification.getTitle(), notification.getBody(), "msg", notificationManager,pendingIntent);
//        n.push();
//    }
}
