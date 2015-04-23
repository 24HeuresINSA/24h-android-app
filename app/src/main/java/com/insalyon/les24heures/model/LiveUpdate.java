package com.insalyon.les24heures.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lbillon on 4/22/15.
 */
public class LiveUpdate implements Parcelable {

    private String title;
    private String message;
    private long time;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(message);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
