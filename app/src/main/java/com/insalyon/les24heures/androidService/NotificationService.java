package com.insalyon.les24heures.androidService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.insalyon.les24heures.DayActivity;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.LiveUpdatesReceivedEvent;
import com.insalyon.les24heures.model.LiveUpdate;

import java.util.List;

import de.greenrobot.event.EventBus;


public class NotificationService extends IntentService {
    private static final String TAG = NotificationService.class.getCanonicalName();
    public static final String PREFS_NAME = "dataFile";
    EventBus eventBus = EventBus.getDefault();


    public NotificationService() {
        super(TAG);
        eventBus.registerSticky(this);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

    }

    //When new Liveupdates are received, show them as notification
    public void onEvent(LiveUpdatesReceivedEvent event) {
        List<LiveUpdate> liveUpdates = event.getLiveUpdates();
        Log.d(TAG, "Got : " + liveUpdates.size() + " LiveUpdates, ");
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        long timeLastLiveUpdateSeen = sharedPreferences.getLong(getResources().getString(R.string.SHARED_PREF_LAST_LIVEUPDATE_SEEN), 0);

        for (LiveUpdate liveUpdate : liveUpdates) {
            if (liveUpdate.wasPublishedAfter(timeLastLiveUpdateSeen)) {
                showLiveUpdateNotification(liveUpdate);
                timeLastLiveUpdateSeen = liveUpdate.getTimePublished();
            }
        }

        updateTimeLastLiveUpdateSeen(sharedPreferences, timeLastLiveUpdateSeen);

    }

    private void updateTimeLastLiveUpdateSeen(SharedPreferences sharedPreferences, long timeLastLiveUpdateSeen) {
        sharedPreferences.edit().putLong(getResources().getString(R.string.SHARED_PREF_LAST_LIVEUPDATE_SEEN), timeLastLiveUpdateSeen).commit();
    }

    private void showLiveUpdateNotification(LiveUpdate liveUpdate) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = getNotificationBuilder(liveUpdate);
        mNotificationManager.notify((int) liveUpdate.getTimePublished(), builder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder(LiveUpdate liveUpdate) {
        Intent intent = new Intent(this, DayActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DayActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        return new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_now)
                .setContentTitle(liveUpdate.getTitle())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(liveUpdate.getMessage()))
                .setContentText(liveUpdate.getMessage())
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
    }


}
