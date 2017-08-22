package com.mad.memome.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.mad.memome.activity.ViewActivity;
import com.mad.memome.model.Reminder;
import com.mad.memome.R;
import com.mad.memome.receivers.MarkdoneReceiver;

/**
 * Creating new notification
 */
public class NotificationUtil {

    public static void createNotification(Context context, Reminder reminder) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.ic_action_alarm);
        builder.setContentTitle(reminder.getTitle());
        builder.setContentText(reminder.getContent());
        long[] pattern = {0, 300, 0};
        builder.setVibrate(pattern);


        Intent intent = new Intent(context, ViewActivity.class);
        intent.putExtra("NOTIFICATION_ID", reminder.getId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Intent Pintent = new Intent(context, MarkdoneReceiver.class);
        Pintent.putExtra("NOTIFICATION_ID", reminder.getId());
        PendingIntent markIntent = PendingIntent.getBroadcast(context, reminder.getId(), Pintent, PendingIntent.FLAG_ONE_SHOT);
        builder.addAction(R.drawable.ic_action_markasdone, "Mark as done", markIntent);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(reminder.getId(), builder.build());



    }

    /**
     * cancel notification
     * @param context
     * @param notificationId
     */
    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }
}
