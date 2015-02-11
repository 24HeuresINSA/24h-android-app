package com.insalyon.les24heures.model;

import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by remi on 26/12/14.
 */
public class DayResource extends Resource{

    LatLng loc;


    @Deprecated
    Marker marker;


    public DayResource(String title, String description, List<Schedule> schedules, LatLng loc, Category category) {
        super( title,  description,schedules,  false,  category);
        this.loc = loc;
    }


    @Deprecated
    public DayResource(String title, String description, List<Schedule> schedules, LatLng loc, Category category, Boolean isFavorites) {
        super(title,  description,schedules,  isFavorites,  category);
        this.loc = loc;
    }


    public DayResource(Parcel in){
        super(in);
        this.loc = in.readParcelable(ClassLoader.getSystemClassLoader());

    }



    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(loc, flags);
    }


    public LatLng getLoc() {
        return loc;
    }


    @Deprecated
    public Marker getMarker() {
        return marker;
    }

    @Deprecated
    public void setMarker(Marker marker) {
        this.marker = marker;
    }


}
