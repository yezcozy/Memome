package com.mad.memome.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mad.memome.database.Datasource;
import com.mad.memome.model.Reminder;
import com.mad.memome.utils.NotificationUtil;

/**
 * receive alarm and make notification
 */
public class AlarmReceiver extends BroadcastReceiver {
    Datasource datasource;

    @Override
    public void onReceive(Context context, Intent intent) {
        datasource = new Datasource(context);
        datasource.open();

        Reminder reminder = datasource.getNotification(intent.getIntExtra("NOTIFICATION_ID", 0));

       NotificationUtil.createNotification(context, reminder);

        datasource.close();

    }
}
