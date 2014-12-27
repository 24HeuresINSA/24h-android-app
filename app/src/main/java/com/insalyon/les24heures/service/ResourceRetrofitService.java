package com.insalyon.les24heures.service;

import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.ResourceDTO;

import java.util.List;
import java.util.Map;


import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by remi on 27/12/14.
 */
public interface ResourceRetrofitService {

    @GET("/anim/animations.json")
    void getResources(Callback< Map< String, Map< Integer , ResourceDTO> > > cb);

}
