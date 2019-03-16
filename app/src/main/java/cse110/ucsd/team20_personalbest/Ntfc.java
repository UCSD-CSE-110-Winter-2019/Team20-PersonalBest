package cse110.ucsd.team20_personalbest;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class Ntfc{

    public String CHANNEL_ID;
    public NotificationCompat.Builder builder;
    public NotificationManager nm;
    public Context context;


    public Ntfc (Context context, String textTitle, String textContent, String id, NotificationManager nm, PendingIntent pendingIntent) {
        CHANNEL_ID = id;
        this.nm = nm;
        this.context = context;

        builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent).setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

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

    public void push () {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
