package uk.co.bensproule.jacqueline;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GCMRegisterWithServer extends Service {

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Context context = getApplicationContext();

        Log.d("GCMRegisterWithServer.onStart", "Sending registration ID to my application server");
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(
                "http://www.britintel.co.uk/gcm/register.php");
        SharedPreferences preferences = context.getSharedPreferences("preferences", 0);
        String registrationID = preferences.getString("registrationID", "");
        String deviceID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            // Get the deviceID
            Log.i("GCMRegisterWithServer.onStart", "Registration ID: " + registrationID);
            Log.i("GCMRegisterWithServer.onStart", "Device ID: " + deviceID);
            nameValuePairs.add(new BasicNameValuePair("deviceid", deviceID));
            nameValuePairs.add(new BasicNameValuePair("registrationid", registrationID));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                Log.e("HttpResponse", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


}
