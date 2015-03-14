package com.insalyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by remi on 26/12/14.
 */
public class Category implements Parcelable {
    public static final Parcelable.Creator<Category> CREATOR
            = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    String _id;
    String name;
    String iconName;

    public Category(String _id, String name, String iconName) {
        this._id = _id;
        this.name = name;
        this.iconName = iconName;
    }

    @Deprecated
    public Category(String name, String iconName) {
        this.name = name;
        this.iconName = iconName;
    }

    private Category(Parcel in) {
        name = in.readString();
        iconName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(iconName);
    }

    //TODO faire le equals sur autre chose que 'name'
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (!iconName.equals(category.iconName) || !_id.equals(category._id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return iconName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String get_id() {
        return _id;
    }
}
