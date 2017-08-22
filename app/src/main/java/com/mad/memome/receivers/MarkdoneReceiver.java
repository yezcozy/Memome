package com.mad.memome.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mad.memome.activity.MarkActivity;
import com.mad.memome.R;
import com.mad.memome.utils.NotificationUtil;

/**
 * Markdone receiver
 */

public class MarkdoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int reminderId = intent.getIntExtra(context.getString(R.string.notification_id), 0);
        MarkActivity ma = new MarkActivity(context);
            ma.mark(reminderId);
        NotificationUtil.cancelNotification(context, reminderId);
    }
}
