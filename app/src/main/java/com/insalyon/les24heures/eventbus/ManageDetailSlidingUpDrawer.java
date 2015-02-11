package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.utils.SlidingUpPannelState;

/**
 * Created by remi on 04/02/15.
 */
public class ManageDetailSlidingUpDrawer {
    SlidingUpPannelState state;
    Resource resource;

    public Resource getResource() {
        return resource;
    }

    public ManageDetailSlidingUpDrawer(SlidingUpPannelState state, Resource resource) {
        this.state = state;
        this.resource = resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
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
