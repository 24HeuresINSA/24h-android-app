package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.DayResource;

import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class ResourcesUpdatedEvent {
    List<DayResource> dayResourceList;

    public ResourcesUpdatedEvent(List<DayResource> dayResourceList) {
        this.dayResourceList = dayResourceList;
    }

    public List<DayResource> getDayResourceList() {
        return dayResourceList;
    }

    public void setDayResourceList(List<DayResource> dayResourceList) {
        this.dayResourceList = dayResourceList;
    }
}
