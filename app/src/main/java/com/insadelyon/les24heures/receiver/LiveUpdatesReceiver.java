package com.insadelyon.les24heures.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.insadelyon.les24heures.androidService.LiveUpdateService;

public class LiveUpdatesReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = LiveUpdateService.class.getCanonicalName();

    public LiveUpdatesReceiver() {
        Log.d(TAG, "Started");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received a GCM message");
        forwardMessageToLiveUpdateService(context, intent);
    }

    private void forwardMessageToLiveUpdateService(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),
                LiveUpdateService.class.getName());

        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
