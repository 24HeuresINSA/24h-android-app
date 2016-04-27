package com.insadelyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 11/02/15.
 */
public class Resource implements Parcelable {
    public static final Parcelable.Creator<DayResource> CREATOR
            = new Parcelable.Creator<DayResource>() {
        public DayResource createFromParcel(Parcel in) {
            return new DayResource(in);
        }

        public DayResource[] newArray(int size) {
            return new DayResource[size];
        }
    };
    Integer _id;
    String title;
    String description;
    List<Schedule> schedules;
    Boolean isFavorites;
    Category category;
    String mainPictureUrl;
    ArrayList<String> pictures;

    @Deprecated
    public Resource(String title, String description, List<Schedule> schedules, Boolean isFavorites, Category category) {
        this.title = title;
        this.description = description;
        this.schedules = schedules;
        this.isFavorites = isFavorites;
        this.category = category;
    }

    @Deprecated
    public Resource(String title, String description, List<Schedule> schedules, Boolean isFavorites, Category category, String mainPictureUrl, ArrayList<String> pictures) {
        this.title = title;
        this.description = description;
        this.schedules = schedules;
        this.isFavorites = isFavorites;
        this.category = category;
        this.mainPictureUrl = mainPictureUrl;
        this.pictures = pictures;
    }

    public Resource(String title, String description, List<Schedule> schedules, Category category, String mainPictureUrl, ArrayList<String> pictures, Integer _id) {
        this.title = title;
        this.description = description;
        this.schedules = schedules;
        this.category = category;
        this.mainPictureUrl = mainPictureUrl;
        this.pictures = pictures;
        this.isFavorites = false;
        this._id = _id;
    }

    public Resource(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        in.readList(this.schedules, ClassLoader.getSystemClassLoader());
        this.category = in.readParcelable(ClassLoader.getSystemClassLoader());
        this.isFavorites = in.readByte() != 0;
        this.mainPictureUrl = in.readString();
        this.pictures = in.readArrayList(ClassLoader.getSystemClassLoader());

    }

    public static Creator<DayResource> getCreator() {
        return CREATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (!category.equals(resource.category)) return false;
        if (!schedules.equals(resource.schedules)) return false;
        if (!title.equals(resource.title)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + schedules.hashCode();
        result = 31 * result + category.hashCode();
        return result;
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
        out.writeParcelable(category, flags);
        out.writeByte((byte) (isFavorites ? 1 : 0));
        out.writeString(mainPictureUrl);
        out.writeList(pictures);
    }

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

    @Override
    public String toString() {
        return "Resource{" +
                "title='" + title + '\'' +
                '}';
    }


    public String getMainPictureUrl() {
        return mainPictureUrl;
    }

    public void setMainPictureUrl(String mainPictureUrl) {
        this.mainPictureUrl = mainPictureUrl;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }
}
