package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class ResourcesUpdatedEvent {
    List<DayResource> dayResourceList;
    List<NightResource> nightResourceList;

    @Deprecated
    public ResourcesUpdatedEvent(List<DayResource> dayResourceList) {
        this.dayResourceList = dayResourceList;
    }

    public ResourcesUpdatedEvent(List<DayResource> dayResourceList, List<NightResource> nightResourceList) {
        this.dayResourceList = dayResourceList;
        this.nightResourceList = nightResourceList;
    }

    public List<NightResource> getNightResourceList() {
        return nightResourceList;
    }

    public List<DayResource> getDayResourceList() {
        return dayResourceList;
    }

    @Deprecated
    public void setNightResourceList(ArrayList<NightResource> nightResourceList) {
        this.nightResourceList = nightResourceList;
    }
}
