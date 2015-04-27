package com.insalyon.les24heures.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.insalyon.les24heures.androidService.NotificationService;

public class PhoneBootReceiver extends BroadcastReceiver {
    public PhoneBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        executePhoneBootActions(context);

    }

    private void executePhoneBootActions(Context context) {
        Intent startNotificationServiceIntent = new Intent(context, NotificationService.class);
        context.startService(startNotificationServiceIntent);
    }
}
