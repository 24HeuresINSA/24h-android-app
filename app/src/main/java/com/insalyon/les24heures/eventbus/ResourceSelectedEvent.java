package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.Resource;

/**
 * Created by remi on 09/02/15.
 */
public class ResourceSelectedEvent {
    private Resource resource;

    public ResourceSelectedEvent(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
