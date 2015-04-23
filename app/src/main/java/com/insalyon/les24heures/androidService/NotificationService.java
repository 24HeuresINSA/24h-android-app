package com.insalyon.les24heures.androidService;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.LiveUpdatesReceivedEvent;
import com.insalyon.les24heures.model.LiveUpdate;

import java.util.List;

import de.greenrobot.event.EventBus;


public class NotificationService extends IntentService {
    private static final String TAG = NotificationService.class.getCanonicalName();
    EventBus eventBus = EventBus.getDefault();

    public NotificationService() {
        super(TAG);
        eventBus.registerSticky(this);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public void onEvent(LiveUpdatesReceivedEvent event) {
        List<LiveUpdate> liveUpdates = event.getLiveUpdates();
        LiveUpdate lastUpdate = liveUpdates.get(0);
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_now)
                        .setContentTitle(lastUpdate.getTitle())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(lastUpdate.getMessage()))
                        .setContentText(lastUpdate.getMessage());

        mNotificationManager.notify(1, mBuilder.build());

    }


}
