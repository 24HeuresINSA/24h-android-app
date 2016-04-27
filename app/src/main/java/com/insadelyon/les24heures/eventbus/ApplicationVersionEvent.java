package com.insadelyon.les24heures.eventbus;

import com.insadelyon.les24heures.utils.ApplicationVersionState;

/**
 * Created by remi on 13/04/15.
 */
public class ApplicationVersionEvent {
    ApplicationVersionState state;

    public ApplicationVersionEvent() {
    }

    public ApplicationVersionEvent(ApplicationVersionState state) {
        this.state = state;
    }

    public ApplicationVersionState getState() {
        return state;
    }

    public void setState(ApplicationVersionState state) {
        this.state = state;
    }
}
