package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.DayResource;

/**
 * Created by remi on 09/02/15.
 */
public class ResourceSelectedEvent {
    private DayResource dayResource;

    public ResourceSelectedEvent(DayResource dayResource) {
        this.dayResource = dayResource;
    }

    public DayResource getDayResource() {
        return dayResource;
    }

}
