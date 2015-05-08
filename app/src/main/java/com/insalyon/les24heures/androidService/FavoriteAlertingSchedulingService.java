package com.insalyon.les24heures.androidService;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.insalyon.les24heures.BuildConfig;
import com.insalyon.les24heures.R;
import com.insalyon.les24heures.eventbus.ResourceUpdatedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.utils.ScheduleToDateTimeConverter2015;
import com.insalyon.les24heures.utils.ScheduleUtil;

import org.joda.time.DateTime;

import de.greenrobot.event.EventBus;


public class FavoriteAlertingSchedulingService extends IntentService {
    private static final String TAG = FavoriteAlertingSchedulingService.class.getCanonicalName();
    public static final String EXTRA_RESOURCE_ID = "RESOURCE_ID";
    public static final String EXTRA_RESOURCE_PARCEL = "RESOURCE";
    public static final String EXTRA_MESSAGE = "MESSAGE";
    public static final String EXTRA_DATETIME = "DATETIME";

    EventBus eventBus = EventBus.getDefault();
    private AlarmManager alarmManager;

    public static void start(Context context) {
        Intent intent = new Intent(context, FavoriteAlertingSchedulingService.class);
        context.startService(intent);
    }

    public FavoriteAlertingSchedulingService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Started");
        eventBus.registerSticky(this);
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
    }


    public void onEvent(ResourceUpdatedEvent event) {
        Resource resource = event.getResource();
        updateAlarmForResource(resource);
    }

    public void onEvent(ResourcesUpdatedEvent event) {
        for (DayResource dayResource : event.getDayResourceList()) {
            updateAlarmForResource(dayResource);
        }
        for (NightResource nightResource : event.getNightResourceList()) {
            updateAlarmForResource(nightResource);
        }
    }

    private void updateAlarmForResource(Resource resource) {
        try {
            if (resource.isFavorites()) {
                setAlarmForResource(resource);
            } else {
                cancelAlarmForResource(resource);
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to get notification time for resource " + String.valueOf(resource.get_id()));
        }
    }

    private void setAlarmForResource(Resource resource) throws Exception {

        DateTime notificationTime = getNotificationTime(ScheduleUtil.getNextSchedule(resource.getSchedules()));

        PendingIntent pendingIntent = getPendingIntentForResource(resource, notificationTime);

        Log.d(TAG,"Setting alarm for "+resource.getTitle() + " at " + notificationTime);
        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getMillis(), pendingIntent);

    }


    private void cancelAlarmForResource(Resource resource) throws Exception {
        DateTime notificationTime = getNotificationTime(ScheduleUtil.getNextSchedule(resource.getSchedules()));

        PendingIntent pendingIntent = getPendingIntentForResource(resource, notificationTime);

        Log.d(TAG,"Canceling alarm for "+resource.getTitle() + " at " + notificationTime);

        alarmManager.cancel(pendingIntent);


    }


    private PendingIntent getPendingIntentForResource(Resource resource, DateTime notificationTime) {
        Intent intent = new Intent(this, FavoriteAlertingNotificationService.class);
        intent.setData(Uri.parse("notif:" + resource.get_id()));

        intent.putExtra(EXTRA_RESOURCE_ID, resource.get_id());
        intent.putExtra(EXTRA_RESOURCE_PARCEL, resource);
        intent.putExtra(EXTRA_DATETIME, notificationTime.toString());
        intent.putExtra(EXTRA_MESSAGE, resource.getTitle() + " " + getResources().getString(R.string.favorite_notification_starts_at) + " " + notificationTime.toString("HH:mm") + "!");

        return PendingIntent.getService(this, resource.get_id(), intent, 0);
    }


    private DateTime getNotificationTime(Schedule schedule) throws Exception {
        if (BuildConfig.DEBUG) {
            return getFakeStartTime(schedule);
        } else {
            return getResourceStartTimeMinus15Minutes(schedule);
        }
    }

    private DateTime getFakeStartTime(Schedule schedule) throws Exception {
        return DateTime.now().plusSeconds(ScheduleToDateTimeConverter2015.getStart(schedule).getHourOfDay());
    }

    private DateTime getResourceStartTimeMinus15Minutes(Schedule schedule) throws Exception {
        DateTime targetDate = ScheduleToDateTimeConverter2015.getStart(schedule);
        targetDate = targetDate.minusMinutes(15);
        return targetDate;
    }


}
