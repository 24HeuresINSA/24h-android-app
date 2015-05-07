package com.insalyon.les24heures.utils;

/**
 * Created by remi on 11/02/15.
 */
public enum Stage {
    BIG("Pression Live"), SMALL("Nord"), NOSTAGE("Indéfinie");

    private String name = "";

    Stage(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
