package uk.co.bensproule.jacqueline;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

public class GCMIntentService extends IntentService {

    private static final Object LOCK = GCMIntentService.class;
    private static PowerManager.WakeLock sWakeLock;

    public GCMIntentService() {
        super("GCM Intent Service");
        // TODO Auto-generated constructor stub
    }

    static void runIntentInService(Context context, Intent intent) {
        synchronized (LOCK) {
            if (sWakeLock == null) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "my_wakelock");
            }
        }
        sWakeLock.acquire();
        intent.setClassName(context, GCMIntentService.class.getName());
        context.startService(intent);
    }

    @Override
    public final void onHandleIntent(Intent intent) {
        try {
            String action = intent.getAction();
            Log.i("GCMIntentService.onHandleIntent", "Action: " + action);
            if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
                handleRegistration(intent);
            } else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
                handleMessage(intent);
            }
        } finally {
            synchronized (LOCK) {
                sWakeLock.release();
            }
        }
    }

    private void handleRegistration(Intent intent) {
        String registrationID = intent.getStringExtra("registration_id");
        String error = intent.getStringExtra("error");
        String unregistered = intent.getStringExtra("unregistered");
        // registration succeeded
        if (registrationID != null) {
            // store registration ID on shared preferences
            // notify 3rd-party server about the registered ID
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("preferences", 0);
            Editor editor = preferences.edit();
            editor.putString("registrationID", registrationID);
            editor.commit();

            Intent serviceIntent = new Intent(getApplicationContext(),
                    GCMRegisterWithServer.class);
            getApplicationContext().startService(serviceIntent);
        }

        // unregistration succeeded
        if (unregistered != null) {
            // get old registration ID from shared preferences
            // notify 3rd-party server about the unregistered ID
            Intent serviceIntent = new Intent(getApplicationContext(),
                    GCMUnregisterWithServer.class);
            getApplicationContext().startService(serviceIntent);
        }

        // last operation (registration or unregistration) returned an error;
        if (error != null) {
            if ("SERVICE_NOT_AVAILABLE".equals(error)) {
                // optionally retry using exponential back-off
                // (see Advanced Topics)
            } else {
                // Unrecoverable error, log it
                Log.i("GCMIntentService.handleRegistration", "Received error: "
                        + error);
            }
        }
    }

    private void handleMessage(Intent intent) {
        Intent serviceIntent = new Intent(getApplicationContext(),
                AppNotification.class);
        Bundle extras = intent.getExtras();
        String contentTitle = extras.getString("contentTitle");
        String contentText = extras.getString("contentText");
        Log.i("GCMIntentService.handleMessage", "Content Title: "
                + contentTitle);
        Log.i("GCMIntentService.handleMessage", "Content Text: " + contentText);
        serviceIntent.putExtra("contentTitle", contentTitle);
        serviceIntent.putExtra("contentText", contentText);
        getApplicationContext().startService(serviceIntent);
    }
}