package com.insalyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.insalyon.les24heures.utils.Day;

import java.util.Date;

/**
 * Created by remi on 26/12/14.
 */

public class Schedule implements Parcelable {
    Day day;
    Date start;
    Date end;

    public Schedule(Day day, Date start, Date end) {
        this.day = day;
        this.start = start;
        this.end = end;
    }


    public Schedule(Parcel in) {
        this.start = new Date(in.readLong());
        this.end = new Date(in.readLong());
        this.day = Day.valueOf(in.readString());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(start.getTime());
        out.writeLong(end.getTime());
        out.writeString(day.toString());

    }


    @Override
    public String toString() {
        return getPrintableDay() + " " + start.getHours() + "h-" + end.getHours() + "h";
    }

    public String getPrintableDay() {
        //TODO minor avoir ca dans un fichier de conf

        switch (day) {
            case FRIDAY:
                return "Vendredi";
            case SATURDAY:
                return "Samedi";
            case SUNDAY:
                return "Dimanche";
            default:
                return "";
        }
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
