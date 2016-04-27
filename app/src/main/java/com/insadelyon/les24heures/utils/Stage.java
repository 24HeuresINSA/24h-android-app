package com.insadelyon.les24heures.utils;

/**
 * Created by remi on 11/02/15.
 */
public enum Stage {
    BIG("Tourtel Twist"), SMALL("Nord"), NOSTAGE("Indéfinie");

    private String name = "";

    Stage(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
