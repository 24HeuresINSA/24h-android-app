package com.insadelyon.les24heures.service;

import com.insadelyon.les24heures.DTO.ApplicationVersionDTO;
import com.insadelyon.les24heures.DTO.AssomakerDTO;
import com.insadelyon.les24heures.DTO.LiveUpdateDTO;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by remi on 27/12/14.
 */
public interface RetrofitService {

    @Deprecated
    @GET("/anim/animations.json")
//    @GET("/artist/artists.json")
    void getResources(Callback<AssomakerDTO> cb);

    @GET("/")
    void getResources(@Query("version") String dataVersion, Callback<AssomakerDTO> cb);

    @GET("/liveUpdates")
    void getLiveUpdates(Callback<List<LiveUpdateDTO>> cb);

    @GET("/version")
    void getApplicationVersion(Callback<ApplicationVersionDTO> cb);

    @FormUrlEncoded
    @POST("/mobileClient")
    void postLiveUpdatesKey(@Field("regid")String regid,Callback<String> cb);



}
