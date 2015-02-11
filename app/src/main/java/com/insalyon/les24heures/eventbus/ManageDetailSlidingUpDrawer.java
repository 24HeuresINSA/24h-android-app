package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.utils.SlidingUpPannelState;

/**
 * Created by remi on 04/02/15.
 */
public class ManageDetailSlidingUpDrawer {
    SlidingUpPannelState state;
    DayResource dayResource;

    public DayResource getDayResource() {
        return dayResource;
    }

    public ManageDetailSlidingUpDrawer(SlidingUpPannelState state, DayResource dayResource) {
        this.state = state;
        this.dayResource = dayResource;
    }

    public void setDayResource(DayResource dayResource) {
        this.dayResource = dayResource;
    }

    public ManageDetailSlidingUpDrawer(SlidingUpPannelState state) {
        this.state = state;
    }

    public SlidingUpPannelState getState() {
        return state;
    }

    public void setState(SlidingUpPannelState state) {
        this.state = state;
    }
}
