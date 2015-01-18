package com.insalyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by remi on 26/12/14.
 */
public class Category implements Parcelable {
    String name;
    String iconeName = "pouet";

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String iconeName) {
        this.name = name;
        this.iconeName = iconeName;
    }

    private Category(Parcel in) {
        name = in.readString();
        iconeName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(iconeName);
    }

    public static final Parcelable.Creator<Category> CREATOR
            = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    //TODO faire le equals sur autre chose que 'name'
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!name.equals(category.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconeName() {
        return iconeName;
    }

    public void setIconeName(String iconeName) {
        this.iconeName = iconeName;
    }


}
