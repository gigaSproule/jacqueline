package uk.co.bensproule.jacqueline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;


public class AppNotification extends Service {
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

        int icon = R.drawable.icon;
        CharSequence tickerText = getText(R.string.app_name);
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, tickerText, when);

        Context context = getApplicationContext();
        Bundle extras = intent.getExtras();
        CharSequence contentTitle = extras.getString("contentTitle");
        CharSequence contentText = extras.getString("contentText");
        Intent notificationIntent = new Intent(getApplicationContext(), Main.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        final int NOTIFICATION_ID = 1;

        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
}
