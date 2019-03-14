package cse110.ucsd.team20_personalbest.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import cse110.ucsd.team20_personalbest.R;

public class Ntfc{

    private String CHANNEL_ID;
    private NotificationCompat.Builder builder;
    private NotificationManager nm;
    private Context context;


    public Ntfc (Context context, String textTitle, String textContent, String id, NotificationManager nm) {
        CHANNEL_ID = id;
        this.nm = nm;
        this.context = context;

        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        createNotificationChannel();
    }

   public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(CHANNEL_ID);
            nm.createNotificationChannel(channel);
        }
    }

    public void push (int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, builder.build());
    }
}
