package com.insadelyon.les24heures.eventbus;

import com.insadelyon.les24heures.model.DayResource;
import com.insadelyon.les24heures.model.NightResource;

import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class ResourcesUpdatedEvent {
    List<DayResource> facilitiesList;
    List<DayResource> dayResourceList;
    List<NightResource> nightResourceList;
    String dataVersion;


    @Deprecated
    public ResourcesUpdatedEvent(List<DayResource> dayResourceList) {
        this.dayResourceList = dayResourceList;
    }

    public ResourcesUpdatedEvent(List<DayResource> dayResourceList, List<NightResource> nightResourceList, List<DayResource> facilitiesList,String dataVersion) {
        this.dayResourceList = dayResourceList;
        this.nightResourceList = nightResourceList;
        this.facilitiesList = facilitiesList;
        this.dataVersion = dataVersion;
    }

    public List<NightResource> getNightResourceList() {
        return nightResourceList;
    }

    public List<DayResource> getDayResourceList() {
        return dayResourceList;
    }

    public List<DayResource> getFacilitiesList() {
        return facilitiesList;
    }

    public String getDataVersion() {
        return dataVersion;
    }
}
