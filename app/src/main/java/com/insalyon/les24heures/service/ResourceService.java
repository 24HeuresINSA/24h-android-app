package com.insalyon.les24heures.service;

import com.google.android.gms.maps.model.LatLng;
import com.insalyon.les24heures.DTO.ResourceDTO;
import com.insalyon.les24heures.model.Category;
import com.insalyon.les24heures.model.Resource;
import com.insalyon.les24heures.model.Schedule;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public interface ResourceService {

    public Resource fromDTO(ResourceDTO resourceDTO);

    public ArrayList<Resource> fromDTO(ArrayList<ResourceDTO> resourceDTOs);
}
