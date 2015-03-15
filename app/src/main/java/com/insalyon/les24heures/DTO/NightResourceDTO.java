package com.insalyon.les24heures.DTO;

import com.insalyon.les24heures.model.Category;

import java.util.ArrayList;

/**
 * Created by remi on 11/02/15.
 */

//TODO mapper comme il faut
public class NightResourceDTO {
    String name;
    String mainPictureUrl;
    ArrayList<ScheduleDTO> horaires;
    String description;
    String stage;
    Category category;
    String display_name;
    String facebook_url;
    String twitter_url;
    String site_url;

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

    public String getMainPictureUrl() {
        return mainPictureUrl;
    }

    public void setMainPictureUrl(String mainPictureUrl) {
        this.mainPictureUrl = mainPictureUrl;
    }

    public ArrayList<ScheduleDTO> getHoraires() {
        return horaires;
    }

    public void setHoraires(ArrayList<ScheduleDTO> horaires) {
        this.horaires = horaires;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }
}
