package com.insadelyon.les24heures.eventbus;

/**
 * Created by remi on 18/04/15.
 */
public class MapsSetIsVisible {
    boolean isVisible;

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public MapsSetIsVisible(boolean isVisible) {
        this.isVisible = isVisible;

    }
}
