package com.insalyon.les24heures.service;

import com.insalyon.les24heures.DTO.AssomakerDTO;
import com.insalyon.les24heures.DTO.ResourceDTO;

import java.util.Map;


import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by remi on 27/12/14.
 */
public interface ResourceRetrofitService {

    @GET("/anim/animations.json")
    void getResources(Callback<AssomakerDTO> cb);

}
