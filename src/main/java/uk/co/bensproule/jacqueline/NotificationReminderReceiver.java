package uk.co.bensproule.jacqueline;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, AppNotification.class);
        serviceIntent.putExtra("contentTitle", "For Jacqueline...");
        serviceIntent.putExtra("contentText", "You're always loved :)");
        context.startService(serviceIntent);
    }
}