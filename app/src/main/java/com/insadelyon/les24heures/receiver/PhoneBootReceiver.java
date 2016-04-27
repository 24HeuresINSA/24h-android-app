package com.insadelyon.les24heures.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.insadelyon.les24heures.androidService.LiveUpdatesNotificationService;

public class PhoneBootReceiver extends BroadcastReceiver {
    public PhoneBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        executePhoneBootActions(context);

    }

    private void executePhoneBootActions(Context context) {
        Intent startNotificationServiceIntent = new Intent(context, LiveUpdatesNotificationService.class);
        context.startService(startNotificationServiceIntent);
    }
}
