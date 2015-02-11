package com.insalyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by remi on 11/02/15.
 */
public class Resource implements Parcelable {
    String title;
    String description;
    List<Schedule> schedules;
    Boolean isFavorites;
    Category category;

    public Resource(String title, String description, List<Schedule> schedules, Boolean isFavorites, Category category) {
        this.title = title;
        this.description = description;
        this.schedules = schedules;
        this.isFavorites = isFavorites;
        this.category = category;
    }

    public Resource(Parcel in){
        this.title = in.readString();
        this.description = in.readString();
        in.readList(this.schedules,ClassLoader.getSystemClassLoader());
        this.category = in.readParcelable(ClassLoader.getSystemClassLoader());
        this.isFavorites = in.readByte() != 0;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(description);
        out.writeList(schedules);
        out.writeParcelable(category,flags);
        out.writeByte((byte) (isFavorites ? 1 : 0));
    }

    public static final Parcelable.Creator<DayResource> CREATOR
            = new Parcelable.Creator<DayResource>() {
        public DayResource createFromParcel(Parcel in) {
            return new DayResource(in);
        }

        public DayResource[] newArray(int size) {
            return new DayResource[size];
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

    public Boolean isFavorites() {
        return isFavorites;
    }

    public void setIsFavorites(Boolean isFavorites) {
        this.isFavorites = isFavorites;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public static Creator<DayResource> getCreator() {
        return CREATOR;
    }
}
