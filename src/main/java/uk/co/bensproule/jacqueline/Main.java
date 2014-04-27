package uk.co.bensproule.jacqueline;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.Calendar;

public class Main extends Activity {
    MediaPlayer mp = new MediaPlayer();
    String SENDER_ID = "474433115002";

    // TextView mDisplay;
    AsyncTask<Void, Void, Void> mRegisterTask;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.i("Main.onCreate", "Starting Program");

        setupNotificationReminder();

        mp = MediaPlayer.create(getApplicationContext(), R.raw.unchained_melody);
        mp.start();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        registerGCM();
    }

    @Override
    public void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main);
        mp.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public void setupNotificationReminder() {
        Intent intent = new Intent(getApplicationContext(),
                NotificationReminderReceiver.class);
        intent.setAction("uk.co.sproule.jacqueline.SET_NOTIFICATION_ALARM");

        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);

        // check if an alarm already exists
        if (!alarmUp) {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Calendar currentCalendar = Calendar.getInstance();
            Log.i("Main.setupNotificationReminder",
                    "Create the notification alarm service at "
                            + currentCalendar.get(Calendar.DAY_OF_MONTH) + "/"
                            + currentCalendar.get(Calendar.MONTH) + "/"
                            + currentCalendar.get(Calendar.YEAR) + ", "
                            + currentCalendar.get(Calendar.HOUR_OF_DAY) + ":"
                            + currentCalendar.get(Calendar.MINUTE) + ":"
                            + currentCalendar.get(Calendar.SECOND)
            );

            // get a Calendar object with current time
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
            }

            Log.d("Main.setupNotificationReminder",
                    "Alarm set for "
                            + cal.get(Calendar.DAY_OF_MONTH) + "/"
                            + cal.get(Calendar.MONTH) + "/"
                            + cal.get(Calendar.YEAR) + ", "
                            + cal.get(Calendar.HOUR_OF_DAY) + ":"
                            + cal.get(Calendar.MINUTE) + ":"
                            + cal.get(Calendar.SECOND)
            );

            // Get the AlarmManager service
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.cancel(pendingIntent);
            am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public void registerGCM() {
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
        // sets the app name in the intent
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0));
        registrationIntent.putExtra("sender", SENDER_ID);
        startService(registrationIntent);
    }

    @Override
    protected void onDestroy() {
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        // sets the app name in the intent
        registrationIntent.putExtra("app", PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(), 0));
        registrationIntent.putExtra("sender", SENDER_ID);
        // startService(registrationIntent);
        super.onDestroy();
        mp.stop();
    }
}
