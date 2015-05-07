package com.insalyon.les24heures.model;

import android.os.Parcel;

import com.insalyon.les24heures.utils.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remi on 11/02/15.
 */
public class NightResource extends Resource {

    private String facebookUrl;
    private String twitterUrl;
    private String siteUrl;
    private Stage stage;
    private Integer position;

    public NightResource(String title, String description, List<Schedule> schedules, Category category, String mainPictureUrl, ArrayList<String> pictures, String facebookUrl, String twitterUrl, String siteUrl, Stage stage, Integer _id, Integer position) {
        super(title, description, schedules, category, mainPictureUrl, pictures,_id);
        this.facebookUrl = facebookUrl;
        this.twitterUrl = twitterUrl;
        this.siteUrl = siteUrl;
        this.stage = stage;
        this.position = position;
    }

    public NightResource(Parcel in) {
        super(in);
        this.facebookUrl = in.readString();
        this.twitterUrl = in.readString();
        this.siteUrl = in.readString();
        this.stage = Stage.valueOf(in.readString());
        this.position = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(facebookUrl);
        out.writeString(twitterUrl);
        out.writeString(siteUrl);
        out.writeString(stage.toString());
        out.writeInt(position);
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

    public Stage getStage() {
        return stage;
    }

    public Integer getPosition() {
        return position;
    }
}
