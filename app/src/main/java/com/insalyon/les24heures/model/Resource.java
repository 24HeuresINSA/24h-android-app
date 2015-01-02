package com.insalyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by remi on 26/12/14.
 */
public class Resource implements Parcelable{
    String title;
    String description;
    List<Schedule> schedules;
    LatLng loc;
    String imageUrl;
    Category category;

    //clairement pas cool ca
    Marker marker;



    public Resource(String title, String description, List<Schedule> schedules, LatLng loc, Category category) {
        this.title = title;
        this.description = description;
        this.schedules = schedules;
        this.loc = loc;
        this.category = category;
    }

    public Resource(String title, String description, List<Schedule> schedules, LatLng loc, String imageUrl, Category category) {
        this.title = title;
        this.description = description;
        this.schedules = schedules;
        this.loc = loc;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public Resource(Parcel in){
        this.title = in.readString();
        this.description = in.readString();
        //TODO read schedule
        this.loc = in.readParcelable(ClassLoader.getSystemClassLoader());
        this.imageUrl = in.readString();
        this.category = in.readParcelable(ClassLoader.getSystemClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(description);
        //TODO write scedule
        out.writeString(imageUrl);
        out.writeParcelable(loc,flags);
        out.writeParcelable(category,flags);
    }

    public static final Parcelable.Creator<Resource> CREATOR
            = new Parcelable.Creator<Resource>() {
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public LatLng getLoc() {
        return loc;
    }

    public void setLoc(LatLng loc) {
        this.loc = loc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }


}
