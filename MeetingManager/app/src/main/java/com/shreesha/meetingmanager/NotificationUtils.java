package com.shreesha.meetingmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

public class NotificationUtils {
    private static final String CHANNEL_ID = "meeting_channel";
    private static final String CHANNEL_NAME = "Meeting Channel";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public static void showMeetingNotification(Context context, String agenda, String time) {
        Toast.makeText(context,"Agenda: " + agenda + "\nTime: " + time , Toast.LENGTH_SHORT).show();
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Meeting Scheduled")
                .setContentText("Agenda: " + agenda + "\nTime: " + time)
                .setSmallIcon(R.drawable.logo);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(1, builder.build());
    }

    public static void showNoMeetingNotification(Context context) {
        Toast.makeText(context, "There are no meetings scheduled for today.", Toast.LENGTH_SHORT).show();
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("No Meeting Scheduled")
                .setContentText("There are no meetings scheduled for today.")
                .setSmallIcon(R.drawable.logo);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(2, builder.build());
    }
}
