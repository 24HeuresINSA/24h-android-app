package com.insadelyon.les24heures.eventbus;

import com.insadelyon.les24heures.model.Resource;

/**
 * Created by remi on 04/03/15.
 */
public class ResourceUpdatedEvent {
    private Resource resource;

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }
}
