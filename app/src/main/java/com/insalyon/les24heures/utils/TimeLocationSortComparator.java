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
    @Override
    public int compare(DayResource res1, DayResource res2) {
        int ret=1;

        Date start1 = res1.getSchedules().get(0).getStart();
        Date start2 = res2.getSchedules().get(0).getStart();

        if (start1.after(start2)){
            ret = 1;
        }
        else if (start1.before(start2)){
            ret = -1;
        }
        //If Schedules begin at the same time, we use distance to compare
        else if(start1.equals(start2)) {
            Location loc1 = new Location("loc");
            loc1.setLongitude(res1.getLoc().longitude);
            loc1.setLatitude(res1.getLoc().latitude);

            Location loc2 = new Location("loc");
            loc2.setLongitude(res2.getLoc().longitude);
            loc2.setLatitude(res2.getLoc().latitude);

            //TODO : Use real position instead of constant lastKnownPosition
            Location lastKnownPosition = new Location("lastKnownPosition");
            lastKnownPosition.setLatitude(45.78401554);
            lastKnownPosition.setLongitude(4.8754406);

            Integer distance1 = Math.round(lastKnownPosition.distanceTo(loc1));
            Integer distance2 = Math.round(lastKnownPosition.distanceTo(loc2));

            if (distance1 > distance2) {
                ret = 1;
            } else {
                ret = -1;
            }
        }

        return ret;
    }
}
