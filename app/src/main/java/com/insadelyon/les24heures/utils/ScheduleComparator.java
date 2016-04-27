package com.insadelyon.les24heures.utils;

import com.insadelyon.les24heures.model.Schedule;

import java.util.Comparator;

/**
 * Created by remi on 09/04/15.
 */
public class ScheduleComparator implements Comparator<Schedule> {
    @Override
    public int compare(Schedule o1, Schedule o2) {
        return o1.getDay().getRank() - o2.getDay().getRank();
    }
}
