package com.insalyon.les24heures.androidService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.service.RetrofitService;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LiveUpdateGCMRegistrationService extends IntentService {
    private static final String TAG = LiveUpdateGCMRegistrationService.class.getCanonicalName();



    public static void startRegisterAction(Context context) {
        Intent intent = new Intent(context, LiveUpdateGCMRegistrationService.class);
        context.startService(intent);
    }


    public LiveUpdateGCMRegistrationService() {
        super("LiveUpdateGCMRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerOnGCM();
    }

    private void registerOnGCM() {
        Log.d(TAG, "Trying to register on GCM...");
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String regid = gcm.register(getResources().getString(R.string.GCM_SENDER_ID));
            Log.i(TAG, "Successful, registration ID=" + regid);

            sendRegistrationIdToServer(regid);

        } catch (IOException ex) {
            Log.e(TAG, "Error :" + ex.getMessage());
        }
    }

    private void sendRegistrationIdToServer(String regid) {
        RetrofitService retrofitService = getLiveUpdatesPutKeyRetrofitService();
        retrofitService.postLiveUpdatesKey(regid, getKeyPostCallback());
    }

    private RetrofitService getLiveUpdatesPutKeyRetrofitService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url_mobile))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter.create(RetrofitService.class);
    }

    private Callback<String> getKeyPostCallback() {
        return new Callback<String>() {

            @Override
            public void success(String result, Response response) {
                Log.d(TAG, "Key successfully sent to server");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, " failure " + error);
            }
        };
    }




}
