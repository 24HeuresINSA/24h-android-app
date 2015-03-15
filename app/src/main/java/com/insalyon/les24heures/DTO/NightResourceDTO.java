package com.insalyon.les24heures.DTO;

import java.util.ArrayList;

/**
 * Created by remi on 11/02/15.
 */

public class NightResourceDTO {
    String name;
    String main_picture_url;
    ArrayList<String> pictures;
    ArrayList<ScheduleDTO> schedule;
    String description;
    String stage;
    String facebook_url;
    String twitter_url;
    String site_url;
    Integer _id;

    public String getFacebook_url() {
        return facebook_url;
    }

    public void setFacebook_url(String facebook_url) {
        this.facebook_url = facebook_url;
    }

    public String getTwitter_url() {
        return twitter_url;
    }

    public void setTwitter_url(String twitter_url) {
        this.twitter_url = twitter_url;
    }

    public String getSite_url() {
        return site_url;
    }

    public void setSite_url(String site_url) {
        this.site_url = site_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getMain_picture_url() {
        return main_picture_url;
    }

    public void setMain_picture_url(String main_picture_url) {
        this.main_picture_url = main_picture_url;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public ArrayList<ScheduleDTO> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<ScheduleDTO> schedule) {
        this.schedule = schedule;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
    }
}
