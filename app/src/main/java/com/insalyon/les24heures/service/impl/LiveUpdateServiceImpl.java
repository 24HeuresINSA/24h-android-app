package com.insalyon.les24heures.service.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.insalyon.les24heures.DTO.AssomakerDTO;
import com.insalyon.les24heures.DTO.CategoryDTO;
import com.insalyon.les24heures.DTO.DayResourceDTO;
import com.insalyon.les24heures.DTO.LiveUpdateDTO;
import com.insalyon.les24heures.DTO.NightResourceDTO;
import com.insalyon.les24heures.eventbus.CategoriesUpdatedEvent;
import com.insalyon.les24heures.eventbus.LiveUpdatesReceivedEvent;
import com.insalyon.les24heures.eventbus.ResourcesUpdatedEvent;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.LiveUpdate;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.LiveUpdateService;
import com.insalyon.les24heures.service.RetrofitService;
import com.insalyon.les24heures.utils.AlphabeticalSortComparator;
import com.insalyon.les24heures.utils.Day;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LiveUpdateServiceImpl implements LiveUpdateService {
    private static final String TAG = LiveUpdateServiceImpl.class.getCanonicalName();

    private static LiveUpdateServiceImpl liveUpdatesService;

    private EventBus eventBus;

    public static LiveUpdateServiceImpl getInstance() {
        if (liveUpdatesService == null) {
            liveUpdatesService = new LiveUpdateServiceImpl();
        }
        return liveUpdatesService;
    }

    public LiveUpdateServiceImpl() {
        eventBus = EventBus.getDefault();
    }


    public void getLiveUpdatesAsyncFromBackend(RetrofitService retrofitService) {
        Log.d(TAG,"Getting live updates from backend");
        retrofitService.getLiveUpdates(new Callback<List<LiveUpdateDTO>>() {
            @Override
            public void success(List<LiveUpdateDTO> liveUpdateDTOs, Response response) {
                Log.d(TAG,"Got : "+liveUpdateDTOs.size()+" LiveUpdates, ");
                List<LiveUpdate> liveUpdates = liveUpdatesService.fromDTO(liveUpdateDTOs);
                LiveUpdatesReceivedEvent liveUpdatesReceivedEvent = new LiveUpdatesReceivedEvent(liveUpdates);
                eventBus.post(liveUpdatesReceivedEvent);
            }


            @Override
            public void failure(RetrofitError error) {

                Log.e(TAG,"getLiveUpdates failure " + error);

            }
        });

    }

    @Override
    public LiveUpdate fromDTO(LiveUpdateDTO liveUpdateDTO) {
        LiveUpdate liveUpdate = new LiveUpdate();
        liveUpdate.setTitle(liveUpdateDTO.getTitle());
        liveUpdate.setDescription(liveUpdateDTO.getDescription());
        return liveUpdate;
    }

    @Override
    public List<LiveUpdate> fromDTO(List<LiveUpdateDTO> liveUpdateDTOs) {
        ArrayList<LiveUpdate> liveUpdates = new ArrayList<LiveUpdate>();
        for (LiveUpdateDTO liveUpdateDTO:liveUpdateDTOs){
            liveUpdates.add(fromDTO(liveUpdateDTO));
        }
        return liveUpdates;
    }


}
