package com.insalyon.les24heures.model;

import android.os.Parcel;

import java.util.List;

/**
 * Created by remi on 11/02/15.
 */
public class NightResource extends Resource {

   private String facebookUrl;
    private String twitterUrl;
    private String siteUrl;


    public NightResource(String title, String description, List<Schedule> schedules, Category category, Boolean isFavorites,String facebookUrl, String twitterUrl, String siteUrl) {
        super(title,  description,schedules,  isFavorites,  category);
        this.facebookUrl = facebookUrl;
        this.twitterUrl = twitterUrl;
        this.siteUrl = siteUrl;
    }



    public NightResource(Parcel in){
        super(in);
        this.facebookUrl = in.readString();
        this.twitterUrl = in.readString();
        this.siteUrl = in.readString();
    }



    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(facebookUrl);
        out.writeString(twitterUrl);
        out.writeString(siteUrl);
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }
}
