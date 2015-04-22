package com.insalyon.les24heures.DTO;

/**
 * Created by lbillon on 4/22/15.
 */
public class LiveUpdateDTO {
    private String title;
    private String description;

    public LiveUpdateDTO() {

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
