package com.insalyon.les24heures.service;

/**
 * Created by remi on 14/03/15.
 */
public interface DataBackendService {

    public void getResourcesAsyncFromBackend(RetrofitService retrofitService, String dataVersion);

    @Deprecated
    public void getResourcesAsyncFromBackend(RetrofitService retrofitService);

    public void getResourcesAsyncMock();


}
