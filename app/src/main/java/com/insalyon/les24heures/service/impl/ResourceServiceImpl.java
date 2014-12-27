package com.insalyon.les24heures.service.impl;

import com.google.android.gms.maps.model.LatLng;
import com.insalyon.les24heures.DTO.ResourceDTO;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;
import com.insalyon.les24heures.service.ResourceRetrofitService;
import com.insalyon.les24heures.service.ResourceService;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceServiceImpl   {

    public static Resource fromDTO(ResourceDTO resourceDTO){
        Category category = null;
        return new Resource(resourceDTO.getNom(),resourceDTO.getDescription(),null,
                new LatLng(Double.valueOf(resourceDTO.getLocX()),Double.valueOf(resourceDTO.getLocY())), category);
    }

    public static ArrayList<Resource> fromDTO(ArrayList<ResourceDTO> resourceDTOs) {
        ArrayList<Resource>  resources = new ArrayList<>();

        for (ResourceDTO resourceDTO : resourceDTOs) {
            resources.add(ResourceServiceImpl.fromDTO(resourceDTO));
        }

        return resources;

    }
}
