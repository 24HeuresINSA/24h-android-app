package com.insalyon.les24heures.utils;

import android.location.Location;

import com.insalyon.les24heures.model.DayResource;

import java.util.Comparator;
import java.util.Date;


import static java.util.Calendar.DAY_OF_WEEK;

/**
 * Created by nicolas on 19/03/15.
 */
public class TimeLocationSortComparator implements Comparator<DayResource> {

    private final Location lastKnownPosition;

    public TimeLocationSortComparator(Location lastKnownPosition)
    {
        this.lastKnownPosition=lastKnownPosition;
    }


    @Override
    public int compare(DayResource res1, DayResource res2) {

        Date start1 = res1.getSchedules().get(0).getStart();
        Date start2 = res2.getSchedules().get(0).getStart();

        if (start1.after(start2)){
            return 1;
        }
        else if (start1.before(start2)){
            return -1;
        }
        //If Schedules begin at the same time, we use distance to compare
        else if(start1.equals(start2)) {
            Location loc1 = new Location("loc");
            loc1.setLongitude(res1.getLoc().longitude);
            loc1.setLatitude(res1.getLoc().latitude);

            Location loc2 = new Location("loc");
            loc2.setLongitude(res2.getLoc().longitude);
            loc2.setLatitude(res2.getLoc().latitude);


            Integer distance1 = Math.round(lastKnownPosition.distanceTo(loc1));
            Integer distance2 = Math.round(lastKnownPosition.distanceTo(loc2));

            return distance1.compareTo(distance2);
        }

        //Default return value (normally never used)
        return 0;
    }
}
