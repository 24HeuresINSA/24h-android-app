package com.insalyon.les24heures.model;

import java.security.Timestamp;

/**
 * Created by remi on 26/12/14.
 */

//TODO
public class Schedule {
    String content;

    public Schedule(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
