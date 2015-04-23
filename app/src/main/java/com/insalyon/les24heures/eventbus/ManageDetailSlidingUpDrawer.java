package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.utils.SlidingUpPannelState;

/**
 * Created by remi on 04/02/15.
 */
public class ManageDetailSlidingUpDrawer {
    SlidingUpPannelState state;
    DayResource dayResource;
    NightResource nightResource;

    public ManageDetailSlidingUpDrawer(SlidingUpPannelState state, DayResource dayResource) {
        this.state = state;
        this.dayResource = dayResource;
    }

    public ManageDetailSlidingUpDrawer(SlidingUpPannelState state) {
        this.state = state;
    }

    public ManageDetailSlidingUpDrawer(SlidingUpPannelState state, NightResource nightResource) {
        this.state = state;
        this.nightResource = nightResource;
    }

    public DayResource getDayResource() {
        return dayResource;
    }

    public NightResource getNightResource() {
        return nightResource;
    }

    public SlidingUpPannelState getState() {
        return state;
    }

}
