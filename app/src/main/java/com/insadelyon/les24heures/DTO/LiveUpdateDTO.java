package com.insadelyon.les24heures.DTO;

/**
 * Created by lbillon on 4/22/15.
 */
public class LiveUpdateDTO {
    private String title;
    private String message;
    private long timePublished;

    public LiveUpdateDTO() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTimePublished() {
        return timePublished;
    }

    public void setTimePublished(long timePublished) {
        this.timePublished = timePublished;
    }
}
