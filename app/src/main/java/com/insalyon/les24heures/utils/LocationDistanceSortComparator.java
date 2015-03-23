package com.insalyon.les24heures.utils;

import android.location.Location;

import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.Resource;

import java.util.Comparator;

/**
 * Created by nicolas on 19/03/15.
 */
public class LocationDistanceSortComparator implements Comparator<DayResource> {

    private final Location lastKnownPosition;


    public LocationDistanceSortComparator(Location lastKnownPosition)
    {
        this.lastKnownPosition=lastKnownPosition;

    }

    @Override
    public int compare(DayResource res1, DayResource res2) {

        Location loc1 = new Location("loc");
        loc1.setLongitude(res1.getLoc().longitude);
        loc1.setLatitude(res1.getLoc().latitude);

        Location loc2 = new Location("loc");
        loc2.setLongitude(res2.getLoc().longitude);
        loc2.setLatitude(res2.getLoc().latitude);

        Integer distance1 = Math.round(lastKnownPosition.distanceTo(loc1));
        Integer distance2 = Math.round(lastKnownPosition.distanceTo(loc2));

        return  distance1.compareTo(distance2);
    }
}
