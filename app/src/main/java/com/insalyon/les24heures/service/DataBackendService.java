package com.insalyon.les24heures.service;

import com.insalyon.les24heures.model.DayResource;
import com.insalyon.les24heures.model.NightResource;
import com.insalyon.les24heures.model.Resource;

import java.util.ArrayList;

/**
 * Created by remi on 14/03/15.
 */
public interface DataBackendService {

    void getResourcesAsyncFromBackend(RetrofitService retrofitService, String dataVersion, ArrayList<DayResource> dayResources, ArrayList<NightResource> nightResources);

    Resource getResourceById(ArrayList<? extends Resource> resources, String resourceId);

    public void getResourcesAsyncMock();


}
