package com.insadelyon.les24heures.utils;

/**
 * Created by remi on 26/12/14.
 */
public enum OutputType {
    MAPS("Maps"),
    LIST("List");

    private String name = "";

    OutputType(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

}
