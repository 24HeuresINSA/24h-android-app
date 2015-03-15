package com.insalyon.les24heures.DTO;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public class DayResourceDTO {
    Integer _id;
    String name;
    String description;
    ArrayList<Float> localisation;
    ArrayList<ScheduleDTO> schedule;
    Integer category;
    String main_picture_url;
    ArrayList<String> pictures;

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer _id) {
        this._id = _id;
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

    public ArrayList<Float> getLocalisation() {
        return localisation;
    }

    public void setLocalisation(ArrayList<Float> localisation) {
        this.localisation = localisation;
    }

    public ArrayList<ScheduleDTO> getSchedule() {
        return schedule;
    }

    public void setSchedule(ArrayList<ScheduleDTO> schedule) {
        this.schedule = schedule;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
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
}
