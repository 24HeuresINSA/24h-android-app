package com.insalyon.les24heures.eventbus;

import com.insalyon.les24heures.model.Resource;

import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceEvent {
    List<Resource> resourceList;

    public ResourceEvent(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }
}
