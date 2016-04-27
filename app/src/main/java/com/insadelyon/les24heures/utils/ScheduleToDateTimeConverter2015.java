package com.insadelyon.les24heures.utils;

import com.insadelyon.les24heures.model.Schedule;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.Date;

/**
 * Created by lbillon on 5/4/2015.
 */
public class ScheduleToDateTimeConverter2015 {

    static int YEAR = 2015;
    static int MONTH = 5;
    static String TIMEZONE_NAME = "Europe/Paris";




    public static DateTime getStart(Schedule schedule) throws Exception {
        LocalDate date = getDate(schedule);
        LocalTime startTime = getLocalTime(schedule.getStart());
        return getFullDateTimeWithTimeZone(date, startTime);
    }


    public static DateTime getEnd(Schedule schedule) throws Exception {
        LocalDate date = getDate(schedule);
        LocalTime endTime = getLocalTime(schedule.getEnd());
        date = getDateWithEndTimeCorrection(date, endTime);
        return getFullDateTimeWithTimeZone(date, endTime);
    }

    private static LocalDate getDateWithEndTimeCorrection(LocalDate date, LocalTime endTime) {
        if(dateNeedsEndTimeCorrection(endTime)){
            return date.plusDays(1);
        }else{
            return date;
        }
    }

    private static boolean dateNeedsEndTimeCorrection(LocalTime endTime) {
        return (0 == endTime.getHourOfDay())&&(0 == endTime.getMinuteOfHour());
    }

    private static DateTime getFullDateTimeWithTimeZone(LocalDate date, LocalTime startTime) {
        return date.toDateTime(startTime, DateTimeZone.forID(TIMEZONE_NAME));
    }

    private static LocalTime getLocalTime(Date time) throws Exception {
        return new LocalTime(time);
    }

    private static LocalDate getDate(Schedule schedule) throws Exception {
        int dayOfMonth = getDayOfMonth(schedule);
        return new LocalDate(YEAR, MONTH, dayOfMonth);
    }

    private static int getDayOfMonth(Schedule schedule) throws Exception {
        Day scheduleDay = schedule.getDay();
        switch (scheduleDay) {
            case THURSDAY:
                return 21;
            case FRIDAY:
                return 22;
            case SATURDAY:
                return 23;
            case SUNDAY:
                return 24;
            default:
                throw new Exception("Unable to parse schedule");

        }
    }


}
