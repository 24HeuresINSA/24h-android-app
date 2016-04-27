package com.insadelyon.les24heures.eventbus;

import com.insadelyon.les24heures.model.LiveUpdate;

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
