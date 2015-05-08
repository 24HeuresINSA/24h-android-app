package com.insalyon.les24heures.androidService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.insalyon.les24heures.DayActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.fragments.LiveUpdatesFragment;
import com.insalyon.les24heures.utils.IntentExtra;


public class FavoriteAlertingNotificationService extends IntentService {
    private static final String TAG = FavoriteAlertingNotificationService.class.getCanonicalName();

    private NotificationCompat.Builder builder;

    public static void start(Context context) {
        Intent intent = new Intent(context, FavoriteAlertingNotificationService.class);
        context.startService(intent);
    }

    public FavoriteAlertingNotificationService() {
        super("FavoriteAlertingNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Log.d(TAG,"Processing notifications...");

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);


        int resource_id = intent.getIntExtra(FavoriteAlertingSchedulingService.EXTRA_RESOURCE_ID, 0);
        String message = intent.getStringExtra(FavoriteAlertingSchedulingService.EXTRA_MESSAGE);

        if (builder == null) {
            builder = getNotificationBuilder(resource_id,message);
        }


        builder.setContentText(message);
        notificationManager.notify(resource_id, builder.build());

    }


    private NotificationCompat.Builder getNotificationBuilder(int resource_id, String message) {

        String notificationTitle = getResources().getString(R.string.favorite_notification_title);

        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentIntent(getNotificationPendingIntent(resource_id))
                .setAutoCancel(true);
    }

    private PendingIntent getNotificationPendingIntent(int resource_id) {
        Intent intent = new Intent(this, DayActivity.class);
        intent.putExtra("nextStaticFragment", LiveUpdatesFragment.class.getCanonicalName());
        intent.putExtra(IntentExtra.toDisplayResourceId.toString(),resource_id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DayActivity.class);
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }


}
