package com.insalyon.les24heures.utils;

/**
 * Created by remi on 26/12/14.
 */
public enum FilterAction {
    ADDED("added"),
    REMOVED("removed");

    private String name = "";

    FilterAction(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
