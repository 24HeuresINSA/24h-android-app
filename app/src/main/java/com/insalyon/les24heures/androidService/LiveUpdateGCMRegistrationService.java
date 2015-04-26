package com.insalyon.les24heures.androidService;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.utils.RetrofitErrorHandler;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LiveUpdateGCMRegistrationService extends IntentService {
    private static final String TAG = LiveUpdateGCMRegistrationService.class.getCanonicalName();
    EventBus eventBus = EventBus.getDefault();

    public LiveUpdateGCMRegistrationService() {
        super("LiveUpdateGCMRegistrationService");
        Log.d(TAG, "Started.");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerOnGCM();
    }

    private void registerOnGCM() {
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
            String regid = gcm.register(getResources().getString(R.string.GCM_SENDER_ID));
            Log.i(TAG, "Device registered in GCM, registration ID=" + regid);

            sendRegistrationIdToServer(regid);

//            TODO:Store Registration Id in prefs, clear it on application update

        } catch (IOException ex) {
            Log.e(TAG,"Error :" + ex.getMessage());
        }
    }

    private void sendRegistrationIdToServer(String regid) {

        RetrofitService retrofitService = getLiveUpdatesPutKeyRetrofitService();
        retrofitService.postLiveUpdatesKey(regid, new Callback<String>() {

            @Override
            public void success(String result, Response response) {
                Log.d(TAG, "Published key successfully");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, " failure " + error);
            }
        });
    }

    private RetrofitService getLiveUpdatesPutKeyRetrofitService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url_mobile))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new RetrofitErrorHandler())
                .build();

        return restAdapter.create(RetrofitService.class);
    }


}
