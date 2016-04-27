package com.insadelyon.les24heures.androidService;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.insadelyon.les24heures.DTO.LiveUpdateDTO;
import com.insadelyon.les24heures.R;
import com.insadelyon.les24heures.eventbus.LiveUpdatesReceivedEvent;
import com.insadelyon.les24heures.model.LiveUpdate;
import com.insadelyon.les24heures.service.RetrofitService;
import com.insadelyon.les24heures.utils.RetrofitErrorHandler;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LiveUpdateService extends IntentService {
    private static final String TAG = LiveUpdateService.class.getCanonicalName();
    EventBus eventBus = EventBus.getDefault();

    public static void start(Context context) {
        Intent intent = new Intent(context, LiveUpdateService.class);
        context.startService(intent);
    }

    public LiveUpdateService() {
        super("LiveUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        retrieveUpdatesFromServer();
    }


    private void retrieveUpdatesFromServer() {
        Log.d(TAG, "Retrieving LiveUpdates from server");
        RetrofitService retrofitService = getLiveUpdatesRetrofitService();
        retrofitService.getLiveUpdates(getGetLiveUpdatesCallback());
    }


    private RetrofitService getLiveUpdatesRetrofitService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getResources().getString(R.string.backend_url_mobile))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setErrorHandler(new RetrofitErrorHandler())
                .build();

        return restAdapter.create(RetrofitService.class);
    }

    private Callback<List<LiveUpdateDTO>> getGetLiveUpdatesCallback() {
        return new Callback<List<LiveUpdateDTO>>() {
            @Override
            public void success(List<LiveUpdateDTO> liveUpdateDTOs, Response response) {
                Log.d(TAG, "Got : " + liveUpdateDTOs.size() + " LiveUpdates, ");
                broadcastNewLiveUpdates(liveUpdateDTOs);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, " failure " + error);
            }
        };
    }

    private void broadcastNewLiveUpdates(List<LiveUpdateDTO> liveUpdateDTOs) {
        List<LiveUpdate> liveUpdates = fromDTO(liveUpdateDTOs);
        LiveUpdatesReceivedEvent liveUpdatesReceivedEvent = new LiveUpdatesReceivedEvent(liveUpdates);
        eventBus.post(liveUpdatesReceivedEvent);
    }

    public LiveUpdate fromDTO(LiveUpdateDTO liveUpdateDTO) {
        LiveUpdate liveUpdate = new LiveUpdate();
        liveUpdate.setTitle(liveUpdateDTO.getTitle());
        liveUpdate.setMessage(liveUpdateDTO.getMessage());
        liveUpdate.setTimePublished(liveUpdateDTO.getTimePublished());
        return liveUpdate;
    }

    public List<LiveUpdate> fromDTO(List<LiveUpdateDTO> liveUpdateDTOs) {
        ArrayList<LiveUpdate> liveUpdates = new ArrayList<>();
        for (LiveUpdateDTO liveUpdateDTO : liveUpdateDTOs) {
            liveUpdates.add(fromDTO(liveUpdateDTO));
        }
        return liveUpdates;
    }


}
