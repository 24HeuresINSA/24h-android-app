package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.LiveUpdate;
import com.insalyon.les24heures.model.NightResource;

import java.util.ArrayList;
import java.util.List;


public class LiveUpdatesReceivedEvent {
    List<LiveUpdate> liveUpdates;


    public LiveUpdatesReceivedEvent(List<LiveUpdate> liveUpdates) {
        this.liveUpdates = liveUpdates;
    }

    public List<LiveUpdate> getLiveUpdates() {
        return liveUpdates;
    }

}
