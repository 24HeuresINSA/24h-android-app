package com.insalyon.les24heures.model;

import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 26/12/14.
 */
public class DayResource extends Resource {

    LatLng loc;

    @Deprecated
    public DayResource(String title, String description, List<Schedule> schedules, LatLng loc, Category category) {
        super(title, description, schedules, false, category);
        this.loc = loc;
    }

    public DayResource(String title, String description, List<Schedule> schedules, Category category, String mainPictureUrl, ArrayList<String> pictures, LatLng loc) {
        super(title, description, schedules, category, mainPictureUrl, pictures);
        this.loc = loc;
    }

    @Deprecated
    public DayResource(String title, String description, List<Schedule> schedules, LatLng loc, Category category, Boolean isFavorites) {
        super(title, description, schedules, isFavorites, category);
        this.loc = loc;
    }


    public DayResource(Parcel in) {
        super(in);
        this.loc = in.readParcelable(ClassLoader.getSystemClassLoader());

    }


    @Override
    public int hashCode() {
       return title.hashCode();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(loc, flags);
    }


    public LatLng getLoc() {
        return loc;
    }


}
